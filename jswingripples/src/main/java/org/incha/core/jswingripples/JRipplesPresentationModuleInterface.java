/*
 * Created on Oct 20, 2005
 *
 */
package org.incha.core.jswingripples;

import java.util.Set;

import org.incha.core.jswingripples.eig.JSwingRipplesEIGNode;

/**
 * Interface of JRipples modules that provide a GUI for the JRipples EIG.
 * @author Maksym Petrenko
 * @see JRipplesEIG
 *
 */
public interface JRipplesPresentationModuleInterface extends
		JRipplesModuleInterface {
	/**
	 * Called to display the whole JRipple EIG in the GUI of this module. Typically is called upon EIG initialization.
	 * @see JRipplesEIG
	 */
	public void DisplayEIG();
	/**
	 * Called to display a set of JRipple EIG nodes in the GUI of this module. Typically is called to reflect changes in particular nodes.
	 * @param changed_nodes
	 * set of {@link JRipplesEIGNode} nodes to be displayed in the GUI
	 */
	public void RefreshEIGAtNodes(Set<JSwingRipplesEIGNode> changed_nodes);
}
