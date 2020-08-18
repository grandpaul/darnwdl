#ifndef __HEADER_DARNWDL_GRAPHDEC_H__
#define __HEADER_DARNWDL_GRAPHDEC_H__

unsigned char * graphdec_jpeg (unsigned char *origdata,int origdata_len);
unsigned char * graphdec_my02 (unsigned char *origdata,int origdata_len,unsigned char *graph_palette,int width,int height);

#endif
