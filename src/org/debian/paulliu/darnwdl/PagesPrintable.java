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

public class PagesPrintable implements java.awt.print.Printable {
    private org.debian.paulliu.darnwdl.PageListGenerator pagelistGenerator;
    private java.util.ArrayList <org.debian.paulliu.darnwdl.Page> pageList;

    public PagesPrintable (org.debian.paulliu.darnwdl.PageListGenerator pagelistGenerator) {
	this.pagelistGenerator = pagelistGenerator;
	this.pageList = pagelistGenerator.getPageList();
    }

    public int print(java.awt.Graphics graphics, java.awt.print.PageFormat pageFormat, int pageIndex) {
	if (!(0 <= pageIndex && pageIndex < this.pageList.size())) {
	    return java.awt.print.Printable.NO_SUCH_PAGE;
	}
	org.debian.paulliu.darnwdl.Page page1 = pageList.get(pageIndex);
	java.awt.Image img = page1.render();
	graphics.drawImage(img, 0, 0, (int)pageFormat.getWidth(), (int)pageFormat.getHeight(), null);
	return java.awt.print.Printable.PAGE_EXISTS;
    }

    public void printAll() {
	javax.print.DocFlavor flavor = javax.print.DocFlavor.SERVICE_FORMATTED.PRINTABLE;
	javax.print.attribute.PrintRequestAttributeSet aset = new javax.print.attribute.HashPrintRequestAttributeSet();
	aset.add(javax.print.attribute.standard.MediaSizeName.ISO_A4);
	javax.print.PrintService[] pss = javax.print.PrintServiceLookup.lookupPrintServices(flavor, aset);
	for (javax.print.PrintService ps1 : pss) {
	    System.out.println(ps1.getName());
	}
	javax.print.Doc myDoc1 = new javax.print.SimpleDoc(this, flavor, null);
	if (pss.length > 0) {
	    javax.print.DocPrintJob job = pss[0].createPrintJob();
	    try {
		job.print(myDoc1, aset);
	    } catch (javax.print.PrintException pe) {
	    }
	}
    }
}
