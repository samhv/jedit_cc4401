package org.incha.core.jswingripples.parser;

/**
 * AST node Visitor use by the ASTExplorer
 * @author Manoel Marques
 */

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.incha.compiler.dom.JavaDomUtils;
import org.incha.core.JavaProject;


public class EdgesCalculator extends JRipplesASTVisitor {
    protected final Set<Edge> edges = new HashSet<Edge>();

    public Set<Edge> getEdges() {
        return this.edges;
    }

	public EdgesCalculator (final ICompilationUnit unit, final ICompilationUnit[] units) throws JavaModelException {
		super(unit, new BindingSupport(units), new NullProgressMonitor());
	}


	public EdgesCalculator (final ICompilationUnit unit, final ICompilationUnit[] units, final int codeBlockFirstCharacter, final int codeBlockLastCharacter)
	        throws JavaModelException {
		super(unit, new BindingSupport(units), new NullProgressMonitor(),
		        codeBlockFirstCharacter, codeBlockLastCharacter);
	}
    @Override
    public boolean visit(final ClassInstanceCreation node) {
        final Type type = node.getType();
        processTypeEdge(type);
        return true;
    }
    //method declaration
    @Override
    public boolean visit(final MethodDeclaration node) {
        final Type type = node.getReturnType2();
        processTypeEdge(type);
        return true;
    }
    @Override
    public boolean visit(final TypeDeclaration node) {
        final Type type = node.getSuperclassType();
        processTypeEdge(type);

        for (final Object in : node.superInterfaceTypes()) {
            processTypeEdge((Type) in);
        }
        return true;
    }
    /* (non-Javadoc)
     * @see org.incha.core.jswingripples.parser.JRipplesASTVisitor#visit(org.eclipse.jdt.core.dom.AnnotationTypeDeclaration)
     */
    @Override
    public boolean visit(final CastExpression node) {
        final Type type = node.getType();
        processTypeEdge(type);
        return true;
    }

    /**
     * @param typeNode
     */
    private void processTypeEdge(final Type typeNode) {
        if (typeNode == null || typeNode.isPrimitiveType()) {
            return;
        }
        analyzeBinding(typeNode.resolveBinding());
    }

    /**
     * @return
     */
    public IType getCurrentType() {
        return getCurrentMember().getDeclaringType();
    }

    @Override
    public boolean visit(final MethodInvocation node) {
        final IMethodBinding binding = node.resolveMethodBinding();
        if (binding == null && node.getExpression() != null) {
            //TODO implement method to method binding.
            final ITypeBinding tb = node.getExpression().resolveTypeBinding();
            if (tb != null) {
                analyzeBinding(tb);
            }
        } else {
            analyzeBinding(binding);
        }
        return true;
    }

    @Override
    public boolean visit(final VariableDeclarationFragment node) {
        final IVariableBinding binding = node.resolveBinding();
        if (binding == null) {
            return true;
        }

        final ITypeBinding typeBinding = binding.getType();
        if (typeBinding != null) {
            final IType fieldFragmentType = (IType) getJavaElement(typeBinding);
            if (fieldFragmentType != null && fieldFragmentType.getFullyQualifiedName() != null) {
                IType declaringType=JavaDomUtils.getTopDeclaringType(fieldFragmentType);

                if (declaringType==null) {
                    declaringType=fieldFragmentType;
                }
                if (edges != null){
                    edges.add(new Edge(getCurrentMember() ,fieldFragmentType));
                }
            }
        }

        return true;
    }

    @Override
    public boolean visit(final SuperConstructorInvocation node) {
        analyzeBinding(node.resolveConstructorBinding());
        return true;
    }
    @Override
    public boolean visit(final SuperMethodInvocation node) {
        analyzeBinding(node.resolveMethodBinding());
        return true;
    }
    @Override
    public boolean visit(final FieldDeclaration node) {
        final Type type = node.getType();
        if (type != null) {
            analyzeBinding(type.resolveBinding());
        }
        return true;
    }

    private void analyzeBinding(final IBinding binding) {
        if (binding==null) {
            return;
        }

        try {
            final IMember member = getCurrentMember();
            final IMember tmpMember = getMember(binding);
            if (tmpMember==null || tmpMember == member) {
                return;
            }

            final IType declaringType = JavaDomUtils.getTopDeclaringType(tmpMember);
            if (declaringType==null || declaringType.getFullyQualifiedName()==null) {
                return;
            }

            edges.add(new Edge(member, tmpMember));
        } catch (final Exception e) {
        }
    }

    /**
     * @param binding
     * @param el
     * @return
     */
    public IMember getMember(final IBinding binding) {
        final IJavaElement el = getJavaElement(binding);
        return el instanceof IMember ? (IMember) el : null;
    }
    /**
     * @param javaProject java project.
     * @param unit compilation unit to process.
     * @param units full set of units of project for resolving of types and variables.
     * @return the set of edges [IMember, IMember]
     * @throws JavaModelException
     */
    public static Set<Edge> getEdges(final JavaProject javaProject, final ICompilationUnit unit,
            final ICompilationUnit[] units) throws JavaModelException {
        return getEdges(new EdgesCalculator(unit, units), javaProject, unit);
    }

    /**
     * @param calculator
     * @param javaProject java project.
     * @param unit compilation unit to process.
     * @return the set of edges [IMember, IMember]
     */
    public static Set<Edge> getEdges(final EdgesCalculator calculator,
            final JavaProject javaProject, final ICompilationUnit unit) {
        calculator.init();

        //parse compilation unit to AST
        final ASTParser parser = createASTParser(javaProject, unit);
        final ASTNode ast=parser.createAST(null);

        //process AST and calculate edges.
        ast.accept(calculator);

        return calculator.getEdges();
    }

    /**
     * @param javaProject java project.
     * @param unit compilation unit to parse.
     * @return
     */
    private static ASTParser createASTParser(final JavaProject javaProject, final ICompilationUnit unit) {
        final ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setProject(null);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setUnitName(unit.getElementName());

        @SuppressWarnings("unchecked")
        final Hashtable<String,String> options=JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE,"1.7");
        options.put(JavaCore.COMPILER_SOURCE,"1.7");

        parser.setCompilerOptions(options);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        //get source folders
        final List<File> sourceFiles = javaProject.getBuildPath().getSources();
        final String[] sources = new String[sourceFiles.size()];
        for (int i = 0; i < sources.length; i++) {
            sources[i] = sourceFiles.get(i).getAbsolutePath();
        }

        parser.setEnvironment(new String[0], sources, null, false);

        try {
            parser.setSource(unit.getSource().toCharArray());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return parser;
    }
}
