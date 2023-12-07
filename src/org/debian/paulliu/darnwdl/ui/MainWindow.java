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
    private java.util.ArrayList <org.debian.paulliu.darnwdl.Page> pages = null;
    private int currentPage = 0;
    private double scaleFactor = 1.0;
    private int fitType = 0;
    private ResourceBundle resources = ResourceBundle.getBundle("darnwdl");

    public MainWindow() {
	super("darnwdl");

	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);

	init();

    }

    public MainWindow(java.io.File wdlFile) {
	super("darnwdl");

	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);

	init();

	openFile(wdlFile);
    }

    /**
     * Load an icon from jar and resize the maximum side to iconSize
     *
     * @fileName the filename of the icon in jar
     * @iconSize resize the icon to iconSize
     * @return an ImageIcon
     */
    private ImageIcon loadIcon(String fileName, int iconSize){
        ImageIcon ret = null;
        ImageIcon orig = null;
	if (fileName.endsWith(".svg")) {
	    org.debian.paulliu.darnwdl.ui.SvgImage svgImage = null;
	    try {
		svgImage = new org.debian.paulliu.darnwdl.ui.SvgImage(getClass().getResource(fileName));
	    } catch (java.io.IOException e) {
		logger.severe(String.format("Cannot load SVG image: %1$s", fileName));
	    }
	    java.awt.Image im1 = null;
	    im1 = svgImage.getImage(32, 32);
	    orig = new javax.swing.ImageIcon(im1);
	} else {
	    orig = new javax.swing.ImageIcon(getClass().getResource(fileName));
	}
        if (orig != null) {
            Image im = orig.getImage();
            Image imScaled = null;
            if (im.getWidth(null) > im.getHeight(null)) {
                imScaled = im.getScaledInstance(iconSize,-1,Image.SCALE_SMOOTH);
            } else {
                imScaled = im.getScaledInstance(-1,iconSize,Image.SCALE_SMOOTH);
            }
            ret = new javax.swing.ImageIcon(imScaled);
        }
        return ret;
    }

    private java.awt.image.BufferedImage resizeImage01(java.awt.Image originalImage, int targetWidth, int targetHeight) {
	java.awt.image.BufferedImage resizedImage = new java.awt.image.BufferedImage(targetWidth, targetHeight, java.awt.image.BufferedImage.TYPE_INT_RGB);
	java.awt.Graphics2D graphics2D = resizedImage.createGraphics();
	graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
	graphics2D.dispose();
	return resizedImage;
    }

    private java.awt.Image resizeImage(java.awt.Image originalImage, int targetWidth, int targetHeight) {
	java.awt.Image resizedImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
	return resizedImage;
    }

    private void drawPage() {
	org.debian.paulliu.darnwdl.Page page;
	if (pages == null) {
	    return;
	}
	page = pages.get(currentPage);
	logger.info(String.format("Rendering page %1$d", currentPage));
	java.awt.Image img = page.render();
	logger.info(String.format("Render page %1$d Done", currentPage));
	javax.swing.ImageIcon image1icon = new javax.swing.ImageIcon(img);
	double imgWidth = (double)image1icon.getIconWidth();
	double imgHeight = (double)image1icon.getIconHeight();
	
	if (fitType == 0 && scaleFactor == 1.0) {
	    /* Do nothing */
	} else if (fitType == 0) {
	    img = resizeImage(img, (int)(imgWidth * scaleFactor), (int)(imgHeight * scaleFactor));
	} else if (fitType == 1) {
	    java.awt.Rectangle viewportRectangle = drawPanelScrollPane.getViewport().getViewRect();
	    scaleFactor = viewportRectangle.getWidth() / imgWidth;
	    if (scaleFactor < 0) {
		scaleFactor = 0;
	    }
	    img = resizeImage(img, (int)(imgWidth * scaleFactor), (int)(imgHeight * scaleFactor));
	} else if (fitType == 2) {
	    java.awt.Rectangle viewportRectangle = drawPanelScrollPane.getViewport().getViewRect();
	    scaleFactor = viewportRectangle.getHeight() / imgHeight;
	    if (scaleFactor < 0) {
		scaleFactor = 0;
	    }
	    img = resizeImage(img, (int)(imgWidth * scaleFactor), (int)(imgHeight * scaleFactor));
	} else if (fitType == 3) {
	    java.awt.Rectangle viewportRectangle = drawPanelScrollPane.getViewport().getViewRect();
	    scaleFactor = Math.min(viewportRectangle.getWidth() / imgWidth, viewportRectangle.getHeight() / imgHeight);
	    if (scaleFactor < 0) {
		scaleFactor = 0;
	    }
	    img = resizeImage(img, (int)(imgWidth * scaleFactor), (int)(imgHeight * scaleFactor));
	}
	drawPanel.drawImage(img);

    }

    private void startFit() {
	java.awt.event.ComponentListener[] componentListeners = drawPanelScrollPane.getComponentListeners();
	if (componentListeners.length <= 0) {
	    drawPanelScrollPane.addComponentListener(new JScrollPaneViewportComponentListener());
	}
    }

    private void stopFit() {
	java.awt.event.ComponentListener[] componentListeners = drawPanelScrollPane.getComponentListeners();
	for (int i=componentListeners.length-1; i>=0; i--) {
	    drawPanelScrollPane.removeComponentListener(componentListeners[i]);
	}
    }
    
    private void openFile(java.io.File wdlFile) {
	this.scaleFactor = 1.0;
	this.fitType = 0;
	stopFit();
	java.io.File wdloFile = null;
	if (wdlFile.getName().toUpperCase().endsWith(".WDL")) {
	    try {
		java.nio.file.Path wdloFilePath = java.nio.file.Files.createTempFile("darnwdl", ".wdlo");
		wdloFile = wdloFilePath.toFile();
	    } catch (java.io.IOException e) {
		logger.severe(String.format("Cannot create temporary file %1$s", e.toString()));
	    }
	    org.debian.paulliu.darnwdl.WPass1 wPass1 = new org.debian.paulliu.darnwdl.WPass1(wdlFile, wdloFile);
	    logger.info("Pass1 done");
	} else if (wdlFile.getName().toUpperCase().endsWith(".WDLO")) {
	    wdloFile = wdlFile;
	}
	if (wdloFile != null) {
	    org.debian.paulliu.darnwdl.WPass2 wPass2 = new org.debian.paulliu.darnwdl.WPass2(wdloFile);
	    logger.info("Pass2 done");
	    org.debian.paulliu.darnwdl.PageListGenerator pageListGenerator = new org.debian.paulliu.darnwdl.PageListGenerator (wPass2);
	    pages = pageListGenerator.getPageList();
	    logger.info("PageListGenerator done");
	    currentPage = 0;
	    if (currentPage < pages.size()) {
		drawPage();
	    }
	    statusBar.setText(String.format("%1$d/%2$d", currentPage+1, pages.size()));
	}
    }
    
    private void chooseWDLFile() {
	JFileChooser chooser = new JFileChooser();
	javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("WDL & WDLO Files", "wdl", "wdlo");
	chooser.setFileFilter(filter);
	int returnVal = chooser.showOpenDialog(this);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	    logger.info ("Open " + chooser.getSelectedFile().getName());
	    openFile(chooser.getSelectedFile());
	}
    }

    private class MenuFileOpenActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    logger.info("File -> Open");
	    chooseWDLFile();
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

    private class ToolboxButtonOpenActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    chooseWDLFile();
	}
    }

    private class ToolboxButtonCloseActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    pages = null;
	    currentPage = 0;
	    fitType=0;
	    scaleFactor=1.0;
	    stopFit();
	    drawPanel.clearImage();
	    statusBar.setText(" ");
	}
    }

    private class ToolboxButtonPrintActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    if (pages == null) {
		return;
	    }
	    boolean r1;
	    org.debian.paulliu.darnwdl.PagesPrintable pagesPrintable = new org.debian.paulliu.darnwdl.PagesPrintable(pages);
	    java.awt.print.PrinterJob pj = java.awt.print.PrinterJob.getPrinterJob();
	    pj.setPrintable(pagesPrintable);
	    r1 = pj.printDialog();
	    if (r1) {
		try {
		    pj.print();
		} catch (java.awt.print.PrinterException pe2) {
		    logger.severe("Print error: "+pe2.toString());
		}
	    }
	}
    }

    private class ToolboxButtonFitPActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    fitType=3;
	    drawPage();
	    startFit();
	}
    }

    private class ToolboxButtonFitWActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    fitType=1;
	    drawPage();
	    startFit();
	}
    }
    
    private class ToolboxButtonFitHActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    fitType=2;
	    drawPage();
	    startFit();
	}
    }

    private class ToolboxButtonZoomInActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    fitType=0;
	    stopFit();
	    scaleFactor += 0.1;
	    drawPage();
	}
    }

    private class ToolboxButtonZoomOutActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    fitType=0;
	    stopFit();
	    if (scaleFactor >= 0.2) {
		scaleFactor -= 0.1;
	    }
	    drawPage();
	}
    }
    
    private class ToolboxButtonFirstActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    currentPage = 0;
	    if (pages == null) {
		return;
	    }
	    if (currentPage < pages.size()) {
		drawPage();
	    }
	    statusBar.setText(String.format("%1$d/%2$d", currentPage+1, pages.size()));
	}
    }

    private class ToolboxButtonForwardActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    if (pages == null) {
		return;
	    }
	    if (currentPage + 1 < pages.size()) {
		currentPage = currentPage + 1;
		drawPage();
		statusBar.setText(String.format("%1$d/%2$d", currentPage+1, pages.size()));
	    }
	}
    }

    private class ToolboxButtonBackActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    if (pages == null) {
		return;
	    }
	    if (currentPage > 0) {
		currentPage = currentPage - 1;
	    }
	    if (currentPage < pages.size()) {
		drawPage();
		statusBar.setText(String.format("%1$d/%2$d", currentPage+1, pages.size()));
	    }
	}
    }
    
    private class ToolboxButtonLastActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    if (pages == null) {
		return;
	    }
	    if (pages.size() > 0) {
		currentPage = pages.size() - 1;
	    } else {
		currentPage = 0;
	    }
	    if (currentPage < pages.size()) {
		drawPage();
		statusBar.setText(String.format("%1$d/%2$d", currentPage+1, pages.size()));
	    }
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
        JMenuItem jMenuItem_File_Open = createMenuItem(resources.getString("Open"), "Tree.openIcon");
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
	    if (logger != null) {
		logger.info("Resized to " + e.getComponent().getSize());
	    }
	}
	@Override
	public void componentMoved(ComponentEvent e) {
	    if (logger != null) {
		logger.info("Moved to " + e.getComponent().getLocation());
	    }
	}
    }

    private class JScrollPaneViewportComponentListener extends java.awt.event.ComponentAdapter {
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
		drawPage();
	    }
	}
    }

    
    private class MainWindowKeyAdapter extends java.awt.event.KeyAdapter {
	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
	    logger.info(String.format("key Typed: %1$s", e.toString()));
	    if (drawPanelScrollPane == null) {
		return;
	    }
	    javax.swing.JScrollBar jScrollBar = drawPanelScrollPane.getVerticalScrollBar();
	    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_PAGE_DOWN) {
		int u = jScrollBar.getVisibleAmount();
		if (jScrollBar.getValue() + u >= jScrollBar.getMaximum()) {
		    if (pages == null) {
			return;
		    }
		    if (currentPage + 1 < pages.size()) {
			currentPage = currentPage + 1;
			drawPage();
			statusBar.setText(String.format("%1$d/%2$d", currentPage+1, pages.size()));
			jScrollBar.setValue(jScrollBar.getMinimum());
		    }
		} else {
		    jScrollBar.setValue(jScrollBar.getValue()+u);
		    logger.info(String.format("jScrollBar current value %1$d, min %2$d, max %3$d, visiable value %4$d", jScrollBar.getValue(), jScrollBar.getMinimum(), jScrollBar.getMaximum(), u));
		}
	    } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_PAGE_UP) {
		int u = jScrollBar.getVisibleAmount();
		if (jScrollBar.getValue() <= jScrollBar.getMinimum()) {
		    if (pages == null) {
			return;
		    }
		    if (currentPage > 0) {
			currentPage = currentPage - 1;
		    } else {
			return;
		    }
		    if (currentPage < pages.size()) {
			drawPage();
			statusBar.setText(String.format("%1$d/%2$d", currentPage+1, pages.size()));
			jScrollBar.setValue(jScrollBar.getMaximum());
		    }
		} else {
		    jScrollBar.setValue(jScrollBar.getValue()-u);
		    logger.info(String.format("jScrollBar current value %1$d, min %2$d, max %3$d, visiable value %4$d", jScrollBar.getValue(), jScrollBar.getMinimum(), jScrollBar.getMaximum(), u));
		}
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
     * @param icon the icon of the button
     * @return the button
     */
    private javax.swing.JButton createToolBoxButton(String text, Icon icon) {
	javax.swing.JButton ret = null;
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
	return createToolBoxButton(text, icon);
    }
    
    public void init() {
	Panel panel = new Panel();
	this.getContentPane().add(panel);
	this.setSize(640,480);
	this.setVisible(true);
	
        this.addWindowListener(new MainWindowListener());
	//this.addComponentListener(new MainWindowComponentListener());
	this.addKeyListener(new MainWindowKeyAdapter());

	this.setJMenuBar(createMenuBar());

	Container cp = panel;

	cp.setLayout(new BorderLayout());

	JPanel toolBox = new JPanel();
	
	toolBox.setLayout(new BoxLayout(toolBox, BoxLayout.X_AXIS));

	javax.swing.JButton toolBoxButton_Open = createToolBoxButton("Open", "FileView.fileIcon");
	javax.swing.JButton toolBoxButton_Close = createToolBoxButton("Close", "OptionPane.errorIcon");
	javax.swing.JButton toolBoxButton_Print = createToolBoxButton("Print", org.debian.paulliu.darnwdl.ui.StockImage.getInstance().getPrint());
	javax.swing.JButton toolBoxButton_Quit = createToolBoxButton("Quit", org.debian.paulliu.darnwdl.ui.StockImage.getInstance().getQuit());
	javax.swing.JButton toolBoxButton_FitP = createToolBoxButton("FitP", org.debian.paulliu.darnwdl.ui.StockImage.getInstance().getFitP());
	javax.swing.JButton toolBoxButton_FitW = createToolBoxButton("FitW", org.debian.paulliu.darnwdl.ui.StockImage.getInstance().getFitW());
	javax.swing.JButton toolBoxButton_FitH = createToolBoxButton("FitH", org.debian.paulliu.darnwdl.ui.StockImage.getInstance().getFitH());
	javax.swing.JButton toolBoxButton_ZoomIn = createToolBoxButton("Zoom In", org.debian.paulliu.darnwdl.ui.StockImage.getInstance().getZoomIn());
	javax.swing.JButton toolBoxButton_ZoomOut = createToolBoxButton("Zoom Out", org.debian.paulliu.darnwdl.ui.StockImage.getInstance().getZoomOut());
	javax.swing.JButton toolBoxButton_First = createToolBoxButton("First", org.debian.paulliu.darnwdl.ui.StockImage.getInstance().getGoFirst());
	javax.swing.JButton toolBoxButton_Back = createToolBoxButton("Back", org.debian.paulliu.darnwdl.ui.StockImage.getInstance().getGoBack());
	javax.swing.JButton toolBoxButton_Forward = createToolBoxButton("Forward", org.debian.paulliu.darnwdl.ui.StockImage.getInstance().getGoForward());
	javax.swing.JButton toolBoxButton_Last = createToolBoxButton("Last", org.debian.paulliu.darnwdl.ui.StockImage.getInstance().getGoLast());
	
	//toolBoxButton_Print.setEnabled(false);

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

	toolBoxButton_Open.addActionListener(new ToolboxButtonOpenActionListener());
	toolBoxButton_Close.addActionListener(new ToolboxButtonCloseActionListener());
	toolBoxButton_Print.addActionListener(new ToolboxButtonPrintActionListener());
	toolBoxButton_FitP.addActionListener(new ToolboxButtonFitPActionListener());
	toolBoxButton_FitW.addActionListener(new ToolboxButtonFitWActionListener());
	toolBoxButton_FitH.addActionListener(new ToolboxButtonFitHActionListener());
	toolBoxButton_ZoomIn.addActionListener(new ToolboxButtonZoomInActionListener());
	toolBoxButton_ZoomOut.addActionListener(new ToolboxButtonZoomOutActionListener());
	toolBoxButton_First.addActionListener(new ToolboxButtonFirstActionListener());
	toolBoxButton_Back.addActionListener(new ToolboxButtonBackActionListener());
	toolBoxButton_Forward.addActionListener(new ToolboxButtonForwardActionListener());
	toolBoxButton_Last.addActionListener(new ToolboxButtonLastActionListener());
	toolBoxButton_Quit.addActionListener(new ToolboxButtonQuitActionListener());
	
	cp.add(toolBox, BorderLayout.NORTH);

	drawPanel = new org.debian.paulliu.darnwdl.ui.DrawPanel();
	drawPanelScrollPane = new JScrollPane(drawPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

	//drawPanelScrollPane.addComponentListener(new JScrollPaneViewportComponentListener());

	cp.add(drawPanelScrollPane, BorderLayout.CENTER);

	statusBar = new JLabel(" ");
	cp.add(statusBar, BorderLayout.SOUTH);

	setIconImage(loadIcon("/pixmaps/darnwdlicon.svg", 10).getImage());

	panel.revalidate();
    }

    public void stop() {
    }

}
