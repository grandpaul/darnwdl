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

package org.debian.paulliu.darnwdl.wdlo.etdata;

public class ETData {
    private java.util.logging.Logger logger;

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
    private org.debian.paulliu.darnwdl.wdlo.ET et;

    public ETData(org.debian.paulliu.darnwdl.wdlo.ET et) {
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	this.et = et;
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

    private java.nio.charset.Charset guessEncoding() {
	java.nio.charset.Charset ret = java.nio.charset.Charset.forName("big5");
	int ftI = et.getReference("FT");
	if (ftI < 0) {
	    return ret;
	}
	org.debian.paulliu.darnwdl.wdlo.Index ftIndex;
	ftIndex = et.getWPass2().getIndexList().get(ftI);
	org.debian.paulliu.darnwdl.wdlo.FT ft = new org.debian.paulliu.darnwdl.wdlo.FT(ftIndex);
	int SP01I = ft.getReference("Special01");
	if (SP01I < 0) {
	    return ret;
	}
	org.debian.paulliu.darnwdl.wdlo.Index SP01Index;
	SP01Index = ft.getWPass2().getIndexList().get(SP01I);
	org.debian.paulliu.darnwdl.wdlo.Special01 sp01 = new org.debian.paulliu.darnwdl.wdlo.Special01(SP01Index);
	java.nio.charset.Charset guessCharset = sp01.getFontFaceCharsetGuess();
	if (guessCharset != null) {
	    ret = guessCharset;
	}
	return ret;
    }

    public String getString() {
	return getString(guessEncoding());
    }

    public String getString(java.nio.charset.Charset charSet) {
	if (this.string_S != null) {
	    return this.string_S;
	}

	java.util.ArrayList<Integer> width = new java.util.ArrayList<Integer> ();
	if (charSet == null) {
	    return null;
	}

	java.io.StringWriter sw = new java.io.StringWriter();
	
	for (int i=0; i<this.stringLen; i++) {
	    if (i < flag1_0x2_width.size()) {
		Integer width1 = flag1_0x2_width.get(i);
		width.add(width1);
	    }
	    if ((Byte.toUnsignedInt(this.string[i]) & 0x80) != 0) {
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
	}
	this.string_S = sw.toString();
	this.string_Width = width;
	return this.string_S;
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
	
	return getString(charSet);
    }

    public java.util.ArrayList<Integer> getWidth() {
	return this.string_Width;
    }
}
