package org.incha.core.jswingripples.eig;

import java.util.EventObject;

/**
 *
 * JRipplesEIGEdgeEvent class represents lifecycle and content events that happen with a particular {@link JSwingRipplesEIGEdge}. That is,
 * creation, changes in From and To nodes this edge connects, mark and probability, and edge removal.
 *
 * @see JSwingRipplesEIGEdge
 * @author Maksym Petrenko
 *
 */
public class JSwingRipplesEvent extends EventObject {
    private static final long serialVersionUID = -7736491959814482153L;

	/**
	 * @param source the event source.
	 */
	public JSwingRipplesEvent(final Object source) {
		super(source);
	}
}
