/* 
   WDL decompressed data parser
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

package org.debian.paulliu.darnwdl;

public class WPass2 {
    private java.io.File inputFile;
    private java.io.RandomAccessFile inputFileStream;
    private java.util.logging.Logger logger = null;
    private java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index> indexList;

    private long readInt32() {
	long ret=0;
	long b;
	long factor = 1;

	for (int i=0; i<4; i++) {
	    try {
		b = inputFileStream.read();
	    } catch (Exception e) {
		logger.severe("Failed to read Int32");
		return -1;
	    }
	    ret = ret + b * factor;
	    factor = factor * 256;
	}
	return ret;
    }

    private int readInt16() {
	int ret=0;
	int b;
	int factor = 1;

	for (int i=0; i<2; i++) {
	    try {
		b = inputFileStream.read();
	    } catch (Exception e) {
		logger.severe("Failed to read Int16");
		return -1;
	    }
	    ret = ret + b * factor;
	    factor = factor * 256;
	}
	return ret;
    }
    
    private int readSignedInt16() {
	int ret=0;
	ret = readInt16();
	if (ret > 32767) {
	    ret = ret - 65536;
	}
	return ret;
    }

    private boolean openInputFile() {
	inputFileStream = null;
	try {
	    inputFileStream = new java.io.RandomAccessFile(inputFile, "r");
	} catch (Exception e) {
	    logger.severe(String.format("Failed to open input file %1$s",inputFile.getName()));
	    inputFileStream = null;
	}
	if (inputFileStream == null) {
	    return false;
	}
	return true;
    }

    private boolean parseIndex() {
	try {
	    inputFileStream.seek(0);
	} catch (java.io.IOException e) {
	    return false;
	}

	byte[] tagBuf = new byte[2];
	try {
	    while (inputFileStream.getFilePointer() < inputFileStream.length()) {
		String tag = null;
		org.debian.paulliu.darnwdl.wdlo.Index wdloIndex;
		inputFileStream.read(tagBuf);
		if (tagBuf[1] == 0) {
		    wdloIndex = new org.debian.paulliu.darnwdl.wdlo.Index(tag, inputFileStream.getFilePointer()-2);
		    wdloIndex.setSpecialByte(tagBuf[0]);
		    tag = "special";
		} else {
		    tag = new String(tagBuf, java.nio.charset.StandardCharsets.UTF_8);
		    wdloIndex = new org.debian.paulliu.darnwdl.wdlo.Index(tag, inputFileStream.getFilePointer()-2);
		}
		indexList.add(wdloIndex);

		if (tag.compareTo("FT") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 4);
		} else if (tag.compareTo("BC") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 4);
		} else if (tag.compareTo("BM") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 2);
		} else if (tag.compareTo("BH") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 4);
		} else if (tag.compareTo("TC") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 4);
		} else if (tag.compareTo("PN") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 4);
		} else if (tag.compareTo("R2") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 2);
		} else if (tag.compareTo("CT") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 2);
		} else if (tag.compareTo("UF") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 6);
		} else if (tag.compareTo("CR") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 8);
		} else if (tag.compareTo("ET") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("EU") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("FR") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("CP") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("PL") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("AP") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("AQ") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("RT") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("WP") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("XD") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("SP") == 0) {
		    long seeklen = readInt16();
		    seeklen = readInt32();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("SD") == 0) {
		    long seeklen = readInt16();
		    if (seeklen != 0) {
			inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		    } else {
			long graphDataLen = 0;
			long graphDataLen2 = 0;
			long unknownInt3 = 0;
			inputFileStream.seek(inputFileStream.getFilePointer() + 40);
			graphDataLen = readInt32();
			inputFileStream.seek(inputFileStream.getFilePointer() + 8);
			unknownInt3 = readInt32();
			inputFileStream.seek(inputFileStream.getFilePointer() + 4);
			if (unknownInt3 != 0) {
			    inputFileStream.seek(inputFileStream.getFilePointer() + 1024);
			}
			graphDataLen2 = readInt32();
			inputFileStream.seek(inputFileStream.getFilePointer() + graphDataLen2);
		    }
		} else if (tag.compareTo("SX") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("EP") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("UT") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tagBuf[1] == 0) {
		    long seeklen = readInt32();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else {
		    logger.warning(String.format("Please report bugs: Unknown tag %1$s", tag));
		    inputFileStream.seek(inputFileStream.getFilePointer() - 1);
		}
	    }
	} catch (java.io.IOException e) {
	}
	return true;
    }

    public java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index> getIndexList() {
	return this.indexList;
    }

    public WPass2(java.io.File inputFile) {
	this.inputFile = inputFile;
	this.logger = java.util.logging.Logger.getLogger(Main.loggerName);

	if (! openInputFile()) {
	    return;
	}

	indexList = new java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index>();
	
	if (! parseIndex()) {
	    return;
	}

    }
}
