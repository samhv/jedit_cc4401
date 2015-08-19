package org.incha.core.jswingripples.parser;

import org.incha.core.jswingripples.eig.JSwingRipplesEIG;
import org.incha.ui.TaskProgressMonitor;

/*
 * Created on Dec 5, 2005
 *
 */
/**
 * @author Maksym Petrenko
 *
 */
public class MethodGranularityDependencyBuilderPolymorphic extends   MethodGranularityDependencyBuilder{

	private boolean accountForPolymorphismMode;

    public MethodGranularityDependencyBuilderPolymorphic (final JSwingRipplesEIG eig) {
		super(eig);
		setAccountForPolymorphismMode(true);
	}
    /**
     * @param b
     */
    private void setAccountForPolymorphismMode(final boolean b) {
        this.accountForPolymorphismMode = b;
    }
    /* (non-Javadoc)
     * @see org.incha.core.jswingripples.parser.MethodGranularityDependencyBuilder#createAnalizer(java.util.HashSet, org.incha.ui.core.TaskProgressMonitor)
     */
    @Override
    protected Analyzer createAnalizer(final TaskProgressMonitor monitor) {
        final Analyzer analizer = super.createAnalizer(monitor);
        analizer.setAccountForPolymorphismMode(accountForPolymorphismMode);
        return analizer;
    }
}
