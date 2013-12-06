/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.java.worker;

import com.codenvy.ide.ext.java.jdt.codeassistant.AbstractJavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.codeassistant.CompletionProposalCollector;
import com.codenvy.ide.ext.java.jdt.codeassistant.FillArgumentNamesCompletionProposalCollector;
import com.codenvy.ide.ext.java.jdt.codeassistant.JavaContentAssistInvocationContext;
import com.codenvy.ide.ext.java.jdt.codeassistant.LazyGenericTypeProposal;
import com.codenvy.ide.ext.java.jdt.codeassistant.TemplateCompletionProposalComputer;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.IJavaElement;
import com.codenvy.ide.ext.java.jdt.core.IType;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.codeassist.CompletionEngine;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.ext.java.messages.ApplyProposalMessage;
import com.codenvy.ide.ext.java.messages.Change;
import com.codenvy.ide.ext.java.messages.ComputeCAProposalsMessage;
import com.codenvy.ide.ext.java.messages.RoutingTypes;
import com.codenvy.ide.ext.java.messages.WorkerProposal;
import com.codenvy.ide.ext.java.messages.impl.MessagesImpls;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.collections.js.JsoStringMap;
import com.codenvy.ide.runtime.AssertionFailedException;
import com.codenvy.ide.text.DocumentEvent;
import com.codenvy.ide.text.DocumentListener;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.util.UUID;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.webworker.client.messages.MessageFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WorkerCodeAssist {


    private Comparator<JavaCompletionProposal> comparator = new Comparator<JavaCompletionProposal>() {

        @Override
        public int compare(JavaCompletionProposal o1, JavaCompletionProposal o2) {

            if (o1.getRelevance() > o2.getRelevance())
                return -1;
            else if (o1.getRelevance() < o2.getRelevance())
                return 1;
            else
                return 0;
        }
    };
    private JavaParserWorker                   worker;
    private INameEnvironment                   nameEnvironment;
    private TemplateCompletionProposalComputer templateCompletionProposalComputer;
    private String                             projectId;
    private String                             docContext;
    private CompilationUnit                    unit;
    private JsoStringMap<JavaCompletionProposal> proposalMap = JsoStringMap.create();
    private String documentContent;

    public WorkerCodeAssist(JavaParserWorker worker, MessageFilter messageFilter, INameEnvironment nameEnvironment,
                            TemplateCompletionProposalComputer templateCompletionProposalComputer, String projectId, String docContext) {
        this.worker = worker;
        this.nameEnvironment = nameEnvironment;
        this.templateCompletionProposalComputer = templateCompletionProposalComputer;
        this.projectId = projectId;
        this.docContext = docContext;
        messageFilter.registerMessageRecipient(RoutingTypes.CA_COMPUTE_PROPOSALS,
                                               new MessageFilter.MessageRecipient<ComputeCAProposalsMessage>() {
                                                   @Override
                                                   public void onMessageReceived(final ComputeCAProposalsMessage message) {
                                                       GWT.runAsync(new RunAsyncCallback() {
                                                           @Override
                                                           public void onFailure(Throwable throwable) {
                                                               throw new RuntimeException(throwable);
                                                               //TODO log error
                                                           }

                                                           @Override
                                                           public void onSuccess() {
                                                               handleCAMessage(message);
                                                           }
                                                       });
                                                   }
                                               });
        messageFilter.registerMessageRecipient(RoutingTypes.APPLY_CA_PROPOSAL, new MessageFilter.MessageRecipient<ApplyProposalMessage>() {
            @Override
            public void onMessageReceived(ApplyProposalMessage message) {
                handleApply(message.id());
            }
        });
    }

    private void handleApply(String id) {
        if (!proposalMap.containsKey(id)) {
            return;
        }

        JavaCompletionProposal proposal = proposalMap.get(id);
        WorkerDocument document = new WorkerDocument(documentContent);
        final JsoArray<Change> changes = JsoArray.create();
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void documentAboutToBeChanged(DocumentEvent event) {
            }

            @Override
            public void documentChanged(DocumentEvent event) {
                MessagesImpls.ChangeImpl change = MessagesImpls.ChangeImpl.make();
                change.setOffset(event.getOffset()).setLength(event.getLength()).setText(event.getText());
                changes.add(change);
            }
        });
        proposal.apply(document);
        MessagesImpls.ProposalAppliedMessageImpl message = MessagesImpls.ProposalAppliedMessageImpl.make();
        message.setChanges(changes);
        Region selection = proposal.getSelection(document);
        if (selection != null) {
            MessagesImpls.RegionImpl region = MessagesImpls.RegionImpl.make();
            region.setLength(selection.getLength()).setOffset(selection.getOffset());
            message.setSelectionRegion(region);
        }
        message.setId(id);
        worker.sendMessage(message.serialize());
    }

    private void handleCAMessage(ComputeCAProposalsMessage message) {
        proposalMap = JsoStringMap.create();
        documentContent = message.docContent();
        JavaCompletionProposal[] proposals =
                computeCompletionProposals(unit, message.offset(), documentContent, message.fileName());

        MessagesImpls.CAProposalsComputedMessageImpl caComputedMessage = MessagesImpls.CAProposalsComputedMessageImpl.make();
        caComputedMessage.setId(message.id());
        JsoArray<WorkerProposal> workerProposals = JsoArray.create();
        for (JavaCompletionProposal proposal : proposals) {
            MessagesImpls.WorkerProposalImpl prop = MessagesImpls.WorkerProposalImpl.make();
            prop.setAutoInsertable(proposal.isAutoInsertable()).setDisplayText(proposal.getDisplayString())
                .setImage(proposal.getImage() == null ? null : proposal.getImage().name());
            String uuid = UUID.uuid();
            prop.setId(uuid);
            proposalMap.put(uuid, proposal);
            workerProposals.add(prop);
        }

        caComputedMessage.setProposals(workerProposals);
        worker.sendMessage(caComputedMessage.serialize());
    }

    public JavaCompletionProposal[] computeCompletionProposals(CompilationUnit unit, int offset, String documentContent,
                                                               String fileName) {
        if (unit == null) {
            return null;
        }
        WorkerDocument document = new WorkerDocument(documentContent);
        CompletionProposalCollector collector =
                //TODO receive vfs id
                new FillArgumentNamesCompletionProposalCollector(unit, document, offset, projectId, docContext,
                                                                 "dev-monit");
        CompletionEngine e = new CompletionEngine(nameEnvironment, collector, JavaCore.getOptions());
        try {
            e.complete(new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(
                    documentContent.toCharArray(),
                    fileName.substring(0, fileName.lastIndexOf('.')), "UTF-8"), offset, 0);

            JavaCompletionProposal[] javaCompletionProposals = collector.getJavaCompletionProposals();
            List<JavaCompletionProposal> types =
                    new ArrayList<JavaCompletionProposal>(Arrays.asList(javaCompletionProposals));
            if (types.size() > 0 && collector.getInvocationContext().computeIdentifierPrefix().length() == 0) {
                IType expectedType = collector.getInvocationContext().getExpectedType();
                if (expectedType != null) {
                    // empty prefix completion - insert LRU types if known, but prune if they already occur in the core list

                    // compute minmimum relevance and already proposed list
                    int relevance = Integer.MAX_VALUE;
                    Set<String> proposed = new HashSet<String>();
                    for (Iterator<JavaCompletionProposal> it = types.iterator(); it.hasNext(); ) {
                        AbstractJavaCompletionProposal p = (AbstractJavaCompletionProposal)it.next();
                        IJavaElement element = p.getJavaElement();
                        if (element instanceof IType)
                            proposed.add(((IType)element).getFullyQualifiedName());
                        relevance = Math.min(relevance, p.getRelevance());
                    }

                    // insert history types
                    List<String> history =
                            WorkerMessageHandler.get().getContentAssistHistory().getHistory(expectedType.getFullyQualifiedName())
                                                .getTypes();
                    relevance -= history.size() + 1;
                    for (Iterator<String> it = history.iterator(); it.hasNext(); ) {
                        String type = it.next();
                        if (proposed.contains(type))
                            continue;

                        JavaCompletionProposal proposal =
                                createTypeProposal(relevance, type, collector.getInvocationContext());

                        if (proposal != null)
                            types.add(proposal);
                        relevance++;
                    }
                }
            }

            List<JavaCompletionProposal> templateProposals =
                    templateCompletionProposalComputer.computeCompletionProposals(collector.getInvocationContext());
            JavaCompletionProposal[] array =
                    templateProposals.toArray(new JavaCompletionProposal[templateProposals.size()]);
            javaCompletionProposals = types.toArray(new JavaCompletionProposal[0]);
            JavaCompletionProposal[] proposals = new JavaCompletionProposal[javaCompletionProposals.length + array.length];
            System.arraycopy(javaCompletionProposals, 0, proposals, 0, javaCompletionProposals.length);
            System.arraycopy(array, 0, proposals, javaCompletionProposals.length, array.length);

            Arrays.sort(proposals, comparator);
            return proposals;
        } catch (AssertionFailedException ex) {
            //todo log errors
//            Log.error(getClass(), ex);
            throw new RuntimeException(ex);

        } catch (Exception ex) {
            //todo log errors
//            Log.error(getClass(), ex);
            throw new RuntimeException(ex);
        }
//        return new JavaCompletionProposal[0];
    }

    private JavaCompletionProposal createTypeProposal(int relevance, String fullyQualifiedType,
                                                      JavaContentAssistInvocationContext context) {
        IType type = WorkerTypeInfoStorage.get().getTypeByFqn(fullyQualifiedType);

        if (type == null)
            return null;

        com.codenvy.ide.ext.java.jdt.core.CompletionProposal proposal =
                com.codenvy.ide.ext.java.jdt.core.CompletionProposal.create(
                        com.codenvy.ide.ext.java.jdt.core.CompletionProposal.TYPE_REF, context.getInvocationOffset());
        proposal.setCompletion(fullyQualifiedType.toCharArray());
        proposal.setDeclarationSignature(Signature.getQualifier(type.getFullyQualifiedName().toCharArray()));
        proposal.setFlags(type.getFlags());
        proposal.setRelevance(relevance);
        proposal.setReplaceRange(context.getInvocationOffset(), context.getInvocationOffset());
        proposal.setSignature(Signature.createTypeSignature(fullyQualifiedType, true).toCharArray());

        return new LazyGenericTypeProposal(proposal, context);

    }

    public void setUnit(CompilationUnit unit) {
        this.unit = unit;
    }
}
