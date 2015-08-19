/*
 * Created on Dec 4, 2005
 *
 */
package org.incha.core.jswingripples.analysis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.incha.core.ModuleConfiguration;
import org.incha.core.jswingripples.JRipplesAnalysisModuleInterface;
import org.incha.core.jswingripples.eig.JSwingRipplesEIG;
import org.incha.core.jswingripples.eig.JSwingRipplesEIGEdge;
import org.incha.core.jswingripples.eig.JSwingRipplesEIGEvent;
import org.incha.core.jswingripples.eig.JSwingRipplesEIGListener;
import org.incha.core.jswingripples.eig.JSwingRipplesEIGNode;
import org.incha.core.jswingripples.eig.JSwingRipplesEIGNodeEvent;
import org.incha.ui.jripples.EIGStatusMarks;
/**
 * @author Maksym Petrenko
 *
 */
public class JRipplesModuleAnalysisDefaultImpactSetConnections implements
		JRipplesAnalysisModuleInterface, JSwingRipplesEIGListener {


	private Set<JSwingRipplesEIGNode> impact_set = new HashSet<JSwingRipplesEIGNode>();
    private final JSwingRipplesEIG eig;
	/**
     *
     */
    public JRipplesModuleAnalysisDefaultImpactSetConnections(final JSwingRipplesEIG eig) {
        super();
        this.eig = eig;
    }
	/*
	 * (non-Javadoc)
	 *
	 * @see org.severe.jripples.modules.interfaces.JRipplesAnalysisModuleInterface#AnalyzeProject(java.lang.String)
	 */
	@Override
    public void AnalyzeProject() {
		if (isConceptLocationModuleUsed()) {
			return;
	    }

		boolean dirty=!impact_set.isEmpty();

		impact_set.clear();

		final JSwingRipplesEIGNode[] nodes=eig.getAllNodes();
		if (nodes.length==0) return;

        for (int i=0;i<nodes.length;i++) {
			final JSwingRipplesEIGNode node = nodes[i];
			final String mark=node.getMark();
			if ((mark.compareTo(EIGStatusMarks.CHANGED)==0) || (mark.compareTo(EIGStatusMarks.IMPACTED)==0)) {
							if (impact_set.add(node))  dirty=true;
							if (impact_set.addAll(Arrays.asList(eig.getNodeMembers(node))))  dirty=true;
			}
		}

		if (dirty)
			calculateCouplingAndUpdateNodes();
	}
    /**
     * @return
     */
    protected boolean isConceptLocationModuleUsed() {
        return eig.getJavaProject().getModuleConfiguration().getIncrementalChange()
		        == ModuleConfiguration.MODULE_CONCEPT_LOCATION;
    }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.severe.jripples.modules.interfaces.JRipplesAnalysisModuleInterface#ReAnalyzeProjectAtNodes(java.util.Set)
	 */
	@Override
    public void ReAnalyzeProjectAtNodes(final Set<JSwingRipplesEIGNode> changed_nodes) {
		if (isConceptLocationModuleUsed())
			return;

		boolean dirty=false;

		final JSwingRipplesEIGNode[] nodes=eig.getAllNodes();
		if (nodes.length==0) return;

		for (int i=0;i<nodes.length;i++) {
			final JSwingRipplesEIGNode node = nodes[i];
			final String mark=node.getMark();
			if ((mark.compareTo(EIGStatusMarks.CHANGED)==0) || (mark.compareTo(EIGStatusMarks.IMPACTED)==0)) {
							if (impact_set.add(node))  dirty=true;
							if (impact_set.addAll(Arrays.asList(eig.getNodeMembers(node))))  dirty=true;
			} else {
				if (impact_set.remove(node))  dirty=true;
				if (impact_set.removeAll(Arrays.asList(eig.getNodeMembers(node))))  dirty=true;
				if ((mark.compareTo(EIGStatusMarks.VISITED_CONTINUE)==0) || (mark.compareTo(EIGStatusMarks.NEXT_VISIT)==0))
					dirty=true;
			}

		}

		if (dirty)
			calculateCouplingAndUpdateNodes();
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.severe.jripples.modules.interfaces.JRipplesModuleInterface#shutDown(int controllerType)
	 */
	@Override
    public void shutDown(final int controllerType) {
	    eig.removeJRipplesEIGListener(this);
	}

	@Override
    public void jRipplesEIGChanged(final JSwingRipplesEIGEvent event) {
		if (isConceptLocationModuleUsed())
			return;

		if (!event.hasNodeEvents()) return;

		boolean dirty=false;

		final JSwingRipplesEIGNodeEvent[] nodeEvents=event.getNodeTypedEvents(
				new int[] {JSwingRipplesEIGNodeEvent.NODE_ADDED,JSwingRipplesEIGNodeEvent.NODE_REMOVED,JSwingRipplesEIGNodeEvent.NODE_MARK_CHANGED});
		if (nodeEvents.length==0) return;

        for (int i=0;i<nodeEvents.length;i++) {
			final JSwingRipplesEIGNode changedNode = nodeEvents[i].getSource();
			switch (nodeEvents[i].getEventType()) {
				case JSwingRipplesEIGNodeEvent.NODE_REMOVED: {
					if (impact_set.remove(changedNode)) dirty=true;
					if (impact_set.removeAll(Arrays.asList(eig.getNodeMembers(changedNode)))) dirty=true;
					break;
				}
				case JSwingRipplesEIGNodeEvent.NODE_ADDED: {
					if (impact_set.add(changedNode))  dirty=true;
					if (impact_set.addAll(Arrays.asList(eig.getNodeMembers(changedNode))))  dirty=true;
					break;
				}
				case JSwingRipplesEIGNodeEvent.NODE_MARK_CHANGED: {
					final String mark=changedNode.getMark();
					if ((mark!=null) && ((mark.compareTo(EIGStatusMarks.CHANGED)==0) || (mark.compareTo(EIGStatusMarks.IMPACTED)==0))) {
							if (impact_set.add(changedNode))  dirty=true;
							if (impact_set.addAll(Arrays.asList(eig.getNodeMembers(changedNode))))  dirty=true;
					} else {
						if (impact_set.remove(changedNode))  dirty=true;
						if (impact_set.removeAll(Arrays.asList(eig.getNodeMembers(changedNode))))  dirty=true;
						if ((mark!=null) && (mark.compareTo(EIGStatusMarks.VISITED_CONTINUE)==0) || (mark.compareTo(EIGStatusMarks.NEXT_VISIT)==0))
							dirty=true;
					}
					break;
				}
			}
		}
		if (dirty)
			calculateCouplingAndUpdateNodes();
	}

	private void calculateCouplingAndUpdateNodes() {


		final JSwingRipplesEIGNode[] nodeArr=eig.getAllNodes();
		final JSwingRipplesEIGEdge[] edgesArr=eig.getAllEdges();

		final HashMap<JSwingRipplesEIGNode,Integer> nodes=new HashMap<JSwingRipplesEIGNode,Integer>();

		for (int i=0;i<nodeArr.length;i++) {
			nodes.put(nodeArr[i], Integer.valueOf(0));
		}

		for (int i=0;i<edgesArr.length;i++) {
			final JSwingRipplesEIGNode nodeFrom=edgesArr[i].getFromNode();
			final JSwingRipplesEIGNode nodeTo=edgesArr[i].getToNode();

			if ((impact_set.contains(nodeFrom))) {
				nodes.put(nodeTo, Integer.valueOf(nodes.get(nodeTo).intValue()+1) );
				if (!nodeTo.isTop()) {
					final JSwingRipplesEIGNode topNode=eig.findTopNodeForMemberNode(nodeTo);
					if (topNode!=null)
					nodes.put(topNode, Integer.valueOf(nodes.get(topNode).intValue()+1));
				}
			}

			if ((impact_set.contains(nodeTo))) {
				nodes.put(nodeFrom, Integer.valueOf(nodes.get(nodeFrom).intValue()+1) );
				if (!nodeFrom.isTop()) {
					final JSwingRipplesEIGNode topNode=eig.findTopNodeForMemberNode(nodeFrom);
					if (topNode!=null)
					nodes.put(topNode, Integer.valueOf(nodes.get(topNode).intValue()+1) );
				}
			}
		}

		for (final Iterator<JSwingRipplesEIGNode> iter=nodes.keySet().iterator();iter.hasNext();){
			final JSwingRipplesEIGNode node=iter.next();
			updateNodeProbability(node, nodes.get(node).toString());
		}
	}


	private void updateNodeProbability(final JSwingRipplesEIGNode node, String newProbability) {
		if (newProbability==null) newProbability="";

		if (node.getMark()==null) {
			newProbability="";
		} else if (!((node.getMark().compareTo(EIGStatusMarks.NEXT_VISIT)==0) || (node.getMark().compareTo(EIGStatusMarks.VISITED_CONTINUE)==0) )) {
			newProbability="";
		}

		String oldProbability=node.getProbability();
		if (oldProbability==null) oldProbability="";
		if (oldProbability.compareTo(newProbability)!=0) node.setProbability(newProbability);
	}
	/* (non-Javadoc)
	 * @see org.incha.core.jswingripples.JRipplesModuleInterface#runInAnalize()
	 */
	@Override
	public void runInAnalize() {
	    AnalyzeProject();
	}
}
