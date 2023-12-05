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

public class FontReplaceTable {
    private java.util.HashSet<String> availableFonts;
    private java.util.HashMap<String, java.util.LinkedList<String> > replacements;
    private static org.debian.paulliu.darnwdl.FontReplaceTable instance = null;

    private void initReplacements() {
	replacements = new java.util.HashMap<String, java.util.LinkedList<String> >();

	java.util.LinkedList<String> list1;

	list1 = new java.util.LinkedList<String>();
	list1.add("AR PL UKai TW");
	list1.add("Serif");
	replacements.put("標楷體", list1);
	replacements.put("@標楷體", list1);

	list1 = new java.util.LinkedList<String>();
	list1.add("AR PL UMing TW");
	list1.add("SansSerif");
	replacements.put("新細明體", list1);
	replacements.put("細明體", list1);
	
    }

    public String getFontReplacement(String fontName) {
	if (getAvailableFonts().contains(fontName)) {
	    return fontName;
	}
	if (!replacements.containsKey(fontName)) {
	    return "Serif";
	}
	for (String ret : replacements.get(fontName)) {
	    if (getAvailableFonts().contains(ret)) {
		return ret;
	    }
	}
	return "Serif";
    }

    public java.util.HashSet<String> getAvailableFonts() {
	return availableFonts;
    }

    public static org.debian.paulliu.darnwdl.FontReplaceTable getInstance() {
	if (instance == null) {
	    instance = new org.debian.paulliu.darnwdl.FontReplaceTable();
	}
	return instance;
    }
    
    public FontReplaceTable() {
	java.awt.GraphicsEnvironment graphicsEnv = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
	availableFonts = new java.util.HashSet<String> ();
	
	String[] families = graphicsEnv.getAvailableFontFamilyNames(java.util.Locale.ENGLISH);
	for (String family : families) {
	    availableFonts.add(family);
	}
	initReplacements();
    }
}
