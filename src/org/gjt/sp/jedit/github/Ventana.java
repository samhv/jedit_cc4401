package org.gjt.sp.jedit.github;


import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;


public class Ventana extends JFrame {

	private Container contenedor;
	private JPanel panel_login;
	private JPanel panel_commit;
	private JPanel panel_push;
	private JPanel panel_issues;
	private JPanel panel_clonar;

	public Ventana() {
		/** permite iniciar las propiedades de los componentes */
		iniciarComponentes();
		/** Asigna un titulo a la barra de titulo */
		setTitle("GitHub");
		/** tamaño de la ventana */
		setSize(400, 180);

		/** pone la ventana en el Centro de la pantalla */
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void iniciarComponentes() {
		contenedor = getContentPane();
		contenedor.setLayout(new GridLayout(1, 1));
		/** instanciamos el contenedor */
		/**
		 * /** Agregamos los componentes al Contenedor
		 */

		JTabbedPane panel_tab = new JTabbedPane();
		contenedor.add(panel_tab);
		// JPanel panel_login = new JPanel();
		// JPanel panel_commit = new JPanel();
		// JPanel panel_push = new JPanel();
		// JPanel panel_issues = new JPanel();

		createLoginPanel();
		createCommitPanel();
		createPushPanel();
		createClonarPanel();
		panel_issues = new JPanel();
		panel_tab.addTab("Login", panel_login);
		panel_tab.addTab("Clone", panel_clonar);
		panel_tab.addTab("Commit", panel_commit);
		panel_tab.addTab("Push", panel_push);
		panel_tab.addTab("Issues", panel_issues);

	}

	public void createLoginPanel() {

		// Create and populate the panel.
		String[] labels = { "Name: ", "Password: " };
		int numPairs = labels.length;

		panel_login = new JPanel(new SpringLayout());
		for (int i = 0; i < numPairs; i++) {
			JLabel l = new JLabel(labels[i], JLabel.TRAILING);
			panel_login.add(l);
			JTextField textField = new JTextField(10);
			l.setLabelFor(textField);
			panel_login.add(textField);
		}
		JButton boton_conectar = new JButton("Connect");

		JButton boton_cancelar = new JButton("Cancel");
		boton_cancelar.addActionListener(new ListenerClose(this));
		panel_login.add(boton_cancelar);
		panel_login.add(boton_conectar);
		// Lay out the panel.
		SpringUtilities.makeCompactGrid(panel_login, numPairs + 1, 2, // rows,
																		// cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

	}
	public void createCommitPanel() {
		// Create and populate the panel.
				String[] labels = { "Comment: " };
				int numPairs = labels.length;

				panel_commit = new JPanel(new SpringLayout());
				for (int i = 0; i < numPairs; i++) {
					JLabel l = new JLabel(labels[i], JLabel.TRAILING);
					panel_commit.add(l);
					JTextField textField = new JTextField(10);
					l.setLabelFor(textField);
					panel_commit.add(textField);
				}
				JButton boton_commit = new JButton("Commit");

				JButton boton_cancelar = new JButton("Cancel");
				boton_cancelar.addActionListener(new ListenerClose(this));
				panel_commit.add(boton_cancelar);
				panel_commit.add(boton_commit);
				// Lay out the panel.
				SpringUtilities.makeCompactGrid(panel_commit, numPairs + 1, 2, // rows,
																				// cols
						6, 6, // initX, initY
						6, 6); // xPad, yPad
	}
	
	public void createPushPanel(){
		panel_push = new JPanel();
		JButton boton_push = new JButton("Push");
		panel_push.add(boton_push);
		
//		// Lay out the panel.
//		SpringUtilities.makeCompactGrid(panel_push,
//				1, 1, // rows,// cols
//				6, 6, // initX, initY
//				6, 6); // xPad, yPad
	}
	public void createClonarPanel(){
		panel_clonar = new JPanel();
		// Create and populate the panel.
				String[] labels = { "Url: ", "Local path: " };
				int numPairs = labels.length;

				panel_clonar = new JPanel(new SpringLayout());
				for (int i = 0; i < numPairs; i++) {
					JLabel l = new JLabel(labels[i], JLabel.TRAILING);
					panel_clonar.add(l);
					JTextField textField = new JTextField(10);
					l.setLabelFor(textField);
					panel_clonar.add(textField);
				}
				JButton boton_conectar = new JButton("Clone");

				JButton boton_cancelar = new JButton("Cancel");
				boton_cancelar.addActionListener(new ListenerClose(this));
				panel_clonar.add(boton_cancelar);
				panel_clonar.add(boton_conectar);
				// Lay out the panel.
				SpringUtilities.makeCompactGrid(panel_clonar, numPairs + 1, 2, // rows,
																				// cols
						6, 6, // initX, initY
						6, 6); // xPad, yPad

		
	}
	
	
	
	public class ListenerClose implements ActionListener{
		private JFrame frame;
		
		public ListenerClose(JFrame frame){
			this.frame = frame;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			frame.dispose();
			
		}
		
	}
	

}
