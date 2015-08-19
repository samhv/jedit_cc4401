package org.incha.ui.stats;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.incha.core.JavaProject;
import org.incha.core.JavaProjectsModel;
import org.incha.core.ModuleConfiguration;
import org.incha.core.Statistics;
import org.incha.ui.jripples.JRipplesDefaultModulesConstants;

public class StartAnalysisDialog extends JDialog {
    private static final long serialVersionUID = 6788138046337076311L;
    final JComboBox<String> projects;
    final JTextField className = new JTextField(30);
    private boolean isOk;
    final JComboBox<String> incrementalChange = new JComboBox<String>(new DefaultComboBoxModel<String>(
        new String[]{
            JRipplesDefaultModulesConstants.MODULE_IMPACT_ANALYSIS_TITLE,
            JRipplesDefaultModulesConstants.MODULE_IMPACT_ANALYSIS_RELAXED_TITLE,
            JRipplesDefaultModulesConstants.MODULE_CHANGE_PROPAGATION_RELAXED_TITLE,
            JRipplesDefaultModulesConstants.MODULE_CHANGE_PROPAGATION_TITLE,
            JRipplesDefaultModulesConstants.MODULE_CONCEPT_LOCATION_TITLE,
            JRipplesDefaultModulesConstants.MODULE_CONCEPT_LOCATION_RELAXED_TITLE
        }
    ));
//    JComboBox<String> presentation = new JComboBox<String>(new DefaultComboBoxModel<String>(
//        new String[]{
//            JRipplesDefaultModulesConstants.MODULE_VIEW_HIERARCHY_TITLE,
//            JRipplesDefaultModulesConstants.MODULE_VIEW_TREE_TITLE
//        }
//    ));
    JComboBox<String> analysis = new JComboBox<String>(new DefaultComboBoxModel<String>(
        new String[]{
            JRipplesDefaultModulesConstants.MODULE_IMPACT_ANALYSIS_TITLE
        }
    ));
    JComboBox<String> dependencyGraph = new JComboBox<String>(new DefaultComboBoxModel<String>(
        new String[]{
            JRipplesDefaultModulesConstants.MODULE_DEPENDENCY_BUILDER,
            JRipplesDefaultModulesConstants.MODULE_DEPENDENCY_BUILDER_WITH_POLYMORPHIC
        }
    ));

    /**
     * Default constructor.
     */
    public StartAnalysisDialog(final Window owner) {
        super(owner);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 5));

        //create projects combobox.
        final List<JavaProject> prg = JavaProjectsModel.getInstance().getProjects();
        final String[] prgArray = new String[prg.size()];
        for (int i = 0; i < prgArray.length; i++) {
            prgArray[i] = prg.get(i).getName();
        }
        projects = new JComboBox<String>(new DefaultComboBoxModel<String>(prgArray));
        projects.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                projectChanged();
            }
        });

        final JPanel center = new JPanel(new FlowLayout(FlowLayout.LEADING));
        final JPanel projectAndType = createCenterPanel();
        center.add(projectAndType);

        getContentPane().add(center, BorderLayout.CENTER);

        //south pane
        final JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        final JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                doOk();
            }
        });
        south.add(ok);

        final JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                doCancel();
            }
        });
        south.add(cancel);
        getContentPane().add(south, BorderLayout.SOUTH);

        //set up default values
        projectChanged();
    }

    /**
     *
     */
    protected void projectChanged() {
        final String projectName = (String) projects.getSelectedItem();
        final JavaProject project = JavaProjectsModel.getInstance().getProject(projectName);

        if (project != null) {
            //set current module configuration

            //dependency graph module
            final ModuleConfiguration cfg = project.getModuleConfiguration();
            final Statistics stats = project.getCurrentStatistics();
            className.setText(stats != null ? stats.getEIG().getMainClass() : null);

            switch (cfg.getDependencyGraphModule()) {
                case ModuleConfiguration.MODULE_DEPENDENCY_BUILDER:
                    dependencyGraph.setSelectedItem(JRipplesDefaultModulesConstants.MODULE_DEPENDENCY_BUILDER);
                break;
                default://MODULE_DEPENDENCY_BUILDER_WITH_POLYMORPHIC
                    dependencyGraph.setSelectedItem(
                            JRipplesDefaultModulesConstants.MODULE_DEPENDENCY_BUILDER_WITH_POLYMORPHIC);
            }

            //Incremental change
            switch (cfg.getIncrementalChange()) {
                case ModuleConfiguration.MODULE_IMPACT_ANALYSIS:
                    incrementalChange.setSelectedItem(
                            JRipplesDefaultModulesConstants.MODULE_IMPACT_ANALYSIS_TITLE);
                break;
                case ModuleConfiguration.MODULE_IMPACT_ANALYSIS_RELAXED:
                    incrementalChange.setSelectedItem(
                            JRipplesDefaultModulesConstants.MODULE_IMPACT_ANALYSIS_RELAXED_TITLE);
                break;
                case ModuleConfiguration.MODULE_CHANGE_PROPAGATION_RELAXED:
                    incrementalChange.setSelectedItem(
                            JRipplesDefaultModulesConstants.MODULE_CHANGE_PROPAGATION_RELAXED_TITLE);
                break;
                case ModuleConfiguration.MODULE_CHANGE_PROPAGATION:
                    incrementalChange.setSelectedItem(
                            JRipplesDefaultModulesConstants.MODULE_CHANGE_PROPAGATION_TITLE);
                break;
                case ModuleConfiguration.MODULE_CONCEPT_LOCATION:
                    incrementalChange.setSelectedItem(
                            JRipplesDefaultModulesConstants.MODULE_CONCEPT_LOCATION_TITLE);
                break;
                case ModuleConfiguration.MODULE_CONCEPT_LOCATION_RELAXED:
                    incrementalChange.setSelectedItem(
                            JRipplesDefaultModulesConstants.MODULE_CONCEPT_LOCATION_RELAXED_TITLE);
                break;
            }

            switch(cfg.getAnalysis()) {
                case ModuleConfiguration.MODULE_IMPACT_ANALYSIS:
                    analysis.setSelectedItem(
                            JRipplesDefaultModulesConstants.MODULE_IMPACT_ANALYSIS_TITLE);
                    break;
            }
        }
    }

    /**
     * @return
     */
    private JPanel createCenterPanel() {
        final JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED),
                new EmptyBorder(3, 3, 3, 3)));

        panel.add(new JLabel("Java project:"));
        projects.setEditable(false);
        panel.add(projects);

        //create class name
        panel.add(new JLabel("Class name:"));
        panel.add(className);

        //Incremental change combobox
        panel.add(new JLabel("Incremental Change"));
        incrementalChange.setEditable(false);
        panel.add(incrementalChange);
//
//        panel.add(new JLabel("Presentation"));
//        panel.add(presentation);

        panel.add(new JLabel("Analysis"));
        analysis.setEditable(false);;
        panel.add(analysis);

        panel.add(new JLabel("Dependency Graph"));
        dependencyGraph.setEditable(false);
        panel.add(dependencyGraph);
        return panel;
    }

    /**
     *
     */
    protected void doCancel() {
        isOk = false;
        dispose();
    }
    /**
     *
     */
    protected void doOk() {
        isOk = true;
        dispose();
    }

    /**
     * @return the isOk
     */
    public boolean isOk() {
        return isOk;
    }
    /**
     * @return the className
     */
    public String getMainClass() {
        return className.getText();
    }
}
