#ifndef __HEADER_DARNWDL_PAGERENDER_H__
#define __HEADER_DARNWDL_PAGERENDER_H__

#include "wpass2.h"

wdloSP01* page2pixmap_getSP01(int index,FILE *wdloFile,wdloIndex *wdloSPI);
int page2pixmap_getSP01_size(wdloSP01 *SP01);
char* page2pixmap_getSP01_fontface(wdloSP01 *SP01);
wdloSP02* page2pixmap_getSP02(int index,FILE *wdloFile,wdloIndex *wdloSPI);
wdloSP03* page2pixmap_getSP03(int index,FILE *wdloFile,wdloIndex *wdloSPI);
GdkPixmap* page2pixmap(int p,FILE *wdloFile,wdloIndex *wdloI,wdloIndex *wdloSPI,int scaleFactor_numerator,int scaleFactor_denominator);
int pagerender_gcd(int a,int b);

#endif
