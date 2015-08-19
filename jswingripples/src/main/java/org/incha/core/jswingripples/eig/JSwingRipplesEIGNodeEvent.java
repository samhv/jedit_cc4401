package org.incha.core.jswingripples.eig;


/**
 *
 * JRipplesEIGNodeEvent class represents lifecycle and content events that happen with a particular {@link JSwingRipplesEIGNode}. That is,
 * creation, changes in underlying member, mark and probability, and node removal.
 *
 * @see JSwingRipplesEIGNode
 * @author Maksym Petrenko
 *
 */

public class JSwingRipplesEIGNodeEvent extends JSwingRipplesEvent {
    private static final long serialVersionUID = 7335031541151638199L;

    /**
	 *  Event constant for {@link JSwingRipplesEIGNode} mark changes. Will occur if  {@link JSwingRipplesEIGNode#setMark(String)} was called.
	 */
	public static final int NODE_MARK_CHANGED = 1;

	/**
	 * Event constant for {@link JSwingRipplesEIGNode} probability changes. Will occur if {@link JSwingRipplesEIGNode#setProbability(String)} was called.
	 */
	public static final int NODE_PROBABILITY_CHANGED = 2;
	/**
	 * Event constant for {@link JSwingRipplesEIGNode} creation. Will occured if {@link JSwingRipplesEIG#addNode(org.eclipse.jdt.core.IMember)} was called.
	 */
	public static final int NODE_ADDED = 8;

	/**
	 * Event constant for {@link JSwingRipplesEIGNode} removal. Will occured if {@link JSwingRipplesEIG#removeNode(JSwingRipplesEIGNode)} was called, or if a Node of declaring class of this node was removed.
	 */
	public static final int NODE_REMOVED = 16;

	private int type = 0;

    private final String newValue;

    private final String oldValue;

	/**
	 * @param node
	 * The node this event occured on
	 * @param type
	 * Type of the node event, where type is one of the constants defined in {@link JSwingRipplesEIGNodeEvent}
	 */
	public JSwingRipplesEIGNodeEvent(final JSwingRipplesEIGNode node,
	        final int type, final String oldValue, final String newValue) {
		super(node);
		this.type = type;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	/**
	 * Returns the node this event occured on.
	 * @return
	 * The node this event occured on
	 */
	@Override
    public JSwingRipplesEIGNode getSource() {
		return (JSwingRipplesEIGNode) super.getSource();
	}
	/**
	 * Returns a type of the node event, where type is one of the constants defined in {@link JSwingRipplesEIGNodeEvent}.
	 * @return
	 * Type of the node event, where type is one of the constants defined in {@link JSwingRipplesEIGNodeEvent}
	 */
	public int getEventType() {
		return type;
	}
    /**
     * @return the value.
     */
    public String getNewValue() {
        return newValue;
    }
    /**
     * @return old value.
     */
    public String getOldValue() {
        return oldValue;
    }
}
