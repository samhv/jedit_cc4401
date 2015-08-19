package org.incha.core.jswingripples.eig;


/**
 * JRipplesEIGEdge class represents a dependency (interoperation) between two nodes of
 * the EIG (Evolving Interoperation Graph). It also serves as the container for the
 * extra information that can be associated with this dependency like
 * Incremental Change status (mark) and probability.
 *
 * @see JSwingRipplesEIG
 * @see JSwingRipplesEIGNode
 * @author Maksym Petrenko
 *
 */
public class JSwingRipplesEIGEdge {


	private JSwingRipplesEIGNode fromNode;

	private JSwingRipplesEIGNode toNode;

	private String mark;
	private String probability;

	private final JSwingRipplesEIG eig;
	private Integer count=1;

	/**
	 * Constructor - creates an edge that represents a dependency
	 * between two nodes  and
	 * sets edge's mark and probability to empty string. <br>( fromNode O-------------------------> toNode )<br>
	 * Please note that edges, created directly with the constructor,
	 * will not be handled in the EIG. To create a dependency that is
	 * handled by the EIG, use {@link JSwingRipplesEIG#addEdge(JSwingRipplesEIGNode, JSwingRipplesEIGNode)}
	 * instead.
	 * @param fromNode
	 * 		a node, from which this dependency originates
	 * @param toNode
	 * 		a node, to which which this dependency points
	 */
	public JSwingRipplesEIGEdge(final JSwingRipplesEIG eig, final JSwingRipplesEIGNode fromNode,
	        final JSwingRipplesEIGNode toNode) {
		this.eig = eig;

		this.setFromNode(fromNode);
		this.setToNode(toNode);
		this.setMark(null);
		this.setProbability(null);
	}

	/**
	 * Associates EIG mark with the edge during Incremental
	 * Change process.
	 *
	 * @return EIG mark of the edge if there is a one; <code>null</code>
	 *         otherwise.
	 * @see #setMark(String)
	 */

	public String getMark() {
		return this.mark;
	}




	/**
	 * Return probability value associated with the edge. Probabilities can
	 * be evaluated through different software metrics during
	 * Incremental Change process.
	 *
	 * @return probability, associated with the edge if there is a one;
	 *         <code>null</code> otherwise.
	 * @see #setProbability(String)
	 */

	public String getProbability() {
		return this.probability;
	}




	/**
	 * Associates EIG mark with the edge during Incremental
	 * Change process.
	 *
	 * @param mark
	 *            EIG mark to be associated with this edge
	 * @see #getMark()
	 */
	public void setMark(final String mark) {
		this.mark = mark;
		eig.fireJRipplesEIGChanged(this,
				JSwingRipplesEIGEdgeEvent.EDGE_PROBABILITY_CHANGED);
	}

	/**
	 * associate probability value with the edge. Probabilities can be
	 * evaluated through different software metrics during
	 * Incremental Change process.
	 *
	 * @param probability
	 *            probability to be associated with this edge
	 * @see #getProbability()
	 */

	public void setProbability(final String probability) {
		if ((probability!=null) && this.probability!=null)
			if (probability.compareTo(this.probability)==0) return;

		this.probability = probability;
		/*undoHistory.addFirst(this.getProbability());
		undoHistory.addFirst("setProbability");
		if (!JRipplesEIG.redoInProgress) clearRedoHistory();
		this.probability = probability;*/
		eig.fireJRipplesEIGChanged(this,
				JSwingRipplesEIGEdgeEvent.EDGE_MARK_CHANGED);
	}



	/**
	 * Returns a {@link JSwingRipplesEIGNode}, from which this dependency originates.
	 * @return a {@link JSwingRipplesEIGNode}, from which this dependency originates
	 */
	public JSwingRipplesEIGNode getFromNode() {
		return this.fromNode;
	}

	/**
	 *Returns a {@link JSwingRipplesEIGNode}, to which which this dependency points.
	 * @return a {@link JSwingRipplesEIGNode}, to which which this dependency points
	 */
	public JSwingRipplesEIGNode getToNode() {
		return this.toNode;
	}


	/**
	 * Returns number of times the edges appears in the code.
	 * @return number of times the edges appears in the code
	 */
	public Integer getCount() {
		return count;
	}


	/**
	 * Sets the number of times the edge appears in the code. Typically used by parsers.
	 * @param count
	 * 		 number of times the edges appears in the code
	 */
	public void setCount(final Integer count) {
		if ((count!=null) && this.count!=null)
			if (count.compareTo(this.count)==0) return;

		this.count = count;
		/*undoHistory.addFirst(this.getProbability());
		undoHistory.addFirst("setProbability");
		if (!JRipplesEIG.redoInProgress) clearRedoHistory();
		this.probability = probability;*/
		eig.fireJRipplesEIGChanged(this,
				JSwingRipplesEIGEdgeEvent.EDGE_COUNT_CHANGED);
	}



	/**
	 * @param param
	 * 		 a {@link JSwingRipplesEIGNode}, from which this dependency originates
	 */
	private void setFromNode(final JSwingRipplesEIGNode param) {
		//undoHistory.addFirst(this.getFromNode());
		//undoHistory.addFirst("setFromNode");
		//if (!JRipplesEIG.redoInProgress) clearRedoHistory();
		this.fromNode = param;
		eig.fireJRipplesEIGChanged(this,
				JSwingRipplesEIGEdgeEvent.EDGE_FROM_NODE_CHANGED);
	}


	/**
	 * @param param
	 * 		a {@link JSwingRipplesEIGNode}, to which which this dependency points
	 */
	private void setToNode(final JSwingRipplesEIGNode param) {
		//undoHistory.addFirst(this.getToNode());
		//undoHistory.addFirst("setToNode");
		//if (!JRipplesEIG.redoInProgress) clearRedoHistory();
		this.toNode = param;
		eig.fireJRipplesEIGChanged(this,
				JSwingRipplesEIGEdgeEvent.EDGE_TO_NODE_CHANGED);
	}
	/**
	 * Returns string representation of the edge in the form of "fromNode O-------------------------> toNode".
	 * @return string representation of the edge in the form of "fromNode O-------------------------> toNode".
	 */
	@Override
    public String toString() {
		return this.getFromNode().getFullName()+" O-------------------------> "+this.getToNode().getFullName();
	}
	/**
     * @return the eig
     */
    public JSwingRipplesEIG getEig() {
        return eig;
    }
}
