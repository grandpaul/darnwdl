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
 * This class is describing Color
 */
public class Special02 extends org.debian.paulliu.darnwdl.wdlo.Index {

    private java.util.logging.Logger logger;
    private int unknownShort01;
    private int r;
    private int g;
    private int b;
    private byte[] unknownData2;
    
    private void loadDataFromFile() {
	try {
	    java.io.RandomAccessFile inputFile = getInputFile();
	    byte[] tagBuf = new byte[2];
	    long seekLen = 0;
	    inputFile.seek(getFilePointer());
	    inputFile.read(tagBuf);
	    seekLen = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
	    unknownShort01 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen -= 2;
	    r = inputFile.read();
	    seekLen -= 1;
	    g = inputFile.read();
	    seekLen -= 1;
	    b = inputFile.read();
	    seekLen -= 1;
	    unknownData2 = new byte[11];
	    inputFile.read(unknownData2);
	    seekLen -= 11;
	} catch (java.io.IOException e) {
	    logger.severe("java.io.IOException: "+e.toString());
	}
    }

    public java.awt.Color getColor() {
	java.awt.Color color = new java.awt.Color(r,g,b);
	return color;
    }

    public Special02(org.debian.paulliu.darnwdl.wdlo.Index index1) {
	super(index1);
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	loadDataFromFile();
    }
    
}
