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

public class StockImage {
    private static org.debian.paulliu.darnwdl.ui.StockImage instance = null;

    public javax.swing.ImageIcon getGoLast() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(30, 30, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(new java.awt.Color(255,255,255,0));
	graphics2D.clearRect(0,0,30,30);
	graphics2D.setColor(java.awt.Color.BLACK);
	graphics2D.drawLine(25,5,25,25);
	graphics2D.drawLine(10,5,25,15);
	graphics2D.drawLine(10,25,25,15);
	graphics2D.drawLine(5,5,20,15);
	graphics2D.drawLine(5,25,20,15);
	graphics2D.dispose();
	return new javax.swing.ImageIcon(ret);
    }

    public javax.swing.ImageIcon getGoForward() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(30, 30, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(new java.awt.Color(255,255,255,0));
	graphics2D.clearRect(0,0,30,30);
	graphics2D.setColor(java.awt.Color.BLACK);
	graphics2D.drawLine(10,5,25,15);
	graphics2D.drawLine(10,25,25,15);
	graphics2D.dispose();
	return new javax.swing.ImageIcon(ret);
    }

    public javax.swing.ImageIcon getGoBack() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(30, 30, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(new java.awt.Color(255,255,255,0));
	graphics2D.clearRect(0,0,30,30);
	graphics2D.setColor(java.awt.Color.BLACK);
	graphics2D.drawLine(20,5,5,15);
	graphics2D.drawLine(20,25,5,15);
	graphics2D.dispose();
	return new javax.swing.ImageIcon(ret);
    }
    
    public javax.swing.ImageIcon getGoFirst() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(30, 30, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(new java.awt.Color(255,255,255,0));
	graphics2D.clearRect(0,0,30,30);
	graphics2D.setColor(java.awt.Color.BLACK);
	graphics2D.drawLine(5,5,5,25);
	graphics2D.drawLine(20,5,5,15);
	graphics2D.drawLine(20,25,5,15);
	graphics2D.drawLine(25,5,10,15);
	graphics2D.drawLine(25,25,10,15);
	graphics2D.dispose();
	return new javax.swing.ImageIcon(ret);
    }

    public javax.swing.ImageIcon getZoomIn() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(30, 30, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(new java.awt.Color(255,255,255,0));
	graphics2D.clearRect(0,0,30,30);
	graphics2D.setColor(java.awt.Color.BLACK);
	graphics2D.drawRect(5,20,5,5);
	graphics2D.drawLine(15,15,20,10);
	graphics2D.drawLine(20,10,20,12);
	graphics2D.drawLine(20,10,18,10);
	java.awt.Stroke dashed = new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT, java.awt.BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
	graphics2D.setStroke(dashed);
	graphics2D.drawRect(5,5,20,20);
	
	graphics2D.dispose();
	return new javax.swing.ImageIcon(ret);
    }

    public javax.swing.ImageIcon getZoomOut() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(30, 30, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(new java.awt.Color(255,255,255,0));
	graphics2D.clearRect(0,0,30,30);
	graphics2D.setColor(java.awt.Color.BLACK);
	graphics2D.drawRect(5,5,20,20);
	graphics2D.drawLine(15,15,20,10);
	graphics2D.drawLine(15,15,15,13);
	graphics2D.drawLine(15,15,17,15);
	java.awt.Stroke dashed = new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT, java.awt.BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
	graphics2D.setStroke(dashed);
	graphics2D.drawRect(5,20,5,5);
	
	graphics2D.dispose();
	return new javax.swing.ImageIcon(ret);
    }


    public javax.swing.ImageIcon getFitW() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(20, 20, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(new java.awt.Color(255,255,255,0));
	graphics2D.clearRect(0,0,20,20);
	graphics2D.setColor(java.awt.Color.BLACK);
	graphics2D.drawRect(5,5,10,10);
	graphics2D.drawLine(5,10,15,10);
	graphics2D.drawLine(5,10,7,8);
	graphics2D.drawLine(5,10,7,12);
	graphics2D.drawLine(15,10,13,8);
	graphics2D.drawLine(15,10,13,12);
	
	graphics2D.dispose();
	return new javax.swing.ImageIcon(ret);
    }

    public javax.swing.ImageIcon getFitH() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(20, 20, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(new java.awt.Color(255,255,255,0));
	graphics2D.clearRect(0,0,20,20);
	graphics2D.setColor(java.awt.Color.BLACK);
	graphics2D.drawRect(5,5,10,10);
	graphics2D.drawLine(10,5,10,15);
	graphics2D.drawLine(10,5,8,7);
	graphics2D.drawLine(10,5,12,7);
	graphics2D.drawLine(10,15,8,13);
	graphics2D.drawLine(10,15,12,13);
	
	graphics2D.dispose();
	return new javax.swing.ImageIcon(ret);
    }

    public javax.swing.ImageIcon getFitP() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(20, 20, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(new java.awt.Color(255,255,255,0));
	graphics2D.clearRect(0,0,20,20);
	graphics2D.setColor(java.awt.Color.BLACK);
	graphics2D.drawRect(5,5,10,10);
	graphics2D.drawLine(10,5,10,15);
	graphics2D.drawLine(10,5,8,7);
	graphics2D.drawLine(10,5,12,7);
	graphics2D.drawLine(10,15,8,13);
	graphics2D.drawLine(10,15,12,13);
	graphics2D.drawLine(5,10,15,10);
	graphics2D.drawLine(5,10,7,8);
	graphics2D.drawLine(5,10,7,12);
	graphics2D.drawLine(15,10,13,8);
	graphics2D.drawLine(15,10,13,12);
	
	graphics2D.dispose();
	return new javax.swing.ImageIcon(ret);
    }

    public javax.swing.ImageIcon getPrint() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(24, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(new java.awt.Color(255,255,255,0));
	graphics2D.clearRect(0,0,24,24);
	graphics2D.setColor(java.awt.Color.BLACK);
	graphics2D.drawRect(4,9,16,9);
	graphics2D.drawRect(6,3,12,6);
	graphics2D.fillRect(8,15,8,3);
	graphics2D.drawRect(9,18,6,3);
	graphics2D.drawLine(8,5,16,5);
	graphics2D.drawLine(8,7,16,7);
	
	graphics2D.dispose();
	return new javax.swing.ImageIcon(ret);
    }

    public javax.swing.ImageIcon getQuit() {
	java.awt.image.BufferedImage ret = new java.awt.image.BufferedImage(22, 22, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	java.awt.Graphics2D graphics2D = ret.createGraphics();
	graphics2D.setBackground(new java.awt.Color(255,255,255,0));
	graphics2D.clearRect(0,0,22,22);
	graphics2D.setColor(java.awt.Color.RED);
	graphics2D.drawRect(4,4,14,14);
	graphics2D.fillRect(4,4,14,4);
	graphics2D.drawLine(8,11,14,11);
	
	graphics2D.dispose();
	return new javax.swing.ImageIcon(ret);
    }
    
    
    
    public static org.debian.paulliu.darnwdl.ui.StockImage getInstance() {
        if (instance == null) {
            instance = new org.debian.paulliu.darnwdl.ui.StockImage();
        }
        return instance;
    }

    public StockImage() {
    }
    
}
