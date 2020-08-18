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

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include <unistd.h>

#include <gdk/gdkkeysyms.h>
#include <gtk/gtk.h>

#include "wpass2.h"
#include "support.h"
#include "graphdec.h"
#include "pagerender.h"

/**
 * get SP01 data by index 
 * This function searches the wdlo index to find the matched SP01 and
 * then parsing the data and return.
 * You should use free() to free the returned data after use.
 *
 * @param index the index number of SP?? structure
 * @param wdloFile the input file
 * @param wdloSPI the head of the index of the input file
 * @return the SP01 data
 */
wdloSP01* page2pixmap_getSP01(int index,FILE *wdloFile,wdloIndex *wdloSPI) {
  wdloIndex *i;
  wdloSP01 *SP01=NULL;
  for (i=wdloSPI; i!=NULL; i=i->next) {
    if (i->tag[1]=='\0') {
      if (i->tag[0]=='\x01') {
        SP01 = parse_wdlo_SP01(wdloFile,i);
        if (index == SP01->index) {
          break;
        }
        free(SP01);
        SP01=NULL;
      }
    }
  }
  return SP01;
}

/**
 * get the font size from SP01 structure 
 * It calculate the new font size from the raw SP01 font size
 *
 * @param SP01 the pointer of SP01 structure
 * @return the font size
 */
int page2pixmap_getSP01_size(wdloSP01 *SP01) {
  int ret=0;
  if (SP01 != NULL) {
    ret = SP01->size;
  }
  if (ret < 0) {
    ret = abs(ret);
  }
  if (ret==0) {
    ret = 16;
  } else {
  }
  return ret;
}

/**
 * get the font face from SP01 structure 
 * It calculate the new font face from the raw SP01 font face
 * The returned data should be freed by free() after use
 *
 * @param SP01 the pointer of SP01 structure
 * @return the font face
 */
char* page2pixmap_getSP01_fontface(wdloSP01 *SP01) {
  char* ret=NULL;
  gchar *conv_result=NULL;
  if (SP01 != NULL) {
    if (strcasecmp(SP01->font_face_encoding_guess,"utf16le")==0) {
      conv_result = g_convert(SP01->font_face,wdlpass2_utf16le_strlen(SP01->font_face)*2,"utf-8",SP01->font_face_encoding_guess,NULL,NULL,NULL);
    } else {
      conv_result = g_convert(SP01->font_face,-1,"utf-8",SP01->font_face_encoding_guess,NULL,NULL,NULL);
    }
    if (conv_result == NULL) {
      conv_result = g_convert(SP01->font_face,-1,"utf-8","big5",NULL,NULL,NULL);
    }
    if (conv_result == NULL) {
      conv_result = g_convert(SP01->font_face,-1,"utf-8","gb2312",NULL,NULL,NULL);
      if (conv_result != NULL) { /* font is gb2312 */
        strcpy(SP01->font_face_encoding_guess,"gb2312");
      }
    }
    if (conv_result != NULL) {
      ret = strdup(conv_result);
      g_free(conv_result);
      conv_result=NULL;
    } else {
      ret = NULL;
    }
  }
  if (ret == NULL) {
    if (SP01->font_face[0] != '\0') {
      ret = strdup(SP01->font_face);
    }
  }
  /* replace non-free fonts */
  if (ret != NULL && strcmp(ret,"細明體")==0) { /* replace Mingliu */
    free(ret);
    ret = strdup("細明體,Mingliu,serif");
  } else if (ret != NULL && strcmp(ret,"新細明體")==0) { /* replace PMingliu */
    free(ret);
    ret = strdup("新細明體,PMingliu,serif");
  } else if (ret != NULL && strcmp(ret,"標楷體")==0) { /* replace DFKai-sb */
    free(ret);
    ret = strdup("標楷體,DFKai-sb,sans");
  } else if (ret != NULL && strcmp(ret,"Times New Roman")==0) { /* replace Times New Roman */
    free(ret);
    ret = strdup("Times New Roman,serif");
  } else if (ret != NULL && strcmp(ret,"Arial")==0) { /* replace Arial */
    free(ret);
    ret = strdup("Arial,sans");
  } else if (ret != NULL && strcmp(ret,"宋体")==0) { /* simsun */
    free(ret);
    ret = strdup("宋体,simsun,AR PL ShanHeiSun Uni,serif");
  } else if (ret != NULL && strcmp(ret,"黑体")==0) { /* simhei */
    free(ret);
    ret = strdup("黑体,simhei,Kochi Gothic,Sazanami Gothic,VL Gothic,sans");
  } else if (ret != NULL && strcmp(ret,"楷体_GB2312")==0) { /* simhei */
    free(ret);
    ret = strdup("楷体_GB2312,AR PL KaitiM GB,sans");
  } else if (ret != NULL && strcmp(ret,"仿宋_GB2312")==0) { /* simhei */
    free(ret);
    ret = strdup("仿宋_GB2312,AR PL SungtiL GB,serif");
  }
  return ret;
}

/**
 * get the font face from SP01 structure 
 * It calculate the new font encoding from the raw SP01 font encoding
 * The returned data should be freed by free() after use
 *
 * @param SP01 the pointer of SP01 structure
 * @return the font encoding
 */
char* page2pixmap_getSP01_encoding(wdloSP01 *SP01) {
  if (SP01!=NULL) {
    return strdup(SP01->font_face_encoding_guess);
  } else {
    return strdup("big5");
  }
}

/**
 * get SP02 data by index 
 * This function searches the wdlo index to find the matched SP02 and
 * then parsing the data and return.
 * You should use free() to free the returned data after use.
 *
 * @param index the index number of SP?? structure
 * @param wdloFile the input file
 * @param wdloSPI the head of the index of the input file
 * @return the SP02 data
 */
wdloSP02* page2pixmap_getSP02(int index,FILE *wdloFile,wdloIndex *wdloSPI) {
  wdloIndex *i;
  wdloSP02 *SP02=NULL;
  for (i=wdloSPI; i!=NULL; i=i->next) {
    if (i->tag[1]=='\0') {
      if (i->tag[0]=='\x02') {
        SP02 = parse_wdlo_SP02(wdloFile,i);
        if (index == SP02->index) {
          break;
        }
        free(SP02);
        SP02=NULL;
      }
    }
  }
  return SP02;
}

/**
 * get SP03 data by index 
 * This function searches the wdlo index to find the matched SP03 and
 * then parsing the data and return.
 * You should use free() to free the returned data after use.
 *
 * @param index the index number of SP?? structure
 * @param wdloFile the input file
 * @param wdloSPI the head of the index of the input file
 * @return the SP03 data
 */
wdloSP03* page2pixmap_getSP03(int index,FILE *wdloFile,wdloIndex *wdloSPI) {
  wdloIndex *i;
  wdloSP03 *SP03=NULL;
  for (i=wdloSPI; i!=NULL; i=i->next) {
    if (i->tag[1]=='\0') {
      if (i->tag[0]=='\x03') {
        SP03 = parse_wdlo_SP03(wdloFile,i);
        if (index == SP03->index) {
          break;
        }
        free(SP03);
        SP03=NULL;
      }
    }
  }
  return SP03;
}

/**
 * set GdkGC values by SP03's value
 *
 * @param gc the GdkGC need to be set
 * @param sp03 the SP03 need to be read from
 * @param scaleFactor_numerator the scale factor upper part
 * @param scaleFactor_denominator the scale factor lower part
 */
void page2pixmap_setGdkGCbySP03(GdkGC *gc,wdloSP03 *sp03,int scaleFactor_numerator,int scaleFactor_denominator) {
  GdkColor gc_color;
  int line_width;
  if (gc==NULL || sp03==NULL) {
    return;
  }
  gc_color.red = sp03->r * 65535 / 255;
  gc_color.green = sp03->g * 65535 / 255;
  gc_color.blue = sp03->b  * 65535 / 255;
  gdk_gc_set_rgb_fg_color(gc,&gc_color);
  line_width = sp03->width*scaleFactor_numerator/scaleFactor_denominator;
  if (line_width < 1) {
    line_width = 1;
  }
  if (sp03->style == 2) {
    gdk_gc_set_line_attributes(gc,line_width,GDK_LINE_ON_OFF_DASH,GDK_CAP_NOT_LAST,GDK_JOIN_MITER);
  } else {
    gdk_gc_set_line_attributes(gc,line_width,GDK_LINE_SOLID,GDK_CAP_NOT_LAST,GDK_JOIN_MITER);
  }
}

/**
 * draw a page to a GdkPixmap
 * This function renders a page to a GdkPixmap. It scales the page by 
 * scaleFactor_numerator/scaleFactor_denominator. If you assign wdloSPI, the
 * render process will be faster.
 * The returned data should be free by g_object_unref().
 *
 * @param p the page number need to be render (start from 1)
 * @param wdloFile the file descriptor of the wdlo file
 * @param wdloI the tag index of the wdloFile
 * @param wdloSPI the head of the SP?? of the input file. It can be NULL 
 *        so we calculate it for you (slower). 
 * @param scaleFactor_numerator the scale factor upper part
 * @param scaleFactor_denominator the scale factor lower part
 * @return the SP03 data
 */
GdkPixmap* page2pixmap(int p,FILE *wdloFile,wdloIndex *wdloI,wdloIndex *wdloSPI,int scaleFactor_numerator,int scaleFactor_denominator) { 
  GdkPixmap *pixmap1,*pixmap2;
  GdkGC *pixmap1_gc = NULL,*pixmap2_gc;
  GdkColor gc_color;
  GdkVisual *systemVisual=NULL;
  int cp,j;
  wdloIndex *i;
  wdloFT *FT=NULL;
  wdloUF *UF=NULL;
  wdloCR *CR=NULL;
  wdloTC *TC=NULL;
  wdloBC *BC=NULL;
  wdloET *ET=NULL,*ETi=NULL;
  wdloUT *UT=NULL,*UTi=NULL;
  wdloPL *PL=NULL,*PLi=NULL;
  wdloAP *AP=NULL,*APi=NULL;
  wdloFR *FR=NULL,*FRi=NULL;
  wdloSD *SD=NULL;
  wdloSP *SP=NULL;
  wdloPN *PN=NULL;
  int PN_sp03index = -1;
  wdloBH *BH=NULL;
  int BH_sp02index = -1;
  PangoLayout *textLayout=NULL;
  PangoFontDescription *textLayoutFont=NULL;
  int clipRegion_x1=0;
  int clipRegion_y1=0;
  int clipRegion_x2=5000;
  int clipRegion_y2=5000;
  char encoding[512] = {"big5"};
  GtkImage* resultImage=NULL;

  /* init */
  resultImage = GTK_IMAGE(gtk_image_new());
  gtk_image_clear(resultImage);
  systemVisual = gdk_screen_get_system_visual(gdk_screen_get_default());
  pixmap1 = gdk_pixmap_new(NULL,clipRegion_x2,clipRegion_y2,systemVisual->depth);
  pixmap1_gc = gdk_gc_new(pixmap1);

  gc_color.red = 65535;
  gc_color.green = 65535;
  gc_color.blue = 65535;
  gdk_gc_set_rgb_fg_color(pixmap1_gc,&gc_color);
  gdk_draw_rectangle(pixmap1,pixmap1_gc,1,0,0,clipRegion_x2,clipRegion_y2);
  memset(&gc_color,0,sizeof(gc_color));
  gdk_gc_set_rgb_fg_color(pixmap1_gc,&gc_color);
  memset(&gc_color,0,sizeof(gc_color));
  gc_color.red = 65535;
  gc_color.green = 65535;
  gc_color.blue = 65535;
  gdk_gc_set_rgb_bg_color(pixmap1_gc,&gc_color);
  textLayoutFont = pango_font_description_from_string("Serif 16");

  if (wdloSPI == NULL) {
    wdloSPI = get_wdlo_index_sphead(wdloI);
  }

  /* start drawing */
  cp=0;
  for (i=wdloI; i!=NULL && cp <= p ; i=i->next) {
    if (strcmp(i->tag,"CR")==0) {
      CR = parse_wdlo_CR(wdloFile,i);
      clipRegion_x1 = CR->x1*scaleFactor_numerator/scaleFactor_denominator;
      clipRegion_y1 = CR->y1*scaleFactor_numerator/scaleFactor_denominator;
      clipRegion_x2 = CR->x2*scaleFactor_numerator/scaleFactor_denominator;
      clipRegion_y2 = CR->y2*scaleFactor_numerator/scaleFactor_denominator;
      if (clipRegion_x2 > 5000+clipRegion_x1) {
        clipRegion_x2 = 5000+clipRegion_x1;
      }
      if (clipRegion_y2 > 5000+clipRegion_y1) {
        clipRegion_y2 = 5000+clipRegion_y1;
      }
      free(CR);
      CR=NULL;
    } else if (strcmp(i->tag,"FT")==0) {
      wdloSP01 *SP01=NULL;
      int fsize=0;
      char *fontface=NULL;
      char *fontencoding=NULL;
      FT = parse_wdlo_FT(wdloFile,i);
      SP01 = page2pixmap_getSP01(FT->index,wdloFile,wdloSPI);
      fsize = page2pixmap_getSP01_size(SP01)*scaleFactor_numerator/scaleFactor_denominator;
      pango_font_description_set_absolute_size(textLayoutFont,fsize*PANGO_SCALE);
      fontface = page2pixmap_getSP01_fontface(SP01);
      if (fontface != NULL) {
        fontencoding = page2pixmap_getSP01_encoding(SP01);
        pango_font_description_set_family(textLayoutFont,fontface);
        if (fontencoding!=NULL && strcasecmp(fontencoding,"gb2312")==0) {
          strncpy(encoding,"gb2312",sizeof(encoding)-1);
        }
        free(fontface);
        fontface=NULL;
        if (fontencoding != NULL) {
          free(fontencoding);
          fontencoding=NULL;
        }
      }
      if (SP01!=NULL) {
        free(SP01);
        SP01=NULL;
      }
      free(FT);
      FT=NULL;
    } else if (strcmp(i->tag,"PN")==0) {
      PN = parse_wdlo_PN(wdloFile,i);
      PN_sp03index = PN->index;
      free(PN);
      PN=NULL;
    } else if (strcmp(i->tag,"BH")==0) {
      BH = parse_wdlo_BH(wdloFile,i);
      BH_sp02index = BH->index;
      free(BH);
      BH=NULL;
    } else if (strcmp(i->tag,"UF")==0) {
      wdloSP01 *SP01=NULL;
      int fsize=0;
      char *fontface=NULL;
      UF = parse_wdlo_UF(wdloFile,i);
      SP01 = page2pixmap_getSP01(UF->index,wdloFile,wdloSPI);
      fsize = page2pixmap_getSP01_size(SP01);
      pango_font_description_set_absolute_size(textLayoutFont,fsize*PANGO_SCALE*scaleFactor_numerator/scaleFactor_denominator);
      fontface = page2pixmap_getSP01_fontface(SP01);
      if (fontface != NULL) {
        pango_font_description_set_family(textLayoutFont,fontface);
        if (strlen(fontface)>=7 && strcmp(&(fontface[strlen(fontface)-7]),"_GB2312")==0) {
          strncpy(encoding,"gb2312",sizeof(encoding)-1);
        }
        free(fontface);
        fontface=NULL;
      }
      if (SP01!=NULL) {
        free(SP01);
        SP01=NULL;
      }
      free(UF);
      UF=NULL;
    } else if (strcmp(i->tag,"TC")==0) {
      TC = parse_wdlo_TC (wdloFile,i);
      memset(&gc_color,0,sizeof(gc_color));
      gc_color.red = TC->r*65535/255;
      gc_color.green = TC->g*65535/255;
      gc_color.blue = TC->b*65535/255;
      gdk_gc_set_rgb_fg_color(pixmap1_gc,&gc_color);
      free(TC);
      TC = NULL;
    } else if (strcmp(i->tag,"BC")==0) {
      BC = parse_wdlo_BC (wdloFile,i);
      gc_color.red = BC->r*65535/255;
      gc_color.green = BC->g*65535/255;
      gc_color.blue = BC->b*65535/255;
      gdk_gc_set_rgb_bg_color(pixmap1_gc,&gc_color);
      free(BC);
      BC = NULL;
    } else if (strcmp(i->tag,"R2")==0) {
      cp++;
    } else if (cp != p) {
      continue;
    } else if (strcmp(i->tag,"ET")==0 || strcmp(i->tag,"EU")==0) {
      ET = parse_wdlo_ET (wdloFile,i);
      for (ETi = ET; ETi != NULL; ETi = ETi->next) {
        int currentX=0;
        currentX=0;
        if (ETi->flag1&0x2) {
          for (j=0 ; j<ETi->stringlen; j++) {
            char cc[3];
            char *cc2=NULL,*cc3;
            if (ETi->string[j] & 0x80) {
              cc[0] = ETi->string[j];
              cc[1] = ETi->string[j+1];
              cc[2] = '\0';
              cc2 = g_convert_with_fallback(cc,-1,"utf-8",encoding,"?",NULL,NULL,NULL);
              if (cc2 != NULL) {
                cc3 = cc2;
              } else {
                cc3 = "?";
              }
              textLayout = gtk_widget_create_pango_layout(GTK_WIDGET(resultImage),cc3);
              pango_layout_set_font_description(textLayout,textLayoutFont);
              gdk_draw_layout(pixmap1,pixmap1_gc,(ETi->x+currentX)*scaleFactor_numerator/scaleFactor_denominator,(ETi->y)*scaleFactor_numerator/scaleFactor_denominator,textLayout);
              g_object_unref(textLayout);
              textLayout=NULL;
              currentX += ETi->flag1_0x2_width[j];
              if (cc2 != NULL) {
                g_free(cc2);
              }
              j++;
            } else {
              cc[0] = ETi->string[j];
              cc[1] = '\0';
              textLayout = gtk_widget_create_pango_layout(GTK_WIDGET(resultImage),cc);
              pango_layout_set_font_description(textLayout,textLayoutFont);
              gdk_draw_layout(pixmap1,pixmap1_gc,(ETi->x+currentX)*scaleFactor_numerator/scaleFactor_denominator,(ETi->y)*scaleFactor_numerator/scaleFactor_denominator,textLayout);
              g_object_unref(textLayout);
              textLayout=NULL;
              currentX += ETi->flag1_0x2_width[j];
            }
          }
        } else {
          char *cc2=NULL;
          cc2 = g_convert_with_fallback(ETi->string,-1,"utf-8",encoding,"?",NULL,NULL,NULL);
          textLayout = gtk_widget_create_pango_layout(GTK_WIDGET(resultImage),cc2);
          pango_layout_set_font_description(textLayout,textLayoutFont);
          gdk_draw_layout(pixmap1,pixmap1_gc,(ETi->x+currentX)*scaleFactor_numerator/scaleFactor_denominator,(ETi->y)*scaleFactor_numerator/scaleFactor_denominator,textLayout);
          g_object_unref(textLayout);
          textLayout=NULL;
          g_free(cc2);
          cc2 = NULL;
        }
      }
      free_wdloET(ET);
      ET = NULL;
      ETi = NULL;
    } else if (strcmp(i->tag,"UT")==0) {
      UT = parse_wdlo_UT (wdloFile,i);
      for (UTi = UT; UTi != NULL; UTi = UTi->next) {
        int currentX=0;
        currentX=0;
        if (UTi->flag1&0x2) {
          for (j=0 ; j<UTi->utf16data_len; j++) {
            char cc[4];
            char *cc2=NULL,*cc3;
            cc[0] = UTi->utf16data[2*j];
            cc[1] = UTi->utf16data[2*j+1];
            cc[2] = '\0';
            cc[3] = '\0';
            cc2 = g_convert_with_fallback(cc,2,"utf-8","utf-16le","?",NULL,NULL,NULL);
            if (cc2 != NULL) {
              cc3 = cc2;
            } else {
              cc3 = "?";
            }
            textLayout = gtk_widget_create_pango_layout(GTK_WIDGET(resultImage),cc3);
            pango_layout_set_font_description(textLayout,textLayoutFont);
            gdk_draw_layout(pixmap1,pixmap1_gc,(UTi->x+currentX)*scaleFactor_numerator/scaleFactor_denominator,(UTi->y)*scaleFactor_numerator/scaleFactor_denominator,textLayout);
            g_object_unref(textLayout);
            textLayout=NULL;
            currentX += UTi->flag1_0x2_width[j];
            if (cc2 != NULL) {
              g_free(cc2);
            }
          }
        } else {
          char *cc2=NULL;
          cc2 = g_convert_with_fallback(UTi->utf16data,UTi->utf16data_len*2,"utf-8","utf-16le","?",NULL,NULL,NULL);
          textLayout = gtk_widget_create_pango_layout(GTK_WIDGET(resultImage),cc2);
          pango_layout_set_font_description(textLayout,textLayoutFont);
          gdk_draw_layout(pixmap1,pixmap1_gc,(UTi->x+currentX)*scaleFactor_numerator/scaleFactor_denominator,(UTi->y)*scaleFactor_numerator/scaleFactor_denominator,textLayout);
          g_object_unref(textLayout);
          textLayout=NULL;
          g_free(cc2);
          cc2 = NULL;
        }
      }
      free_wdloUT(UT);
      UT = NULL;
      UTi = NULL;
    } else if (strcmp(i->tag,"PL")==0) {
      wdloSP03 *SP03=NULL;
      GdkGC *pixmap1_gc_PN=pixmap1_gc;
      if (PN_sp03index != -1) {
        SP03 = page2pixmap_getSP03(PN_sp03index,wdloFile,wdloSPI);
        if (SP03 != NULL) {
          pixmap1_gc_PN = gdk_gc_new(pixmap1);
          page2pixmap_setGdkGCbySP03(pixmap1_gc_PN,SP03,scaleFactor_numerator,scaleFactor_denominator);
          free(SP03);
          SP03=NULL;
        }
      }
      PL = parse_wdlo_PL (wdloFile,i);
      for (PLi = PL; PLi != NULL; PLi = PLi->next) {
        for (j=0 ; j+1<PLi->N; j++) {
          gdk_draw_line(pixmap1,pixmap1_gc_PN,PLi->x[j]*scaleFactor_numerator/scaleFactor_denominator,PLi->y[j]*scaleFactor_numerator/scaleFactor_denominator,PLi->x[j+1]*scaleFactor_numerator/scaleFactor_denominator,PLi->y[j+1]*scaleFactor_numerator/scaleFactor_denominator);
        }
      }
      if (pixmap1_gc_PN != pixmap1_gc) {
        g_object_unref(pixmap1_gc_PN);
      }
      free_wdloPL(PL);
      PL = NULL;
      PLi = NULL;
    } else if (strcmp(i->tag,"SD")==0) {
      GdkPixmap *srcPixmap=NULL;
      GdkPixbuf *srcPixbuf=NULL,*dstPixbuf=NULL;
      GdkGC *srcPixmapGC=NULL;
      GdkColor srcColor;
      int px,py;
      SD = parse_wdlo_SD (wdloFile,i);
      srcPixmap = gdk_pixmap_new(pixmap1,SD->src_width,SD->src_height,-1);
      srcPixmapGC = gdk_gc_new(srcPixmap);
      memset(&srcColor,0,sizeof(srcColor));
      for (py=0 ; py<SD->src_height; py++) {
        for (px = 0 ; px < SD->src_width; px++) {
          srcColor.red = ((unsigned int)SD->graph_data[(py*(SD->src_width)+px)*3])*65535/255;
          srcColor.green = ((unsigned int)SD->graph_data[(py*(SD->src_width)+px)*3+1])*65535/255;
          srcColor.blue = ((unsigned int)SD->graph_data[(py*(SD->src_width)+px)*3+2])*65535/255;
          gdk_gc_set_rgb_fg_color(srcPixmapGC,&srcColor);
          gdk_draw_point(srcPixmap,srcPixmapGC,px,py);
        }
      }
      srcPixbuf = gdk_pixbuf_get_from_drawable(NULL,srcPixmap,gdk_colormap_get_system(),0,0,0,0,SD->src_width,SD->src_height);
      g_object_unref(srcPixmapGC);
      g_object_unref(srcPixmap);
      dstPixbuf = gdk_pixbuf_scale_simple(srcPixbuf,SD->dest_width*scaleFactor_numerator/scaleFactor_denominator,SD->dest_height*scaleFactor_numerator/scaleFactor_denominator,GDK_INTERP_BILINEAR);
      g_object_unref(srcPixbuf);
      gdk_draw_pixbuf (pixmap1,NULL,dstPixbuf,0,0,SD->dest_x*scaleFactor_numerator/scaleFactor_denominator,SD->dest_y*scaleFactor_numerator/scaleFactor_denominator,-1,-1,GDK_RGB_DITHER_NORMAL,0,0);
      g_object_unref(dstPixbuf);
      free_wdloSD(SD);
      SD=NULL;
    } else if (strcmp(i->tag,"SP")==0) {
      GdkPixbuf *srcPixbuf=NULL,*dstPixbuf=NULL;
      unsigned char *buffer=NULL;

      SP = parse_wdlo_SP (wdloFile,i);
      if (SP->graph_data != NULL && SP->compression_method==1) {
        buffer = graphdec_jpeg(SP->graph_data,SP->graph_data_len_2);
      } else if (SP->graph_data != NULL && SP->graph_palette != NULL &&  SP->compression_method==2) {
        buffer = graphdec_my02(SP->graph_data,SP->graph_data_len_2,SP->graph_palette,SP->src_width,SP->src_height);
      }
      if (buffer != NULL) {
        srcPixbuf = gdk_pixbuf_new_from_data(buffer,GDK_COLORSPACE_RGB,0,8,SP->src_width,SP->src_height,SP->src_width*3,NULL,NULL);
        dstPixbuf = gdk_pixbuf_scale_simple(srcPixbuf,SP->dest_width*scaleFactor_numerator/scaleFactor_denominator,SP->dest_height*scaleFactor_numerator/scaleFactor_denominator,GDK_INTERP_BILINEAR);
        g_object_unref(srcPixbuf);
        free(buffer);
        buffer=NULL;
        gdk_draw_pixbuf (pixmap1,NULL,dstPixbuf,0,0,SP->dest_x*scaleFactor_numerator/scaleFactor_denominator,SP->dest_y*scaleFactor_numerator/scaleFactor_denominator,-1,-1,GDK_RGB_DITHER_NORMAL,0,0);
        g_object_unref(dstPixbuf);
      }
      free_wdloSP(SP);
      SP=NULL;
    } else if (strcmp(i->tag,"AP")==0) {
      int fill=0;
      GdkPoint *points=NULL;
      wdloSP03 *SP03=NULL;
      GdkGC *pixmap1_gc_PN=pixmap1_gc;
      wdloSP02 *SP02=NULL;
      GdkGC *pixmap1_gc_BH=pixmap1_gc;
      GdkColor gc_color_BH;
      if (PN_sp03index != -1) {
        SP03 = page2pixmap_getSP03(PN_sp03index,wdloFile,wdloSPI);
        if (SP03 != NULL) {
          pixmap1_gc_PN = gdk_gc_new(pixmap1);
          page2pixmap_setGdkGCbySP03(pixmap1_gc_PN,SP03,scaleFactor_numerator,scaleFactor_denominator);
          free(SP03);
          SP03=NULL;
        }
      }
      if (BH_sp02index != -1) {
        SP02 = page2pixmap_getSP02(BH_sp02index,wdloFile,wdloSPI);
        if (SP02 != NULL && SP02->unknown_short01==0) { /* FIXME: unknown short is some pattern*/
          pixmap1_gc_BH = gdk_gc_new(pixmap1);
          gc_color_BH.red = SP02->r * 65535 / 255;
          gc_color_BH.green = SP02->g * 65535 / 255;
          gc_color_BH.blue = SP02->b * 65535 / 255;
          gdk_gc_set_rgb_fg_color(pixmap1_gc_BH,&gc_color_BH);
          free(SP02);
          SP02=NULL;
          fill=1;
        }
      }
      AP = parse_wdlo_AP (wdloFile,i);
      for (APi = AP; APi != NULL; APi = APi->next) {
        points = (GdkPoint*)g_malloc(sizeof(GdkPoint)*(APi->N));
        for (j=0 ; j<APi->N; j++) {
          points[j].x = APi->x[j]*scaleFactor_numerator/scaleFactor_denominator;
          points[j].y = APi->y[j]*scaleFactor_numerator/scaleFactor_denominator;
        }
        gdk_draw_polygon(pixmap1,pixmap1_gc_BH,fill,points,APi->N);
        g_free(points);
        points=NULL;
      }
      if (pixmap1_gc_PN != pixmap1_gc) {
        g_object_unref(pixmap1_gc_PN);
        pixmap1_gc_PN = pixmap1_gc;
      }
      if (pixmap1_gc_BH != pixmap1_gc) {
        g_object_unref(pixmap1_gc_BH);
        pixmap1_gc_BH = pixmap1_gc;
      }
      free_wdloAP(AP);
      AP = NULL;
      APi = NULL;
    } else if (strcmp(i->tag,"FR")==0) {
      int fill=0;
      wdloSP03 *SP03=NULL;
      GdkGC *pixmap1_gc_PN=pixmap1_gc;
      wdloSP02 *SP02=NULL;
      GdkGC *pixmap1_gc_BH=pixmap1_gc;
      GdkColor gc_color_BH;
      if (PN_sp03index != -1) {
        SP03 = page2pixmap_getSP03(PN_sp03index,wdloFile,wdloSPI);
        if (SP03 != NULL) {
          pixmap1_gc_PN = gdk_gc_new(pixmap1);
          page2pixmap_setGdkGCbySP03(pixmap1_gc_PN,SP03,scaleFactor_numerator,scaleFactor_denominator);
          free(SP03);
          SP03=NULL;
        }
      }
      if (BH_sp02index != -1) {
        SP02 = page2pixmap_getSP02(BH_sp02index,wdloFile,wdloSPI);
        if (SP02 != NULL && SP02->unknown_short01==0) { /* FIXME: unknown short is some pattern */
          pixmap1_gc_BH = gdk_gc_new(pixmap1);
          gc_color_BH.red = SP02->r * 65535 / 255;
          gc_color_BH.green = SP02->g * 65535 / 255;
          gc_color_BH.blue = SP02->b * 65535 / 255;
          gdk_gc_set_rgb_fg_color(pixmap1_gc_BH,&gc_color_BH);
          free(SP02);
          SP02=NULL;
          fill=1;
        }
      }
      FR = parse_wdlo_FR (wdloFile,i);
      for (FRi = FR; FRi != NULL; FRi = FRi->next) {
        gdk_draw_rectangle(pixmap1,pixmap1_gc_BH,fill,FRi->x1*scaleFactor_numerator/scaleFactor_denominator,FRi->y1*scaleFactor_numerator/scaleFactor_denominator,(FRi->x2-FRi->x1)*scaleFactor_numerator/scaleFactor_denominator,(FRi->y2-FRi->y1)*scaleFactor_numerator/scaleFactor_denominator);
      }
      if (pixmap1_gc_PN != pixmap1_gc) {
        g_object_unref(pixmap1_gc_PN);
        pixmap1_gc_PN = pixmap1_gc;
      }
      if (pixmap1_gc_BH != pixmap1_gc) {
        g_object_unref(pixmap1_gc_BH);
        pixmap1_gc_BH = pixmap1_gc;
      }
      free_wdloFR(FR);
      FR = NULL;
      FRi = NULL;
    }
  }
  /* draw image */
  pixmap2 = gdk_pixmap_new(pixmap1,clipRegion_x2-clipRegion_x1,clipRegion_y2-clipRegion_y1,-1);
  pixmap2_gc = gdk_gc_new(pixmap2);
  gdk_draw_drawable(pixmap2,pixmap2_gc,pixmap1,clipRegion_x1,clipRegion_y1,0,0,clipRegion_x2-clipRegion_x1,clipRegion_y2-clipRegion_y1);
  g_object_unref(pixmap1_gc);
  g_object_unref(pixmap1);
  g_object_unref(pixmap2_gc);
  pango_font_description_free (textLayoutFont);
  gtk_widget_destroy(GTK_WIDGET(resultImage));
  resultImage=NULL;
  return pixmap2;
}

/**
 * calculate greatest common divisor GCD(a,b)
 * @param a the first number
 * @param b the second number
 * @return GCD(a,b)
 */
int pagerender_gcd(int a,int b) {
  int r;
  if (a==0) return b;
  if (b==0) return a;
  while (b!=0) {
    r = a%b;
    a = b;
    b = r;
  }
  return a;    
}
