/*
    Copyright (C) 2017  Ying-Chun Liu (PaulLiu) <paulliu@debian.org>

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

public class IO {
    public IO() {
    }

    public static int readInt16(java.io.RandomAccessFile inputFile) {
	int ret=0;
	int b;
	int factor = 1;
	java.util.logging.Logger logger = null;

	logger = java.util.logging.Logger.getLogger(Main.loggerName);

	for (int i=0; i<2; i++) {
	    try {
		b = inputFile.read();
	    } catch (Exception e) {
		logger.severe("Failed to read Int16");
		return -1;
	    }
	    ret = ret + b * factor;
	    factor = factor * 256;
	}
	return ret;
    }

    public static int readInt16(java.io.InputStream inputFile) {
	int ret=0;
	int b;
	int factor = 1;
	java.util.logging.Logger logger = null;

	logger = java.util.logging.Logger.getLogger(Main.loggerName);

	for (int i=0; i<2; i++) {
	    try {
		b = inputFile.read();
	    } catch (Exception e) {
		logger.severe("Failed to read Int16");
		return -1;
	    }
	    ret = ret + b * factor;
	    factor = factor * 256;
	}
	return ret;
    }
    
    public static long readInt32(java.io.RandomAccessFile inputFile) {
	long ret=0;
	long b;
	long factor = 1;
	java.util.logging.Logger logger = null;

	logger = java.util.logging.Logger.getLogger(Main.loggerName);

	for (int i=0; i<4; i++) {
	    try {
		b = inputFile.read();
	    } catch (Exception e) {
		logger.severe("Failed to read Int32");
		return -1;
	    }
	    ret = ret + b * factor;
	    factor = factor * 256;
	}
	return ret;
    }

    public static long readInt32(java.io.InputStream inputFile) {
	long ret=0;
	long b;
	long factor = 1;
	java.util.logging.Logger logger = null;

	logger = java.util.logging.Logger.getLogger(Main.loggerName);

	for (int i=0; i<4; i++) {
	    try {
		b = inputFile.read();
	    } catch (Exception e) {
		logger.severe("Failed to read Int32");
		return -1;
	    }
	    ret = ret + b * factor;
	    factor = factor * 256;
	}
	return ret;
    }

}
