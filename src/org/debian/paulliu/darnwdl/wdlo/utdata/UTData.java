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

package org.debian.paulliu.darnwdl.wdlo.utdata;

public class UTData {
    public int x;
    public int y;
    public int flag1;
    public String string;
    public int flag1_0x1_x1;
    public int flag1_0x1_y1;
    public int flag1_0x1_x2;
    public int flag1_0x1_y2;
    public java.util.ArrayList<Integer> flag1_0x2_width;

    private org.debian.paulliu.darnwdl.wdlo.UT ut;

    public UTData(org.debian.paulliu.darnwdl.wdlo.UT ut) {
	this.ut = ut;
	x = 0;
	y = 0;
	flag1 = 0;
	string = null;
	flag1_0x1_x1 = 0;
	flag1_0x1_y1 = 0;
	flag1_0x1_x2 = 0;
	flag1_0x1_y2 = 0;
	flag1_0x2_width = new java.util.ArrayList<Integer>();
    }

    /**
     * get String that stored in this UTData structure
     *
     * @return String encoded by encoding
     */
    public String getString() {
	if (string == null) {
	    return new String();
	}
	return string;
    }

    public java.util.ArrayList<Integer> getWidth() {
	return this.flag1_0x2_width;
    }
}


