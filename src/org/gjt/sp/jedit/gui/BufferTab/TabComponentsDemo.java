package org.gjt.sp.jedit.gui.BufferTab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
 

public class TabComponentsDemo extends JFrame {    
 
    private final int tabNumber = 5;
    private final JTabbedPane pane = new JTabbedPane();
 
     
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){    
            	UIManager.put("swing.boldMetal", Boolean.FALSE);
            	new TabComponentsDemo("DEMO1").runTest();
            }
        });
    }
     
    public TabComponentsDemo(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(pane);        
    }
     
    public void runTest() {
        pane.removeAll();
        for (int i = 0; i < tabNumber; i++) {
            String title = "Tab " + i;
            pane.add(title, new JLabel(title));
            initTabComponent(i);
        }
       
        pane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
      
        setSize(new Dimension(400, 200));
        setLocationRelativeTo(null);
        setVisible(true);
    }
     
     
    private void initTabComponent(int i) {
        pane.setTabComponentAt(i,
                 new ButtonTabComponent(pane));
    }    
 
  
}
