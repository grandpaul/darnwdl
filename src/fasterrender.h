#ifndef __HEADER_DARNWDL_FASTERRENDER_H__
#define __HEADER_DARNWDL_FASTERRENDER_H__

#include "wpass2.h"

extern int fasterrender_page_divide;
extern int fasterrender_previous_data_slot;

typedef struct fasterrender_data_s {
  FILE *wdloFile;
  wdloIndex *wdloI;
  wdloIndex *wdloSPI;
  int total_pages;
  int div;
  wdloIndex **pages_div;
  wdloIndex ***previous_data;
} fasterrender_data;
  
fasterrender_data* fasterrender_init(FILE *wdloFile,wdloIndex *wdloI);
void fasterrender_destroy(fasterrender_data *fasterdata);
GdkPixmap* fasterrender_page2pixmap(int p,fasterrender_data *fasterdata,int scaleFactor_numerator,int scaleFactor_denominator);

#endif
