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

package org.debian.paulliu.darnwdl.ui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.applet.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

public class DrawPanel extends JPanel {
    private java.awt.Image image1;
    
    public DrawPanel() {
	super();
	this.setPreferredSize(new Dimension(1024,768));
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
	if (image1 != null) {
	    ImageIcon image1icon = new ImageIcon(image1);
	    this.setPreferredSize(new Dimension(image1icon.getIconWidth(),image1icon.getIconHeight()));
	    g.drawImage(image1, 0, 0, java.awt.Color.WHITE, null);
	}
    }

    public void drawImage(java.awt.Image img) {
	this.image1 = img;
	this.repaint();
    }

    public void clearImage() {
	image1 = null;
	this.setPreferredSize(new Dimension(1024,768));
	this.repaint();
    }
    
}
