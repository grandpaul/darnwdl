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

#ifdef HAVE_CONFIG_H
#  include <config.h>
#endif


#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include <unistd.h>

#include <gdk/gdkkeysyms.h>
#include <gtk/gtk.h>

#include "wpass2.h"
#include "support.h"
#include "callbacks.h"
#include "graphdec.h"
#include "pagerender.h"
#include "fasterrender.h"
#include "myfunc.h"

fasterrender_data* myfunc_fasterdata=NULL;

int myfunc_getPages(void) {
  int ret=0;
  if (myfunc_fasterdata != NULL) {
    ret = myfunc_fasterdata->total_pages;
  }
  return ret;
}

int myfunc_openwdlo_file(FILE *inputFile) {
  FILE* myfunc_file=NULL;
  wdloIndex* myfunc_docIndex=NULL;
  myfunc_file = inputFile;
  myfunc_docIndex = generate_wdlo_index(myfunc_file);
  myfunc_fasterdata = fasterrender_init(myfunc_file,myfunc_docIndex);
  return 0;
}

int myfunc_openwdlo(const char *filename) {
  FILE *inputFile=NULL;
  inputFile = fopen(filename,"r");
  myfunc_openwdlo_file(inputFile);
  return 0;
}

int myfunc_closewdlo(void) {
  if (myfunc_fasterdata == NULL) {
    return 0;
  }
  if (myfunc_fasterdata->wdloI != NULL) {
    free_wdloIndex(myfunc_fasterdata->wdloI);
    myfunc_fasterdata->wdloI = NULL;
  }
  if (myfunc_fasterdata->wdloFile != NULL) {
    fclose(myfunc_fasterdata->wdloFile);
    myfunc_fasterdata->wdloFile=NULL;
  }
  if (myfunc_fasterdata != NULL) {
    fasterrender_destroy(myfunc_fasterdata);
    myfunc_fasterdata=NULL;
  }
  return 0;
}

int myfunc_currentpage=1;
int myfunc_setCurrentPage(int p) {
  myfunc_currentpage = p;
  return p;
}
int myfunc_getCurrentPage(void) {
  return myfunc_currentpage;
}

int myfunc_scalefactor_numerator=1;
int myfunc_scalefactor_denominator=10;

int myfunc_getScaleFactor_numerator(void) {
  return myfunc_scalefactor_numerator;
}

int myfunc_getScaleFactor_denominator(void) {
  return myfunc_scalefactor_denominator;
}

int myfunc_getScaleFactor(void) {
  if (myfunc_scalefactor_numerator==0) {
    return 0;
  }
  return myfunc_scalefactor_denominator/myfunc_scalefactor_numerator;
}

int myfunc_setScaleFactor(int n,int d) {
  static int max_q=30;
  static int max_n=30000;
  int g;
  if (n<=0) {
    myfunc_scalefactor_numerator=0;
    myfunc_scalefactor_denominator=1;
    return 0;
  }
  while (n>=max_n || d >= max_n) {
    n/=10;
    d/=10;
  }
  if (d<0) {
    d=1;
  }
  if (n*max_q<d) {
    d = max_q;
    n = 1;
  }
  g = pagerender_gcd(d,n);
  d = d/g;
  n = n/g;
  myfunc_scalefactor_numerator = n;
  myfunc_scalefactor_denominator = d;
  return 0;
}

int myfunc_updateStatus(GtkStatusbar *resultStatusbar,int p,int n) {
  gchar str[1000];
  guint id;
  id = gtk_statusbar_get_context_id (resultStatusbar, "pageinfo");
  gtk_statusbar_pop(resultStatusbar,id);
  g_snprintf(str,sizeof(str)-1,"%d/%d",p,n);
  gtk_statusbar_push(resultStatusbar,id,str);
  return 0;
}

int myfunc_showpage(int p,GtkImage *resultImage,GtkLayout *resultLayout) { 
  GdkPixmap *pixmap2;
  int w,h;

  /* init */
  gtk_image_clear(resultImage);

  /* draw image */
  pixmap2 = fasterrender_page2pixmap(p,myfunc_fasterdata,myfunc_getScaleFactor_numerator(),myfunc_getScaleFactor_denominator());
  if (pixmap2 == NULL) {
    return 0;
  }
  gdk_drawable_get_size(pixmap2,&w,&h);
  gtk_layout_set_size(resultLayout,w,h);
  gtk_image_set_from_pixmap(resultImage,pixmap2,NULL);
  gtk_widget_queue_draw_area(GTK_WIDGET(resultLayout),0,0,w,h);
  g_object_unref(pixmap2);
  return 0;
}


GtkWidget* myfunc_mainWindow=NULL;
GtkWidget* registerMainWindow(GtkWidget *p) {
  if (myfunc_mainWindow==NULL) {
    myfunc_mainWindow = lookup_widget(p,"mainWindow");
  }
  return myfunc_mainWindow;
}

GtkWidget* getMainWindow(void) {
  return myfunc_mainWindow;
}

