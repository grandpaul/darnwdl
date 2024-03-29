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

public class WPass2 {
    private java.io.File inputFile;
    private java.io.RandomAccessFile inputFileStream;
    private java.util.logging.Logger logger = null;
    private java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index> indexList;
    private int firstSPTagIndex = -1;

    private long readInt32() {
	return IO.readInt32(inputFileStream);
    }

    private int readInt16() {
	return IO.readInt16(inputFileStream);
    }
    
    private int readSignedInt16() {
	int ret=0;
	ret = readInt16();
	if (ret > 32767) {
	    ret = ret - 65536;
	}
	return ret;
    }

    private boolean openInputFile() {
	inputFileStream = null;
	try {
	    inputFileStream = new java.io.RandomAccessFile(inputFile, "r");
	} catch (Exception e) {
	    logger.severe(String.format("Failed to open input file %1$s",inputFile.getName()));
	    inputFileStream = null;
	}
	if (inputFileStream == null) {
	    return false;
	}
	return true;
    }

    private boolean parseIndex() {
	try {
	    inputFileStream.seek(0);
	} catch (java.io.IOException e) {
	    return false;
	}

	/* variable that stores attribute structures */
	int lastFT = -1;
	int lastTC = -1;
	int lastBC = -1;
	int lastPN = -1;
	int lastBH = -1;
	int lastUF = -1;
	int lastCR = -1;

	/* handle special tags index reference */
	java.util.LinkedList<Integer> needSpecialStructure = new java.util.LinkedList<Integer> ();
	
	byte[] tagBuf = new byte[2];
	try {
	    while (inputFileStream.getFilePointer() < inputFileStream.length()) {
		String tag = null;
		org.debian.paulliu.darnwdl.wdlo.Index wdloIndex;
		inputFileStream.read(tagBuf);
		if (tagBuf[1] == 0) {
		    wdloIndex = new org.debian.paulliu.darnwdl.wdlo.Index(tag, inputFileStream.getFilePointer()-2, this);
		    wdloIndex.setSpecialByte(tagBuf[0]);
		    tag = "special";
		} else {
		    tag = new String(tagBuf, java.nio.charset.StandardCharsets.UTF_8);
		    wdloIndex = new org.debian.paulliu.darnwdl.wdlo.Index(tag, inputFileStream.getFilePointer()-2, this);
		}
		indexList.add(wdloIndex);

		if (tag.compareTo("FT") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 4);
		    lastFT = indexList.size()-1;
		    needSpecialStructure.add(Integer.valueOf(indexList.size()-1));
		} else if (tag.compareTo("BC") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 4);
		    lastBC = indexList.size()-1;
		} else if (tag.compareTo("BM") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 2);
		} else if (tag.compareTo("BH") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 4);
		    lastBH = indexList.size()-1;
		    needSpecialStructure.add(Integer.valueOf(indexList.size()-1));
		} else if (tag.compareTo("TC") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 4);
		    lastTC = indexList.size()-1;
		} else if (tag.compareTo("PN") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 4);
		    lastPN = indexList.size()-1;
		    needSpecialStructure.add(Integer.valueOf(indexList.size()-1));
		} else if (tag.compareTo("R2") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 2);
		    if (lastCR != -1) {
			wdloIndex.setReference("CR", lastCR);
		    }
		} else if (tag.compareTo("CT") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 2);
		} else if (tag.compareTo("UF") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 6);
		    lastUF = indexList.size()-1;
		    needSpecialStructure.add(Integer.valueOf(indexList.size()-1));
		} else if (tag.compareTo("CR") == 0) {
		    inputFileStream.seek(inputFileStream.getFilePointer() + 8);
		    lastCR = indexList.size()-1;
		} else if (tag.compareTo("ET") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		    if (lastFT != -1) {
			wdloIndex.setReference("FT", lastFT);
		    }
		    if (lastTC != -1) {
			wdloIndex.setReference("TC", lastTC);
		    }
		    if (lastBC != -1) {
			wdloIndex.setReference("BC", lastBC);
		    }
		} else if (tag.compareTo("EU") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		    if (lastFT != -1) {
			wdloIndex.setReference("FT", lastFT);
		    }
		    if (lastTC != -1) {
			wdloIndex.setReference("TC", lastTC);
		    }
		    if (lastBC != -1) {
			wdloIndex.setReference("BC", lastBC);
		    }
		} else if (tag.compareTo("FR") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		    if (lastPN != -1) {
			wdloIndex.setReference("PN", lastPN);
		    }
		    if (lastBH != -1) {
			wdloIndex.setReference("BH", lastBH);
		    }
		    if (lastTC != -1) {
			wdloIndex.setReference("TC", lastTC);
		    }
		    if (lastBC != -1) {
			wdloIndex.setReference("BC", lastBC);
		    }
		} else if (tag.compareTo("CP") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("PL") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		    if (lastPN != -1) {
			wdloIndex.setReference("PN", lastPN);
		    }
		    if (lastTC != -1) {
			wdloIndex.setReference("TC", lastTC);
		    }
		    if (lastBC != -1) {
			wdloIndex.setReference("BC", lastBC);
		    }
		} else if (tag.compareTo("AP") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		    if (lastPN != -1) {
			wdloIndex.setReference("PN", lastPN);
		    }
		    if (lastBH != -1) {
			wdloIndex.setReference("BH", lastBH);
		    }
		    if (lastTC != -1) {
			wdloIndex.setReference("TC", lastTC);
		    }
		    if (lastBC != -1) {
			wdloIndex.setReference("BC", lastBC);
		    }
		} else if (tag.compareTo("AQ") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("RT") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("WP") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("XD") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("SP") == 0) {
		    int unknownShort0 = readInt16();
		    long seeklen = readInt32();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("SD") == 0) {
		    long seeklen = readInt16();
		    if (seeklen != 0) {
			inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		    } else {
			long graphDataLen = 0;
			long graphDataLen2 = 0;
			long unknownInt3 = 0;
			inputFileStream.seek(inputFileStream.getFilePointer() + 40);
			graphDataLen = readInt32();
			inputFileStream.seek(inputFileStream.getFilePointer() + 8);
			unknownInt3 = readInt32();
			inputFileStream.seek(inputFileStream.getFilePointer() + 4);
			if (unknownInt3 != 0) {
			    inputFileStream.seek(inputFileStream.getFilePointer() + 1024);
			}
			graphDataLen2 = readInt32();
			inputFileStream.seek(inputFileStream.getFilePointer() + graphDataLen2);
		    }
		} else if (tag.compareTo("SX") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("EP") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else if (tag.compareTo("UT") == 0) {
		    long seeklen = readInt16();
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		    if (lastUF != -1) {
			wdloIndex.setReference("UF", lastUF);
		    }
		    if (lastTC != -1) {
			wdloIndex.setReference("TC", lastTC);
		    }
		    if (lastBC != -1) {
			wdloIndex.setReference("BC", lastBC);
		    }
		} else if (tagBuf[1] == 0) {
		    if (tagBuf[0] == 6) {
			break;
		    }
		    long seeklen = readInt32();
		    if (firstSPTagIndex == -1) {
			firstSPTagIndex = indexList.size()-1;
		    }
		    inputFileStream.seek(inputFileStream.getFilePointer() + seeklen);
		} else {
		    logger.warning(String.format("Please report bugs: Unknown tag %1$s", tag));
		    inputFileStream.seek(inputFileStream.getFilePointer() - 1);
		}
	    }
	} catch (java.io.IOException e) {
	    logger.severe(String.format("WPass2 error %1$s",e.toString()));
	}
	for (Integer i1 : needSpecialStructure) {
	    int i = i1.intValue();
	    org.debian.paulliu.darnwdl.wdlo.Index index1 = indexList.get(i);
	    org.debian.paulliu.darnwdl.wdlo.SpecialTagReference sr1 = null;
	    if (index1.getTag().compareTo("FT") == 0) {
		sr1 = new org.debian.paulliu.darnwdl.wdlo.FT(index1);
	    } else if (index1.getTag().compareTo("BH") == 0) {
		sr1 = new org.debian.paulliu.darnwdl.wdlo.BH(index1);
	    } else if (index1.getTag().compareTo("PN") == 0) {
		sr1 = new org.debian.paulliu.darnwdl.wdlo.PN(index1);
	    } else if (index1.getTag().compareTo("UF") == 0) {
		sr1 = new org.debian.paulliu.darnwdl.wdlo.UF(index1);
	    }
	    for (int j=firstSPTagIndex; j>=0 && j<indexList.size(); j++) {
		if (indexList.get(j).getTag() != null) {
		    continue;
		}
		if (sr1.getFilePointerToSP() != indexList.get(j).getFilePointer() - indexList.get(firstSPTagIndex).getFilePointer()) {
		    continue;
		}
		if (sr1.getTag().compareTo("FT") == 0 || sr1.getTag().compareTo("UF") == 0) {
		    if (indexList.get(j).getSpecialByte() == 1) {
			index1.setReference("Special01", Integer.valueOf(j));
			logger.info(String.format("Set tag %1$d %2$s reference %3$d Special %4$02x", i, index1.getTag(), j, indexList.get(j).getSpecialByte()));
			break;
		    }
		} else if (sr1.getTag().compareTo("BH") == 0) {
		    if (indexList.get(j).getSpecialByte() == 2) {
			index1.setReference("Special02", Integer.valueOf(j));
			logger.info(String.format("Set tag %1$d %2$s reference %3$d Special %4$02x", i, index1.getTag(), j, indexList.get(j).getSpecialByte()));
			break;
		    }
		} else if (sr1.getTag().compareTo("PN") == 0) {
		    if (indexList.get(j).getSpecialByte() == 3) {
			index1.setReference("Special03", Integer.valueOf(j));
			logger.info(String.format("Set tag %1$d %2$s reference %3$d Special %4$02x", i, index1.getTag(), j, indexList.get(j).getSpecialByte()));
			break;
		    }
		}
	    }
	}
	if (firstSPTagIndex == -1) {
	    firstSPTagIndex = 0;
	}
	return true;
    }

    public java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index> getIndexList() {
	return this.indexList;
    }

    public java.io.RandomAccessFile getInputFile() {
	return inputFileStream;
    }

    public int getFirstSPTagIndex() {
	return firstSPTagIndex;
    }

    public WPass2(java.io.File inputFile) {
	this.inputFile = inputFile;
	this.logger = java.util.logging.Logger.getLogger(Main.loggerName);

	if (! openInputFile()) {
	    return;
	}

	indexList = new java.util.ArrayList <org.debian.paulliu.darnwdl.wdlo.Index>();
	
	if (! parseIndex()) {
	    return;
	}

    }
}
