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
 * This class is for UTF-16LE encoded text
 */
public class SD extends org.debian.paulliu.darnwdl.wdlo.Index {
    private java.util.logging.Logger logger;
    private int destX;
    private int destY;
    private int destWidth;
    private int destHeight;
    private byte[] unknownBytes1;
    private int srcWidthShort;
    private int srcHeightShort;
    private byte[] unknownBytes2;
    private int srcWidth;
    private int srcHeight;
    private int unknownShort1;
    private int colorDepth;
    private int unknownShort2;
    private int compressionMethod;
    private long graphDataLen;
    private long unknownInt1;
    private long unknownInt2;
    private long unknownInt3;
    private long unknownInt4;
    private long graphDataLen2;
    private byte[] graphData;

    public java.awt.Rectangle getDestPosition() {
	java.awt.Rectangle ret = new java.awt.Rectangle(destX, destY, destWidth, destHeight);
	
	return ret;
    }

    public java.awt.image.BufferedImage getSrcImage() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(srcWidth, srcHeight, java.awt.image.BufferedImage.TYPE_INT_RGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(java.awt.Color.WHITE);
        graphics2D.clearRect(0, 0, srcWidth, srcHeight);
	for (int x=0; x<srcWidth; x++) {
	    for (int y=0; y<srcHeight; y++) {
		int r = Byte.toUnsignedInt(graphData[(y*srcWidth+x)*3]);
		int g = Byte.toUnsignedInt(graphData[(y*srcWidth+x)*3+1]);
		int b = Byte.toUnsignedInt(graphData[(y*srcWidth+x)*3+2]);
		java.awt.Color color1 = new java.awt.Color(r,g,b);
		graphics2D.setColor(color1);
		graphics2D.drawLine(x,y,x,y);
	    }
	}
	graphics2D.dispose();
	
	return ret;
    }
    
    private void loadDataFromFile() {
	try {
	    java.io.RandomAccessFile inputFile = getInputFile();
	    byte[] tagBuf = new byte[2];
	    long seekLen;
	    long seekLenOriginal;
	    byte[] paletteData = null;
	    int N;
	    inputFile.seek(getFilePointer());
	    inputFile.read(tagBuf);
	    seekLenOriginal = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen = seekLenOriginal;
	    destX = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen -= 2;
	    destY = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen -= 2;
	    destWidth = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen -= 2;
	    destHeight = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen -= 2;
	    unknownBytes1 = new byte[4];
	    inputFile.read(unknownBytes1);
	    seekLen -= 4;
	    srcWidthShort = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen -= 2;
	    srcHeightShort = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen -= 2;
	    unknownBytes2 = new byte[8];
	    inputFile.read(unknownBytes2);
	    seekLen -= 8;
	    srcWidth = (int)org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
	    seekLen -= 4;
	    srcHeight = (int)org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
	    seekLen -= 4;
	    unknownShort1 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen -= 2;
	    colorDepth = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen -= 2;
	    unknownShort2 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen -= 2;
	    compressionMethod = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen -= 2;
	    graphDataLen = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
	    seekLen -= 4;
	    unknownInt1 = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
	    seekLen -= 4;
	    unknownInt2 = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
	    seekLen -= 4;
	    unknownInt3 = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
	    seekLen -= 4;
	    unknownInt4 = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
	    seekLen -= 4;
	    if (unknownInt3 != 0) {
		paletteData = new byte[1024];
		inputFile.read(paletteData);
		seekLen -= 1024;
	    }
	    graphDataLen2 = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
	    seekLen -= 4;
	    N = srcWidth * srcHeight * 3;
	    graphData = new byte[N];
	    if (paletteData == null) {
		for (int i = srcHeight-1; i>=0; i--) {
		    for (int j=0; j<srcWidth; j++) {
			int r;
			int g;
			int b;
			b = inputFile.read();
			seekLen --;
			g = inputFile.read();
			seekLen --;
			r = inputFile.read();
			seekLen --;
			graphData[(i*srcWidth+j)*3] = (byte)r;
			graphData[(i*srcWidth+j)*3+1] = (byte)g;
			graphData[(i*srcWidth+j)*3+2] = (byte)b;
		    }
		    if ( srcWidth*3%4 != 0) {
			for (int j=0; j<4-(srcWidth*3%4); j++) {
			    inputFile.read();
			    seekLen --;
			}
		    }
		}
	    } else {
		for (int i = srcHeight-1; i>=0; i--) {
		    for (int j=0; j<srcWidth; j++) {
			int cIndex;
			cIndex = inputFile.read();
			seekLen --;
			graphData[(i*srcWidth+j)*3] = paletteData[cIndex * 4 + 2];
			graphData[(i*srcWidth+j)*3+1] = paletteData[cIndex * 4 + 1];
			graphData[(i*srcWidth+j)*3+2] = paletteData[cIndex * 4];
		    }
		    if ( srcWidth*3%4 != 0) {
			for (int j=0; j<4-(srcWidth%4); j++) {
			    inputFile.read();
			    seekLen --;
			}
		    }
		}
	    }
	    if (seekLenOriginal != 0 && seekLen != 0) {
		logger.warning(String.format("Warning: Please report bugs: seeklen = %1$d != 0", seekLen));
	    }		
	} catch (java.io.IOException e) {
	    logger.severe("java.io.IOException: "+e.toString());
	}
    }
    
    public SD(org.debian.paulliu.darnwdl.wdlo.Index index1) {
	super(index1);
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	loadDataFromFile();
    }
}
