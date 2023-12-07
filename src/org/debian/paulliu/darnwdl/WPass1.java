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

package org.debian.paulliu.darnwdl;

public class WPass1 {
    private java.io.File inputFile;
    private java.io.FileInputStream inputFileStream;
    private java.io.File outputFile;
    private java.io.FileOutputStream outputFileStream;
    private byte[] magicNumber;
    private int version;
    private int current;
    private int size;
    private java.util.logging.Logger logger = null;
    private java.util.LinkedHashMap<String,java.util.ArrayList<Long> > properties;

    private long readInt32() {
	return IO.readInt32(inputFileStream);
    }

    private int readInt16() {
	return IO.readInt16(inputFileStream);
    }

    private String readString(int len) {
	String ret = null;
	byte[] b = null;
	try {
	    b = inputFileStream.readNBytes(len);
	} catch (Exception e) {
	    logger.severe(String.format("Cannot read string for length %1$d", len));
	    return null;
	}
	ret = new String(b, java.nio.charset.StandardCharsets.UTF_8);

	return ret;
    }
    
    private boolean openInputFile() {
	inputFileStream = null;
	try {
	    inputFileStream = new java.io.FileInputStream(inputFile);
	} catch (Exception e) {
	    logger.severe(String.format("Failed to open input file %1$s",inputFile.getName()));
	    inputFileStream = null;
	}
	if (inputFileStream == null) {
	    return false;
	}
	return true;
    }
    
    private boolean decodeMagicNumber() {
	byte[] header = null;
	byte[] magicNumber = { 'D', 'D', 'o', 'c' };

	try {
	    header = inputFileStream.readNBytes(6);
	} catch (Exception e) {
	    logger.severe("Cannot read input file magic number");
	    return false;
	}

	if (header != null && header.length == 6) {
	    for (int i=0; i<magicNumber.length; i++) {
		if (header[i] != magicNumber[i]) {
		    logger.severe("Input file magic number doesn't match");
		    return false;
		}
	    }
	}

	this.magicNumber = header;
	this.version = readInt16();
	if (this.version < 0) {
	    return false;
	}
	
	return true;
    }

    private boolean decodeHeader() {
	String[] headerProperties = {"font", "indx", "name", "pape", "stru", "thum"};
	java.util.HashSet<String> headerPropertiesSet = new java.util.HashSet<String>();
	for (int i=0; i<headerProperties.length; i++) {
	    headerPropertiesSet.add(headerProperties[i]);
	}

	properties = new java.util.LinkedHashMap<String,java.util.ArrayList<Long> >();
	for (int i=0; i<headerProperties.length; i++) {
	    String prop = readString(4);
	    java.util.ArrayList<Long> data = new java.util.ArrayList<Long>();
	    for (int j=0; j<3; j++) {
		long value = readInt32();
		if (value < 0) {
		    logger.severe(String.format("Cannot read property %1$s's value",prop));
		    return false;
		}
		data.add(Long.valueOf(value));
	    }
	    if (headerPropertiesSet.contains(prop)) {
		properties.put(prop, data);
	    }
	}				   
	return true;
    }

    private boolean openOutputFile() {
	outputFileStream = null;
	try {
	    outputFileStream = new java.io.FileOutputStream(outputFile);
	} catch (Exception e) {
	    logger.severe(String.format("Failed to open output file %1$s",outputFile.getName()));
	    outputFileStream = null;
	}
	if (outputFileStream == null) {
	    return false;
	}
	return true;
    }

    private java.util.ArrayList<Long> readDynaPKCP() {
	java.util.ArrayList<Long> ret = null;
	long l;
	long crc32;
	long compressedLen = -1;
	long uncompressedSize = 0;
	String header;
	header = readString(8);
	if (header == null || header.compareTo("DynaPKCP") != 0) {
	    return ret;
	}
	l = readInt32();
	if (l<0) {
	    return ret;
	}
	uncompressedSize = readInt32();
	if (uncompressedSize < 0) {
	    return ret;
	}
	compressedLen = readInt32();
	if (compressedLen < 0) {
	    return ret;
	}
	crc32 = readInt32();
	if (l<0) {
	    return ret;
	}
	ret = new java.util.ArrayList<Long>();
	ret.add(Long.valueOf(compressedLen));
	ret.add(Long.valueOf(uncompressedSize));
	ret.add(Long.valueOf(crc32));
	return ret;
    }

    private boolean decode() {
	
	try {
	    while (inputFileStream.available() > 0) {
		java.util.ArrayList<Long> dataLength = readDynaPKCP();
		long compressedDataLength = dataLength.get(0).longValue();
		long decompressedDataLength = dataLength.get(1).longValue();
		byte[] data = inputFileStream.readNBytes(((int)compressedDataLength));
		byte[] result;
		long currentPos;

		org.debian.paulliu.darnwdl.jni.DynamiteJNI dynamiteJNI = org.debian.paulliu.darnwdl.jni.DynamiteJNI.getInstance();

		result = dynamiteJNI.explode(data);
		outputFileStream.write(result);
		try {
		    currentPos = inputFileStream.getChannel().position();
		} catch (Exception e) {
		    currentPos = 0;
		}
	    }
	} catch (Exception e) {
	    logger.severe(String.format("Decode error: %1$s", e.toString()));
	    return false;
	}

	return true;
    }

    private void skipUnknownDataWithoutUsingName() {
	/* unknown data */
	try {
	    inputFileStream.skipNBytes(50);
	} catch (Exception e) {
	    logger.severe("Cannot skip unknown data");
	    return;
	}
	long forwardLen;
	forwardLen = readInt32();
	if (forwardLen < 0) {
	    logger.severe("Cannot read unknown data forward length");
	    return;
	}
	logger.info(String.format("forwardLen = %1$d", forwardLen));
	forwardLen -= 38;
	try {
	    inputFileStream.skipNBytes(forwardLen);
	} catch (Exception e) {
	    logger.severe("Cannot skip forward data");
	    return;
	}
    }

    private void skipNameData() {
	long endOfName = properties.get("name").get(1).longValue() + properties.get("name").get(2).longValue();
	long currentPos = 0;
	try {
	    currentPos = inputFileStream.getChannel().position();
	} catch (Exception e) {
	    logger.severe("Cannot get current position: " + e.toString());
	    return;
	}	    
	if (currentPos > endOfName) {
	    logger.severe(String.format("Cannot skip Name Data due to we missed it. Current pos: %1$d, End of Name pos: %2$d", currentPos, endOfName));
	    return;
	}
	if (endOfName > currentPos) {
	    try {
		inputFileStream.skipNBytes(endOfName - currentPos);
	    } catch (Exception e) {
		logger.severe("Cannot skip Name Data");
		return;
	    }
	}
    }
    
    public WPass1(java.io.File inputFile, java.io.File outputFile) {
	this.inputFile = inputFile;
	this.outputFile = outputFile;

	this.logger = java.util.logging.Logger.getLogger(Main.loggerName);

	if (! openInputFile()) {
	    return;
	}

	if (! decodeMagicNumber()) {
	    return;
	}

	if (! decodeHeader()) {
	    return;
	}

	if (properties.containsKey("name")) {
	    skipNameData();
	} else {
	    skipUnknownDataWithoutUsingName();
	}

	if (! openOutputFile()) {
	    return;
	}
	logger.info("Start decoding");
	if (! decode()) {
	    return;
	}
	try {
	    outputFileStream.flush();
	} catch (java.io.IOException e) {
	    logger.warning("Cannot flush wdlo file");
	}
	try {
	    outputFileStream.close();
	} catch (java.io.IOException e) {
	    logger.warning("Cannot close wdlo file");
	}
    }
}
