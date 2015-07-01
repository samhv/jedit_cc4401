package org.gjt.sp.jedit.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.bufferset.BufferSet;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.ThreadUtilities;

public class TabbedBufferSwitcher extends JPanel {
	
		private Set<Tab> tabs;
		private final EditPane editPane;
		private JPanel center;
		private Tab selected;
		private boolean updating;
		private int maxOpenFile;
		
		public TabbedBufferSwitcher(final EditPane editPane){
			this.editPane = editPane;
			JPanel up = new JPanel();
			JPanel down = new JPanel();
			center = new JPanel();
			
			up.setBackground(new Color(0xeeeeee));
			down.setBackground(new Color(0xffffff));
			center.setBackground(new Color(0xeeeeee));
			
			center.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 0));
			
			updating = false;
		}
	
	public void updateBufferList() {
		// if the buffer count becomes 0, then it is guaranteed to
				// become 1 very soon, so don't do anything in that case.
				final BufferSet bufferSet = editPane.getBufferSet();
				if(bufferSet.size() == 0)
					return;

				Runnable runnable = new Runnable()
				{
					@Override
					public void run()
					{
						updating = true;
						setMaximumRowCount(jEdit.getIntegerProperty("bufferSwitcher.maxRowCount",10));
						Buffer[] buffers = bufferSet.getAllBuffers();
						if (jEdit.getBooleanProperty("bufferswitcher.sortBuffers", true)) {
							Arrays.sort(buffers, new Comparator<Buffer>(){
									public int compare(Buffer a, Buffer b) {
										if (jEdit.getBooleanProperty("bufferswitcher.sortByName", true)) {
											return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());		
										} else {
											return a.getPath().toLowerCase().compareTo(b.getPath().toLowerCase());	
										}
									}
							});
						}
						setTabs(buffers);
						setSelectedItem(editPane.getBuffer());
						setToolTipText(editPane.getBuffer().getPath(true));
						//addDnD();
						updating = false;
					}
				};
				ThreadUtilities.runInDispatchThread(runnable);
	}
	
	protected void setTabs(Buffer[] buffers) {
		center.removeAll();
		tabs.clear();
		for(int i = 0; i<maxOpenFile; i++){
			Tab t = new Tab(buffers[i],this);
			center.add(t);
			tabs.add(t);
		}
	}

	public void setMaximumRowCount(int integerProperty) {
		maxOpenFile = integerProperty;		
	}

	public void addTab(Object anObject){
				
	}

	public void handlePropertiesChanged(PropertiesChanged msg) {
		updateBufferList();
	}

	public Object getSelectedItem() {
		return selected;
	}


	public void setSelectedItem(Object anObject) {
		for (Tab t : tabs){
			if (t == anObject){
				selected = t;
				break;
			}
		}
	}

	public boolean specialState() {
		return false;
	}
	
	private class Tab extends JPanel{
		
		final TabbedBufferSwitcher tp;
		private Buffer buffer;
		
		Tab(Buffer anObject, final TabbedBufferSwitcher switcher){
			tp = switcher;
			buffer = anObject;
			add(new JLabel(buffer.getName()));
			setToolTipText(buffer.getPath());
		}
		
		public Object getObject(){
			return buffer;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			int w = getWidth();
			int h = getHeight();
			g.setColor(getBackground());
			g.fillRect(0, 5, w, h-5);
			g.fillRoundRect(0, 0, w, h-5, 10, 10);
		}
	}
}
