package org.incha.ui.buildpath;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.table.TableCellRenderer;

import org.incha.core.BuildPath;
import org.incha.core.JavaProject;

public class ClassPathEditor extends AbstractBuildPathEditor {
    private static final long serialVersionUID = 1L;

    /**
     * @param project the java project.
     */
    public ClassPathEditor(final JavaProject project) {
        super(project);
    }

    /* (non-Javadoc)
     * @see org.incha.ui.AbstractBuildPathEditor#fireFilesChanged(java.lang.String, java.util.List, java.util.List)
     */
    @Override
    protected void fireFilesChanged(final String propertyName,
            final List<File> originOldFiles, final List<File> originNewFiles) {
        if (!BuildPath.CLASSPATH.equals(propertyName)) {
            return;
        }
        super.fireFilesChanged(propertyName, originOldFiles, originNewFiles);
    }
    /**
     * @return the cell renderer.
     */
    @Override
    protected TableCellRenderer createCellRenderer() {
        return new ClassPathEntryRenderer();
    }
    /**
     * Synchronizes the view with project.
     */
    @Override
    protected void synchronizeBuildPath() {
        //synchronize source files.
        for (final File f : project.getBuildPath().getClassPath()) {
            handleFileAdded(f);
        }
    }
    /**
     * @param file the file to delete from project.
     */
    @Override
    protected void deleteFileFromProject(final File file) {
        project.getBuildPath().deleteClassPath(file);
    }
    /**
     * @param f the file to add to project.
     */
    @Override
    protected void addFileToProject(final File f) {
        project.getBuildPath().addClassPath(f);
    }
    /**
     * @return the file chooser.
     */
    @Override
    protected JFileChooser createFileChooser() {
        final JFileChooser chooser = super.createFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        return chooser;
    }
}
