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
 * PN. Set foreground color and line width and type.
 * The index reference an SP03 structure.
 */
public class PN extends org.debian.paulliu.darnwdl.wdlo.SpecialTagReference {

    private java.util.logging.Logger logger;

    private void loadDataFromFile() {
	try {
	    java.io.RandomAccessFile inputFile = getInputFile();
	    byte[] tagBuf = new byte[2];
	    inputFile.seek(getFilePointer());
	    inputFile.read(tagBuf);
	    super.setFilePointerToSP(org.debian.paulliu.darnwdl.IO.readInt32(inputFile));
	} catch (java.io.IOException e) {
	    logger.severe("java.io.IOException: "+e.toString());
	}
    }
    
    public PN(org.debian.paulliu.darnwdl.wdlo.Index index1) {
	super(index1);
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	loadDataFromFile();
    }
    
}
