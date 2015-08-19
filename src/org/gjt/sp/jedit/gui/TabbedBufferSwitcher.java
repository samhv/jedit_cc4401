package org.gjt.sp.jedit.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.bufferset.BufferSet;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.ThreadUtilities;


public class TabbedBufferSwitcher extends JPanel {

	private int maxOpenFile;
	private int index;
	private boolean updating;
	private List<Tab> tabs;
	private final EditPane editPane;
	private JPanel center;
	private JPanel middle;
	private Tab selected;

	private JPanel left;
	private JPanel right;
	private int width_parent;

	private static Color selectedColor = new Color(0xffffff);
	private static Color unselectedColor = new Color(0xeeeeee);
	private static Color borderColor = new Color(0x7a8a99);
	private static Color buttonClose = new Color(0xc0bcfe);

	private static Border emptyBorder = BorderFactory.createEmptyBorder();
	private static Border colorBorder = new AbstractBorder() {
		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Tab t = (Tab)c;
			if (!t.isSelected()){
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(TabbedBufferSwitcher.borderColor);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.drawArc(0, 0, 20, 20, 90, 90);
				g2.drawArc(width-21, 0,20, 20, 0, 90);
				g2.drawLine(10, 0, width-11, 0);
				g2.drawLine(0, 10, 0, height-1);
				g2.drawLine(width-1, 10, width-1, height-1);
			}
		}
	};
	private static Border moveBorders = new AbstractBorder() {
		private Color border = new Color(0x7a8a99);
		
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(border);
			g.drawLine(0, 0, 0, height);
			g.drawLine(0, 0, width-1, 0);
			g.drawLine(width-1, 0, width-1, height);
			
		};
	};

	public TabbedBufferSwitcher(final EditPane editPane){
		//Things for custom tab
		this.editPane = editPane;
		JPanel up = new JPanel();
		center = new JPanel();
		middle = new JPanel();
		setLayout(new BorderLayout(0,0));
		middle.setLayout(new BorderLayout());
		
		up.setBackground(TabbedBufferSwitcher.unselectedColor);
		up.setPreferredSize(new Dimension(0,2));
		center.setBackground(new Color(0xeeeeee));

		center.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 0));
		
		middle.add(center,BorderLayout.CENTER);
		add(up,BorderLayout.NORTH);
		add(middle,BorderLayout.CENTER);
		//add(down,BorderLayout.SOUTH);

		tabs = new ArrayList<Tab>();
		index = 0;
		left = new JPanel(new FlowLayout(FlowLayout.CENTER));
		left.add(new JLabel(GUIUtilities.loadIcon("arrow-left.png")));
		left.setBorder(TabbedBufferSwitcher.moveBorders);
		
		right = new JPanel();
		right.add(new JLabel(GUIUtilities.loadIcon("arrow-right.png")));
		right.setBorder(TabbedBufferSwitcher.moveBorders);
		
		width_parent = -1;

		//Things for jedit
		updating = false;

		setMaximumRowCount(jEdit.getIntegerProperty("bufferSwitcher.maxRowCount", 10));
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setUITabs();
			}
		});
		
		left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {			
				if (index == 0)
					return;
				index --;
				setUITabs();
				
			}
		});
		
		right.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {			
				if (tabs.size()-1 == index)
					return;
				index ++;
				setUITabs();
				
			}
		});

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
				/*if (jEdit.getBooleanProperty("bufferswitcher.sortBuffers", true)) {
					Arrays.sort(buffers, new Comparator<Buffer>(){
						public int compare(Buffer a, Buffer b) {
							if (jEdit.getBooleanProperty("bufferswitcher.sortByName", true)) {
								return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());		
							} else {
								return a.getPath().toLowerCase().compareTo(b.getPath().toLowerCase());	
							}
						}
					});
				}*/
				setTabs(buffers);
				int result;
				do{
					result = setUITabs();
					switch(result){
					case 1:
						index++;
						break;
					case 2:
						index--;
						break;
					}
				}while(result==1 || result==2);
				setSelectedItem(editPane.getBuffer());
				//addDnD();
				updating = false;
			}
		};
		ThreadUtilities.runInDispatchThread(runnable);
	}

	private void setTabs(Buffer[] buffers) {
		List<Tab> tbuff = new LinkedList<Tab>(tabs);
		for(int i = 0; i<buffers.length && i<maxOpenFile; i++){
			Tab t = new Tab(buffers[i],this);
			if (!tabs.contains(t)){
				tabs.add(t);
			}
			tbuff.remove(t);
		}
		tabs.removeAll(tbuff);
		//setUITabs();
	}

	private int setUITabs() {
		center.removeAll();
		middle.remove(left);
		middle.remove(right);
		int length = 17*2, width = getWidth();
		int position = -1;
		if (width_parent == -1)
			return position;
		boolean overflow = false;
		Tab t;
		int j;
		for(j = index ; j < tabs.size() ; j++){
			t = tabs.get(j);
			if (t.getWidth() + length > width){
				overflow = true;
				break;
			}
			if (t.equals(getSelectedItem()))
				position = 0;
			length += t.getWidth();
			center.add(t);
		}
		if (overflow){
			if (tabs.subList(j, tabs.size()).contains(getSelectedItem()))
				position = 1;
			middle.add(right,BorderLayout.EAST);
			middle.add(left,BorderLayout.WEST);
		}else{
			while(index > 0){
				t = tabs.get(index-1);
				if ( t.getWidth() + length > width){
					middle.add(right,BorderLayout.EAST);
					middle.add(left,BorderLayout.WEST);
					break;
				}
				if (t.equals(getSelectedItem()))
					position = 0;
				index--;
				length += t.getWidth();
				center.add(t,0);
			}
		}
		middle.revalidate();
		middle.repaint();
		center.revalidate();
		center.repaint();
		return position==-1 ? 2 :position;
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

	public EditPane getEditPane(){
		return editPane;
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

	@Override
	public void paint(Graphics g) {
		if (width_parent == -1){
			width_parent = 1;
			updateBufferList();
		}
		super.paint(g);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
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
			add(new RoundButton("",GUIUtilities.loadIcon(jEdit.getProperty("close-tab.icon")),this));

			addMouseListener(new MouseAdapter(){

				@Override
				public void mousePressed(MouseEvent e) {
					Component t = e.getComponent();
					if (t instanceof Tab){
						Tab tab = (Tab) t;
						tp.setTab(tab);
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					setBorder(TabbedBufferSwitcher.colorBorder);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					setBorder(TabbedBufferSwitcher.emptyBorder);
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

		public boolean isSelected(){
			return getBackground() == TabbedBufferSwitcher.selectedColor;
		}

		@Override
		protected void paintComponent(Graphics g) {
			int w = getWidth();
			int h = getHeight();
			g.setColor(getBackground());
			g.fillRect(0, 5, w, h-5);
			g.fillRoundRect(0, 0, w, h-5, 10, 10);
		}

		public TabbedBufferSwitcher getTabbedPane(){
			return tp;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Tab){
				Tab t = (Tab) obj;
				return this.buffer.equals(t.buffer);
			}
			return false;
		}
	}

	private class RoundButton extends JButton {

		private final Tab tab;
		private boolean over;

		public RoundButton(String text, Icon icon, final Tab tab) {
			this.tab = tab;
			over = false;

			setModel(new DefaultButtonModel());
			init(text, icon);
			if(icon==null) {
				return;
			}

			setPreferredSize(new Dimension(icon.getIconWidth() + 20, icon.getIconHeight() + 20));

			setBorder(BorderFactory.createRaisedBevelBorder());
			//setBackground(Color.BLACK);
			setContentAreaFilled(false);
			setFocusPainted(false);
			//setVerticalAlignment(SwingConstants.TOP);
			setAlignmentY(Component.TOP_ALIGNMENT);

			initShape();

			setToolTipText("Close");
			setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
			addMouseListener(new MouseAdapter() {

				@Override
				public void mouseEntered(MouseEvent e) {
					tab.dispatchEvent(e);
					if (e.getComponent() instanceof RoundButton){
						final RoundButton b = (RoundButton)e.getComponent();
						b.over = true;

						b.setBorder(new AbstractBorder() {

							@Override
							public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
								Graphics2D g2 = (Graphics2D) g;
								g2.setColor(TabbedBufferSwitcher.borderColor);
								g2.drawOval(0, 0, width - 1, height - 1);
							}

							public Insets getBorderInsets(Component c, Insets insets) {
								insets.set(2, 2, 2, 2);
								return insets;
							};

						});
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (e.getComponent() instanceof RoundButton){
						RoundButton b = (RoundButton)e.getComponent();
						b.over = false;

						b.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
					}
				}
			});

			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					close();
				}
			});
		}

		protected Shape shape, base;
		protected Insets insets;

		protected void initShape() {
			if(!getBounds().equals(base)) {
				Dimension s = getPreferredSize();
				base = getBounds();
				shape = new Ellipse2D.Float(0, 0, s.width-1, s.height-1);
			}
		}

		@Override 
		public Dimension getPreferredSize() {
			Icon icon = getIcon();
			Insets i = getInsets();
			int iw = Math.max(icon.getIconWidth(), icon.getIconHeight());
			return new Dimension(iw+i.right+i.left, iw+i.top+i.bottom);
		}

		@Override 
		public boolean contains(int x, int y) {
			initShape();
			return shape.contains(x, y);
			//or return super.contains(x, y) && ((image.getRGB(x, y) >> 24) & 0xff) > 0;
		}

		private void close(){
			jEdit.closeBuffer( tab.getTabbedPane().getEditPane(), (Buffer)tab.getObject());
		}

		@Override
		protected void paintComponent(Graphics g) {
			if (over){
				g.setColor(TabbedBufferSwitcher.buttonClose);
				g.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
			}
			super.paintComponent(g);
		}
	}
}
