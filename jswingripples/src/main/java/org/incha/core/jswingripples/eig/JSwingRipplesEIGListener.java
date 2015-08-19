package org.incha.core.jswingripples.eig;
/**
 * 
 * JRipplesEIGListener interface should be implemented by classes who wish to receive updates on lifecyle and content events of nodes and edges.
 * 
 * @see JSwingRipplesEIGEvent 
 * @see JSwingRipplesEIG#addJRipplesEIGListener(JRipplesEIGListener)
 * @see JSwingRipplesEIG#removeJRipplesEIGListener(JRipplesEIGListener)
 * @see JSwingRipplesEIGNodeEvent
 * @see JSwingRipplesEIGEdgeEvent
 * @author Maksym Petrenko
 *
 */

public interface JSwingRipplesEIGListener {
	/**
	 * The method is called upon events in the JRipples EIG
	 * @param evt
	 * 	 an event that occured in the JRipples EIG
	 */
	void jRipplesEIGChanged(JSwingRipplesEIGEvent evt);
}
