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
 * This class is describing FONT
 */
public class Special01 extends org.debian.paulliu.darnwdl.wdlo.Index {

    private java.util.logging.Logger logger;
    private byte[] unknownData;
    private byte[] fontFace;
    private int fontSize;
    private java.nio.charset.Charset fontFaceCharsetGuess;
    private byte[][] gb2312Fonts = {
	{ (byte)0xd3, (byte)0xd7, (byte)0xd4, (byte)0xb2, 0 },
	{ (byte)0xcb, (byte)0xce, (byte)0xcc, (byte)0xe5, 0 },
	{ (byte)0xba, (byte)0xda, (byte)0xcc, (byte)0xe5, 0 },
	{ (byte)0xc1, (byte)0xa5, (byte)0xca, (byte)0xe9, 0 }
    };

    private int readInt16() {
	java.io.RandomAccessFile inputFile = getInputFile();
        return org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
    }
    
    private int readSignedInt16() {
        int ret=0;
        ret = readInt16();
        if (ret > 32767) {
            ret = ret - 65536;
        }
        return ret;
    }

    private int strlen(byte[] b) {
	for (int i=0; i<b.length; i++) {
	    if (b[i] == 0) {
		return i;
	    }
	}
	return b.length;
    }

    private int strlen_UTF16(byte[] b) {
	for (int i=0; i+1<b.length; i+=2) {
	    if (b[i] == 0 && b[i+1] == 0) {
		return i;
	    }
	}
	return b.length;
    }

    private int strcmpABN(byte[] a, int aIndex, byte[] b, int bIndex, int N) {
	for (int i=0; i<N; i++) {
	    if (i+aIndex >= a.length) {
		if (i+bIndex < b.length) {
		    return -1;
		} else {
		    return 0;
		}
	    }
	    if (i+bIndex >= b.length) {
		if (i+aIndex < a.length) {
		    return 1;
		} else {
		    return 0;
		}
	    }
	    if (Byte.toUnsignedInt(a[i+aIndex]) > Byte.toUnsignedInt(b[i+bIndex])) {
		return 1;
	    } else if (Byte.toUnsignedInt(a[i+aIndex]) < Byte.toUnsignedInt(b[i+bIndex])) {
		return -1;
	    }
	}
	return 0;
    }
    
    private void loadDataFromFile() {
	try {
	    java.io.RandomAccessFile inputFile = getInputFile();
	    byte[] tagBuf = new byte[2];
	    long seekLen = 0;
	    inputFile.seek(getFilePointer());
	    inputFile.read(tagBuf);
	    seekLen = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
	    fontSize = readSignedInt16();
	    seekLen -= 2;
	    unknownData = new byte[16];
	    inputFile.read(unknownData);
	    seekLen -= 16;
	    if (seekLen == 64) {
		fontFace = new byte[64];
		inputFile.read(fontFace);
		seekLen -= 64;
		fontFaceCharsetGuess = java.nio.charset.StandardCharsets.UTF_16LE;
	    } else if (seekLen == 32) {
		int fontFaceLen = 0;
		String gb2312Str = "_GB2312";
		fontFace = new byte[32];
		inputFile.read(fontFace);
		seekLen -= 32;
		fontFaceLen = strlen(fontFace);
		if (fontFaceLen >= 7 && strcmpABN(fontFace, fontFaceLen - 7, gb2312Str.getBytes(java.nio.charset.StandardCharsets.UTF_8), 0, 7) == 0) {
		    fontFaceCharsetGuess = java.nio.charset.Charset.forName("gb2312");
		} else {
		    fontFaceCharsetGuess = null;
		    for (int i=0; i<gb2312Fonts.length; i++) {
			if (strcmpABN(fontFace, 0, gb2312Fonts[i], 0, Math.min(fontFace.length, gb2312Fonts[i].length)) == 0) {
			    fontFaceCharsetGuess = java.nio.charset.Charset.forName("gb18030");
			    break;
			}
		    }
		    if (fontFaceCharsetGuess == null) {
			fontFaceCharsetGuess = java.nio.charset.Charset.forName("big5");
		    }
		}
	    } else {
		if (seekLen <= 32) {
		    fontFace = new byte[(int)seekLen];
		    inputFile.read(fontFace);
		    seekLen = 0;
		} else {
		    fontFace = new byte[32];
		    inputFile.read(fontFace);
		    seekLen -= 32;
		}
	    }
	    if (seekLen != 0) {
		logger.warning(String.format("Please report bugs: seekLen = %1$d != 0", seekLen));
	    }
	} catch (java.io.IOException e) {
	    logger.severe("java.io.IOException: "+e.toString());
	}
    }

    public java.nio.charset.Charset getFontFaceCharsetGuess() {
	return fontFaceCharsetGuess;
    }

    public int getFontSize() {
	return fontSize;
    }

    public byte[] getFontFace() {
	return fontFace;
    }

    public String getFontFaceString() {
	String ret;
	int len = strlen(getFontFace());
	if (getFontFaceCharsetGuess() == null) {
	    ret = new String(getFontFace(), 0, len, java.nio.charset.Charset.forName("big5"));
	    return ret;
	}

	if (getFontFaceCharsetGuess() == java.nio.charset.StandardCharsets.UTF_16LE) {
	    len = strlen_UTF16(getFontFace());
	    ret = new String(getFontFace(), 0, len, getFontFaceCharsetGuess());
	    return ret;
	}
	ret = new String(getFontFace(), 0, len, getFontFaceCharsetGuess());

	return ret;
    }

    public Special01(org.debian.paulliu.darnwdl.wdlo.Index index1) {
	super(index1);
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	fontFaceCharsetGuess = null;
	loadDataFromFile();
    }
    
}
