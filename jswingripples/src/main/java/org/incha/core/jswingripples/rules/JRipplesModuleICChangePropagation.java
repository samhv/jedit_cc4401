/*
 * Created on Dec 4, 2005
 *
 */
package org.incha.core.jswingripples.rules;

import java.awt.Color;
import java.awt.Image;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.incha.core.jswingripples.JRipplesICModuleInterface;
import org.incha.core.jswingripples.eig.JSwingRipplesEIG;
import org.incha.core.jswingripples.eig.JSwingRipplesEIGNode;
import org.incha.ui.jripples.EIGStatusMarks;
/**
 * @author Maksym Petrenko
 *
 */
public class JRipplesModuleICChangePropagation implements
		JRipplesICModuleInterface {
    private static final Log log = LogFactory.getLog(JRipplesModuleICChangePropagation.class);
    private final JSwingRipplesEIG eig;

    /**
     * @param eig eig.
     */
    public JRipplesModuleICChangePropagation(final JSwingRipplesEIG eig) {
        super();
        this.eig = eig;
    }
	/*
	 * (non-Javadoc)
	 *
	 * @see org.severe.jripples.modules.interfaces.JRipplesICModuleInterface#GetAvailableRulesForMark(java.lang.String)
	 */
	@Override
    public Set<String> GetAvailableRulesForMark(final String mark) {

		if (mark == null) {
			return null;
		} else if (mark.compareTo(EIGStatusMarks.BLANK) == 0) {
			return null;
		} else if (mark.compareTo(EIGStatusMarks.NEXT_VISIT) == 0) {
			final String marks[] = { EIGStatusMarks.CHANGED, EIGStatusMarks.VISITED_CONTINUE, EIGStatusMarks.VISITED };
			return (new LinkedHashSet<String>(Arrays.asList(marks)));
		} else if (mark.compareTo(EIGStatusMarks.CHANGED) == 0) {
			final String marks[] = { EIGStatusMarks.CHANGED};
			return (new LinkedHashSet<String>(Arrays.asList(marks)));
		} else if (mark.compareTo(EIGStatusMarks.VISITED_CONTINUE) == 0) {
			final String marks[] = { EIGStatusMarks.CHANGED,EIGStatusMarks.VISITED_CONTINUE};
			return (new LinkedHashSet<String>(Arrays.asList(marks)));
		} else {
			return null;
		}
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.severe.jripples.modules.interfaces.JRipplesModuleInterface#shutDown(int controllerType)
	 */
	@Override
    public void shutDown(final int controllerType) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.severe.jripples.modules.interfaces.JRipplesICModuleInterface#initializeStage()
	 */
	@Override
    public void InitializeStage() {
        final JSwingRipplesEIGNode[] nodes = eig.getAllNodes();
        final Set<JSwingRipplesEIGNode> impactedMemberNodes = new LinkedHashSet<JSwingRipplesEIGNode>();
        final Set<JSwingRipplesEIGNode> impactedTopNodes = new LinkedHashSet<JSwingRipplesEIGNode>();
        if (nodes != null) {
            for (int i = 0; i < nodes.length; i++) {
                if ((nodes[i].getMark().compareTo(EIGStatusMarks.LOCATED) != 0) && (nodes[i].getMark().compareTo(EIGStatusMarks.IMPACTED) != 0) && (nodes[i].getMark().compareTo(EIGStatusMarks.CHANGED) != 0))
                    nodes[i].setMark(EIGStatusMarks.BLANK);
                else
                    if (!nodes[i].isTop()) impactedMemberNodes.add(nodes[i]);
                    else impactedTopNodes.add(nodes[i]);
            }
            //          Process members first
            for (final Iterator<JSwingRipplesEIGNode> iter = impactedMemberNodes.iterator(); iter.hasNext();) {
                final JSwingRipplesEIGNode impacted_node = iter.next();
                impacted_node.setMark(EIGStatusMarks.NEXT_VISIT);
                CommonEIGRules.applyRuleToNode(eig, impacted_node,EIGStatusMarks.CHANGED,0);
            }

            //          Process top nodes if any
            for (final Iterator<JSwingRipplesEIGNode> iter = impactedTopNodes.iterator(); iter.hasNext();) {
                final JSwingRipplesEIGNode impacted_node = iter.next();

                if ( (impacted_node.getMark().compareTo(EIGStatusMarks.CHANGED) != 0)) {
                    impacted_node.setMark(EIGStatusMarks.NEXT_VISIT);
                    CommonEIGRules.applyRuleToNode(eig, impacted_node,EIGStatusMarks.CHANGED,0);
                }
            }

        }

        eig.getHistory().clear();
	}

	@Override
    public Set<String> getAllMarks() {
		final String marks[] = { EIGStatusMarks.CHANGED, EIGStatusMarks.VISITED_CONTINUE, EIGStatusMarks.VISITED,EIGStatusMarks.BLANK ,EIGStatusMarks.NEXT_VISIT};
		return (new LinkedHashSet<String>(Arrays.asList(marks)));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.severe.jripples.modules.interfaces.JRipplesICModuleInterface#ApplyRuleAtNode(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
    public void ApplyRuleAtNode(final String rule, final JSwingRipplesEIGNode node, final int granularity) {
        try {
            CommonEIGRules.applyRuleToNode(eig, node,rule,granularity);
        } catch (final Exception e) {
            log.error(e);
        }
	}

	@Override
    public void ApplyRuleAtNode(final String rule,
            final JSwingRipplesEIGNode nodeFrom, final JSwingRipplesEIGNode nodeTo) {
        if ((rule.compareTo(EIGStatusMarks.CHANGED) == 0) || (rule.compareTo(EIGStatusMarks.VISITED_CONTINUE) == 0)) {
            CommonEIGRules.assignMarkToNodeAndNeighbor(eig, nodeFrom, nodeTo,rule,EIGStatusMarks.NEXT_VISIT);

        } else if (rule.compareTo(EIGStatusMarks.VISITED) == 0) {

            CommonEIGRules.applyRuleToNode(eig, nodeFrom,rule,0);
        }
	}

	@Override
    public  Image getImageDescriptorForMark(final String mark) {
		return EIGStatusMarks.getImageDescriptorForMark(mark);
	}

	@Override
    public Color getColorForMark(final String mark) {
		return EIGStatusMarks.getColorForMark(mark);
	}
	/* (non-Javadoc)
	 * @see org.incha.core.jswingripples.JRipplesModuleInterface#initializeStage()
	 */
	@Override
	public void runInAnalize() {
	    InitializeStage();
	}
}
