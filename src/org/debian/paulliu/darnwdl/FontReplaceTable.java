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
    private java.util.HashMap<String, java.util.LinkedList<String> > replacementsData;
    private static org.debian.paulliu.darnwdl.FontReplaceTable instance = null;

    private void initReplacementsData() {
	replacementsData = new java.util.HashMap<String, java.util.LinkedList<String> >();

	java.util.LinkedList<String> list1;

	/* init replacements of the fonts */
	
	list1 = new java.util.LinkedList<String>();
	list1.add("DFKai-sb");
	list1.add("AR PL UKai TW");
	list1.add("AR PL KaitiM Big5");
	list1.add("Serif");
	replacementsData.put("標楷體", list1);
	replacementsData.put("@標楷體", list1);

	list1 = new java.util.LinkedList<String>();
	list1.add("Mingliu");
	list1.add("AR PL UMing TW");
	list1.add("AR PL Mingti2L Big5");
	list1.add("SansSerif");
	replacementsData.put("細明體", list1);

	list1 = new java.util.LinkedList<String>();
	list1.add("PMingliu");
	list1.add("AR PL UMing TW");
	list1.add("AR PL Mingti2L Big5");
	list1.add("SansSerif");
	replacementsData.put("新細明體", list1);
	
	list1 = new java.util.LinkedList<String>();
	list1.add("SansSerif");
	replacementsData.put("Times New Roman", list1);

	list1 = new java.util.LinkedList<String>();
	list1.add("Serif");
	replacementsData.put("Arial", list1);

	list1 = new java.util.LinkedList<String>();
	list1.add("simsun");
	list1.add("AR PL UMing CN");
	list1.add("AR PL SungtiL GB");
	list1.add("SansSerif");
	replacementsData.put("宋体", list1);

	list1 = new java.util.LinkedList<String>();
	list1.add("simhei");
	list1.add("WenQuanYi Zen Hei");
	list1.add("Kochi Gothic");
	list1.add("Sazanami Gothic");
	list1.add("VL Gothic");
	list1.add("TakaoGothic");
	list1.add("Serif");
	replacementsData.put("黑体", list1);

	list1 = new java.util.LinkedList<String>();
	list1.add("AR PL KaitiM GB");
	list1.add("AR PL UKai CN");
	list1.add("Serif");
	replacementsData.put("楷体_GB2312", list1);

	list1 = new java.util.LinkedList<String>();
	list1.add("AR PL SungtiL GB");
	list1.add("AR PL UMing CN");
	list1.add("SansSerif");
	replacementsData.put("仿宋_GB2312", list1);

	/* remove unsupported replacements */

	for (String key : replacementsData.keySet()) {
	    while (replacementsData.get(key).size() > 0) {
		String data = replacementsData.get(key).peekFirst();
		if (data == null) {
		    break;
		}
		if (! getAvailableFonts().contains(data)) {
		    replacementsData.get(key).pollFirst();
		} else {
		    break;
		}
	    }
	    if (replacementsData.get(key).size() <= 0) {
		replacementsData.remove(key);
	    }
	}
    }

    public String getFontReplacement(String fontName) {
	String retDefault = new String("Serif");
	String ret = retDefault;
	if (getAvailableFonts().contains(fontName)) {
	    return fontName;
	}
	if (!replacementsData.containsKey(fontName)) {
	    return ret;
	}
	ret = replacementsData.get(fontName).peekFirst();
	if (ret == null) {
	    ret = retDefault;
	}
	return ret;
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
	availableFonts = new java.util.LinkedHashSet<String> ();
	
	String[] families = graphicsEnv.getAvailableFontFamilyNames(java.util.Locale.ENGLISH);
	for (String family : families) {
	    if (!availableFonts.contains(family)) {
		availableFonts.add(family);
	    }
	}

	families = graphicsEnv.getAvailableFontFamilyNames();
	for (String family : families) {
	    if (!availableFonts.contains(family)) {
		availableFonts.add(family);
	    }
	}
	initReplacementsData();
    }
}
