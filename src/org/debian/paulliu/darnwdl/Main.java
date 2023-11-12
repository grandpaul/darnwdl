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
		} else {
		    System.out.println(String.format("special %1$d: %2$d", i.getSpecialByte(), i.getFilePointer()));
		}
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
