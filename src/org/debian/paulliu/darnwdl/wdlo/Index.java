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

public class Index {
    private String tag;
    private long filePointer;
    private byte specialByte;
    private org.debian.paulliu.darnwdl.WPass2 wPass2;
    private java.util.HashMap < String , Integer > referenceMap;

    public String getTag() {
	return tag;
    }

    public long getFilePointer() {
	return filePointer;
    }

    public void setSpecialByte(byte specialByte) {
	this.specialByte = specialByte;
    }
    public byte getSpecialByte() {
	return specialByte;
    }

    public org.debian.paulliu.darnwdl.WPass2 getWPass2() {
	return this.wPass2;
    }

    public java.io.RandomAccessFile getInputFile() {
	return wPass2.getInputFile();
    }

    public void setReference(String tag, int index) {
	referenceMap.put(tag, Integer.valueOf(index));
    }

    public int getReference(String tag) {
	if (referenceMap == null) {
	    return -1;
	}
	if (!referenceMap.containsKey(tag)) {
	    return -1;
	}
	return referenceMap.get(tag).intValue();
    }
    
    public Index (String tag, long filePointer, org.debian.paulliu.darnwdl.WPass2 wPass2) {
	this.tag = tag;
	this.filePointer = filePointer;
	this.wPass2 = wPass2;
	this.setSpecialByte((byte)0);
	this.referenceMap = new java.util.HashMap < String , Integer > ();
    }

    public Index (org.debian.paulliu.darnwdl.wdlo.Index index1) {
	this.tag = index1.getTag();
	this.filePointer = index1.getFilePointer();
	this.wPass2 = index1.getWPass2();
	this.setSpecialByte (index1.getSpecialByte());
	this.referenceMap = index1.referenceMap;
    }
}
