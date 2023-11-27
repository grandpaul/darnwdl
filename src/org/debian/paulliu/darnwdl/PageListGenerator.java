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

    private java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index> indexList;

    public PageListGenerator(java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index> indexList) {
	this.indexList = indexList;
    }

    public java.util.ArrayList <org.debian.paulliu.darnwdl.Page> getPageList() {
	ArrayList<org.debian.paulliu.darnwdl.Page> ret = new ArrayList<org.debian.paulliu.darnwdl.Page>();
	org.debian.paulliu.darnwdl.Page page1 = new org.debian.paulliu.darnwdl.Page(indexList);
	page1.setStartIndex(0);
	for (int i=0; i<indexList.size(); i++) {
	    org.debian.paulliu.darnwdl.wdlo.Index index1;
	    page1.setEndIndex(i);
	    index1 = indexList.get(i);
	    if (index1.getTag().compareTo("R2") == 0) {
		ret.add(page1);
		page1 = new org.debian.paulliu.darnwdl.Page(indexList);
		page1.setStartIndex(i+1);
	    }
	}
	if (page1.getStartIndex() <= page1.getEndIndex()) {
	    ret.add(page1);
	}

	return ret;
    }
}
