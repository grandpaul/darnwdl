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

    /**
     * Init class data here
     */
    private void init() {
	mainWindow = new org.debian.paulliu.darnwdl.ui.MainWindow();
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
			System.out.println(String.format(" index: %1$d", ft.getIndex()));
		    } else if (i.getTag().compareTo("ET") == 0 || i.getTag().compareTo("EU") == 0) {
			org.debian.paulliu.darnwdl.wdlo.ET et = new org.debian.paulliu.darnwdl.wdlo.ET(i);
			java.util.ArrayList<org.debian.paulliu.darnwdl.wdlo.ET.ETData> etDataList = et.getETDataList();
			java.io.StringWriter sw = new java.io.StringWriter();
			for (org.debian.paulliu.darnwdl.wdlo.ET.ETData etData : etDataList) {
			    sw.write("{");
			    sw.write(String.format("x: %1$d, ", etData.x));
			    sw.write(String.format("y: %1$d, ", etData.y));
			    sw.write(String.format("flag1: 0x%1$x, ", etData.flag1));
			    if ((etData.flag1 & 0x02) != 0) {
				sw.write("flag1_0x2_width: [");
				for (Integer width1 : etData.flag1_0x2_width) {
				    sw.write(width1.toString());
				    sw.write(", ");
				}
				sw.write("], ");
			    }
			    sw.write("}, ");
			}
			System.out.println(" etdata: "+sw.toString());
		    }
		} else {
		    System.out.println(String.format("special %1$d: %2$d", i.getSpecialByte(), i.getFilePointer()));
		}
	    }
	    return;
	}
	if (args.length >= 2 && args[0].equals("pages")) {
	    java.util.logging.Logger.getLogger(Main.loggerName).setLevel(java.util.logging.Level.INFO);
	    java.io.File inputFile = new File(args[1]);
	    WPass2 wpass2 = new WPass2(inputFile);
	    java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index> indexList = wpass2.getIndexList();
	    org.debian.paulliu.darnwdl.PageListGenerator pageListGenerator = new org.debian.paulliu.darnwdl.PageListGenerator (indexList);
	    java.util.ArrayList <org.debian.paulliu.darnwdl.Page> pageList = pageListGenerator.getPageList();
	    for (org.debian.paulliu.darnwdl.Page i : pageList) {
		System.out.println(String.format("%1$d %2$d", i.getStartIndex(), i.getEndIndex()));
	    }
	    return;
	}
	java.util.logging.Logger.getLogger(Main.loggerName).setLevel(java.util.logging.Level.SEVERE);
	for (int i=0; args!=null && i<args.length; i++) {
	    if (args[i].equals("debug")) {
		java.util.logging.Logger.getLogger(Main.loggerName).setLevel(java.util.logging.Level.INFO);
	    }
	}
	myMain.begin();
    }
}
