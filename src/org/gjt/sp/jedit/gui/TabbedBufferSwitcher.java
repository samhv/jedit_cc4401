package org.gjt.sp.jedit.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.bufferset.BufferSet;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.ThreadUtilities;


public class TabbedBufferSwitcher extends JPanel {

	private List<Tab> tabs;
	private final EditPane editPane;
	private JPanel center;
	private Tab selected;
	private boolean updating;
	private int maxOpenFile;

	private static Color selectedColor = new Color(0xffffff);
	private static Color unselectedColor = new Color(0xeeeeee);

	public TabbedBufferSwitcher(final EditPane editPane){
		//Things for custom tab
		this.editPane = editPane;
		JPanel up = new JPanel();
		JPanel down = new JPanel();
		center = new JPanel();
		setLayout(new BorderLayout(0,0));

		up.setBackground(TabbedBufferSwitcher.unselectedColor);
		down.setBackground(TabbedBufferSwitcher.selectedColor);
		up.setPreferredSize(new Dimension(0,2));
		down.setPreferredSize(new Dimension(0,2));
		center.setBackground(new Color(0xeeeeee));

		center.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 0));
		
		add(up,BorderLayout.NORTH);
		add(center,BorderLayout.CENTER);
		add(down,BorderLayout.SOUTH);

		tabs = new LinkedList<Tab>();

		//Things for jedit
		updating = false;

		setMaximumRowCount(jEdit.getIntegerProperty("bufferSwitcher.maxRowCount", 10));

		EditBus.addToBus(this);
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
				//addDnD();
				updating = false;
			}
		};
		ThreadUtilities.runInDispatchThread(runnable);
	}

	private void setTabs(Buffer[] buffers) {
		center.removeAll();
		tabs.clear();
		for(int i = 0; i<buffers.length && i<maxOpenFile; i++){
			Tab t = new Tab(buffers[i],this);
			center.add(t);
			tabs.add(t);
		}
		center.revalidate();
		center.repaint();
	}

	public void setMaximumRowCount(int integerProperty) {
		maxOpenFile = integerProperty;		
	}

	public void handlePropertiesChanged(PropertiesChanged msg) {
		updateBufferList();
	}

	public Object getSelectedItem() {
		return selected;
	}


	public void setSelectedItem(Object anObject) {
		if (selected != null) selected.unselect();
		for (Tab t : tabs){
			if (t.getObject() == anObject){
				selected = t;
				selected.select();
				break;
			}
		}
	}
	
	private void setTab(Tab tab) {
		setSelectedItem(tab.getObject());
		editPane.setBuffer((Buffer)tab.getObject());
	}

	public boolean specialState() {
		return false;
	}

	public void showPopup(){ }
	
	private class Tab extends JPanel{

		final TabbedBufferSwitcher tp;
		private Buffer buffer;

		Tab(Buffer anObject, final TabbedBufferSwitcher switcher){
			tp = switcher;
			buffer = anObject;
			add(new JLabel(buffer.getName()));
			setToolTipText(buffer.getPath());

			addMouseListener(new MouseAdapter(){

				@Override
				public void mousePressed(MouseEvent e) {
					Component t = e.getComponent();
					if (t instanceof Tab){
						Tab tab = (Tab) t;
						tp.setTab(tab);
					}
				}

			});
		}

		public void select() {
			setBackground(TabbedBufferSwitcher.selectedColor);			
		}

		public void unselect() {
			setBackground(TabbedBufferSwitcher.unselectedColor);
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
