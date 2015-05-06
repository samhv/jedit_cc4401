package org.gjt.sp.jedit.github;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Ventana extends JFrame implements ActionListener {

	private Container contenedor;
	

	public Ventana() {
		/** permite iniciar las propiedades de los componentes */
		iniciarComponentes();
		/** Asigna un titulo a la barra de titulo */
		setTitle("GitHub");
		/** tamaño de la ventana */
		setSize(300, 180);
		
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
	
		
		/** Agregamos los componentes al Contenedor */
	
		JTabbedPane panel_tab = new JTabbedPane();
        contenedor.add(panel_tab);
        JPanel panel_login = new JPanel();
        JPanel panel_commit = new JPanel();
        JPanel panel_push = new JPanel();
        JPanel panel_issues = new JPanel();

        /** Labels para cada panel */
        
        JLabel label_panel_login = new JLabel();
        label_panel_login.setText("Panel para realizar el login");
        JLabel label_panel_commit = new JLabel();
        label_panel_commit.setText("Panel para realizar commit");
        JLabel label_panel_push = new JLabel();
        label_panel_push.setText("Panel para realizar los push");
        JLabel label_panel_issues = new JLabel();
        label_panel_issues.setText("Panel para realizar los issues");
        
        panel_login.add(label_panel_login);
        panel_commit.add(label_panel_commit);
        panel_push.add(label_panel_push);
        panel_issues.add(label_panel_issues);
      
        panel_tab.addTab("Login", panel_login);
        panel_tab.addTab("Commit", panel_commit);
        panel_tab.addTab("Push", panel_push);
        panel_tab.addTab("Issues", panel_issues);
        
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
