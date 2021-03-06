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
package com.codenvy.ide.ext.java.jdt.core.dom;

import com.codenvy.ide.ext.java.jdt.core.compiler.CategorizedProblem;
import com.codenvy.ide.ext.java.jdt.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.jdt.internal.compiler.CompilationResult;
import com.codenvy.ide.ext.java.jdt.internal.compiler.Compiler;
import com.codenvy.ide.ext.java.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ICompilerRequestor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.IErrorHandlingPolicy;
import com.codenvy.ide.ext.java.jdt.internal.compiler.IProblemFactory;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.AccessRestriction;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ISourceType;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.CompilerOptions;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.AnnotationBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.Binding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.ExtraCompilerModifiers;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.PackageBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.parser.Parser;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.AbortCompilation;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.DefaultProblemFactory;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.ProblemReporter;
import com.codenvy.ide.ext.java.jdt.internal.compiler.util.HashtableOfObject;
import com.codenvy.ide.ext.java.jdt.internal.core.CancelableProblemFactory;
import com.codenvy.ide.ext.java.jdt.internal.core.util.BindingKeyResolver;
import com.codenvy.ide.ext.java.jdt.internal.core.util.CommentRecorderParser;
import com.codenvy.ide.util.ExceptionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class CompilationUnitResolver extends Compiler {
    public static final int RESOLVE_BINDING = 0x1;

    public static final int PARTIAL = 0x2;

    public static final int STATEMENT_RECOVERY = 0x4;

    public static final int IGNORE_METHOD_BODIES = 0x8;

    public static final int BINDING_RECOVERY = 0x10;

    public static final int INCLUDE_RUNNING_VM_BOOTCLASSPATH = 0x20;

    /**
     * Constant indicating that a reconcile operation should enable the bindings recovery
     *
     * @see ASTParser#setBindingsRecovery(boolean)
     * @see IBinding#isRecovered()
     * @since 3.3
     */
    public static final int ENABLE_BINDINGS_RECOVERY = 0x04;

    /**
     * Constant indicating that a reconcile operation should enable the statements recovery.
     *
     * @see ASTParser#setStatementsRecovery(boolean)
     * @since 3.3
     */
    public static final int ENABLE_STATEMENTS_RECOVERY = 0x02;

    /*
     * The sources that were requested. Map from file name (char[]) to org.eclipse.jdt.internal.compiler.env.ICompilationUnit.
     */
    HashtableOfObject requestedSources;

    /*
     * The binding keys that were requested. Map from file name (char[]) to BindingKey (or ArrayList if multiple keys in the same
     * file).
     */
    HashtableOfObject requestedKeys;

    DefaultBindingResolver.BindingTables bindingTables;

    boolean hasCompilationAborted;

    /** Set to <code>true</code> if the receiver was initialized using a java project name environment */
    boolean fromJavaProject;

    /**
     * Answer a new CompilationUnitVisitor using the given name environment and compiler options. The environment and options will
     * be in effect for the lifetime of the compiler. When the compiler is run, compilation results are sent to the given
     * requestor.
     *
     * @param environment
     *         org.eclipse.jdt.internal.compiler.api.env.INameEnvironment Environment used by the compiler in order to
     *         resolve type and package names. The name environment implements the actual connection of the compiler to the
     *         outside world (for example, in batch mode the name environment is performing pure file accesses, reuse previous
     *         build state or connection to repositories). Note: the name environment is responsible for implementing the actual
     *         classpath rules.
     * @param policy
     *         org.eclipse.jdt.internal.compiler.api.problem.IErrorHandlingPolicy Configurable part for problem handling,
     *         allowing the compiler client to specify the rules for handling problems (stop on first error or accumulate them
     *         all) and at the same time perform some actions such as opening a dialog in UI when compiling interactively.
     * @param compilerOptions
     *         The compiler options to use for the resolution.
     * @param requestor
     *         org.eclipse.jdt.internal.compiler.api.ICompilerRequestor Component which will receive and persist all
     *         compilation results and is intended to consume them as they are produced. Typically, in a batch compiler, it is
     *         responsible for writing out the actual .class files to the file system.
     * @param problemFactory
     *         org.eclipse.jdt.internal.compiler.api.problem.IProblemFactory Factory used inside the compiler to
     *         create problem descriptors. It allows the compiler client to supply its own representation of compilation problems
     *         in order to avoid object conversions. Note that the factory is not supposed to accumulate the created problems,
     *         the compiler will gather them all and hand them back as part of the compilation unit result.
     * @see com.codenvy.ide.ext.java.jdt.internal.compiler.DefaultErrorHandlingPolicies
     * @see com.codenvy.ide.ext.java.jdt.internal.compiler.CompilationResult
     */
    public CompilationUnitResolver(INameEnvironment environment, IErrorHandlingPolicy policy,
                                   CompilerOptions compilerOptions, ICompilerRequestor requestor, IProblemFactory problemFactory,
                                   boolean fromJavaProject) {

        super(environment, policy, compilerOptions, requestor, problemFactory);
        this.hasCompilationAborted = false;
        this.fromJavaProject = fromJavaProject;
    }

    /* Add additional source types */
    @Override
    public void accept(ISourceType[] sourceTypes, PackageBinding packageBinding, AccessRestriction accessRestriction) {
        // Need to reparse the entire source of the compilation unit so as to get source positions
        // (case of processing a source that was not known by beginToCompile (e.g. when asking to createBinding))
        // TODO
        // SourceTypeElementInfo sourceType = (SourceTypeElementInfo)sourceTypes[0];
        // accept((com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit)sourceType.getHandle().getCompilationUnit(),
        // accessRestriction);
    }

    @Override
    public synchronized void accept(com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit sourceUnit,
                                    AccessRestriction accessRestriction) {
        super.accept(sourceUnit, accessRestriction);
    }

    /**
     * Add the initial set of compilation units into the loop -> build compilation unit declarations, their bindings and record
     * their results.
     */
    protected void beginToCompile(com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit[] sourceUnits,
                                  String[] bindingKeys) {
        int sourceLength = sourceUnits.length;
        int keyLength = bindingKeys.length;
        int maxUnits = sourceLength + keyLength;
        this.totalUnits = 0;
        this.unitsToProcess = new CompilationUnitDeclaration[maxUnits];
        int index = 0;

        // walks the source units
        this.requestedSources = new HashtableOfObject();
        for (int i = 0; i < sourceLength; i++) {
            com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit sourceUnit = sourceUnits[i];
            CompilationUnitDeclaration parsedUnit;
            CompilationResult unitResult =
                    new CompilationResult(sourceUnit, index++, maxUnits, this.options.maxProblemsPerUnit);
            try {
                // diet parsing for large collection of units
                if (this.totalUnits < this.parseThreshold) {
                    parsedUnit = this.parser.parse(sourceUnit, unitResult);
                } else {
                    parsedUnit = this.parser.dietParse(sourceUnit, unitResult);
                }
                // initial type binding creation
                this.lookupEnvironment.buildTypeBindings(parsedUnit, null /*
                                                                       * no access restriction
                                                                       */);
                addCompilationUnit(sourceUnit, parsedUnit);
                this.requestedSources.put(unitResult.getFileName(), sourceUnit);
            } finally {
                sourceUnits[i] = null; // no longer hold onto the unit
            }
        }

        // walk the binding keys
        this.requestedKeys = new HashtableOfObject();
        for (int i = 0; i < keyLength; i++) {
            BindingKeyResolver resolver = new BindingKeyResolver(bindingKeys[i], this, this.lookupEnvironment);
            resolver.parse(true/* pause after fully qualified name */);
            // If it doesn't have a type name, then it is either an array type, package or base type, which will definitely not have
            // a compilation unit.
            // Skipping it will speed up performance because the call will open jars. (theodora)
            CompilationUnitDeclaration parsedUnit =
                    resolver.hasTypeName() ? resolver.getCompilationUnitDeclaration() : null;
            if (parsedUnit != null) {
                char[] fileName = parsedUnit.compilationResult.getFileName();
                Object existing = this.requestedKeys.get(fileName);
                if (existing == null) {
                    this.requestedKeys.put(fileName, resolver);
                } else if (existing instanceof ArrayList) {
                    ((ArrayList)existing).add(resolver);
                } else {
                    ArrayList list = new ArrayList();
                    list.add(existing);
                    list.add(resolver);
                    this.requestedKeys.put(fileName, list);
                }

            } else {
                char[] key = resolver.hasTypeName() ? resolver.getKey().toCharArray() // binary binding
                                                    : CharOperation
                                     .concatWith(resolver.compoundName(), '.'); // package binding or base type binding
                this.requestedKeys.put(key, resolver);
            }
        }

        // binding resolution
        this.lookupEnvironment.completeTypeBindings();
    }

    IBinding createBinding(String key) {
        if (this.bindingTables == null) {
            throw new RuntimeException("Cannot be called outside ASTParser#createASTs(...)"); //$NON-NLS-1$
        }
        BindingKeyResolver keyResolver = new BindingKeyResolver(key, this, this.lookupEnvironment);
        Binding compilerBinding = keyResolver.getCompilerBinding();
        if (compilerBinding == null) {
            return null;
        }
        DefaultBindingResolver resolver =
                new DefaultBindingResolver(this.lookupEnvironment, this.bindingTables, false, this.fromJavaProject);
        return resolver.getBinding(compilerBinding);
    }

    public static CompilationUnit convert(CompilationUnitDeclaration compilationUnitDeclaration, char[] source,
                                          int apiLevel, Map<String, String> options, boolean needToResolveBindings,
                                          DefaultBindingResolver.BindingTables bindingTables, int flags, boolean fromJavaProject) {
        BindingResolver resolver = null;
        AST ast = AST.newAST(apiLevel);
        ast.setDefaultNodeFlag(ASTNode.ORIGINAL);
        CompilationUnit compilationUnit = null;
        ASTConverter converter = new ASTConverter(options, needToResolveBindings);
        if (needToResolveBindings) {
            resolver =
                    new DefaultBindingResolver(compilationUnitDeclaration.scope, bindingTables,
                                               (flags & ENABLE_BINDINGS_RECOVERY) != 0, fromJavaProject);
            ast.setFlag(flags | AST.RESOLVED_BINDINGS);
        } else {
            resolver = new BindingResolver();
            ast.setFlag(flags);
        }
        ast.setBindingResolver(resolver);
        converter.setAST(ast);
        compilationUnit = converter.convert(compilationUnitDeclaration, source);
        compilationUnit.setLineEndTable(compilationUnitDeclaration.compilationResult.getLineSeparatorPositions());
        ast.setDefaultNodeFlag(0);
        ast.setOriginalModificationCount(ast.modificationCount());
        return compilationUnit;
    }

    protected static CompilerOptions getCompilerOptions(Map options, boolean statementsRecovery) {
        CompilerOptions compilerOptions = new CompilerOptions(options);
        compilerOptions.performMethodsFullRecovery = statementsRecovery;
        compilerOptions.performStatementsRecovery = statementsRecovery;
        compilerOptions.parseLiteralExpressionsAsConstants = false;
        compilerOptions.storeAnnotations = true /*
                                               * store annotations in the bindings
                                               */;
        return compilerOptions;
    }

    /* Low-level API performing the actual compilation */
    protected static IErrorHandlingPolicy getHandlingPolicy() {

        // passes the initial set of files to the batch oracle (to avoid finding more than once the same units when case insensitive
        // match)
        return new IErrorHandlingPolicy() {
            @Override
            public boolean stopOnFirstError() {
                return false;
            }

            @Override
            public boolean proceedOnErrors() {
                return false; // stop if there are some errors
            }
        };
    }

    /*
     * Answer the component to which will be handed back compilation results from the compiler
     */
    protected static ICompilerRequestor getRequestor() {
        return new ICompilerRequestor() {
            @Override
            public void acceptResult(CompilationResult compilationResult) {
                // do nothing
            }
        };
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.Compiler#initializeParser()
     */
    @Override
    public void initializeParser() {
        this.parser = new CommentRecorderParser(this.problemReporter, false);
    }

    @Override
    public void process(CompilationUnitDeclaration unit, int i) {
        // don't resolve a second time the same unit (this would create the same binding twice)
        char[] fileName = unit.compilationResult.getFileName();
        if (this.requestedKeys.get(fileName) == null && this.requestedSources.get(fileName) == null) {
            super.process(unit, i);
        }
    }

    /* Compiler crash recovery in case of unexpected runtime exceptions */
    @Override
    protected void handleInternalException(Throwable internalException, CompilationUnitDeclaration unit,
                                           CompilationResult result) {
        super.handleInternalException(internalException, unit, result);
        if (unit != null) {
            removeUnresolvedBindings(unit);
        }
    }

    /* Compiler recovery in case of internal AbortCompilation event */
    @Override
    protected void handleInternalException(AbortCompilation abortException, CompilationUnitDeclaration unit) {
        super.handleInternalException(abortException, unit);
        if (unit != null) {
            removeUnresolvedBindings(unit);
        }
        this.hasCompilationAborted = true;
    }

    // public static void parse(ICompilationUnit[] compilationUnits, ASTRequestor astRequestor, int apiLevel, Map options,
    // int flags, IProgressMonitor monitor)
    // {
    // try
    // {
    // CompilerOptions compilerOptions = new CompilerOptions(options);
    // compilerOptions.ignoreMethodBodies = (flags & ICompilationUnit.IGNORE_METHOD_BODIES) != 0;
    // Parser parser =
    // new CommentRecorderParser(new ProblemReporter(DefaultErrorHandlingPolicies.proceedWithAllProblems(),
    // compilerOptions, new DefaultProblemFactory()), false);
    // int unitLength = compilationUnits.length;
    // if (monitor != null)
    //            monitor.beginTask("", unitLength); //$NON-NLS-1$
    // for (int i = 0; i < unitLength; i++)
    // {
    // org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit =
    // (org.eclipse.jdt.internal.compiler.env.ICompilationUnit)compilationUnits[i];
    // CompilationResult compilationResult =
    // new CompilationResult(sourceUnit, 0, 0, compilerOptions.maxProblemsPerUnit);
    // CompilationUnitDeclaration compilationUnitDeclaration = parser.dietParse(sourceUnit, compilationResult);
    //
    // if (compilationUnitDeclaration.ignoreMethodBodies)
    // {
    // compilationUnitDeclaration.ignoreFurtherInvestigation = true;
    // // if initial diet parse did not work, no need to dig into method bodies.
    // continue;
    // }
    //
    // //fill the methods bodies in order for the code to be generated
    // //real parse of the method....
    // org.eclipse.jdt.internal.compiler.ast.TypeDeclaration[] types = compilationUnitDeclaration.types;
    // if (types != null)
    // {
    // for (int j = 0, typeLength = types.length; j < typeLength; j++)
    // {
    // types[j].parseMethods(parser, compilationUnitDeclaration);
    // }
    // }
    //
    // // convert AST
    // CompilationUnit node =
    // convert(compilationUnitDeclaration, parser.scanner.getSource(), apiLevel, options,
    // false/*don't resolve binding*/, null/*no binding table needed*/, flags /* flags */, monitor, true);
    // node.setTypeRoot(compilationUnits[i]);
    //
    // // accept AST
    // astRequestor.acceptAST(compilationUnits[i], node);
    //
    // if (monitor != null)
    // monitor.worked(1);
    // }
    // }
    // finally
    // {
    // if (monitor != null)
    // monitor.done();
    // }
    // }

//   public static void parse(String[] sourceUnits, String[] encodings, FileASTRequestor astRequestor, int apiLevel,
//      Map<String, String> options, int flags)
//   {
//      CompilerOptions compilerOptions = new CompilerOptions(options);
//      compilerOptions.ignoreMethodBodies = (flags & IGNORE_METHOD_BODIES) != 0;
//      Parser parser =
//         new CommentRecorderParser(new ProblemReporter(DefaultErrorHandlingPolicies.proceedWithAllProblems(),
//            compilerOptions, new DefaultProblemFactory()), false);
//      int unitLength = sourceUnits.length;
//      for (int i = 0; i < unitLength; i++)
//      {
//         char[] contents = null;
//         String encoding = encodings != null ? encodings[i] : null;
//         // try { TODO
//         // contents = Util.getFileCharContent(new File(sourceUnits[i]), encoding);
//         // } catch(IOException e) {
//         // // go to the next unit
//         // continue;
//         // }
//         if (contents == null)
//         {
//            // go to the next unit
//            continue;
//         }
//         com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit compilationUnit =
//            new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(contents, sourceUnits[i], encoding);
//         com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit sourceUnit = compilationUnit;
//         CompilationResult compilationResult =
//            new CompilationResult(sourceUnit, 0, 0, compilerOptions.maxProblemsPerUnit);
//         CompilationUnitDeclaration compilationUnitDeclaration = parser.dietParse(sourceUnit, compilationResult);
//
//         if (compilationUnitDeclaration.ignoreMethodBodies)
//         {
//            compilationUnitDeclaration.ignoreFurtherInvestigation = true;
//            // if initial diet parse did not work, no need to dig into method bodies.
//            continue;
//         }
//
//         // fill the methods bodies in order for the code to be generated
//         // real parse of the method....
//         com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeDeclaration[] types =
//            compilationUnitDeclaration.types;
//         if (types != null)
//         {
//            for (int j = 0, typeLength = types.length; j < typeLength; j++)
//            {
//               types[j].parseMethods(parser, compilationUnitDeclaration);
//            }
//         }
//
//         // convert AST
//         CompilationUnit node =
//            convert(compilationUnitDeclaration, parser.scanner.getSource(), apiLevel, options, false/*
//                                                                                                     * don 't resolve binding
//                                                                                                     */, null/*
//                                                                                                              * no binding
//                                                                                                              * table needed
//                                                                                                              */,
//               flags /* flags */, true);
//
//         // accept AST
//         astRequestor.acceptAST(sourceUnits[i], node);
//
//      }
//   }

    public static CompilationUnitDeclaration parse(
            com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit sourceUnit, NodeSearcher nodeSearcher,
            Map<String, String> settings, int flags) {
        if (sourceUnit == null) {
            throw new IllegalStateException();
        }
        CompilerOptions compilerOptions = new CompilerOptions(settings);
        boolean statementsRecovery = (flags & ENABLE_STATEMENTS_RECOVERY) != 0;
        compilerOptions.performMethodsFullRecovery = statementsRecovery;
        compilerOptions.performStatementsRecovery = statementsRecovery;
        compilerOptions.ignoreMethodBodies = (flags & IGNORE_METHOD_BODIES) != 0;
        Parser parser =
                new CommentRecorderParser(new ProblemReporter(DefaultErrorHandlingPolicies.proceedWithAllProblems(),
                                                              compilerOptions, new DefaultProblemFactory()), false);
        CompilationResult compilationResult = new CompilationResult(sourceUnit, 0, 0, compilerOptions.maxProblemsPerUnit);
        CompilationUnitDeclaration compilationUnitDeclaration = parser.dietParse(sourceUnit, compilationResult);

        if (compilationUnitDeclaration.ignoreMethodBodies) {
            compilationUnitDeclaration.ignoreFurtherInvestigation = true;
            // if initial diet parse did not work, no need to dig into method bodies.
            return compilationUnitDeclaration;
        }

        if (nodeSearcher != null) {
            char[] source = parser.scanner.getSource();
            int searchPosition = nodeSearcher.position;
            if (searchPosition < 0 || searchPosition > source.length) {
                // the position is out of range. There is no need to search for a node.
                return compilationUnitDeclaration;
            }

            compilationUnitDeclaration.traverse(nodeSearcher, compilationUnitDeclaration.scope);

            com.codenvy.ide.ext.java.jdt.internal.compiler.ast.ASTNode node = nodeSearcher.found;
            if (node == null) {
                return compilationUnitDeclaration;
            }

            com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeDeclaration enclosingTypeDeclaration =
                    nodeSearcher.enclosingType;

            if (node instanceof AbstractMethodDeclaration) {
                ((AbstractMethodDeclaration)node).parseStatements(parser, compilationUnitDeclaration);
            } else if (enclosingTypeDeclaration != null) {
                if (node instanceof com.codenvy.ide.ext.java.jdt.internal.compiler.ast.Initializer) {
                    ((com.codenvy.ide.ext.java.jdt.internal.compiler.ast.Initializer)node).parseStatements(parser,
                                                                                                          enclosingTypeDeclaration,
                                                                                                          compilationUnitDeclaration);
                } else if (node instanceof com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeDeclaration) {
                    ((com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeDeclaration)node).parseMethods(parser,
                                                                                                           compilationUnitDeclaration);
                }
            }
        } else {
            // fill the methods bodies in order for the code to be generated
            // real parse of the method....
            com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeDeclaration[] types =
                    compilationUnitDeclaration.types;
            if (types != null) {
                for (int j = 0, typeLength = types.length; j < typeLength; j++) {
                    types[j].parseMethods(parser, compilationUnitDeclaration);
                }
            }
        }
        return compilationUnitDeclaration;
    }

    // public static void resolve(ICompilationUnit[] compilationUnits, String[] bindingKeys, ASTRequestor requestor,
    // int apiLevel, Map options, IJavaProject javaProject, int flags, IProgressMonitor monitor)
    // {
    // TODO
    // CancelableNameEnvironment environment = null;
    // CancelableProblemFactory problemFactory = null;
    // try {
    // if (monitor != null) {
    // int amountOfWork = (compilationUnits.length + bindingKeys.length) * 2; // 1 for beginToCompile, 1 for resolve
    //            monitor.beginTask("", amountOfWork); //$NON-NLS-1$
    // }
    // environment = new CancelableNameEnvironment(((JavaProject) javaProject), monitor);
    // problemFactory = new CancelableProblemFactory(monitor);
    // CompilerOptions compilerOptions = getCompilerOptions(options, (flags & ICompilationUnit.ENABLE_STATEMENTS_RECOVERY) != 0);
    // compilerOptions.ignoreMethodBodies = (flags & ICompilationUnit.IGNORE_METHOD_BODIES) != 0;
    // CompilationUnitResolver resolver =
    // new CompilationUnitResolver(
    // environment,
    // getHandlingPolicy(),
    // compilerOptions,
    // getRequestor(),
    // problemFactory,
    // monitor,
    // javaProject != null);
    // resolver.resolve(compilationUnits, bindingKeys, requestor, apiLevel, options, owner, flags);
    // if (NameLookup.VERBOSE) {
    //            System.out.println(Thread.currentThread() + " TIME SPENT in NameLoopkup#seekTypesInSourcePackage: " + environment
    // .nameLookup.timeSpentInSeekTypesInSourcePackage + "ms");  //$NON-NLS-1$ //$NON-NLS-2$
    //            System.out.println(Thread.currentThread() + " TIME SPENT in NameLoopkup#seekTypesInBinaryPackage: " + environment
    // .nameLookup.timeSpentInSeekTypesInBinaryPackage + "ms");  //$NON-NLS-1$ //$NON-NLS-2$
    // }
    // } catch (JavaModelException e) {
    // // project doesn't exist -> simple parse without resolving
    // parse(compilationUnits, requestor, apiLevel, options, flags, monitor);
    // } finally {
    // if (monitor != null) monitor.done();
    // if (environment != null) {
    // environment.setMonitor(null); // don't hold a reference to this external object
    // }
    // if (problemFactory != null) {
    // problemFactory.monitor = null; // don't hold a reference to this external object
    // }
    // }
    // }

    public static void resolve(String[] sourceUnits, String[] encodings, String[] bindingKeys,
                               FileASTRequestor requestor, int apiLevel, Map<String, String> options, List classpaths, int flags) {
        // TODO
        // INameEnvironmentWithProgress environment = null;
        // CancelableProblemFactory problemFactory = null;
        // try {
        // if (monitor != null) {
        // int amountOfWork = (sourceUnits.length + bindingKeys.length) * 2; // 1 for beginToCompile, 1 for resolve
        //               monitor.beginTask("", amountOfWork); //$NON-NLS-1$
        // }
        // Classpath[] allEntries = new Classpath[classpaths.size()];
        // classpaths.toArray(allEntries);
        // environment = new NameEnvironmentWithProgress(allEntries, null, monitor);
        // problemFactory = new CancelableProblemFactory(monitor);
        // CompilerOptions compilerOptions = getCompilerOptions(options, (flags & ICompilationUnit.ENABLE_STATEMENTS_RECOVERY) !=
        // 0);
        // compilerOptions.ignoreMethodBodies = (flags & ICompilationUnit.IGNORE_METHOD_BODIES) != 0;
        // CompilationUnitResolver resolver =
        // new CompilationUnitResolver(
        // environment,
        // getHandlingPolicy(),
        // compilerOptions,
        // getRequestor(),
        // problemFactory,
        // monitor,
        // false);
        // resolver.resolve(sourceUnits, encodings, bindingKeys, requestor, apiLevel, options, flags);
        // if (NameLookup.VERBOSE && (environment instanceof CancelableNameEnvironment)) {
        // CancelableNameEnvironment cancelableNameEnvironment = (CancelableNameEnvironment) environment;
        //               System.out.println(Thread.currentThread() + " TIME SPENT in NameLoopkup#seekTypesInSourcePackage: " +
        // cancelableNameEnvironment.nameLookup.timeSpentInSeekTypesInSourcePackage + "ms");  //$NON-NLS-1$ //$NON-NLS-2$
        //               System.out.println(Thread.currentThread() + " TIME SPENT in NameLoopkup#seekTypesInBinaryPackage: " +
        // cancelableNameEnvironment.nameLookup.timeSpentInSeekTypesInBinaryPackage + "ms");  //$NON-NLS-1$ //$NON-NLS-2$
        // }
        // } finally {
        // if (monitor != null) monitor.done();
        // if (environment != null) {
        // environment.setMonitor(null); // don't hold a reference to this external object
        // }
        // if (problemFactory != null) {
        // problemFactory.monitor = null; // don't hold a reference to this external object
        // }
        // }
    }

    // TODO
    public static CompilationUnitDeclaration resolve(
            com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit sourceUnit, List classpaths,
            NodeSearcher nodeSearcher, Map options, int flags, INameEnvironment nameEnvironment) {
        CompilationUnitDeclaration unit = null;
        CancelableProblemFactory problemFactory = null;
        CompilationUnitResolver resolver = null;
        try {
            problemFactory = new CancelableProblemFactory();
            CompilerOptions compilerOptions = getCompilerOptions(options, (flags & ENABLE_STATEMENTS_RECOVERY) != 0);
            boolean ignoreMethodBodies = (flags & IGNORE_METHOD_BODIES) != 0;
            compilerOptions.ignoreMethodBodies = ignoreMethodBodies;
            resolver =
                    new CompilationUnitResolver(nameEnvironment, getHandlingPolicy(), compilerOptions, getRequestor(),
                                                problemFactory, false);
            boolean analyzeAndGenerateCode = !ignoreMethodBodies;
            unit = resolver.resolve(null, // no existing compilation unit declaration
                                    sourceUnit, nodeSearcher, true, // method verification
                                    analyzeAndGenerateCode, // analyze code
                                    analyzeAndGenerateCode); // generate code
            if (resolver.hasCompilationAborted) {
                // the bindings could not be resolved due to missing types in name environment
                // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=86541
                CompilationUnitDeclaration unitDeclaration = parse(sourceUnit, nodeSearcher, options, flags);
                final int problemCount = unit.compilationResult.problemCount;
                if (problemCount != 0) {
                    unitDeclaration.compilationResult.problems = new CategorizedProblem[problemCount];
                    System.arraycopy(unit.compilationResult.problems, 0, unitDeclaration.compilationResult.problems, 0,
                                     problemCount);
                    unitDeclaration.compilationResult.problemCount = problemCount;
                }
                return unitDeclaration;
            }
            // if (NameLookup.VERBOSE && environment instanceof CancelableNameEnvironment) {
            // CancelableNameEnvironment cancelableNameEnvironment = (CancelableNameEnvironment) environment;
            //               System.out.println(Thread.currentThread() + " TIME SPENT in NameLoopkup#seekTypesInSourcePackage: " +
            // cancelableNameEnvironment.nameLookup.timeSpentInSeekTypesInSourcePackage + "ms");  //$NON-NLS-1$ //$NON-NLS-2$
            //               System.out.println(Thread.currentThread() + " TIME SPENT in NameLoopkup#seekTypesInBinaryPackage: " +
            // cancelableNameEnvironment.nameLookup.timeSpentInSeekTypesInBinaryPackage + "ms");  //$NON-NLS-1$ //$NON-NLS-2$
            // }
            return unit;
        } catch (Exception e) {
            //TODO log error
            throw new RuntimeException(e);
//            Log.error(CompilationUnitDeclaration.class, e);
//            return null;
        }
    }

    // public static IBinding[] resolve(final IJavaElement[] elements, int apiLevel, Map compilerOptions,
    // IJavaProject javaProject, int flags, IProgressMonitor monitor)
    // {
    //
    // final int length = elements.length;
    // final HashMap sourceElementPositions = new HashMap(); // a map from ICompilationUnit to int[] (positions in elements)
    // int cuNumber = 0;
    // final HashtableOfObjectToInt binaryElementPositions = new HashtableOfObjectToInt(); // a map from String (binding key) to
    // int (position in elements)
    // for (int i = 0; i < length; i++)
    // {
    // IJavaElement element = elements[i];
    // //TODO
    // // if (!(element instanceof SourceRefElement))
    //         //            throw new IllegalStateException(element + " is not part of a compilation unit or class file"); //$NON-NLS-1$
    // Object cu = element.getAncestor(IJavaElement.COMPILATION_UNIT);
    // if (cu != null)
    // {
    // // source member
    // IntArrayList intList = (IntArrayList)sourceElementPositions.get(cu);
    // if (intList == null)
    // {
    // sourceElementPositions.put(cu, intList = new IntArrayList());
    // cuNumber++;
    // }
    // intList.add(i);
    // }
    // else
    // {
    // // binary member
    // // try {
    // // String key = ((BinaryMember) element).getKey(true/*open to get resolved info*/);
    // // binaryElementPositions.put(key, i);
    // // } catch (JavaModelException e) {
    //            //               throw new IllegalArgumentException(element + " does not exist"); //$NON-NLS-1$
    // // }
    // }
    // }
    // ICompilationUnit[] cus = new ICompilationUnit[cuNumber];
    // sourceElementPositions.keySet().toArray(cus);
    //
    // int bindingKeyNumber = binaryElementPositions.size();
    // String[] bindingKeys = new String[bindingKeyNumber];
    // binaryElementPositions.keysToArray(bindingKeys);
    //
    // class Requestor extends ASTRequestor
    // {
    // IBinding[] bindings = new IBinding[length];
    //
    // public void acceptAST(ICompilationUnit source, CompilationUnit ast)
    // {
    // // TODO (jerome) optimize to visit the AST only once
    // IntArrayList intList = (IntArrayList)sourceElementPositions.get(source);
    // for (int i = 0; i < intList.length; i++)
    // {
    // //TODO
    // // final int index = intList.list[i];
    // // SourceRefElement element = (SourceRefElement)elements[index];
    // // DOMFinder finder = new DOMFinder(ast, element, true/*resolve binding*/);
    // // try
    // // {
    // // finder.search();
    // // }
    // // catch (JavaModelException e)
    // // {
    //               //                  throw new IllegalArgumentException(element + " does not exist"); //$NON-NLS-1$
    // // }
    // // this.bindings[index] = finder.foundBinding;
    // }
    // }
    //
    // public void acceptBinding(String bindingKey, IBinding binding)
    // {
    // int index = binaryElementPositions.get(bindingKey);
    // this.bindings[index] = binding;
    // }
    // }
    // Requestor requestor = new Requestor();
    // resolve(cus, bindingKeys, requestor, apiLevel, compilerOptions, javaProject, flags, monitor);
    // return requestor.bindings;
    // }

    /*
     * When unit result is about to be accepted, removed back pointers to unresolved bindings
     */
    public void removeUnresolvedBindings(CompilationUnitDeclaration compilationUnitDeclaration) {
        final com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeDeclaration[] types =
                compilationUnitDeclaration.types;
        if (types != null) {
            for (int i = 0, max = types.length; i < max; i++) {
                removeUnresolvedBindings(types[i]);
            }
        }
    }

    private void removeUnresolvedBindings(com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeDeclaration type) {
        final com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeDeclaration[] memberTypes = type.memberTypes;
        if (memberTypes != null) {
            for (int i = 0, max = memberTypes.length; i < max; i++) {
                removeUnresolvedBindings(memberTypes[i]);
            }
        }
        if (type.binding != null && (type.binding.modifiers & ExtraCompilerModifiers.AccUnresolved) != 0) {
            type.binding = null;
        }

        final com.codenvy.ide.ext.java.jdt.internal.compiler.ast.FieldDeclaration[] fields = type.fields;
        if (fields != null) {
            for (int i = 0, max = fields.length; i < max; i++) {
                if (fields[i].binding != null && (fields[i].binding.modifiers & ExtraCompilerModifiers.AccUnresolved) != 0) {
                    fields[i].binding = null;
                }
            }
        }

        final AbstractMethodDeclaration[] methods = type.methods;
        if (methods != null) {
            for (int i = 0, max = methods.length; i < max; i++) {
                if (methods[i].binding != null
                    && (methods[i].binding.modifiers & ExtraCompilerModifiers.AccUnresolved) != 0) {
                    methods[i].binding = null;
                }
            }
        }
    }

    // private void resolve(ICompilationUnit[] compilationUnits, String[] bindingKeys, ASTRequestor astRequestor,
    // int apiLevel, Map compilerOptions, int flags)
    // {
    //
    // // temporarily connect ourselves to the ASTResolver - must disconnect when done
    // astRequestor.compilationUnitResolver = this;
    // this.bindingTables = new DefaultBindingResolver.BindingTables();
    // CompilationUnitDeclaration unit = null;
    // try
    // {
    // int length = compilationUnits.length;
    // org.eclipse.jdt.internal.compiler.env.ICompilationUnit[] sourceUnits =
    // new org.eclipse.jdt.internal.compiler.env.ICompilationUnit[length];
    // System.arraycopy(compilationUnits, 0, sourceUnits, 0, length);
    // beginToCompile(sourceUnits, bindingKeys);
    // // process all units (some more could be injected in the loop by the lookup environment)
    // for (int i = 0; i < this.totalUnits; i++)
    // {
    // if (resolvedRequestedSourcesAndKeys(i))
    // {
    // // no need to keep resolving if no more ASTs and no more binding keys are needed
    // // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=114935
    // // cleanup remaining units
    // for (; i < this.totalUnits; i++)
    // {
    // this.unitsToProcess[i].cleanUp();
    // this.unitsToProcess[i] = null;
    // }
    // break;
    // }
    // unit = this.unitsToProcess[i];
    // try
    // {
    // super.process(unit, i); // this.process(...) is optimized to not process already known units
    //
    // // requested AST
    // char[] fileName = unit.compilationResult.getFileName();
    // ICompilationUnit source = (ICompilationUnit)this.requestedSources.get(fileName);
    // if (source != null)
    // {
    // // convert AST
    // CompilationResult compilationResult = unit.compilationResult;
    // org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit = compilationResult.compilationUnit;
    // char[] contents = sourceUnit.getContents();
    // AST ast = AST.newAST(apiLevel);
    // ast.setFlag(flags | AST.RESOLVED_BINDINGS);
    // ast.setDefaultNodeFlag(ASTNode.ORIGINAL);
    // ASTConverter converter =
    // new ASTConverter(compilerOptions, true/*need to resolve bindings*/, this.monitor);
    // BindingResolver resolver =
    // new DefaultBindingResolver(unit.scope, this.bindingTables,
    // (flags & ICompilationUnit.ENABLE_BINDINGS_RECOVERY) != 0, this.fromJavaProject);
    // ast.setBindingResolver(resolver);
    // converter.setAST(ast);
    // CompilationUnit compilationUnit = converter.convert(unit, contents);
    // compilationUnit.setTypeRoot(source);
    // compilationUnit.setLineEndTable(compilationResult.getLineSeparatorPositions());
    // ast.setDefaultNodeFlag(0);
    // ast.setOriginalModificationCount(ast.modificationCount());
    //
    // // pass it to requestor
    // astRequestor.acceptAST(source, compilationUnit);
    //
    // worked(1);
    //
    // // remove at the end so that we don't resolve twice if a source and a key for the same file name have been requested
    // this.requestedSources.put(fileName, null); // mark it as removed
    // }
    //
    // // requested binding
    // Object key = this.requestedKeys.get(fileName);
    // if (key != null)
    // {
    // if (key instanceof BindingKeyResolver)
    // {
    // reportBinding(key, astRequestor, unit);
    // worked(1);
    // }
    // else if (key instanceof ArrayList)
    // {
    // Iterator iterator = ((ArrayList)key).iterator();
    // while (iterator.hasNext())
    // {
    // reportBinding(iterator.next(), astRequestor, unit);
    // worked(1);
    // }
    // }
    //
    // // remove at the end so that we don't resolve twice if a source and a key for the same file name have been requested
    // this.requestedKeys.put(fileName, null); // mark it as removed
    // }
    // }
    // finally
    // {
    // // cleanup compilation unit result
    // unit.cleanUp();
    // }
    // this.unitsToProcess[i] = null; // release reference to processed unit declaration
    // this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
    // }
    //
    // // remaining binding keys
    // DefaultBindingResolver resolver =
    // new DefaultBindingResolver(this.lookupEnvironment, this.bindingTables,
    // (flags & ICompilationUnit.ENABLE_BINDINGS_RECOVERY) != 0, true);
    // Object[] keys = this.requestedKeys.valueTable;
    // for (int j = 0, keysLength = keys.length; j < keysLength; j++)
    // {
    // BindingKeyResolver keyResolver = (BindingKeyResolver)keys[j];
    // if (keyResolver == null)
    // continue;
    // Binding compilerBinding = keyResolver.getCompilerBinding();
    // IBinding binding = compilerBinding == null ? null : resolver.getBinding(compilerBinding);
    // // pass it to requestor
    // astRequestor.acceptBinding(((BindingKeyResolver)this.requestedKeys.valueTable[j]).getKey(), binding);
    // worked(1);
    // }
    // }
    // catch (AbortCompilation e)
    // {
    // this.handleInternalException(e, unit);
    // }
    // catch (Error e)
    // {
    // this.handleInternalException(e, unit, null);
    // throw e; // rethrow
    // }
    // catch (RuntimeException e)
    // {
    // this.handleInternalException(e, unit, null);
    // throw e; // rethrow
    // }
    // finally
    // {
    // // disconnect ourselves from ast requestor
    // astRequestor.compilationUnitResolver = null;
    // }
    // }

    private void resolve(String[] sourceCompilationUnits, String[] encodings, String[] bindingKeys,
                         FileASTRequestor astRequestor, int apiLevel, Map compilerOptions, int flags) {

        // temporarily connect ourselves to the ASTResolver - must disconnect when done
        astRequestor.compilationUnitResolver = this;
        this.bindingTables = new DefaultBindingResolver.BindingTables();
        CompilationUnitDeclaration unit = null;
        try {
            int length = sourceCompilationUnits.length;
            com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit[] sourceUnits =
                    new com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit[length];
            int count = 0;
            for (int i = 0; i < length; i++) {
                char[] contents = null;
                String encoding = encodings != null ? encodings[i] : null;
                String sourceUnitPath = sourceCompilationUnits[i];
                // TODO
                // try {
                // contents = Util.getFileCharContent(new File(sourceUnitPath), encoding);
                // } catch(IOException e) {
                // // go to the next unit
                // continue;
                // }
                if (contents == null) {
                    // go to the next unit
                    continue;
                }
                sourceUnits[count++] =
                        new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(contents, sourceUnitPath, encoding);
            }
            beginToCompile(sourceUnits, bindingKeys);
            // process all units (some more could be injected in the loop by the lookup environment)
            for (int i = 0; i < this.totalUnits; i++) {
                if (resolvedRequestedSourcesAndKeys(i)) {
                    // no need to keep resolving if no more ASTs and no more binding keys are needed
                    // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=114935
                    // cleanup remaining units
                    for (; i < this.totalUnits; i++) {
                        this.unitsToProcess[i].cleanUp();
                        this.unitsToProcess[i] = null;
                    }
                    break;
                }
                unit = this.unitsToProcess[i];
                try {
                    super.process(unit, i); // this.process(...) is optimized to not process already known units

                    // requested AST
                    char[] fileName = unit.compilationResult.getFileName();
                    com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit source =
                            (com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit)this.requestedSources
                                    .get(fileName);
                    if (source != null) {
                        // convert AST
                        CompilationResult compilationResult = unit.compilationResult;
                        com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit sourceUnit =
                                compilationResult.compilationUnit;
                        char[] contents = sourceUnit.getContents();
                        AST ast = AST.newAST(apiLevel);
                        ast.setFlag(flags | AST.RESOLVED_BINDINGS);
                        ast.setDefaultNodeFlag(ASTNode.ORIGINAL);
                        ASTConverter converter = new ASTConverter(compilerOptions, true/*
                                                                                  * need to resolve bindings
                                                                                  */);
                        BindingResolver resolver =
                                new DefaultBindingResolver(unit.scope, this.bindingTables,
                                                           (flags & ENABLE_BINDINGS_RECOVERY) != 0, this.fromJavaProject);
                        ast.setBindingResolver(resolver);
                        converter.setAST(ast);
                        CompilationUnit compilationUnit = converter.convert(unit, contents);
                        compilationUnit.setLineEndTable(compilationResult.getLineSeparatorPositions());
                        ast.setDefaultNodeFlag(0);
                        ast.setOriginalModificationCount(ast.modificationCount());

                        // pass it to requestor
                        astRequestor.acceptAST(new String(source.getFileName()), compilationUnit);

                        // remove at the end so that we don't resolve twice if a source and a key for the same file name have been
                        // requested
                        this.requestedSources.put(fileName, null); // mark it as removed
                    }

                    // requested binding
                    Object key = this.requestedKeys.get(fileName);
                    if (key != null) {
                        if (key instanceof BindingKeyResolver) {
                            reportBinding(key, astRequestor, unit);
                        } else if (key instanceof ArrayList) {
                            Iterator iterator = ((ArrayList)key).iterator();
                            while (iterator.hasNext()) {
                                reportBinding(iterator.next(), astRequestor, unit);
                            }
                        }

                        // remove at the end so that we don't resolve twice if a source and a key for the same file name have been
                        // requested
                        this.requestedKeys.put(fileName, null); // mark it as removed
                    }
                } finally {
                    // cleanup compilation unit result
                    unit.cleanUp();
                }
                this.unitsToProcess[i] = null; // release reference to processed unit declaration
                this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
            }

            // remaining binding keys
            DefaultBindingResolver resolver =
                    new DefaultBindingResolver(this.lookupEnvironment, this.bindingTables,
                                               (flags & ENABLE_BINDINGS_RECOVERY) != 0, true);
            Object[] keys = this.requestedKeys.valueTable;
            for (int j = 0, keysLength = keys.length; j < keysLength; j++) {
                BindingKeyResolver keyResolver = (BindingKeyResolver)keys[j];
                if (keyResolver == null) {
                    continue;
                }
                Binding compilerBinding = keyResolver.getCompilerBinding();
                IBinding binding = compilerBinding == null ? null : resolver.getBinding(compilerBinding);
                // pass it to requestor
                astRequestor.acceptBinding(((BindingKeyResolver)this.requestedKeys.valueTable[j]).getKey(), binding);
            }
        } catch (AbortCompilation e) {
            this.handleInternalException(e, unit);
        } catch (Error e) {
            this.handleInternalException(e, unit, null);
            throw e; // rethrow
        } catch (RuntimeException e) {
            this.handleInternalException(e, unit, null);
            throw e; // rethrow
        } finally {
            // disconnect ourselves from ast requestor
            astRequestor.compilationUnitResolver = null;
        }
    }

    private void reportBinding(Object key, ASTRequestor astRequestor, CompilationUnitDeclaration unit) {
        BindingKeyResolver keyResolver = (BindingKeyResolver)key;
        Binding compilerBinding = keyResolver.getCompilerBinding();
        if (compilerBinding != null) {
            DefaultBindingResolver resolver =
                    new DefaultBindingResolver(unit.scope, this.bindingTables, false, this.fromJavaProject);
            AnnotationBinding annotationBinding = keyResolver.getAnnotationBinding();
            IBinding binding;
            if (annotationBinding != null) {
                binding = resolver.getAnnotationInstance(annotationBinding);
            } else {
                binding = resolver.getBinding(compilerBinding);
            }
            if (binding != null) {
                astRequestor.acceptBinding(keyResolver.getKey(), binding);
            }
        }
    }

    private void reportBinding(Object key, FileASTRequestor astRequestor, CompilationUnitDeclaration unit) {
        BindingKeyResolver keyResolver = (BindingKeyResolver)key;
        Binding compilerBinding = keyResolver.getCompilerBinding();
        if (compilerBinding != null) {
            DefaultBindingResolver resolver =
                    new DefaultBindingResolver(unit.scope, this.bindingTables, false, this.fromJavaProject);
            AnnotationBinding annotationBinding = keyResolver.getAnnotationBinding();
            IBinding binding;
            if (annotationBinding != null) {
                binding = resolver.getAnnotationInstance(annotationBinding);
            } else {
                binding = resolver.getBinding(compilerBinding);
            }
            if (binding != null) {
                astRequestor.acceptBinding(keyResolver.getKey(), binding);
            }
        }
    }

    private CompilationUnitDeclaration resolve(CompilationUnitDeclaration unit,
                                               com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit sourceUnit,
                                               NodeSearcher nodeSearcher,
                                               boolean verifyMethods, boolean analyzeCode, boolean generateCode) {

        try {

            if (unit == null) {
                // build and record parsed units
                this.parseThreshold = 0; // will request a full parse
                beginToCompile(new com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit[]{sourceUnit});
                // find the right unit from what was injected via accept(ICompilationUnit,..):
                for (int i = 0, max = this.totalUnits; i < max; i++) {
                    CompilationUnitDeclaration currentCompilationUnitDeclaration = this.unitsToProcess[i];
                    if (currentCompilationUnitDeclaration != null
                        && currentCompilationUnitDeclaration.compilationResult.compilationUnit == sourceUnit) {
                        unit = currentCompilationUnitDeclaration;
                        break;
                    }
                }
                if (unit == null) {
                    unit = this.unitsToProcess[0]; // fall back to old behavior
                }
            } else {
                // initial type binding creation
                this.lookupEnvironment.buildTypeBindings(unit, null /*
                                                                 * no access restriction
                                                                 */);

                // binding resolution
                this.lookupEnvironment.completeTypeBindings();
            }

            if (nodeSearcher == null) {
                this.parser.getMethodBodies(unit); // no-op if method bodies have already been parsed
            } else {
                int searchPosition = nodeSearcher.position;
                char[] source = sourceUnit.getContents();
                int length = source.length;
                if (searchPosition >= 0 && searchPosition <= length) {
                    unit.traverse(nodeSearcher, unit.scope);

                    com.codenvy.ide.ext.java.jdt.internal.compiler.ast.ASTNode node = nodeSearcher.found;

                    if (node != null) {
                        // save existing values to restore them at the end of the parsing process
                        // see bug 47079 for more details
                        int[] oldLineEnds = this.parser.scanner.lineEnds;
                        int oldLinePtr = this.parser.scanner.linePtr;

                        this.parser.scanner.setSource(source, unit.compilationResult);

                        com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeDeclaration enclosingTypeDeclaration =
                                nodeSearcher.enclosingType;
                        if (node instanceof AbstractMethodDeclaration) {
                            ((AbstractMethodDeclaration)node).parseStatements(this.parser, unit);
                        } else if (enclosingTypeDeclaration != null) {
                            if (node instanceof com.codenvy.ide.ext.java.jdt.internal.compiler.ast.Initializer) {
                                ((com.codenvy.ide.ext.java.jdt.internal.compiler.ast.Initializer)node).parseStatements(
                                        this.parser, enclosingTypeDeclaration, unit);
                            } else if (node instanceof com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeDeclaration) {
                                ((com.codenvy.ide.ext.java.jdt.internal.compiler.ast.TypeDeclaration)node).parseMethods(
                                        this.parser, unit);
                            }
                        }
                        // this is done to prevent any side effects on the compilation unit result
                        // line separator positions array.
                        this.parser.scanner.lineEnds = oldLineEnds;
                        this.parser.scanner.linePtr = oldLinePtr;
                    }
                }
            }

            if (unit.scope != null) {
                // fault in fields & methods
                unit.scope.faultInTypes();
                if (unit.scope != null && verifyMethods) {
                    // http://dev.eclipse.org/bugs/show_bug.cgi?id=23117
                    // verify inherited methods
                    unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
                }
                // type checking
                unit.resolve();

                // flow analysis
                if (analyzeCode) {
                    unit.analyseCode();
                }

                // // code generation
                if (generateCode) {
                    unit.generateCode();
                }

                // finalize problems (suppressWarnings)
                unit.finalizeProblems();
            }
            if (this.unitsToProcess != null) {
                this.unitsToProcess[0] = null; // release reference to processed unit declaration
            }
            this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
            return unit;
        } catch (AbortCompilation e) {
            this.handleInternalException(e, unit);
            return unit == null ? this.unitsToProcess[0] : unit;
        } catch (Error e) {
            this.handleInternalException(e, unit, null);
            throw e; // rethrow
        } catch (RuntimeException e) {
            String traceAsString = ExceptionUtils.getStackTraceAsString(e);
            invokeBrowserLogger(traceAsString);
            this.handleInternalException(e, unit, null);
            throw e; // rethrow
        } finally {
            // No reset is performed there anymore since,
            // within the CodeAssist (or related tools),
            // the compiler may be called *after* a call
            // to this resolve(...) method. And such a call
            // needs to have a compiler with a non-empty
            // environment.
            // this.reset();
        }
    }

    private static native void invokeBrowserLogger(Object o) /*-{
        if (console && console["error"]) {
            console["error"](o);
        }
        return;
    }-*/;

    /*
     * Internal API used to resolve a given compilation unit. Can run a subset of the compilation process
     */
    @Override
    public CompilationUnitDeclaration resolve(
            com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit sourceUnit, boolean verifyMethods,
            boolean analyzeCode, boolean generateCode) {

        return resolve(null, /* no existing compilation unit declaration */
                       sourceUnit, null/* no node searcher */, verifyMethods, analyzeCode, generateCode);
    }

    boolean resolvedRequestedSourcesAndKeys(int unitIndexToProcess) {
        if (unitIndexToProcess < this.requestedSources.size() && unitIndexToProcess < this.requestedKeys.size()) {
            return false; // must process at least this many units before checking to see if all are done
        }

        Object[] sources = this.requestedSources.valueTable;
        for (int i = 0, l = sources.length; i < l; i++) {
            if (sources[i] != null) {
                return false;
            }
        }
        Object[] keys = this.requestedKeys.valueTable;
        for (int i = 0, l = keys.length; i < l; i++) {
            if (keys[i] != null) {
                return false;
            }
        }
        return true;
    }

    /*
     * Internal API used to resolve a given compilation unit. Can run a subset of the compilation process
     */
    @Override
    public CompilationUnitDeclaration resolve(CompilationUnitDeclaration unit,
                                              com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit sourceUnit,
                                              boolean verifyMethods,
                                              boolean analyzeCode, boolean generateCode) {

        return resolve(unit, sourceUnit, null/* no node searcher */, verifyMethods, analyzeCode, generateCode);
    }
}
