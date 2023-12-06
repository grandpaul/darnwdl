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
 * This class is for compressed image.
 */
public class SP extends org.debian.paulliu.darnwdl.wdlo.Index {
    private java.util.logging.Logger logger;
    private int unknownShort0;
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
    private byte[] paletteData;

    public java.awt.Rectangle getDestPosition() {
	java.awt.Rectangle ret = new java.awt.Rectangle(destX, destY, destWidth, destHeight);
	
	return ret;
    }

    public java.awt.image.BufferedImage getSrcImage() {
	if (graphData != null && compressionMethod == 1) {
	    java.io.ByteArrayInputStream in1 = new java.io.ByteArrayInputStream(graphData);
	    java.awt.image.BufferedImage img = null;
	    try {
		img = javax.imageio.ImageIO.read(in1);
	    } catch (java.io.IOException e) {
		logger.warning("Decode JPEG error");
	    }

	    /* Flip the image vertically */
	    java.awt.geom.AffineTransform tx = java.awt.geom.AffineTransform.getScaleInstance(1, -1);
	    javax.swing.ImageIcon img1 = new javax.swing.ImageIcon(img);
	    tx.translate(0, -img1.getIconHeight());
	    java.awt.image.AffineTransformOp op = new java.awt.image.AffineTransformOp(tx, java.awt.image.AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	    img = op.filter(img, null);

	    return img;
	}
	return null;
    }    
    
    private void loadDataFromFile() {
	try {
	    java.io.RandomAccessFile inputFile = getInputFile();
	    byte[] tagBuf = new byte[2];
	    long seekLen;
	    inputFile.seek(getFilePointer());
	    inputFile.read(tagBuf);
	    unknownShort0 = org.debian.paulliu.darnwdl.IO.readInt16(inputFile);
	    seekLen = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
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
	    unknownBytes2 = new byte[10];
	    inputFile.read(unknownBytes2);
	    seekLen -= 10;
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
	    if (this.compressionMethod == 1) {
		graphDataLen2 = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
		seekLen -= 4;
	    } else if (this.compressionMethod == 2
		       || this.compressionMethod == 6
		       || this.compressionMethod == 7) {
		int N = 0;
		if (this.colorDepth == 1) {
		    paletteData = new byte[4*2];
		    inputFile.read(paletteData);
		    N = 4*2;
		} else if (this.colorDepth == 8) {
		    paletteData = new byte[4*256];
		    inputFile.read(paletteData);
		    N = 4*256;
		}
		seekLen -= N;
		graphDataLen2 = org.debian.paulliu.darnwdl.IO.readInt32(inputFile);
		seekLen -= 4;
	    }
	    if (this.graphDataLen2 > 0) {
		graphData = new byte[(int)graphDataLen2];
		inputFile.read(graphData);
		seekLen -= graphDataLen2;
	    }
	    if (seekLen != 0) {
		logger.warning(String.format("Warning: Please report bugs: seeklen = %1$d != 0", seekLen));
	    }		
	} catch (java.io.IOException e) {
	    logger.severe("java.io.IOException: "+e.toString());
	}
    }
    
    public SP(org.debian.paulliu.darnwdl.wdlo.Index index1) {
	super(index1);
	this.logger = java.util.logging.Logger.getLogger(org.debian.paulliu.darnwdl.Main.loggerName);
	loadDataFromFile();
    }
}
