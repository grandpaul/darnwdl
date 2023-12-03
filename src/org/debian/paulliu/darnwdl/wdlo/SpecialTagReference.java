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
 * This class is describing structure that need to reference a special
 * structure.
 * The file pointer reference an Special ?? structure.
 */
public class SpecialTagReference extends org.debian.paulliu.darnwdl.wdlo.Index {

    private java.util.logging.Logger logger;
    private long filePointerToSP;

    /**
     * Get file pointer that reference to a Special Structure
     */
    public long getFilePointerToSP() {
	return filePointerToSP;
    }

    protected void setFilePointerToSP(long filePointerToSP) {
	this.filePointerToSP = filePointerToSP;
    }
    
    public SpecialTagReference(org.debian.paulliu.darnwdl.wdlo.Index index1) {
	super(index1);
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
    }
    
}
