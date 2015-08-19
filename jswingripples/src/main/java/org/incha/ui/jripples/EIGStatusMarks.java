package org.incha.ui.jripples;

import java.awt.Color;
import java.awt.Image;

public class EIGStatusMarks {
	public static final String BLANK = "";
	public static final String VISITED = "Unchanged";
	public static final String VISITED_CONTINUE = "Propagating";
	public static final String CHANGED = "Changed";
	public static final String NEXT_VISIT = "Next";
	public static final String IMPACTED = "Impacted"; // to continue from from
	public static final String LOCATED = "Located";

	public  static Image getImageDescriptorForMark(final String mark) {

		if (mark==null)
			return JRipplesResources.getImage("icons/Class.gif");

		if (mark.equals(EIGStatusMarks.BLANK)) {
			return JRipplesResources.getImage("icons/icicons/blank1.gif");
		} else if (mark.equals(EIGStatusMarks.NEXT_VISIT)) {
			return JRipplesResources.getImage("icons/icicons/nextvisit1.gif");

		} else if (mark.equals(EIGStatusMarks.VISITED)) {
			return JRipplesResources.getImage("icons/icicons/visited1.gif");

		} else if (mark.equals(EIGStatusMarks.VISITED_CONTINUE)) {
			return JRipplesResources.getImage("icons/icicons/through1.gif");

		} else if ((mark.equals(EIGStatusMarks.LOCATED))
		        || (mark.equals(EIGStatusMarks.IMPACTED))
		        || (mark.equals(EIGStatusMarks.CHANGED))) {
			return JRipplesResources.getImage("icons/icicons/changed1.gif");

		}
		return null;
	}


	public static Color getColorForMark(final String mark) {
		if (mark==null)
			return null;

		if (mark.equals(EIGStatusMarks.BLANK)) {
			return null;
		} else if (mark.equals(EIGStatusMarks.NEXT_VISIT)) {
			return  new Color(0,170,85);

		} else if (mark.equals(EIGStatusMarks.VISITED)) {
			return  new Color(192,192,192);

		} else if (mark.equals(EIGStatusMarks.VISITED_CONTINUE)) {
			return  new Color(255,140,0);

		} else if ((mark.equals(EIGStatusMarks.LOCATED))
		        || (mark.equals(EIGStatusMarks.IMPACTED))
		        || (mark.equals(EIGStatusMarks.CHANGED))) {
			return  new Color(255,128,128);
		}
		return null;
	}
}
