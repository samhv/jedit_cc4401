package org.incha.core.jswingripples.eig;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

/**
 * JRipplesEIGEvent represent an {@link JSwingRipplesEIG} event that encapsulates various events of nodes and edges lifecyle, and content.
 * Event may contain any number of {@link JSwingRipplesEIGNodeEvent} and {@link JSwingRipplesEIGEdgeEvent} events of any particular type, that happened in the same time (i.e. between calls of
 *  {@link JSwingRipplesEIG#doLock(Object)} and {@link JSwingRipplesEIG#doUnLock(Object)} methods).
 * @author Maksym Petrenko
 *
 */
public class JSwingRipplesEIGEvent extends EventObject {
    private static final long serialVersionUID = 596785116755112817L;

    private final JSwingRipplesEvent[] events;
    private final JSwingRipplesEIGNodeEvent[] nodeEvents;
    private final JSwingRipplesEIGEdgeEvent[] edgeEvents;

	public JSwingRipplesEIGEvent(final JSwingRipplesEIG EIG,  final JSwingRipplesEvent[] events) {
		super(EIG);
		this.events = events;

        final List<JSwingRipplesEIGEdgeEvent> edgeEvents = new LinkedList<JSwingRipplesEIGEdgeEvent>();
        final List<JSwingRipplesEIGNodeEvent> nodeEvents = new LinkedList<JSwingRipplesEIGNodeEvent>();

        for (final JSwingRipplesEvent e : events) {
            if (e instanceof JSwingRipplesEIGNodeEvent) {
                nodeEvents.add((JSwingRipplesEIGNodeEvent) e);
            } else {
                edgeEvents.add((JSwingRipplesEIGEdgeEvent) e);
            }
        }

        this.nodeEvents = nodeEvents.toArray(new JSwingRipplesEIGNodeEvent[nodeEvents.size()]);
        this.edgeEvents = edgeEvents.toArray(new JSwingRipplesEIGEdgeEvent[edgeEvents.size()]);
	}


	/**
	 * Returns {@link JSwingRipplesEIG} object in which this event happened. As JRipplesEIG stores its data in a static way, this method is likely to have no specific use.
	 * @return
	 * {@link JSwingRipplesEIG} object in which this event happened. As JRipplesEIG stores its data in a static way, this method is likely to have no specific use.
	 */
	@Override
    public JSwingRipplesEIG getSource() {
		return (JSwingRipplesEIG) super.getSource();
	}

	/**
	 * Tells whether the event has any node events.
	 * @return
	 * 	<code>true</code> if this event has any node events, <code>false</code> otherwise
	 */
	public boolean hasNodeEvents() {
		return (nodeEvents.length>0);
	}

	/**
	 * Tells whether the event has any edge events.
	 * @return
	 * 	<code>true</code> if this event has any edge events, <code>false</code> otherwise
	 */
	public boolean hasEdgeEvents() {
		return (edgeEvents.length>0);
	}

	/**
	 * Returns the array of all node events related to this event.
	 * @return
	 * 	Array of all node events related to this event. If there is no node events registered with this event, empty array will be returned.
	 */
	public JSwingRipplesEIGNodeEvent[] getNodeEvents() {
		return nodeEvents;
	}

	/**
	 * Returns specific node events.
	 * @param types
	 * 	desired types of event, where types are one or more of constants defined in {@link JSwingRipplesEIGNodeEvent}
	 * @return
	 * 	Array of node events of the requested types related to this event. If there is no node events of requested types registered with this event, empty array will be returned.
	 */


	public JSwingRipplesEIGNodeEvent[] getNodeTypedEvents(final int[] types) {
		final ArrayList<JSwingRipplesEIGNodeEvent> tmpEventsSet=new ArrayList<JSwingRipplesEIGNodeEvent>();

		final ArrayList<Integer> tmpTypesSet=new ArrayList<Integer>();
		for (int i=0;i<types.length;i++)
		{
			tmpTypesSet.add(Integer.valueOf(types[i]));
		}

		for (int i=0;i<nodeEvents.length;i++) {
			if (tmpTypesSet.contains(Integer.valueOf(nodeEvents[i].getEventType())))
					tmpEventsSet.add(nodeEvents[i]);
		}

		return tmpEventsSet.toArray(new JSwingRipplesEIGNodeEvent[tmpEventsSet.size()]);
	}

	/**
	 * Returns specific node events.
	 * @param type
	 * 	desired type of event, where type is one of a constants defined in {@link JSwingRipplesEIGNodeEvent}
	 * @return
	 * 	Array of node events of the requested type related to this event. If there is no node events of requested type registered with this event, empty array will be returned.
	 */
	public JSwingRipplesEIGNodeEvent[] getNodeTypedEvents(final int type) {
		final ArrayList<JSwingRipplesEIGNodeEvent> tmpEventsSet=new ArrayList<JSwingRipplesEIGNodeEvent>();


		for (int i=0;i<nodeEvents.length;i++) {
			if (type==nodeEvents[i].getEventType())
					tmpEventsSet.add(nodeEvents[i]);
		}

		return tmpEventsSet.toArray(new JSwingRipplesEIGNodeEvent[tmpEventsSet.size()]);
	}
	/**
	 * Returns all edge events.
	 * @return
	 * 	Array of all edge events related to this event. If there is no edge events registered with this event, empty array will be returned.
	 */

	public JSwingRipplesEIGEdgeEvent[] getEdgeEvents() {

		return edgeEvents;
	}
	/**
	 * Returns specific edge events.
	 * @param types
	 * 	desired types of event, where types are one or more of constants defined in {@link JSwingRipplesEIGEdgeEvent}
	 * @return
	 * 	Array of edge events of the requested types related to this event. If there is no edge events of requested types registered with this event, empty array will be returned.
	 */
	public JSwingRipplesEIGEdgeEvent[] getEdgeTypedEvents(final int[] types) {
		final ArrayList<Integer> tmpTypesSet=new ArrayList<Integer>();

		for (int i=0;i<types.length;i++)
		{
			tmpTypesSet.add(Integer.valueOf(types[i]));
		}

        final ArrayList<JSwingRipplesEIGEdgeEvent> tmpEventsSet=new ArrayList<JSwingRipplesEIGEdgeEvent>();
		for (int i=0;i<edgeEvents.length;i++) {
			if (tmpTypesSet.contains(Integer.valueOf(edgeEvents[i].getEventType())))
					tmpEventsSet.add(edgeEvents[i]);
		}

		return tmpEventsSet.toArray(new JSwingRipplesEIGEdgeEvent[tmpEventsSet.size()]);
	}
	/**
	 * Returns specific edge events.
	 * @param type
	 * 	desired type of event, where type is one of a constants defined in {@link JSwingRipplesEIGEdgeEvent}
	 * @return
	 * 	Array of edge events of the requested type related to this event. If there is no edge events of requested types registered with this event, empty array will be returned.
	 */

	public JSwingRipplesEIGEdgeEvent[] getEdgeTypedEvents(final int type) {
		final ArrayList<JSwingRipplesEIGEdgeEvent> tmpEventsSet=new ArrayList<JSwingRipplesEIGEdgeEvent>();

		for (int i=0;i<edgeEvents.length;i++) {
			if (type==edgeEvents[i].getEventType())
					tmpEventsSet.add(edgeEvents[i]);
		}

		return tmpEventsSet.toArray(new JSwingRipplesEIGEdgeEvent[tmpEventsSet.size()]);
	}
	/**
     * @return the events
     */
    public JSwingRipplesEvent[] getEvents() {
        return events;
    }
}
