import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.DockableWindowManager;



public class JRipples extends JPanel implements EBComponent
{
    private View view;
    private boolean floating;

    //
    // Constructor
    //

    public JRipples(View view, String position)
    {
        super(new BorderLayout());

        this.view = view;
        this.floating = position.equals(
            DockableWindowManager.FLOATING);

    }

    //
    // EBComponent implementation
    //

    public void handleMessage(EBMessage message)
    {
        // TODO
    }


    private void propertiesChanged()
    {
        String propertyFilename = jEdit.getProperty(
            JRipplesPlugin.OPTION_PREFIX + "filepath");
    }

}

