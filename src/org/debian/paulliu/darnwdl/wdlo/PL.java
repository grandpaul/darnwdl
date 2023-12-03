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
 * This class is describing Path.
 */
public class PL extends org.debian.paulliu.darnwdl.wdlo.Index {
    private java.util.logging.Logger logger;
    private java.util.ArrayList < java.awt.geom.Path2D.Float > lines;

    private void loadDataFromFile() {
	try {
	    java.io.RandomAccessFile inputFile = getInputFile();
	    byte[] tagBuf = new byte[2];
	    long seekLen;
	    inputFile.seek(getFilePointer());
	    inputFile.read(tagBuf);
	    seekLen = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    while (seekLen > 0) {
		int N = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		seekLen -= 2;
		java.awt.geom.Path2D.Float line1 = new java.awt.geom.Path2D.Float();
		for (int i=0; i<N; i++) {
		    int x;
		    int y;
		    x = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		    seekLen -= 2;
		    y = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		    seekLen -= 2;
		    if (i == 0) {
			line1.moveTo((float) x, (float) y);
		    } else {
			line1.lineTo((float) x, (float) y);
		    }
		}
		lines.add(line1);
	    }
	    if (seekLen != 0) {
		logger.warning(String.format("Warning: Please report bugs: seeklen = %1$d != 0", seekLen));
	    }		
	} catch (java.io.IOException e) {
	    logger.severe("java.io.IOException: "+e.toString());
	}
    }
    
    public PL(org.debian.paulliu.darnwdl.wdlo.Index index1) {
	super(index1);
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	lines = new java.util.ArrayList < java.awt.geom.Path2D.Float >();
	loadDataFromFile();
    }

}
