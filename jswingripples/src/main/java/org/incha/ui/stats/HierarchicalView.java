package org.incha.ui.stats;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.incha.core.JavaProject;
import org.incha.core.jswingripples.eig.JSwingRipplesEIGNode;
import org.incha.ui.classview.ClassTreeView;

public class HierarchicalView extends ClassTreeView {
    private static final long serialVersionUID = -725916023414871313L;

    /**
     * Default constructor.
     */
    public HierarchicalView(final JavaProject project, final List<JSwingRipplesEIGNode> nodes) {
        super(project);
        setData(nodes);
    	
        addMouseListener(new MouseAdapter() {

            /* (non-Javadoc)
             * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e.getX(), e.getY());
                } else if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                	final ICompilationUnit unit = getSelectedItem(e.getX(), e.getY()).getNodeIMember().getCompilationUnit();
                	final File fileToOpen = new File(unit.getPath().toString());
                	try {
						Desktop.getDesktop().open(fileToOpen);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                }
                
            }
            
        });
    }

    /**
     * @param x x mouse coordinate.
     * @param y y mouse coordinate.
     */
    protected void showPopupMenu(final int x, final int y) {
        final JSwingRipplesEIGNode node = getSelectedItem(x, y);
        if (node != null) {
            ICActionsManager.getInstance().showMenuForNode(node, x, y, this);
        }
    }
}
