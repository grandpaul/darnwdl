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

public class FT extends org.debian.paulliu.darnwdl.wdlo.Index {

    private long index;
    private java.io.RandomAccessFile inputFile;

    public long getIndex() {
	return index;
    }

    public void loadDataFromFile() {
	try {
	    inputFile.seek(getFilePointer());
	} catch (java.io.IOException e) {
	}
    }
    
    public FT(org.debian.paulliu.darnwdl.wdlo.Index index1, java.io.RandomAccessFile inputFile) {
	super(index1.getTag(), index1.getFilePointer());
	this.inputFile = inputFile;
	super.setSpecialByte(index1.getSpecialByte());
    }
    
}

    
