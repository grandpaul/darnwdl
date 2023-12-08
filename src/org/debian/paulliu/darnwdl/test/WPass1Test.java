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

package org.debian.paulliu.darnwdl.test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class WPass1Test {

    @Test
    void testWPass1_RFIDDoc() {
	java.io.File inputFile = new java.io.File("testdata/RFID-930628.wdl");
	java.io.File outputFile = null;
	try {
	    java.nio.file.Path wdloFilePath = java.nio.file.Files.createTempFile("darnwdl", ".wdlo");
	    outputFile = wdloFilePath.toFile();
	} catch (java.io.IOException e) {
	    outputFile = null;
	}
	assertNotNull(outputFile);
	org.debian.paulliu.darnwdl.WPass1 wpass1 = new org.debian.paulliu.darnwdl.WPass1(inputFile, outputFile);
    }
}
