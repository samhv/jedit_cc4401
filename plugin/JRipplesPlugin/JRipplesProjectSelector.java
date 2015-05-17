import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.DockableWindowManager;

public class JRipplesProjectSelector extends JPanel {
	private View view;
	private boolean floating;

	//
	// Constructor
	//

	public JRipplesProjectSelector(View view, String position) {
		super(new BorderLayout());

		this.view = view;
		this.floating = position.equals(DockableWindowManager.FLOATING);

		InitView();

	}

	// components
	JButton btnSelect;
	JFileChooser chooser;
	JTextField textFolder;

	private void InitView() {
		setLayout(null);
		
		//JTextField folder path
		textFolder = new JTextField();
		textFolder.setEditable(false);
		textFolder.setBounds(10,10,100,20);
		add(textFolder);

		// button select folder
		btnSelect = new JButton();
		btnSelect.setText("Browse");
		btnSelect.setBounds(120,10,40,20);
		btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnSelect_actionPerformed(arg0);
			}
		});
		add(btnSelect);

		// browse for folder dialog
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Select folder");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		setBounds(100, 100, 450, 300);
		setSize(300, 300);

	}

	/* Events */
	private void btnSelect_actionPerformed(ActionEvent arg0) {
		// show browse for folder dialog
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			textFolder.setText(chooser.getCurrentDirectory().getAbsolutePath());
		} else {

		}
	}

}
