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

package org.debian.paulliu.darnwdl;

import java.util.*;
import java.io.*;
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

/**
 * Main class. The program entry
 */
public class Main {
    private Scanner stdin = new Scanner(System.in);
    private java.util.logging.Logger logger = null;
    public static String loggerName = "MainLogger";
    private JFrame mainWindow = null;
    private java.io.File wdlFile = null;

    /**
     * Init class data here
     */
    private void init() {
	if (wdlFile != null) {
	    mainWindow = new org.debian.paulliu.darnwdl.ui.MainWindow(wdlFile);
	} else {
	    mainWindow = new org.debian.paulliu.darnwdl.ui.MainWindow();
	}
    }

    /**
     * Handle the input here.
     * This method will call solve() method inside to solve the problem.
     * The return value indicates if there are more input data need to 
     * be handled. If it doesn't return 0, means this function have to be
     * called again to solve next data.
     * @return 0: end. 1: need to call input() again for next data.
     */
    private int input() {
	int ret=0;
	String com1;

	if (stdin.hasNextLine()) {
	    com1 = stdin.nextLine();
	} else {
	    return ret;
	}

	solve();
	ret=1;
    	return ret;
    }

    /**
     * Solve the problems here.
     * It will call output to output the results.
     */
    private void solve() {
	output();
    }

    /**
     * Output the results
     */
    private void output() {
    }


    /**
     * log information for debugging.
     */
    public void logInfo(String a, Object... args) {
	if (logger != null) {
	    logger.info(String.format(a,args));
	}
    }

    public void begin() {
	this.logger = java.util.logging.Logger.getLogger(Main.loggerName);
	if (this.logger.getLevel() != java.util.logging.Level.INFO) {
	    this.logger = null;
	}
	init();
    }

    public void unittest() {
	this.logger = java.util.logging.Logger.getLogger(Main.loggerName);
    }

    public static void main (String args[]) {
	Main myMain = new Main();
	if (args.length >= 1 && args[0].equals("unittest")) {
	    myMain.unittest();
	    return;
	}
	if (args.length >= 3 && args[0].equals("wpass1")) {
	    java.util.logging.Logger.getLogger(Main.loggerName).setLevel(java.util.logging.Level.INFO);
	    java.io.File inputFile = new File(args[1]);
	    java.io.File outputFile = new File(args[2]);
	    WPass1 wpass1 = new WPass1(inputFile, outputFile);
	    return;
	}
	if (args.length >= 2 && args[0].equals("wpass2")) {
	    java.util.logging.Logger.getLogger(Main.loggerName).setLevel(java.util.logging.Level.INFO);
	    java.io.File inputFile = new File(args[1]);
	    WPass2 wpass2 = new WPass2(inputFile);
	    java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index> indexList = wpass2.getIndexList();
	    for (org.debian.paulliu.darnwdl.wdlo.Index i : indexList) {
		if (i.getTag() != null) {
		    System.out.println(String.format("%1$s: %2$d", i.getTag(), i.getFilePointer()));
		    if (i.getTag().compareTo("R2") == 0) {
			org.debian.paulliu.darnwdl.wdlo.R2 r2 = new org.debian.paulliu.darnwdl.wdlo.R2(i);
			System.out.println(String.format(" unknown: %1$d", r2.getUnknownShort()));
		    } else if (i.getTag().compareTo("FT") == 0) {
			org.debian.paulliu.darnwdl.wdlo.FT ft = new org.debian.paulliu.darnwdl.wdlo.FT(i);
			System.out.println(String.format(" index: %1$d", ft.getFilePointerToSP()));
		    } else if (i.getTag().compareTo("ET") == 0 || i.getTag().compareTo("EU") == 0) {
			org.debian.paulliu.darnwdl.wdlo.ET et = new org.debian.paulliu.darnwdl.wdlo.ET(i);
			java.util.ArrayList<org.debian.paulliu.darnwdl.wdlo.etdata.ETData> etDataList = et.getETDataList();
			java.io.StringWriter sw = new java.io.StringWriter();
			for (org.debian.paulliu.darnwdl.wdlo.etdata.ETData etData : etDataList) {
			    String out1 = etData.getString();
			    sw.write("{");
			    sw.write(String.format("x: %1$d, ", etData.x));
			    sw.write(String.format("y: %1$d, ", etData.y));
			    sw.write(String.format("string: %1$s, ", out1));
			    sw.write(String.format("flag1: 0x%1$x, ", etData.flag1));
			    if ((etData.flag1 & 0x02) != 0) {
				sw.write("flag1_0x2_width: [");
				for (Integer width1 : etData.getWidth()) {
				    sw.write(width1.toString());
				    sw.write(", ");
				}
				sw.write("], ");
			    }
			    sw.write("}, ");
			}
			System.out.println(" etdata: "+sw.toString());
		    } else if (i.getTag().compareTo("UT") == 0) {
			org.debian.paulliu.darnwdl.wdlo.UT ut = new org.debian.paulliu.darnwdl.wdlo.UT(i);
			java.util.ArrayList<org.debian.paulliu.darnwdl.wdlo.utdata.UTData> utDataList = ut.getUTDataList();
			java.io.StringWriter sw = new java.io.StringWriter();
			for (org.debian.paulliu.darnwdl.wdlo.utdata.UTData utData : utDataList) {
			    String out1 = utData.getString();
			    sw.write("{");
			    sw.write(String.format("x: %1$d, ", utData.x));
			    sw.write(String.format("y: %1$d, ", utData.y));
			    sw.write(String.format("string: %1$s, ", out1));
			    sw.write(String.format("flag1: 0x%1$x, ", utData.flag1));
			    if ((utData.flag1 & 0x02) != 0) {
				sw.write("flag1_0x2_width: [");
				for (Integer width1 : utData.getWidth()) {
				    sw.write(width1.toString());
				    sw.write(", ");
				}
				sw.write("], ");
			    }
			    sw.write("}, ");
			}
			System.out.println(" utdata: "+sw.toString());
		    } else if (i.getTag().compareTo("CR") == 0) {
			org.debian.paulliu.darnwdl.wdlo.CR cr = new org.debian.paulliu.darnwdl.wdlo.CR(i);
			System.out.println(String.format(" rectangle: %1$s", cr.getRectangle().toString()));
		    }
		} else {
		    System.out.println(String.format("special %1$d: %2$d", i.getSpecialByte(), i.getFilePointer()));
		    if (i.getSpecialByte() == 1) {
			org.debian.paulliu.darnwdl.wdlo.Special01 sp01 = new org.debian.paulliu.darnwdl.wdlo.Special01 (i);
			String fontFaceString = sp01.getFontFaceString();
			int fontSize = sp01.getFontSize();
			System.out.println(String.format(" fontFace=%1$s, fontSize=%2$d, guessCharset=%3$s", fontFaceString, fontSize, sp01.getFontFaceCharsetGuess().name()));
		    }
		}
	    }
	    return;
	}
	if (args.length >= 2 && args[0].equals("pages")) {
	    java.util.logging.Logger.getLogger(Main.loggerName).setLevel(java.util.logging.Level.INFO);
	    java.io.File inputFile = new File(args[1]);
	    WPass2 wPass2 = new WPass2(inputFile);
	    org.debian.paulliu.darnwdl.PageListGenerator pageListGenerator = new org.debian.paulliu.darnwdl.PageListGenerator (wPass2);
	    java.util.ArrayList <org.debian.paulliu.darnwdl.Page> pageList = pageListGenerator.getPageList();
	    for (org.debian.paulliu.darnwdl.Page i : pageList) {
		System.out.println(String.format("%1$d %2$d", i.getStartIndex(), i.getEndIndex()));
	    }
	    return;
	}
	if (args.length >= 1 && args[0].equals("font")) {
	    java.util.logging.Logger.getLogger(Main.loggerName).setLevel(java.util.logging.Level.INFO);
	    org.debian.paulliu.darnwdl.FontReplaceTable frt = org.debian.paulliu.darnwdl.FontReplaceTable.getInstance();
	    for (String fontName : frt.getAvailableFonts()) {
		System.out.println(fontName);
	    }
	    return;
	}
	java.util.logging.Logger.getLogger(Main.loggerName).setLevel(java.util.logging.Level.SEVERE);
	for (int i=0; args!=null && i<args.length; i++) {
	    if (args[i].equals("debug")) {
		java.util.logging.Logger.getLogger(Main.loggerName).setLevel(java.util.logging.Level.INFO);
	    }
	}
	for (int i=0; args != null && i<args.length; i++) {
	    if (args[i].toUpperCase().endsWith(".WDLO") || args[i].toUpperCase().endsWith(".WDL")) {
		myMain.wdlFile = new java.io.File(args[i]);
	    }
	}
	myMain.begin();
    }
}
