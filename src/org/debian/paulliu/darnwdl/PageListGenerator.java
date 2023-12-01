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

import java.util.*;

public class PageListGenerator {

    private org.debian.paulliu.darnwdl.WPass2 wPass2;

    public PageListGenerator(org.debian.paulliu.darnwdl.WPass2 wPass2) {
	this.wPass2 = wPass2;
    }

    public org.debian.paulliu.darnwdl.WPass2 getWPass2() {
	return wPass2;
    }

    public java.util.ArrayList <org.debian.paulliu.darnwdl.Page> getPageList() {
	int firstSPTagIndex=-1;
	ArrayList<org.debian.paulliu.darnwdl.Page> ret = new ArrayList<org.debian.paulliu.darnwdl.Page>();
	java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index> indexList = wPass2.getIndexList();
	for (int i=0; i<wPass2.getIndexList().size(); ) {
	    org.debian.paulliu.darnwdl.wdlo.Index index1;
	    org.debian.paulliu.darnwdl.Page page1;
	    int j;
	    index1 = indexList.get(i);
	    if (firstSPTagIndex == -1 && index1.getTag() == null) {
		firstSPTagIndex = i;
	    }
	    if (index1.getTag() == null || index1.getTag().compareTo("R2") != 0) {
		i++;
		continue;
	    }
	    for (j=i+1; j<indexList.size(); j++) {
		if (indexList.get(j).getTag() == null || indexList.get(j).getTag().compareTo("R2") == 0) {
		    break;
		}
	    }
	    page1 = new org.debian.paulliu.darnwdl.Page(this);
	    page1.setStartIndex(i+1);
	    page1.setEndIndex(j-1);
	    ret.add(page1);
	    i=j;
	}
	if (firstSPTagIndex != -1) {
	    for (org.debian.paulliu.darnwdl.Page p1 : ret) {
		p1.setFirstSPTagIndex(firstSPTagIndex);
	    }
	}

	return ret;
    }
}
