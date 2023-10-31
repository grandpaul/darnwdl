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

package org.debian.paulliu.darnwdl.jni;

public class DynamiteJNI {
    private static DynamiteJNI instance;
    
    static {
	System.loadLibrary("darnwdldynamite");
    }

    public static DynamiteJNI getInstance() {
	if (instance == null) {
	    instance = new DynamiteJNI();
	}
	return instance;
    }
    
    public static void main(String[] args) {
	String hello="Hello world";
	DynamiteJNI dynamiteJNI = null;
	dynamiteJNI = new DynamiteJNI();
	dynamiteJNI.explode(hello.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
    
    public native byte[] explode(byte[] input);
}
