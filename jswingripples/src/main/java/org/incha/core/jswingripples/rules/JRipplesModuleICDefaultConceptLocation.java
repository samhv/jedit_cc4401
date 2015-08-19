/*
 * Created on Dec 4, 2005
 *
 */
package org.incha.core.jswingripples.rules;

import java.awt.Color;
import java.awt.Image;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.incha.core.jswingripples.JRipplesICModuleInterface;
import org.incha.core.jswingripples.eig.JSwingRipplesEIG;
import org.incha.core.jswingripples.eig.JSwingRipplesEIGNode;
import org.incha.ui.jripples.EIGStatusMarks;

/**
 * @author Maksym Petrenko
 *
 */
public class JRipplesModuleICDefaultConceptLocation implements JRipplesICModuleInterface {
	private JSwingRipplesEIGNode currentNode = null;
	private final JSwingRipplesEIG eig;

	/**
     * @param eig the eig.
     */
    public JRipplesModuleICDefaultConceptLocation(final JSwingRipplesEIG eig) {
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
			final String marks[] = { EIGStatusMarks.LOCATED, EIGStatusMarks.VISITED_CONTINUE, EIGStatusMarks.VISITED };
			return (new LinkedHashSet<String>(Arrays.asList(marks)));
		} else if (mark.compareTo(EIGStatusMarks.LOCATED) == 0) {
			final String marks[] = { EIGStatusMarks.LOCATED};
			return (new LinkedHashSet<String>(Arrays.asList(marks)));
		} else if (mark.compareTo(EIGStatusMarks.VISITED_CONTINUE) == 0) {
			final String marks[] = { EIGStatusMarks.LOCATED,EIGStatusMarks.VISITED_CONTINUE};
			return (new LinkedHashSet<String>(Arrays.asList(marks)));
		} else {
			return null;
		}
	}
	public JSwingRipplesEIGNode getCurrentNode() {
		return currentNode;
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
        if (nodes != null) {
            for (int i = 0; i < nodes.length; i++) {
                nodes[i].setMark(EIGStatusMarks.BLANK);
            }

            if (eig.getMainClass() != null) {
                final JSwingRipplesEIGNode mainType = getType(nodes, eig.getMainClass());
                if (mainType != null) {
                    mainType.setMark(EIGStatusMarks.NEXT_VISIT);
                    currentNode = mainType;
                }
            }
        }

        eig.getHistory().clear();
	}

	/**
     * @param nodes
     * @param mainClass
     * @return
     */
    private JSwingRipplesEIGNode getType(final JSwingRipplesEIGNode[] nodes, final String mainClass) {
        for (int i = 0; i < nodes.length; i++) {
            final IMember member = nodes[i].getNodeIMember();
            if (member instanceof IType && ((IType) member).getFullyQualifiedName().equals(
                    eig.getMainClass())) {
                return nodes[i];
            }
        }
        return null;
    }

    @Override
    public Set<String> getAllMarks() {
		final String marks[] = { EIGStatusMarks.LOCATED, EIGStatusMarks.VISITED_CONTINUE, EIGStatusMarks.VISITED,EIGStatusMarks.BLANK ,EIGStatusMarks.NEXT_VISIT};
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
        currentNode=node;
        if (rule.compareTo(EIGStatusMarks.LOCATED) == 0) {
            CommonEIGRules.assignMarkToNodeAndParents(eig, node,EIGStatusMarks.LOCATED);
        } else if (rule.compareTo(EIGStatusMarks.VISITED_CONTINUE) == 0) {
            CommonEIGRules.applyRuleToNode(eig, node,rule,granularity);

        } else if (rule.compareTo(EIGStatusMarks.VISITED) == 0) {

            CommonEIGRules.applyRuleToNode(eig, node,rule,0);
        }

	}

	@Override
    public void ApplyRuleAtNode(final String rule, final JSwingRipplesEIGNode nodeFrom, final JSwingRipplesEIGNode nodeTo) {
        if (rule.compareTo(EIGStatusMarks.LOCATED) == 0) {
            CommonEIGRules.assignMarkToNodeAndParents(eig, nodeFrom,EIGStatusMarks.LOCATED);

        } else if (rule.compareTo(EIGStatusMarks.VISITED_CONTINUE) == 0) {
            CommonEIGRules.assignMarkToNodeAndNeighbor(eig, nodeFrom, nodeTo, rule,EIGStatusMarks.NEXT_VISIT);

        } else if (rule.compareTo(EIGStatusMarks.VISITED) == 0) {

            CommonEIGRules.applyRuleToNode(eig, nodeFrom,rule,0);
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
