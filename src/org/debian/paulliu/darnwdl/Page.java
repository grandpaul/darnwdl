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
    private double renderFactor = 1.0/14.0;
    private int startIndex;
    private int endIndex;
    private int firstSPTagIndex;
    private org.debian.paulliu.darnwdl.PageListGenerator pageListGenerator;

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

    public void setFirstSPTagIndex(int firstSPTagIndex) {
	this.firstSPTagIndex = firstSPTagIndex;
    }
    public int getFirstSPTagIndex() {
	return firstSPTagIndex;
    }

    public java.awt.Image render() {
	java.util.ArrayList<org.debian.paulliu.darnwdl.wdlo.Index> indexList = pageListGenerator.getWPass2().getIndexList();
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(5000, 5000, java.awt.image.BufferedImage.TYPE_INT_RGB);
	// TODO: rendering page to Image

	for (int i=startIndex; i<=endIndex && i<indexList.size(); i++) {
	    org.debian.paulliu.darnwdl.wdlo.Index index1 = indexList.get(i);
	    if (index1.getTag() == null) {
		continue;
	    }
	    if (index1.getTag().compareTo("ET") == 0 || index1.getTag().compareTo("EU") == 0) {
		org.debian.paulliu.darnwdl.wdlo.ET et = new org.debian.paulliu.darnwdl.wdlo.ET(index1);
		
	    }
	    
	}
	return null;
    }

    public Page(org.debian.paulliu.darnwdl.PageListGenerator pageListGenerator) {
	this.pageListGenerator = pageListGenerator;
	this.startIndex = 0;
	this.endIndex = 0;
	this.firstSPTagIndex = -1;
    }

    
}
