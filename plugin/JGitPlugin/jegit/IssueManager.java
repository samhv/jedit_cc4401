package jegit;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import core.GitHubConfig;
import core.PropertyManager;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;

public class IssueManager extends JPanel {
	private GitHubConfig _config;

	private JPanel contentPane;
	private JTable table;
	private JButton btnAddIssue;

	
	
	private View view;
	private boolean floating;
	 //
	 // Constructor
	 //
	 public IssueManager(View view, String position)
	 {
		super(new BorderLayout());
		this.view = view;
		this.floating = position.equals(
		DockableWindowManager.FLOATING);		 
		 
		setBounds(100, 100, 450, 300);
		setLayout(null);

		table = new JTable();
		table.setBounds(20, 50, 450, 450);
		table.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "Title" }));
		add(table);

			btnAddIssue = new JButton();
			btnAddIssue.setBounds(20, 10, 100, 30);
			btnAddIssue.setText("Add Issue");
			btnAddIssue.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO: Mostrar ventana
					openModalDialogBtnActionPerformed(arg0);
				}
			});
		add(btnAddIssue);

		InitView();
	 }

	private void openModalDialogBtnActionPerformed(ActionEvent e) {
		Window topWindow = SwingUtilities.getWindowAncestor(this);
		JDialog modalDialog = new JDialog(topWindow, "Modal Dialog",
				ModalityType.APPLICATION_MODAL);
		modalDialog.pack();
		modalDialog.setLocationRelativeTo(topWindow);
		modalDialog.getContentPane().setLayout(null);

		JLabel labelIssueTitle = new JLabel();
		labelIssueTitle.setBounds(20, 20, 100, 20);
		labelIssueTitle.setText("Title");
		labelIssueTitle.setHorizontalAlignment(SwingConstants.RIGHT);
		modalDialog.getContentPane().add(labelIssueTitle);

		JLabel labelIssueDesc = new JLabel();
		labelIssueDesc.setBounds(20, 50, 100, 20);
		labelIssueDesc.setHorizontalAlignment(SwingConstants.RIGHT);
		labelIssueDesc.setText("Description");
		modalDialog.getContentPane().add(labelIssueDesc);

		final JTextField textTitle = new JTextField();
		textTitle.setBounds(130, 20, 200, 20);
		modalDialog.getContentPane().add(textTitle);

		final JTextArea textDesc = new JTextArea();
		textDesc.setBounds(130, 50, 200, 100);
		modalDialog.getContentPane().add(textDesc);

		JButton btnCreateIssue = new JButton();
		btnCreateIssue.setBounds(130, 160, 80, 30);
		btnCreateIssue.setText("Submit");
		modalDialog.getContentPane().add(btnCreateIssue);

		btnCreateIssue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Issue issue = new Issue();
				issue.setTitle(textTitle.getText());
				issue.setBody(textDesc.getText());
				if(!AddIssue(issue)){
					JOptionPane.showMessageDialog(null, "Error creating the issue");
				}
				else
					FillIssues();
				modalDialog.dispose();
			}
		});

		modalDialog.setSize(400, 250);
		modalDialog.setVisible(true);

	}

	private void InitView() {
		/*
		 * GitHubConfigService service = new GitHubConfigService(); _config =
		 * service.GetConfig(); if (_config == null) { // show Credential Window
		 * _config = ShowCredentialWindows(); }
		 */
		
		_config = getGitHubConfig();
		FillIssues();
	}
	private GitHubConfig getGitHubConfig(){
		String repoUrl =PropertyManager.GetProperty("repoUrl");
		String user = PropertyManager.GetProperty("user");
		String password = PropertyManager.GetProperty("password");
		return new GitHubConfig(repoUrl, user, password);
	}


	private void FillIssues() {
		List<Issue> issues = GetIssueForRepo();
		if (issues == null)
			return;

		DefaultTableModel dtm = new DefaultTableModel(0, 0);
		// add header of the table
		String header[] = new String[] { "Title" };
		dtm.setColumnIdentifiers(header);
		table.setModel(dtm);
		for (Issue issue : issues) {
			dtm.addRow(new Object[] { issue.getTitle() });
		}
	}

	private List<Issue> GetIssueForRepo() {
		try {
			GitHubClient client = createClient("https://api.github.com",
					_config.getGitHubUser(), _config.getGitHubPass());

			IssueService issueService = new IssueService(client);
			return issueService.getIssues(_config.GetOwner(),
					_config.getRepoName(), null);

		} catch (Exception exp) {
			return null;
		}
	}

	private Boolean AddIssue(Issue issue) {
		try {
			GitHubClient client = createClient("https://api.github.com",
					PropertyManager.GetProperty("user"),
					PropertyManager.GetProperty("password"));
			IssueService issueService = new IssueService(client);
			issueService.createIssue(_config.GetOwner(), _config.getRepoName(),
					issue);
			return true;
		} catch (Exception exp) {
			return false;
		}
	}

	private static GitHubClient createClient(String url, String user,
			String password) throws IOException {
		GitHubClient client = null;
		if (url != null) {
			URL parsed = new URL(url);
			client = new GitHubClient(parsed.getHost(), parsed.getPort(),
					parsed.getProtocol());
		} else
			client = new GitHubClient();
		client.setCredentials(user, password);
		return client;
	}

}

