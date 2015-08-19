package org.incha.core.jswingripples.eig;


/**
 *
 * JRipplesEIGEdgeEvent class represents lifecycle and content events that happen with a particular {@link JSwingRipplesEIGEdge}. That is,
 * creation, changes in From and To nodes this edge connects, mark and probability, and edge removal.
 *
 * @see JSwingRipplesEIGEdge
 * @author Maksym Petrenko
 *
 */
public class JSwingRipplesEIGEdgeEvent extends JSwingRipplesEvent {
    private static final long serialVersionUID = -7736491959814482153L;

    /**
	 * Event constant for edge's mark changes. Will occur if  {@link JSwingRipplesEIGEdge#setMark(String)} was called.
	 */
	public static final int EDGE_MARK_CHANGED = 1;

	/**
	 * Event constant for edge's probability changes. Will occur if {@link JSwingRipplesEIGEdge#setProbability(String)} was called.
	 */
	public static final int EDGE_PROBABILITY_CHANGED = 2;

	/**
	 * Event constant for {@link JSwingRipplesEIGEdge} From Node changes. Will occured if {@link JSwingRipplesEIGEdge#JRipplesEIGEdge(JRipplesEIGNode, JRipplesEIGNode)} constructor was called.
	 */
	public static final int EDGE_FROM_NODE_CHANGED = 4;

	/**
	 * Event constant for {@link JSwingRipplesEIGEdge} To Node changes. Will occured if {@link JSwingRipplesEIGEdge#JRipplesEIGEdge(JRipplesEIGNode, JRipplesEIGNode)} constructor was called.
	 */
	public static final int EDGE_TO_NODE_CHANGED = 8;


	/**
	 * Event constant for {@link JSwingRipplesEIGEdge} creation. Will occured if {@link JSwingRipplesEIG#addEdge(JRipplesEIGNode, JRipplesEIGNode)} was called.
	 */
	public static final int EDGE_ADDED = 16;

	/**
	 * Event constant for {@link JSwingRipplesEIGEdge} removal. Will occured if {@link JSwingRipplesEIG#removeEdge(JSwingRipplesEIGEdge)} was called or either Form Node or To Node was removed fromt the EIG (or their respective declaring class Nodes).
	 */
	public static final int EDGE_REMOVED = 32;

	/**
	 * Event constant for edge's count changes. Will occur if {@link JSwingRipplesEIGEdge#setCount(Integer)} was called.
	 */
	public static final int EDGE_COUNT_CHANGED = 64;
	private int type = 0;


	/**
	 * @param edge
	 * The edge this event occured on
	 * @param type
	 * Type of the edge event, where type is one of the constants defined in {@link JSwingRipplesEIGEdgeEvent}
	 */
	public JSwingRipplesEIGEdgeEvent(final JSwingRipplesEIGEdge edge, final int type) {
		super(edge);
		this.type = type;
	}
	/**
	 * Returns the type of the edge event, where type is one of the constants defined in {@link JSwingRipplesEIGEdgeEvent}.
	 * @return
	 * Type of the edge event, where type is one of the constants defined in {@link JSwingRipplesEIGEdgeEvent}
	 */
	public int getEventType() {
		return type;
	}
	/* (non-Javadoc)
	 * @see java.util.EventObject#getSource()
	 */
	@Override
	public JSwingRipplesEIGEdge getSource() {
	    return (JSwingRipplesEIGEdge) super.getSource();
	}
}
