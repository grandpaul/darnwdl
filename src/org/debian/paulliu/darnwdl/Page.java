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

public class Page {
    private int startIndex;
    private int endIndex;
    private java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index> indexList;

    public void setStartIndex(int startIndex) {
	this.startIndex = startIndex;
    }
    public int getStartIndex() {
	return startIndex;
    }

    public void setEndIndex(int endIndex) {
	this.endIndex = endIndex;
    }
    public int getEndIndex() {
	return endIndex;
    }

    public Page(java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index> indexList) {
	this.indexList = indexList;
	this.startIndex = 0;
	this.endIndex = 0;
    }

    
}
