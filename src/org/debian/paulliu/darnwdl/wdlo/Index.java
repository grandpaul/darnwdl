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
    
    public Index (String tag, long filePointer) {
	this.tag = tag;
	this.filePointer = filePointer;
    }
}
