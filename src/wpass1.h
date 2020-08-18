#ifndef __HEADER_DARNWDL_WPASS1_H__
#define __HEADER_DARNWDL_WPASS1_H__

#include <stdio.h>

typedef struct wdlpass1_fileheaderS {
  char headtag[7];
  int version;
  int fontvalue[3];
  int indxvalue[3];
  int namevalue[3];
  int papevalue[3];
  int struvalue[3];
  int thumvalue[3];
} wdlpass1_fileheader;

wdlpass1_fileheader* wdlpass1_dec_file(FILE *outputfile,const char *infilename);
wdlpass1_fileheader* wdlpass1_dec(const char *outfilename,const char *infilename);

#endif
