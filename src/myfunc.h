#ifndef __HEADER_DARNWDL_MYFUNC_H__
#define __HEADER_DARNWDL_MYFUNC_H__

#include <gtk/gtk.h>
#include <stdlib.h>
#include <string.h>
#include "wpass1.h"

int myfunc_showpage(int p,GtkImage *resultImage,GtkLayout *resultLayout);
int myfunc_updateStatus(GtkStatusbar *resultStatusbar,int p,int n);
int myfunc_setScaleFactor(int n,int d);
int myfunc_getScaleFactor(void);
int myfunc_getScaleFactor_numerator(void);
int myfunc_getScaleFactor_denominator(void);
int myfunc_getCurrentPage(void);
int myfunc_setCurrentPage(int p);
int myfunc_openwdlo_file(FILE *inputFile);
int myfunc_openwdlo(const char *filename);
int myfunc_closewdlo(void);
int myfunc_getPages(void);
GtkWidget* getInstance_fileselection1(void);
GtkWidget* registerMainWindow(GtkWidget *p);
GtkWidget* getMainWindow(void);

#endif
