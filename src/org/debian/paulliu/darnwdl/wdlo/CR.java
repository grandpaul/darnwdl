/* 
   WDL decompressor
   Copyright (C) 2005-2007 Ying-Chun Liu (PaulLiu)
   Copyright (C) 2006 Dan Jacobson http://jidanni.org/

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

package org.debian.paulliu.darnwdl.wdlo;

/**
 * This class is describing Clip Region
 */
public class CR extends org.debian.paulliu.darnwdl.wdlo.Index {

    private java.util.logging.Logger logger;

    private int x1;
    private int y1;
    private int x2;
    private int y2;

    public java.awt.Rectangle getRectangle() {
	java.awt.Rectangle ret;
	ret = new java.awt.Rectangle(x1, y1, x2-x1, y2-y1);
	return ret;
    }

    private void loadDataFromFile() {
	try {
	    java.io.RandomAccessFile inputFile = getInputFile();
	    byte[] tagBuf = new byte[2];
	    inputFile.seek(getFilePointer());
	    inputFile.read(tagBuf);
	    x1 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    y1 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    x2 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    y2 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	} catch (java.io.IOException e) {
	    logger.severe("java.io.IOException: "+e.toString());
	}
    }
    
    public CR(org.debian.paulliu.darnwdl.wdlo.Index index1) {
	super(index1);
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	loadDataFromFile();
    }
    
}
