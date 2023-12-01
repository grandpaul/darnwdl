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

public class ET extends org.debian.paulliu.darnwdl.wdlo.Index {

    public class ETData {
	public int x;
	public int y;
	public int flag1;
	public int stringLen;
	public byte[] string;
	public int flag1_0x1_x1;
	public int flag1_0x1_y1;
	public int flag1_0x1_x2;
	public int flag1_0x1_y2;
	public java.util.ArrayList<Integer> flag1_0x2_width;

	public ETData() {
	    x = 0;
	    y = 0;
	    flag1 = 0;
	    stringLen = 0;
	    string = null;
	    flag1_0x1_x1 = 0;
	    flag1_0x1_y1 = 0;
	    flag1_0x1_x2 = 0;
	    flag1_0x1_y2 = 0;
	    flag1_0x2_width = new java.util.ArrayList<Integer>();
	}
    }

    private java.util.logging.Logger logger;
    private java.util.ArrayList<ETData> etDataList;

    public java.util.ArrayList<ETData> getETDataList() {
	return this.etDataList;
    }
    
    private void loadDataFromFile() {
	etDataList = new java.util.ArrayList<ETData>();
	try {
	    java.io.RandomAccessFile inputFile = getInputFile();
	    byte[] tagBuf = new byte[2];
	    long seekLen;
	    inputFile.seek(getFilePointer());
	    inputFile.read(tagBuf);
	    seekLen = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    while (seekLen > 0) {
		ETData etData = new ETData();
		etData.x = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		seekLen -= 2;
		etData.y = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		seekLen -= 2;
		etData.stringLen = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		seekLen -= 2;
		etData.flag1 = inputFile.read();
		seekLen -= 1;
		etData.string = new byte[etData.stringLen];
		inputFile.read(etData.string);
		seekLen -= etData.stringLen;
		if ((etData.flag1 & 0x01) != 0) {
		    etData.flag1_0x1_x1 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		    seekLen -= 2;
		    etData.flag1_0x1_y1 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		    seekLen -= 2;
		    etData.flag1_0x1_x2 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		    seekLen -= 2;
		    etData.flag1_0x1_y2 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
		    seekLen -= 2;
		}
		if ((etData.flag1 & 0x02) != 0) {
		    for (int i=0; i<etData.stringLen; i++) {
			int data1 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
			seekLen -= 2;
			etData.flag1_0x2_width.add(Integer.valueOf(data1));
		    }
		}
		if (etData.flag1 > 3) {
		    logger.warning(String.format("Warning: Please report bugs: unknown ET flag01: %1$d", etData.flag1));
		}
		etDataList.add(etData);
	    }
	    if (seekLen != 0) {
		logger.warning(String.format("Warning: Please report bugs: seeklen = %1$d != 0", seekLen));
	    }		
	} catch (java.io.IOException e) {
	    logger.severe("java.io.IOException: "+e.toString());
	}
    }
    
    public ET(org.debian.paulliu.darnwdl.wdlo.Index index1) {
	super(index1.getTag(), index1.getFilePointer(), index1.getInputFile());
	super.setSpecialByte(index1.getSpecialByte());
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	loadDataFromFile();
    }
}
