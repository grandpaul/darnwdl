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
 * This class is for Big5 or GB2312 encoded text
 */
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

	private String string_S;
	private java.util.ArrayList<Integer> string_Width;

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
	    string_S = null;
	    string_Width = null;
	}

	/**
	 * get String that stored in this ETData structure
	 *
	 * @param encoding encoding of the ETData
	 * @return String encoded by encoding
	 */
	public String getString(String encoding) {
	    if (this.string_S != null) {
		return this.string_S;
	    }

	    java.util.ArrayList<Integer> width = new java.util.ArrayList<Integer> ();
	    java.nio.charset.Charset charSet = null;
	    try {
		charSet = java.nio.charset.Charset.forName(encoding);
	    } catch (java.nio.charset.IllegalCharsetNameException e) {
		logger.severe("conver string error: "+e.toString());
	    } catch (java.lang.IllegalArgumentException e) {
		logger.severe("conver string error: "+e.toString());
	    }
	    if (charSet == null) {
		return null;
	    }

	    java.io.StringWriter sw = new java.io.StringWriter();
	    
	    for (int i=0; i<this.stringLen; i++) {
		Integer width1 = flag1_0x2_width.get(i);
		if ((this.string[i] & 0x80) != 0) {
		    byte[] buf1 = null;
		    if (i+1 < this.stringLen) {
			buf1 = new byte[2];
			buf1[0] = this.string[i];
			buf1[1] = this.string[i+1];
			i++;
		    } else {
			buf1 = new byte[1];
			buf1[0] = this.string[i];
		    }
		    String s1 = new String(buf1, charSet);
		    if (s1.length() != 1) {
			logger.warning("String encoding conversion error because the length should be 1 here");
		    }
		    sw.write(s1);
		} else {
		    byte[] buf1 = new byte[1];
		    buf1[0] = this.string[i];
		    String s1 = new String(buf1, charSet);
		    if (s1.length() != 1) {
			logger.warning("String encoding conversion error because the length should be 1 here");
		    }
		    sw.write(s1);
		}
		width.add(width1);
	    }
	    this.string_S = sw.toString();
	    this.string_Width = width;
	    return this.string_S;
	}

	public java.util.ArrayList<Integer> getWidth() {
	    return this.string_Width;
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
	super(index1);
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	loadDataFromFile();
    }
}
