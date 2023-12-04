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

    public int getFirstSPTagIndex() {
	return this.pageListGenerator.getWPass2().getFirstSPTagIndex();
    }

    public java.awt.Image render() {
	java.util.ArrayList<org.debian.paulliu.darnwdl.wdlo.Index> indexList = pageListGenerator.getWPass2().getIndexList();
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(5000, 5000, java.awt.image.BufferedImage.TYPE_INT_RGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(java.awt.Color.WHITE);
	graphics2D.clearRect(0,0,5000,5000);
	for (int i=startIndex; i<=endIndex && i<indexList.size(); i++) {
	    org.debian.paulliu.darnwdl.wdlo.Index index1 = indexList.get(i);
	    if (index1.getTag() == null) {
		continue;
	    }
	    if (index1.getTag().compareTo("ET") == 0 || index1.getTag().compareTo("EU") == 0) {
		org.debian.paulliu.darnwdl.wdlo.ET et = new org.debian.paulliu.darnwdl.wdlo.ET(index1);
		int indexFT = et.getReference("FT");
		String fontName = "Serif";
		int fontSize = 16;
		if (indexFT >= 0) {
		    org.debian.paulliu.darnwdl.wdlo.FT ft = new org.debian.paulliu.darnwdl.wdlo.FT(indexList.get(indexFT));
		    int indexSP01 = ft.getReference("Special01");
		    if (indexSP01 >= 0) {
			org.debian.paulliu.darnwdl.wdlo.Special01 sp01 = new org.debian.paulliu.darnwdl.wdlo.Special01(indexList.get(indexSP01));
			fontName = sp01.getFontFaceString();
			fontSize = Math.abs(sp01.getFontSize());
			if (fontSize <= 0) {
			    fontSize = 16;
			}
		    }
		}
		int indexTC = et.getReference("TC");
		for (org.debian.paulliu.darnwdl.wdlo.etdata.ETData etData : et.getETDataList()) {
		    int currentX = 0;
		    String str = etData.getString();
		    if ((etData.flag1 & 0x2) != 0) {
			for (int j=0; j<str.length(); j++) {
			    String char1 = str.substring(j, j+1);
			    java.text.AttributedString char2 = new java.text.AttributedString(char1);
			    java.awt.Font font1 = new java.awt.Font(fontName, java.awt.Font.PLAIN, ((int)(fontSize * renderFactor)));
			    char2.addAttribute(java.awt.font.TextAttribute.FONT, font1);
			    if (indexTC >= 0) {
				org.debian.paulliu.darnwdl.wdlo.TC tc = new org.debian.paulliu.darnwdl.wdlo.TC(indexList.get(indexTC));
				char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, tc.getColor());
			    } else {
				char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, java.awt.Color.BLACK);
			    }
			    graphics2D.drawString(char2.getIterator(), (float)((currentX + etData.x) * renderFactor), (float)(etData.y * renderFactor));
			    currentX += etData.getWidth().get(j).intValue();
			}
		    } else {
			java.awt.Font font1 = new java.awt.Font(fontName, java.awt.Font.PLAIN, (int)(fontSize * renderFactor));
			java.text.AttributedString char2 = new java.text.AttributedString(str);
			char2.addAttribute(java.awt.font.TextAttribute.FONT, font1);
			if (indexTC >= 0) {
			    org.debian.paulliu.darnwdl.wdlo.TC tc = new org.debian.paulliu.darnwdl.wdlo.TC(indexList.get(indexTC));
			    char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, tc.getColor());
			} else {
			    char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, java.awt.Color.BLACK);
			}
			graphics2D.drawString(char2.getIterator(), (float)(etData.x * renderFactor), (float)(etData.y * renderFactor));
		    }
		}
	    }
	}
	org.debian.paulliu.darnwdl.wdlo.Index indexEnd = indexList.get(endIndex);
	int indexCR = indexEnd.getReference("CR");
	if (indexCR >= 0) {
	    org.debian.paulliu.darnwdl.wdlo.CR cr = new org.debian.paulliu.darnwdl.wdlo.CR(indexList.get(indexCR));
	    java.awt.Rectangle clipR = cr.getRectangle();
	    ret = ret.getSubimage((int)(clipR.getX()*renderFactor), (int)(clipR.getY()*renderFactor), (int)(clipR.getWidth()*renderFactor), (int)(clipR.getHeight()*renderFactor));
	}
	return ret;
    }

    public Page(org.debian.paulliu.darnwdl.PageListGenerator pageListGenerator) {
	this.pageListGenerator = pageListGenerator;
	this.startIndex = 0;
	this.endIndex = 0;
    }

    
}
