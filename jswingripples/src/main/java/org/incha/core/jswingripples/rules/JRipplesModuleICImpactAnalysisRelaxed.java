/*
 * Created on Dec 4, 2005
 *
 */
package org.incha.core.jswingripples.rules;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.incha.core.jswingripples.eig.JSwingRipplesEIG;
import org.incha.ui.jripples.EIGStatusMarks;
/**
 * @author Maksym Petrenko
 *
 */
public class JRipplesModuleICImpactAnalysisRelaxed extends JRipplesModuleICImpactAnalysis {
	/**
     * @param eig
     */
    public JRipplesModuleICImpactAnalysisRelaxed(final JSwingRipplesEIG eig) {
        super(eig);
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see org.severe.jripples.modules.interfaces.JRipplesICModuleInterface#GetAvailableRulesForMark(java.lang.String)
	 */
	@Override
    public Set<String> GetAvailableRulesForMark(final String mark) {

		if (mark == null) {
			final String marks[] = { EIGStatusMarks.IMPACTED, EIGStatusMarks.VISITED_CONTINUE, EIGStatusMarks.VISITED };
			return (new LinkedHashSet<String>(Arrays.asList(marks)));
		} else if (mark.compareTo(EIGStatusMarks.BLANK) == 0) {
			final String marks[] = { EIGStatusMarks.IMPACTED, EIGStatusMarks.VISITED_CONTINUE, EIGStatusMarks.VISITED };
			return (new LinkedHashSet<String>(Arrays.asList(marks)));
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
}
