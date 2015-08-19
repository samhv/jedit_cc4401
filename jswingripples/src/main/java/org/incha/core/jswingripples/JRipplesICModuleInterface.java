/*
 * Created on Oct 20, 2005
 *
 */
package org.incha.core.jswingripples;

import java.awt.Color;
import java.awt.Image;
import java.util.Set;

import org.incha.core.jswingripples.eig.JSwingRipplesEIGNode;

/**
 * Interface of JRipples modules that provide and execute Incremental Change (IC) propagation rules for JRipplesEIG nodes.
 * @author Maksym Petrenko
 * @see JRipplesEIG
 * @see JRipplesEIGNode
 *
 */
public interface JRipplesICModuleInterface extends JRipplesModuleInterface {
	/**
	 * Called to initialize Incremental Change stage that this module supports. Typically is called either on EIG initialization or upon switching to the next stage of Incremental Change.
	 */
	public void InitializeStage();
	/**
	 * Returns a set of marks (names of propagation rules), available for a node with the supplied current mark. This is called to determine which propagation rules can still be applied to a particular node and display this rules in GUI.
	 * @param mark
	 *  current mark of a node
	 * @return
	 * 	a set of marks (of type String)
	 */
	public Set<String> GetAvailableRulesForMark(String mark);
	/**
	 * Applies the selected propagation rule at the selected node.
	 * @param rule
	 *  rule to apply
	 * @param node
	 *  node to apply the rule at
	 * @param granularity
	 * granularity at which the rule is applied
	 */
	public void ApplyRuleAtNode(String rule, JSwingRipplesEIGNode node, int granularity);
	/**
	 * Applies the selected propagation rule at the selected node using the particular dependency instead of the whole dependency graph.
	 * @param rule
	 * rule to apply
	 * @param nodeFrom
	 * node to apply the rule at
	 * @param nodeTo
	 * node, to which the rule propagates

	 */
	public void ApplyRuleAtNode(String rule, JSwingRipplesEIGNode nodeFrom, JSwingRipplesEIGNode nodeTo);


	/**
	 * Returns a set of all marks (names of propagation rules), used by an Incremental Change stage that is supported in this module.
	 * @return
	 * a set of marks (of type String)
	 */
	public Set<String> getAllMarks();
	/**
	 * Returns image descriptor that is used to display the supplied mark in a GUI.
	 * @param mark
	 *  mark, for which image descriptor is needed
	 * @return
	 *  image descriptor if any, <br><code>null</code> otherwise
	 */
	public Image getImageDescriptorForMark(String mark);
	/**
	 * Returns color that is used to decorate rows of the tables in a GUI.
	 * @param mark
	 *  mark, for which color is needed
	 * @return
	 *  Color if any, <br><code>null</code> otherwise
	 */
	public Color getColorForMark(String mark) ;
}
