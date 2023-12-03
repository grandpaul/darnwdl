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
 * This class is for UTF-16LE encoded text
 */
public class UT extends org.debian.paulliu.darnwdl.wdlo.Index {

    private java.nio.charset.Charset charSet;

    public class UTData {
	public int x;
	public int y;
	public int flag1;
	public String string;
	public int flag1_0x1_x1;
	public int flag1_0x1_y1;
	public int flag1_0x1_x2;
	public int flag1_0x1_y2;
	public java.util.ArrayList<Integer> flag1_0x2_width;

	public UTData() {
	    x = 0;
	    y = 0;
	    flag1 = 0;
	    string = null;
	    flag1_0x1_x1 = 0;
	    flag1_0x1_y1 = 0;
	    flag1_0x1_x2 = 0;
	    flag1_0x1_y2 = 0;
	    flag1_0x2_width = new java.util.ArrayList<Integer>();
	}

	/**
	 * get String that stored in this UTData structure
	 *
	 * @return String encoded by encoding
	 */
	public String getString() {
	    if (string == null) {
		return new String();
	    }
	    return string;
	}
    }

    private java.util.logging.Logger logger;
    private java.util.ArrayList<UTData> utDataList;

    public java.util.ArrayList<UTData> getUTDataList() {
	return this.utDataList;
    }
    
    private void loadDataFromFile() {
	utDataList = new java.util.ArrayList<UTData>();
	try {
	    java.io.RandomAccessFile inputFile = getInputFile();
	    byte[] tagBuf = new byte[2];
	    long seekLen;
	    inputFile.seek(getFilePointer());
	    inputFile.read(tagBuf);
	    seekLen = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    while (seekLen > 0) {
		UTData utData = new UTData();
		int stringLen = 0;
		byte[] utf16data;
		utData.x = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		seekLen -= 2;
		utData.y = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		seekLen -= 2;
		stringLen = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		seekLen -= 2;
		utData.flag1 = inputFile.read();
		seekLen -= 1;
		utf16data = new byte[stringLen * 2];
		inputFile.read(utf16data);
		utData.string = new String(utf16data, charSet);
		seekLen -= (stringLen * 2);
		if ((utData.flag1 & 0x01) != 0) {
		    utData.flag1_0x1_x1 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		    seekLen -= 2;
		    utData.flag1_0x1_y1 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		    seekLen -= 2;
		    utData.flag1_0x1_x2 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		    seekLen -= 2;
		    utData.flag1_0x1_y2 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		    seekLen -= 2;
		}
		if ((utData.flag1 & 0x02) != 0) {
		    for (int i=0; i<utData.string.length(); i++) {
			int data1 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
			seekLen -= 2;
			utData.flag1_0x2_width.add(Integer.valueOf(data1));
		    }
		}
		if (utData.flag1 > 3) {
		    logger.warning(String.format("Warning: Please report bugs: unknown ET flag01: %1$d", utData.flag1));
		}
		utDataList.add(utData);
	    }
	    if (seekLen != 0) {
		logger.warning(String.format("Warning: Please report bugs: seeklen = %1$d != 0", seekLen));
	    }		
	} catch (java.io.IOException e) {
	    logger.severe("java.io.IOException: "+e.toString());
	}
    }
    
    public UT(org.debian.paulliu.darnwdl.wdlo.Index index1) {
	super(index1);
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	charSet = java.nio.charset.StandardCharsets.UTF_16LE;
	loadDataFromFile();
    }
}
