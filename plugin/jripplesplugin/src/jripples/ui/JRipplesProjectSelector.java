package jripples.ui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import java.nio.file.Paths;

/**
 * Esta clase contiene la funcionalidad de seleccionar una carpeta.
 * @author rcc
 *
 */
public class JRipplesProjectSelector extends JPanel {
	private View view;
	private boolean floating;
	// components
	JTextField textFolder;
	JTextField textDir;
	JTextField textName;

	//
	// Constructor
	//

	public JRipplesProjectSelector(View view, String position) {
		super(new BorderLayout());
 
		this.view = view;
		this.floating = position.equals(DockableWindowManager.FLOATING);
		InitView();
	}
	private void InitView() {
		setLayout(null);

		JLabel nuevoProyectoLabel = new JLabel("New Project (Optional)");
		nuevoProyectoLabel.setBounds(0, 0, 300, 20);
		add(nuevoProyectoLabel);

		JLabel nombreLabel = new JLabel("Name:");
		nombreLabel.setBounds(15, 20, 55, 20);
		add(nombreLabel);

		textName = new JTextField();
		textName.setBounds(70,20,300,20);
		add(textName);

		JLabel sourceLabel = new JLabel("Source:");
		sourceLabel.setBounds(15, 45, 55, 20);
		add(sourceLabel);
		
		//JTextField folder path
		textFolder = new JTextField();
		textFolder.setEditable(false);
		textFolder.setBounds(70,45,300,20);
		add(textFolder);

		// button select folder
		JButton btnSelect = new JButton();
		btnSelect.setText("Add sources");
		btnSelect.setBounds(375,45,150,20);
		btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setSources();
			}
		});
		add(btnSelect);

		JButton btnStartAnalysis = new JButton();
		btnStartAnalysis.setText("Start Analysis");
		btnStartAnalysis.setBounds(180, 70, 150, 20);
		btnStartAnalysis.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String jRipplesDir = (String) jEdit.getProperty("JRIPPLES_DIR");
				if (jRipplesDir == null) {
					setJRipplesDir();
				} else {
					if (!(new File(jRipplesDir, "jswingripples.jar").exists())) {
						JOptionPane.showMessageDialog(null, "We couldn't find jswingripples.jar file in " + jRipplesDir + ". Please check that the directory and name of the jar are correct");
					} else {
						try {
							String newProjectName = textName.getText();
							String newProjectSources = textFolder.getText();
							if (newProjectName == null && newProjectSources != null) {
								DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
								Date date = new Date();
								newProjectName = "jEdit" + dateFormat.format(date); 
							}
							if (newProjectName != null && newProjectSources == null) { 
								Runtime.getRuntime().exec("java -jar " + jRipplesDir + File.separator + "jswingripples.jar" + " " + newProjectName.replace(" ", ""));
							} else if (newProjectName != null && newProjectSources != null) {
								Runtime.getRuntime().exec("java -jar " + jRipplesDir + File.separator + "jswingripples.jar" + " " + newProjectName.replace(" ", "") + " " + newProjectSources);
							} else {
								Runtime.getRuntime().exec("java -jar " + jRipplesDir + File.separator + "jswingripples.jar");
							}
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "There was an error trying to run jswingripples.jar. Check your permissions");
						}
					}
				}
			}
		});
		add(btnStartAnalysis);

		JButton btnChangeDir = new JButton();
		btnChangeDir.setText("Change");
		btnChangeDir.setBounds(200, 140, 100, 20);
		btnChangeDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setJRipplesDir();
			}
		});
		add(btnChangeDir);

		textDir = new JTextField();
		textDir.setEditable(false);
		textDir.setBounds(100,110,300,20);
		if (jEdit.getProperty("JRIPPLES_DIR") != null) {
			textDir.setText(jEdit.getProperty("JRIPPLES_DIR"));
		} else {
			textDir.setText("Not Set");
		}
		add(textDir);

		JLabel dirLabel = new JLabel("JSwingRipples Dir:");
		dirLabel.setBounds(200, 90, 300, 20);
		add(dirLabel);

		
		setSize(400, 200);
	}

	/* Events */
	private void setSources() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Select folder");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String sources = chooser.getSelectedFile().getAbsolutePath();
			textFolder.setText(sources);
		} 
	}

	private void setJRipplesDir() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Select folder");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String selectedFolder = chooser.getSelectedFile().getAbsolutePath();
			jEdit.setProperty("JRIPPLES_DIR", selectedFolder);
			textDir.setText(selectedFolder);
		}
	}

}
