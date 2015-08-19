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
public class JRipplesModuleICImpactAnalysis implements
		JRipplesICModuleInterface {
    private static final Log log = LogFactory.getLog(JRipplesModuleICImpactAnalysis.class);
//algorithm
//1. Identify all members or parents at specified granularity
//2. find all neighbors of these members
//3. filter them based on the specified granularity
//4. apply mark to the member and the neighbors
//5. verify bottom-up the consistency of the marks of all involved parties and in between
    private final JSwingRipplesEIG eig;
    /**
     * @param eig the eig.
     */
    public JRipplesModuleICImpactAnalysis(final JSwingRipplesEIG eig) {
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
			final String marks[] = { EIGStatusMarks.IMPACTED, EIGStatusMarks.VISITED_CONTINUE, EIGStatusMarks.VISITED };
			return (new LinkedHashSet<String>(Arrays.asList(marks)));
		} else if (mark.compareTo(EIGStatusMarks.IMPACTED) == 0) {
			final String marks[] = { EIGStatusMarks.IMPACTED};
			return (new LinkedHashSet<String>(Arrays.asList(marks)));
		} else if (mark.compareTo(EIGStatusMarks.VISITED_CONTINUE) == 0) {
			final String marks[] = { EIGStatusMarks.IMPACTED,EIGStatusMarks.VISITED_CONTINUE};
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
		final Set<JSwingRipplesEIGNode> locatedMemberNodes = new LinkedHashSet<JSwingRipplesEIGNode>();
		final Set<JSwingRipplesEIGNode> locatedTopNodes = new LinkedHashSet<JSwingRipplesEIGNode>();
		if (nodes != null) {
			for (int i = 0; i < nodes.length; i++) {
				if ((nodes[i].getMark()==null) || ( (nodes[i].getMark().compareTo(EIGStatusMarks.LOCATED) != 0) && (nodes[i].getMark().compareTo(EIGStatusMarks.IMPACTED) != 0) && (nodes[i].getMark().compareTo(EIGStatusMarks.CHANGED) != 0)))
					nodes[i].setMark(EIGStatusMarks.BLANK);
				else {
					if (!nodes[i].isTop()) locatedMemberNodes.add(nodes[i]);
						else locatedTopNodes.add(nodes[i]);
				}
			}
			//Process members first
			for (final Iterator<JSwingRipplesEIGNode> iter = locatedMemberNodes.iterator(); iter.hasNext();) {
				final JSwingRipplesEIGNode located_node = iter.next();
				located_node.setMark(EIGStatusMarks.NEXT_VISIT);
				CommonEIGRules.applyRuleToNode(eig, located_node,EIGStatusMarks.IMPACTED,0);
			}

			//Process top nodes if any
			for (final Iterator<JSwingRipplesEIGNode> iter = locatedTopNodes.iterator(); iter.hasNext();) {
				final JSwingRipplesEIGNode located_node = iter.next();

				if ( (located_node.getMark().compareTo(EIGStatusMarks.IMPACTED) != 0)) {
					located_node.setMark(EIGStatusMarks.NEXT_VISIT);
					CommonEIGRules.applyRuleToNode(eig, located_node,EIGStatusMarks.IMPACTED,0);
				}
			}

		}
		eig.getHistory().clear();
	}

	@Override
    public Set<String> getAllMarks() {
		final String marks[] = { EIGStatusMarks.IMPACTED, EIGStatusMarks.VISITED_CONTINUE, EIGStatusMarks.VISITED,EIGStatusMarks.BLANK ,EIGStatusMarks.NEXT_VISIT};
		return (new LinkedHashSet<String>(Arrays.asList(marks)));
	}

	@Override
    public void ApplyRuleAtNode(final String rule,  final JSwingRipplesEIGNode nodeFrom, final JSwingRipplesEIGNode nodeTo) {
        if ((rule.compareTo(EIGStatusMarks.IMPACTED) == 0) || (rule.compareTo(EIGStatusMarks.VISITED_CONTINUE) == 0)) {
            CommonEIGRules.assignMarkToNodeAndNeighbor(eig, nodeFrom, nodeTo,rule,EIGStatusMarks.NEXT_VISIT);

        } else if (rule.compareTo(EIGStatusMarks.VISITED) == 0) {

            CommonEIGRules.applyRuleToNode(eig, nodeFrom,rule,0);
        }
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
    public Image getImageDescriptorForMark(final String mark) {
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
