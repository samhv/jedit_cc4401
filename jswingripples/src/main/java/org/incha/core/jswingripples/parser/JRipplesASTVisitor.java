package org.incha.core.jswingripples.parser;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

public class JRipplesASTVisitor extends ASTVisitor {
    protected IProgressMonitor monitor;

    protected boolean checkForRange = false;
    protected int codeBlockFirstCharacter = 0;
    protected int codeBlockLastCharacter = 0;
    private final BindingSupport support;
    private final MemberSearchSupport memberSearch;
    private final Map<IBinding, IJavaElement> cache = new HashMap<IBinding, IJavaElement>();

    private IMember ownerMember;

    protected JRipplesASTVisitor(final ICompilationUnit unit, final BindingSupport bindingSupport)
            throws JavaModelException {
        super(false);
        support = bindingSupport;
        memberSearch = new MemberSearchSupport(unit);
    }

    protected JRipplesASTVisitor(final ICompilationUnit unit,  final BindingSupport bindingSupport,
            final IProgressMonitor monitor) throws JavaModelException {
        this(unit, bindingSupport);
        this.monitor = monitor;
        this.checkForRange = false;
    }

    protected JRipplesASTVisitor(final ICompilationUnit unit,
            final BindingSupport bindingSupport,
            final IProgressMonitor monitor, final int codeBlockFirstCharacter,
            final int codeBlockLastCharacter) throws JavaModelException {
        this(unit, bindingSupport);
        this.monitor = monitor;
        this.checkForRange = true;
        this.codeBlockFirstCharacter = codeBlockFirstCharacter;
        this.codeBlockLastCharacter = codeBlockLastCharacter;

    }

    private boolean isVisitChildren(final ASTNode node) {

        if (this.checkForRange) {
            if (((this.codeBlockLastCharacter >= node.getStartPosition()) && (this.codeBlockLastCharacter <= (node
                    .getStartPosition() + node.getLength())))
                    || ((this.codeBlockFirstCharacter >= node
                            .getStartPosition()) && (this.codeBlockFirstCharacter <= (node
                            .getStartPosition() + node.getLength())))
                    || ((this.codeBlockFirstCharacter < node.getStartPosition()) && (this.codeBlockLastCharacter > (node
                            .getStartPosition() + node.getLength()))))
                return true;
            else
                return false;
        } else {
            if (monitor != null)
                return (!(this.monitor.isCanceled()));
        }

        return true;
    }

    public void init() {
        this.checkForRange = false;
        this.codeBlockFirstCharacter = 0;
        this.codeBlockLastCharacter = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.jdt.core.dom.ASTVisitor#preVisit2(org.eclipse.jdt.core.dom
     * .ASTNode)
     */
    @Override
    public final boolean preVisit2(final ASTNode node) {
        if (!isVisitChildren(node)) {
            return false;
        }
        try {
            this.ownerMember = this.memberSearch.getOwnerMember(node.getStartPosition(), node.getLength());
        } catch (final JavaModelException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    /**
     * @return the ownerMember
     */
    public IMember getCurrentMember() {
        return ownerMember;
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.jdt.core.dom.ASTVisitor#preVisit(org.eclipse.jdt.core.dom
     * .ASTNode)
     */
    @Override
    public final void preVisit(final ASTNode node) {
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.jdt.core.dom.ASTVisitor#postVisit(org.eclipse.jdt.core.dom
     * .ASTNode)
     */
    @Override
    public final void postVisit(final ASTNode node) {
    }

    /**
     * @param binding
     * @return
     */
    protected IJavaElement getJavaElement(final IBinding binding) {
        if (cache.containsKey(binding)) {
            return cache.get(binding);
        }

        final IJavaElement el = support.findJavaElement(binding);
        cache.put(binding, el);
        return el;
    }
    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final AnnotationTypeDeclaration node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final AnnotationTypeMemberDeclaration node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final AnonymousClassDeclaration node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ArrayAccess node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ArrayCreation node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ArrayInitializer node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ArrayType node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final AssertStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final Assignment node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final Block node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final BlockComment node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final BooleanLiteral node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final BreakStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final CastExpression node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final CatchClause node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final CharacterLiteral node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ContinueStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final DoStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final EmptyStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final EnhancedForStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final EnumConstantDeclaration node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final EnumDeclaration node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ExpressionStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final FieldAccess node) {

        return isVisitChildren(node);
    }
    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ForStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final IfStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ImportDeclaration node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final InfixExpression node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final InstanceofExpression node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final Initializer node) {

        return isVisitChildren(node);
    }

    /**
     * Visits the given AST node.
     * <p>
     * Unlike other node types, the boolean returned by the default
     * implementation is controlled by a constructor-supplied
     * parameter  {@link #ASTVisitor(boolean) ASTVisitor(boolean)}
     * which is <code>false</code> by default.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @see #ASTVisitor()
     * @see #ASTVisitor(boolean)
     */
    @Override
    public boolean visit(final Javadoc node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final LabeledStatement node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final LineComment node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final MarkerAnnotation node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final MemberRef node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final MemberValuePair node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final MethodRef node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final MethodRefParameter node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final SimpleType node) {

        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final SingleMemberAnnotation node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final SingleVariableDeclaration node) {

        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final StringLiteral node) {
        return isVisitChildren(node);
    }
    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final SuperFieldAccess node) {
        return isVisitChildren(node);
    }
    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final SwitchCase node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final SwitchStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final SynchronizedStatement node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final TagElement node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final TextElement node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ThisExpression node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ThrowStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final TryStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final WhileStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final WildcardType node) {
        return isVisitChildren(node);
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final AnnotationTypeDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final AnnotationTypeMemberDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final AnonymousClassDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ArrayAccess node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ArrayCreation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ArrayInitializer node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ArrayType node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final AssertStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final Assignment node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final Block node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final BlockComment node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final BooleanLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final BreakStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final CastExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final CatchClause node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final CharacterLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ClassInstanceCreation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final CompilationUnit node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ConditionalExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ConstructorInvocation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ContinueStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final DoStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final EmptyStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final EnhancedForStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final EnumConstantDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final EnumDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ExpressionStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final FieldAccess node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final FieldDeclaration node) {



    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ForStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final IfStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ImportDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final InfixExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final InstanceofExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final Initializer node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final Javadoc node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final LabeledStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final LineComment node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final MarkerAnnotation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final MemberRef node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final MemberValuePair node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final MethodRef node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final MethodRefParameter node) {
        // default implementation: do nothing
    }
    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final MethodInvocation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final Modifier node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final NormalAnnotation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final NullLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final NumberLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final PackageDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final ParameterizedType node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ParenthesizedExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final PostfixExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final PrefixExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final PrimitiveType node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final QualifiedName node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final QualifiedType node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ReturnStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final SimpleName node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final SimpleType node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final SingleMemberAnnotation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final SingleVariableDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final StringLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final SuperConstructorInvocation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final SuperFieldAccess node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final SuperMethodInvocation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final SwitchCase node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final SwitchStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final SynchronizedStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final TagElement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final TextElement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ThisExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final ThrowStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final TryStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final WhileStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final WildcardType node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final TypeDeclarationStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final TypeLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @since 3.0
     */
    @Override
    public final void endVisit(final TypeParameter node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final VariableDeclarationExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final VariableDeclarationStatement node) {
        // default implementation: do nothing
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final TypeDeclarationStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final TypeLiteral node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final TypeParameter node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final VariableDeclarationExpression node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final VariableDeclarationStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final Modifier node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final NormalAnnotation node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final NullLiteral node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final NumberLiteral node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final PackageDeclaration node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final ParameterizedType node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ParenthesizedExpression node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final PostfixExpression node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final PrefixExpression node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final PrimitiveType node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final QualifiedName node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     * @since 3.0
     */
    @Override
    public boolean visit(final QualifiedType node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ReturnStatement node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final CompilationUnit node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ConditionalExpression node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ClassInstanceCreation node) {
        return isVisitChildren(node);
    }
    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final ConstructorInvocation node) {
        return isVisitChildren(node);
    }
    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final MethodDeclaration node) {
        return isVisitChildren(node);
    }
    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final MethodInvocation node) {
        return isVisitChildren(node);
    }


    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final SimpleName node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final TypeDeclaration node) {
        return isVisitChildren(node);
    }
    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final VariableDeclarationFragment node) {
        return isVisitChildren(node);
    }
    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final TypeDeclaration node) {
    }
    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final VariableDeclarationFragment node) {
    }
    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final SuperConstructorInvocation node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final SuperMethodInvocation node) {
        return isVisitChildren(node);
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true.
     * Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     * @return <code>true</code> if the children of this node should be
     * visited, and <code>false</code> if the children of this node should
     * be skipped
     */
    @Override
    public boolean visit(final FieldDeclaration node) {
        return isVisitChildren(node);
    }
    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node the node to visit
     */
    @Override
    public final void endVisit(final MethodDeclaration node) {
    }
}
