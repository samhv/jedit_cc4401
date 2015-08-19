package org.incha.ui;

public interface TaskProgressMonitor {
    /**
     * @param name task name.
     */
    void setTaskName(String name);
    /**
     * @return task name.
     */
    String getTaskName();
    /**
     * @param taskName task name.
     * @param max max value.
     */
    void beginTask(String taskName, int max);
    /**
     * @return maximum value.
     */
    int getMaximum();
    /**
     * @return progress value.
     */
    int getProgress();
    /**
     * Notify task finished.
     */
    void done();
    /**
     * @return check task canceled.
     */
    boolean isCanceled();
    /**
     * @param value sets the task finished.
     */
    void setCanceled(boolean value);
    /**
     * @param value new current value.
     */
    void worked(int value);
    /**
     * @param max
     */
    void setMaximum(int max);
}
