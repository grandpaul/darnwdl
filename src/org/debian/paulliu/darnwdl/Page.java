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
    private double renderFactor = 1.0;
    private int maxDimension = 1500;
    private int startIndex;
    private int endIndex;
    private org.debian.paulliu.darnwdl.PageListGenerator pageListGenerator;
    private java.util.logging.Logger logger = null;

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
	graphics2D.clearRect(0,0,maxDimension,maxDimension);
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
		int indexBC = et.getReference("BC");
		for (org.debian.paulliu.darnwdl.wdlo.etdata.ETData etData : et.getETDataList()) {
		    int currentX = 0;
		    String str = etData.getString();
		    if ((etData.flag1 & 0x2) != 0) {
			for (int j=0; j<str.length(); j++) {
			    String char1 = str.substring(j, j+1);
			    java.text.AttributedString char2 = new java.text.AttributedString(char1);
			    java.awt.Font font1 = new java.awt.Font(org.debian.paulliu.darnwdl.FontReplaceTable.getInstance().getFontReplacement(fontName), java.awt.Font.PLAIN, ((int)(fontSize * renderFactor)));
			    java.awt.font.LineMetrics font1Metrics = font1.getLineMetrics(char1, 0, char1.length(), graphics2D.getFontRenderContext());
			    char2.addAttribute(java.awt.font.TextAttribute.FONT, font1);
			    if (indexTC >= 0) {
				org.debian.paulliu.darnwdl.wdlo.TC tc = new org.debian.paulliu.darnwdl.wdlo.TC(indexList.get(indexTC));
				char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, tc.getColor());
			    } else {
				char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, java.awt.Color.BLACK);
			    }
			    if (indexBC >= 0) {
				org.debian.paulliu.darnwdl.wdlo.BC bc = new org.debian.paulliu.darnwdl.wdlo.BC(indexList.get(indexBC));
				char2.addAttribute(java.awt.font.TextAttribute.BACKGROUND, bc.getColor());
			    } else {
				char2.addAttribute(java.awt.font.TextAttribute.BACKGROUND, java.awt.Color.WHITE);
			    }
			    logger.info(String.format("Draw string %1$s at (%2$f, %3$f)", char1, (float)((currentX + etData.x) * renderFactor), (float)(etData.y * renderFactor)));
			    graphics2D.drawString(char2.getIterator(), (float)((currentX + etData.x) * renderFactor), (float)(etData.y * renderFactor + font1Metrics.getAscent()));
			    currentX += etData.getWidth().get(j).intValue();
			}
		    } else {
			java.awt.Font font1 = new java.awt.Font(org.debian.paulliu.darnwdl.FontReplaceTable.getInstance().getFontReplacement(fontName), java.awt.Font.PLAIN, (int)(fontSize * renderFactor));
			java.text.AttributedString char2 = new java.text.AttributedString(str);
			char2.addAttribute(java.awt.font.TextAttribute.FONT, font1);
			java.awt.font.LineMetrics font1Metrics = font1.getLineMetrics(str, 0, str.length(), graphics2D.getFontRenderContext());
			if (indexTC >= 0) {
			    org.debian.paulliu.darnwdl.wdlo.TC tc = new org.debian.paulliu.darnwdl.wdlo.TC(indexList.get(indexTC));
			    char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, tc.getColor());
			} else {
			    char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, java.awt.Color.BLACK);
			}
			if (indexBC >= 0) {
			    org.debian.paulliu.darnwdl.wdlo.BC bc = new org.debian.paulliu.darnwdl.wdlo.BC(indexList.get(indexBC));
			    char2.addAttribute(java.awt.font.TextAttribute.BACKGROUND, bc.getColor());
			} else {
			    char2.addAttribute(java.awt.font.TextAttribute.BACKGROUND, java.awt.Color.WHITE);
			}
			logger.info(String.format("Draw string %1$s at (%2$f, %3$f)", str, (float)(etData.x * renderFactor), (float)(etData.y * renderFactor)));
			graphics2D.drawString(char2.getIterator(), (float)(etData.x * renderFactor), (float)(etData.y * renderFactor + font1Metrics.getAscent()));
		    }
		}
	    } else if (index1.getTag().compareTo("UT") == 0) {
		org.debian.paulliu.darnwdl.wdlo.UT ut = new org.debian.paulliu.darnwdl.wdlo.UT(index1);
		int indexUF = ut.getReference("UF");
		String fontName = "Serif";
		int fontSize = 16;
		if (indexUF >= 0) {
		    org.debian.paulliu.darnwdl.wdlo.UF uf = new org.debian.paulliu.darnwdl.wdlo.UF(indexList.get(indexUF));
		    int indexSP01 = uf.getReference("Special01");
		    if (indexSP01 >= 0) {
			org.debian.paulliu.darnwdl.wdlo.Special01 sp01 = new org.debian.paulliu.darnwdl.wdlo.Special01(indexList.get(indexSP01));
			fontName = sp01.getFontFaceString();
			fontSize = Math.abs(sp01.getFontSize());
			if (fontSize <= 0) {
			    fontSize = 16;
			}
		    }
		}
		int indexTC = ut.getReference("TC");
		int indexBC = ut.getReference("BC");
		for (org.debian.paulliu.darnwdl.wdlo.utdata.UTData utData : ut.getUTDataList()) {
		    int currentX = 0;
		    String str = utData.getString();
		    if ((utData.flag1 & 0x2) != 0) {
			for (int j=0; j<str.length(); j++) {
			    String char1 = str.substring(j, j+1);
			    java.text.AttributedString char2 = new java.text.AttributedString(char1);
			    java.awt.Font font1 = new java.awt.Font(org.debian.paulliu.darnwdl.FontReplaceTable.getInstance().getFontReplacement(fontName), java.awt.Font.PLAIN, ((int)(fontSize * renderFactor)));
			    java.awt.font.LineMetrics font1Metrics = font1.getLineMetrics(char1, 0, char1.length(), graphics2D.getFontRenderContext());
			    char2.addAttribute(java.awt.font.TextAttribute.FONT, font1);
			    if (indexTC >= 0) {
				org.debian.paulliu.darnwdl.wdlo.TC tc = new org.debian.paulliu.darnwdl.wdlo.TC(indexList.get(indexTC));
				char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, tc.getColor());
			    } else {
				char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, java.awt.Color.BLACK);
			    }
			    if (indexBC >= 0) {
				org.debian.paulliu.darnwdl.wdlo.BC bc = new org.debian.paulliu.darnwdl.wdlo.BC(indexList.get(indexBC));
				char2.addAttribute(java.awt.font.TextAttribute.BACKGROUND, bc.getColor());
			    } else {
				char2.addAttribute(java.awt.font.TextAttribute.BACKGROUND, java.awt.Color.WHITE);
			    }
			    logger.info(String.format("Draw string %1$s at (%2$f, %3$f), fontAscent: %4$f", char1, (float)((currentX + utData.x) * renderFactor), (float)(utData.y * renderFactor), (float)font1Metrics.getAscent()));
			    graphics2D.drawString(char2.getIterator(), (float)((currentX + utData.x) * renderFactor), (float)(utData.y * renderFactor + font1Metrics.getAscent()));
			    currentX += utData.getWidth().get(j).intValue();
			}
		    } else {
			java.awt.Font font1 = new java.awt.Font(org.debian.paulliu.darnwdl.FontReplaceTable.getInstance().getFontReplacement(fontName), java.awt.Font.PLAIN, (int)(fontSize * renderFactor));
			java.text.AttributedString char2 = new java.text.AttributedString(str);
			char2.addAttribute(java.awt.font.TextAttribute.FONT, font1);
			java.awt.font.LineMetrics font1Metrics = font1.getLineMetrics(str, 0, str.length(), graphics2D.getFontRenderContext());
			if (indexTC >= 0) {
			    org.debian.paulliu.darnwdl.wdlo.TC tc = new org.debian.paulliu.darnwdl.wdlo.TC(indexList.get(indexTC));
			    char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, tc.getColor());
			} else {
			    char2.addAttribute(java.awt.font.TextAttribute.FOREGROUND, java.awt.Color.BLACK);
			}
			if (indexBC >= 0) {
			    org.debian.paulliu.darnwdl.wdlo.BC bc = new org.debian.paulliu.darnwdl.wdlo.BC(indexList.get(indexBC));
			    char2.addAttribute(java.awt.font.TextAttribute.BACKGROUND, bc.getColor());
			} else {
			    char2.addAttribute(java.awt.font.TextAttribute.BACKGROUND, java.awt.Color.WHITE);
			}
			logger.info(String.format("Draw string %1$s at (%2$f, %3$f), fontAscent: %4$f", str, (float)((currentX + utData.x) * renderFactor), (float)(utData.y * renderFactor), (float)font1Metrics.getAscent()));
			graphics2D.drawString(char2.getIterator(), (float)(utData.x * renderFactor), (float)(utData.y * renderFactor + font1Metrics.getAscent()));
		    }
		}
	    }
	}
	org.debian.paulliu.darnwdl.wdlo.Index indexEnd = indexList.get(endIndex);
	int indexCR = indexEnd.getReference("CR");
	if (indexCR >= 0) {
	    org.debian.paulliu.darnwdl.wdlo.CR cr = new org.debian.paulliu.darnwdl.wdlo.CR(indexList.get(indexCR));
	    java.awt.Rectangle clipR = cr.getRectangle();
	    logger.info(String.format("Clip Region: x: %1$d, y: %2$d, width: %3$d, height: %4$d", (int)(clipR.getX()*renderFactor), (int)(clipR.getY()*renderFactor), (int)(clipR.getWidth()*renderFactor), (int)(clipR.getHeight()*renderFactor)));
	    ret = ret.getSubimage((int)(clipR.getX()*renderFactor), (int)(clipR.getY()*renderFactor), (int)(clipR.getWidth()*renderFactor), (int)(clipR.getHeight()*renderFactor));
	}
	return ret;
    }

    public Page(org.debian.paulliu.darnwdl.PageListGenerator pageListGenerator) {
	this.logger = java.util.logging.Logger.getLogger(Main.loggerName);
	this.pageListGenerator = pageListGenerator;
	this.startIndex = 0;
	this.endIndex = 0;
	if ((int)this.pageListGenerator.getMaxDimension().getWidth() >= maxDimension) {
	    renderFactor = Math.min(renderFactor, (((double)maxDimension) / (double)(this.pageListGenerator.getMaxDimension().getWidth()+1)));
	}
	if ((int)this.pageListGenerator.getMaxDimension().getHeight() > maxDimension) {
	    renderFactor = Math.min(renderFactor, (((double)maxDimension) / (double)(this.pageListGenerator.getMaxDimension().getHeight()+1)));
	}
    }
}
