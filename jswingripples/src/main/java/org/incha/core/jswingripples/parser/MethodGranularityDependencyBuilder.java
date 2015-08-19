package org.incha.core.jswingripples.parser;
/*
 * Created on Dec 5, 2005
 *
 */
import java.awt.Window;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.core.IMember;
import org.incha.core.jswingripples.JRipplesDependencyGraphModuleInterface;
import org.incha.core.jswingripples.eig.JSwingRipplesEIG;
import org.incha.core.jswingripples.eig.JSwingRipplesEIGNode;
import org.incha.ui.JSwingRipplesApplication;
import org.incha.ui.TaskProgressMonitor;
import org.incha.ui.util.ModalContext;
import org.incha.ui.util.NullMonitor;
import org.incha.ui.util.RunnableWithProgress;
/**
 * @author Maksym Petrenko
 *
 */
public class MethodGranularityDependencyBuilder implements  JRipplesDependencyGraphModuleInterface{
    private static final Log log = LogFactory.getLog(MethodGranularityDependencyBuilder.class);
    private final JSwingRipplesEIG eig;

    /**
     * @param eig the eight.
     */
    public MethodGranularityDependencyBuilder(final JSwingRipplesEIG eig) {
        super();
        this.eig = eig;
    }

	private class AnalysisJob implements RunnableWithProgress {

		public boolean canceled=false;

		public AnalysisJob(final Set<IMember> EIGNodes) {
			if (EIGNodes==null) return;
			if (EIGNodes.size()==0) return;

			for (final Iterator<IMember> iter=EIGNodes.iterator();iter.hasNext();) {
				final JSwingRipplesEIGNode EIGnode=(JSwingRipplesEIGNode) iter.next();
				if (EIGnode.getNodeIMember()!=null) {
					final JSwingRipplesEIGNode[] refferedNodes=eig.getOutgoingAnyNodeNeighbors(EIGnode);
					if (refferedNodes!=null)
					for (int i=0;i<refferedNodes.length;i++) {
						eig.removeEdge(eig.getEdge(EIGnode, refferedNodes[i]));
					}
				 }
			}
		}

		/* (non-Javadoc)
		 * @see org.incha.ui.core.RunnableWithProgress#run(org.incha.ui.core.TaskProgressMonitor)
		 */
		@Override
		public void run(final TaskProgressMonitor monitor) {
            try {
                monitor.beginTask("Building call graph",10);
                final Thread thread = createAnalizer(monitor);
                thread.start();

                while (thread.isAlive())  {
                    if (monitor.isCanceled()) {
                        thread.interrupt();
                        monitor.setTaskName("Canceled");
                        canceled=true;
                        monitor.done();
                        return;
                    }
                    Thread.yield();; //XXX Make this more general
                }
            } finally {
                monitor.done();
            }
		}
	}

	/* (non-Javadoc)
	 * @see org.severe.jripples.defaultmodules.parsers.interfaces.JRipplesDependencyGraphModuleInterface#AnalyzeProject()
	 */
	@Override
    public void AnalyzeProject() {

	//	if (!JRipplesEIG.isLocked())
		runn(null);
	}

    /**
     * @param nodes
     * @param monitor
     * @return
     */
    protected Analyzer createAnalizer(final TaskProgressMonitor monitor) {
        return new Analyzer(eig, monitor);
    }

	/* (non-Javadoc)
	 * @see org.severe.jripples.defaultmodules.parsers.interfaces.JRipplesDependencyGraphModuleInterface#ReAnalyzeProjectAtNodes(java.util.Set)
	 */
	@Override
    public void ReAnalyzeProjectAtNodes(final Set<JSwingRipplesEIGNode> changed_nodes) {
	}
	/* (non-Javadoc)
	 * @see org.severe.jripples.defaultmodules.parsers.interfaces.JRipplesModuleInterface#shutDown(int controllerType)
	 */
	@Override
    public void shutDown(final int controllerType) {
	}

//	----------------------------------------------------------------------
	private void runn(final Set<IMember> changed_nodes){
		AnalysisJob job=null;
		final Window window = JSwingRipplesApplication.getInstance();
        if (window != null) {

     		try {
     			job=new AnalysisJob(changed_nodes);
     			final JSwingRipplesApplication app = JSwingRipplesApplication.getInstance();
     			try {
     			    ModalContext.run(job, false, app.getProgressMonitor());
     			} finally {
     			    app.getProgressMonitor().done();
     			}
     		}
     		catch (final Exception e) {
     			log.error(e);
     		};


        } else {
        	final NullMonitor monitor=new NullMonitor();
        	job=new AnalysisJob(changed_nodes);
        	try {
        		job.run(monitor);
        	} catch (final Exception e) {
     			log.error(e);
     		};
        }

        if (job==null) return;
 		if (job.canceled) {
 		    JOptionPane.showMessageDialog(JSwingRipplesApplication.getInstance(),
 		           "Parser analysis was canceled.", "Parsing canceled",
 		            JOptionPane.ERROR_MESSAGE);
 		    return ;
 		}
	}
	/* (non-Javadoc)
	 * @see org.incha.core.jswingripples.JRipplesModuleInterface#initializeStage()
	 */
	@Override
	public void runInAnalize() {
	    AnalyzeProject();
	}
}
