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
 * BH. Set foreground color.
 * The index reference an SP02 structure.
 */
public class BH extends org.debian.paulliu.darnwdl.wdlo.Index {

    private java.util.logging.Logger logger;
    private long index;

    /**
     * Get BH's index
     */
    public long getIndex() {
	return index;
    }

    private void loadDataFromFile() {
	try {
	    java.io.RandomAccessFile inputFile = getInputFile();
	    byte[] tagBuf = new byte[2];
	    inputFile.seek(getFilePointer());
	    inputFile.read(tagBuf);
	    index = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
	} catch (java.io.IOException e) {
	    logger.severe("java.io.IOException: "+e.toString());
	}
    }
    
    public BH(org.debian.paulliu.darnwdl.wdlo.Index index1) {
	super(index1.getTag(), index1.getFilePointer(), index1.getWPass2());
	super.setSpecialByte(index1.getSpecialByte());
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	loadDataFromFile();
    }
    
}
