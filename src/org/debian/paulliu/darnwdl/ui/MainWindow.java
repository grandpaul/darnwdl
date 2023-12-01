/*
    Copyright (C) 2017  Ying-Chun Liu (PaulLiu) <paulliu@debian.org>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.debian.paulliu.darnwdl.ui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.applet.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

public class MainWindow extends JFrame {
    private java.util.logging.Logger logger = null;
    private org.debian.paulliu.darnwdl.ui.DrawPanel drawPanel = null;
    private JScrollPane drawPanelScrollPane = null;
    private JLabel statusBar = null;

    public MainWindow() {
	super("darnwdl");

	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);

	init();

    }

    private class MenuFileOpenActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("File -> Open");
	}
    }

    private class MenuFileQuitActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("File -> Quit");
	    stop();
	    System.exit(0);
	}
    }

    private class MenuHelpAboutActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Help -> About");
	}
    }

    private class ToolboxButtonQuitActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("Toolbox Quit");
	    stop();
	    System.exit(0);
	}
    }

    
    /**
     * Create menu item
     *
     * @param text The text of the button
     * @param iconName the icon of the button
     * @return the menu item
     */
    private JMenuItem createMenuItem(String text, String iconName) {
	Icon icon = null;
	JMenuItem ret = null;
	if (iconName != null) {
	    icon = javax.swing.UIManager.getIcon(iconName);
	}
	if (icon != null) {
	    ret = new JMenuItem(text, icon);
	} else {
	    ret = new JMenuItem(text);
	}
	return ret;
    }

    /**
     * Create menu bar
     *
     * This function creates the menu bar for this app.
     *
     * @return the menu bar.
     */
    private JMenuBar createMenuBar() {
	JMenuBar jMenuBar = new JMenuBar();
	JMenu jMenu_File = new JMenu("File");
        JMenuItem jMenuItem_File_New = createMenuItem("New", "FileView.fileIcon");
        JMenuItem jMenuItem_File_Open = createMenuItem("Open", "Tree.openIcon");
        JMenuItem jMenuItem_File_Save = createMenuItem("Save", "FileView.floppyDriveIcon");
        JMenuItem jMenuItem_File_SaveAs = createMenuItem("Save As", "FileView.floppyDriveIcon");
        JMenuItem jMenuItem_File_Quit = createMenuItem("Quit", "InternalFrame.closeIcon");
	jMenu_File.add(jMenuItem_File_New);
	jMenu_File.add(jMenuItem_File_Open);
	jMenu_File.add(jMenuItem_File_Save);
	jMenu_File.add(jMenuItem_File_SaveAs);
	jMenu_File.add(new javax.swing.JSeparator());
	jMenu_File.add(jMenuItem_File_Quit);

	jMenuItem_File_Open.addActionListener(new MenuFileOpenActionListener());
	jMenuItem_File_Quit.addActionListener(new MenuFileQuitActionListener());
	
	jMenuBar.add(jMenu_File);

	jMenuItem_File_New.setEnabled(false);
	jMenuItem_File_Save.setEnabled(false);
	jMenuItem_File_SaveAs.setEnabled(false);

	JMenu jMenu_Edit = new JMenu("Edit");
        JMenuItem jMenuItem_Edit_Cut = createMenuItem("Cut", null);
        JMenuItem jMenuItem_Edit_Copy = createMenuItem("Copy", null);
        JMenuItem jMenuItem_Edit_Paste = createMenuItem("Paste", null);
        JMenuItem jMenuItem_Edit_Delete = createMenuItem("Delete", null);
	jMenu_Edit.add(jMenuItem_Edit_Cut);
	jMenu_Edit.add(jMenuItem_Edit_Copy);
	jMenu_Edit.add(jMenuItem_Edit_Paste);
	jMenu_Edit.add(jMenuItem_Edit_Delete);
	jMenuBar.add(jMenu_Edit);

	jMenuItem_Edit_Cut.setEnabled(false);
	jMenuItem_Edit_Copy.setEnabled(false);
	jMenuItem_Edit_Paste.setEnabled(false);
	jMenuItem_Edit_Delete.setEnabled(false);
	
	JMenu jMenu_View = new JMenu("View");
	jMenuBar.add(jMenu_View);

	JMenu jMenu_Help = new JMenu("Help");
        JMenuItem jMenuItem_Help_About = createMenuItem("About", "OptionPane.informationIcon");
	jMenu_Help.add(jMenuItem_Help_About);
	jMenuBar.add(jMenu_Help);
	
	jMenuItem_Help_About.addActionListener(new MenuHelpAboutActionListener());

	return jMenuBar;
    }
 
    private class MainWindowListener extends java.awt.event.WindowAdapter {
	public void windowClosing(WindowEvent e) {
	    Window w = e.getWindow();
	    logger.info("Window closing");
	    stop();
	    System.exit(0);
	}
    }

    private class MainWindowComponentListener extends java.awt.event.ComponentAdapter {
	@Override
	public void componentResized(ComponentEvent e) {
	    /* We can get the width and height of the viewPort's size.
	       So that FitH and FitW works */
	    double viewPortWidth = 0;
	    double viewPortHeight = 0;
	    if (drawPanelScrollPane != null && logger != null) {
		Rectangle r1 = drawPanelScrollPane.getViewport().getViewRect();
		logger.info("Resized to " + e.getComponent().getSize());
		logger.info("Rectangle: " + r1.toString());
		viewPortWidth = r1.getWidth();
		viewPortHeight = r1.getHeight();
	    }
	}
	@Override
	public void componentMoved(ComponentEvent e) {
	    if (logger != null) {
		logger.info("Moved to " + e.getComponent().getLocation());
	    }
	}
    }

    /**
     * Create the Button for Tool Box
     *
     * There is a row of Tool Box in the GUI. This functions creates
     * the button of the tool box
     *
     * @param text The text of the button
     * @param iconName the icon of the button
     * @return the button
     */
    private javax.swing.JButton createToolBoxButton(String text, String iconName) {
	javax.swing.JButton ret = null;
	Icon icon = null;
	if (iconName != null) {
	    icon = javax.swing.UIManager.getIcon(iconName);
	}
	if (icon != null) {
	    ret = new javax.swing.JButton(text, icon);
	} else {
	    ret = new javax.swing.JButton(text);
	}

	ret.setHorizontalTextPosition(SwingConstants.CENTER);
	ret.setVerticalTextPosition(SwingConstants.BOTTOM);

	java.awt.Insets insects1 = null;
	int toolBoxButtonHorizontalSpace = 3;
	insects1 = ret.getMargin();
	ret.setMargin(new Insets(insects1.top,toolBoxButtonHorizontalSpace,insects1.bottom,toolBoxButtonHorizontalSpace));
	java.awt.Dimension dim1;
	java.awt.Dimension dim2;
	dim1 = ret.getMaximumSize();
	dim2 = new java.awt.Dimension();
	dim2.setSize(dim1.getWidth(), 100);
	ret.setMaximumSize(dim2);
	return ret;
    }
    
    public void init() {
	Panel panel = new Panel();
	this.getContentPane().add(panel);
	this.setSize(640,480);
	this.setVisible(true);
	
        this.addWindowListener(new MainWindowListener());
	//this.addComponentListener(new MainWindowComponentListener());

	this.setJMenuBar(createMenuBar());

	Container cp = panel;

	cp.setLayout(new BorderLayout());

	JPanel toolBox = new JPanel();
	
	toolBox.setLayout(new BoxLayout(toolBox, BoxLayout.X_AXIS));

	javax.swing.JButton toolBoxButton_Open = createToolBoxButton("Open", null);
	javax.swing.JButton toolBoxButton_Close = createToolBoxButton("Close", "OptionPane.errorIcon");
	javax.swing.JButton toolBoxButton_Print = createToolBoxButton("Print", null);
	javax.swing.JButton toolBoxButton_Quit = createToolBoxButton("Quit", "InternalFrame.closeIcon");
	javax.swing.JButton toolBoxButton_FitP = createToolBoxButton("FitP", null);
	javax.swing.JButton toolBoxButton_FitW = createToolBoxButton("FitW", null);
	javax.swing.JButton toolBoxButton_FitH = createToolBoxButton("FitH", null);
	javax.swing.JButton toolBoxButton_ZoomIn = createToolBoxButton("Zoom In", null);
	javax.swing.JButton toolBoxButton_ZoomOut = createToolBoxButton("Zoom Out", null);
	javax.swing.JButton toolBoxButton_First = createToolBoxButton("First", null);
	javax.swing.JButton toolBoxButton_Back = createToolBoxButton("Back", null);
	javax.swing.JButton toolBoxButton_Forward = createToolBoxButton("Forward", null);
	javax.swing.JButton toolBoxButton_Last = createToolBoxButton("Last", null);
	
	toolBoxButton_Print.setEnabled(false);

	toolBox.add(toolBoxButton_Open);
	toolBox.add(toolBoxButton_Close);
	toolBox.add(toolBoxButton_Print);
	toolBox.add(toolBoxButton_Quit);
	toolBox.add(toolBoxButton_FitP);
	toolBox.add(toolBoxButton_FitW);
	toolBox.add(toolBoxButton_FitH);
	toolBox.add(toolBoxButton_ZoomIn);
	toolBox.add(toolBoxButton_ZoomOut);
	toolBox.add(toolBoxButton_First);
	toolBox.add(toolBoxButton_Back);
	toolBox.add(toolBoxButton_Forward);
	toolBox.add(toolBoxButton_Last);

	toolBoxButton_Quit.addActionListener(new ToolboxButtonQuitActionListener());
	
	cp.add(toolBox, BorderLayout.NORTH);

	drawPanel = new org.debian.paulliu.darnwdl.ui.DrawPanel();
	drawPanelScrollPane = new JScrollPane(drawPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

	drawPanelScrollPane.addComponentListener(new MainWindowComponentListener());
	//drawPanel.addComponentListener(new MainWindowComponentListener());

	cp.add(drawPanelScrollPane, BorderLayout.CENTER);

	statusBar = new JLabel(" ");
	cp.add(statusBar, BorderLayout.SOUTH);

	panel.revalidate();
    }

    public void stop() {
    }
}
