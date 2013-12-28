/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.ui.text;


import com.codenvy.ide.ext.java.jdt.core.util.StringTokenizer;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentCommand;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.TextUtilities;
import com.codenvy.ide.text.TypedRegion;
import com.codenvy.ide.texteditor.api.DefaultIndentLineAutoEditStrategy;

/** Auto indent strategy for java strings */
public class JavaStringAutoIndentStrategy extends DefaultIndentLineAutoEditStrategy {

    private String fPartitioning;

    /**
     * The input string doesn't contain any line delimiter.
     *
     * @param inputString
     *         the given input string
     * @param indentation
     *         the indentation
     * @param delimiter
     *         the line delimiter
     * @return the display string
     */
    private String displayString(String inputString, String indentation, String delimiter) {

        int length = inputString.length();
        StringBuffer buffer = new StringBuffer(length);
        StringTokenizer tokenizer = new StringTokenizer(inputString, "\n\r", true); //$NON-NLS-1$
        while (tokenizer.hasMoreTokens()) {

            String token = tokenizer.nextToken();
            if (token.equals("\r")) { //$NON-NLS-1$
                buffer.append("\\r"); //$NON-NLS-1$
                if (tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken();
                    if (token.equals("\n")) { //$NON-NLS-1$
                        buffer.append("\\n"); //$NON-NLS-1$
                        buffer.append("\" + " + delimiter); //$NON-NLS-1$
                        buffer.append(indentation);
                        buffer.append("\""); //$NON-NLS-1$
                        continue;
                    } else {
                        buffer.append("\" + " + delimiter); //$NON-NLS-1$
                        buffer.append(indentation);
                        buffer.append("\""); //$NON-NLS-1$
                    }
                } else {
                    continue;
                }
            } else if (token.equals("\n")) { //$NON-NLS-1$
                buffer.append("\\n"); //$NON-NLS-1$
                buffer.append("\" + " + delimiter); //$NON-NLS-1$
                buffer.append(indentation);
                buffer.append("\""); //$NON-NLS-1$
                continue;
            }

            StringBuffer tokenBuffer = new StringBuffer();
            for (int i = 0; i < token.length(); i++) {
                char c = token.charAt(i);
                switch (c) {
                    case '\r':
                        tokenBuffer.append("\\r"); //$NON-NLS-1$
                        break;
                    case '\n':
                        tokenBuffer.append("\\n"); //$NON-NLS-1$
                        break;
                    case '\b':
                        tokenBuffer.append("\\b"); //$NON-NLS-1$
                        break;
                    case '\t':
                        // keep tabs verbatim
                        tokenBuffer.append("\t"); //$NON-NLS-1$
                        break;
                    case '\f':
                        tokenBuffer.append("\\f"); //$NON-NLS-1$
                        break;
                    case '\"':
                        tokenBuffer.append("\\\""); //$NON-NLS-1$
                        break;
                    case '\\':
                        tokenBuffer.append("\\\\"); //$NON-NLS-1$
                        break;
                    default:
                        tokenBuffer.append(c);
                }
            }
            buffer.append(tokenBuffer);
        }
        return buffer.toString();
    }

    /**
     * Creates a new Java string auto indent strategy for the given document partitioning.
     *
     * @param partitioning
     *         the document partitioning
     */
    public JavaStringAutoIndentStrategy(String partitioning) {
        super();
        fPartitioning = partitioning;
    }

    private boolean isLineDelimiter(Document document, String text) {
        String[] delimiters = document.getLegalLineDelimiters();
        if (delimiters != null)
            return TextUtilities.equals(delimiters, text) > -1;
        return false;
    }

    private String getLineIndentation(Document document, int offset) throws BadLocationException {

        // find start of line
        int adjustedOffset = (offset == document.getLength() ? offset - 1 : offset);
        Region line = document.getLineInformationOfOffset(adjustedOffset);
        int start = line.getOffset();

        // find white spaces
        int end = findEndOfWhiteSpace(document, start, offset);

        return document.get(start, end - start);
    }

    private String getModifiedText(String string, String indentation, String delimiter) {
        return displayString(string, indentation, delimiter);
    }

    private void javaStringIndentAfterNewLine(Document document, DocumentCommand command) throws BadLocationException {

        TypedRegion partition = TextUtilities.getPartition(document, fPartitioning, command.offset, true);
        int offset = partition.getOffset();
        int length = partition.getLength();

        if (command.offset == offset + length && document.getChar(offset + length - 1) == '\"')
            return;

        String indentation = getLineIndentation(document, command.offset);
        String delimiter = TextUtilities.getDefaultLineDelimiter(document);

        Region line = document.getLineInformationOfOffset(offset);
        String string = document.get(line.getOffset(), offset - line.getOffset()).trim();
        if (string.length() != 0 && !string.equals("+")) //$NON-NLS-1$
            indentation += String.valueOf("\t\t"); //$NON-NLS-1$

//		IPreferenceStore preferenceStore= JavaPlugin.getDefault().getPreferenceStore();
        boolean isLineDelimiter = isLineDelimiter(document, command.text);
        if (isLineDelimiter)
            command.text = "\" +" + command.text + indentation + "\"";  //$NON-NLS-1$//$NON-NLS-2$
        else if (command.text.length() > 1 && !isLineDelimiter)
            command.text = getModifiedText(command.text, indentation, delimiter);
    }

    private boolean isSmartMode() {
//		IWorkbenchPage page= JavaPlugin.getActivePage();
//		if (page != null)  {
//			IEditorPart part= page.getActiveEditor();
//			if (part instanceof ITextEditorExtension3) {
//				ITextEditorExtension3 extension= (ITextEditorExtension3) part;
//				return extension.getInsertMode() == ITextEditorExtension3.SMART_INSERT;
//			}
//		}
//		return false;
        return true;
    }

    /*
     * @see org.eclipse.jface.text.IAutoIndentStrategy#customizeDocumentCommand(Document, DocumentCommand)
     */
    @Override
    public void customizeDocumentCommand(Document document, DocumentCommand command) {
        try {
            if (command.text == null)
                return;
            if (isSmartMode())
                javaStringIndentAfterNewLine(document, command);
        } catch (BadLocationException e) {
        }
    }
}
