/* 
   WDL decompressor
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
#include <ctype.h>

#include <error.h>

#include <libdynamite.h>

#include "wpass1.h"
#include "wpass2.h"

typedef struct wdlpass1_explodeDataStruct {
  FILE *inputFile;  /* inputfile descriptor */
  int current;      /* current read point */
  int size;         /* size of data left to be read */
  FILE *outputFile; /* outputfile descriptor */
} wdlpass1_explodeData;


/**
 * Read DynaPKCP header. After reading the header, the file pointer is 
 * at the begining of the compressed data. And the length of compressed data
 * is returned.
 * @param file1 input file descriptor 
 * @return the length of compressed data
 */
int wdlpass1_readDynaPKCP (FILE * file1) {
  char header[9];
  int int01, uncompressedsize, seeklen, int04;
  memset (header, 0, sizeof (header));
  if (fread (header, 1, 8, file1) <= 0) {
    return (-1);
  }
  header[8] = '\0';
  if (strcmp (header, "DynaPKCP") != 0) {
    error_at_line(0,0,__FILE__,__LINE__,"Error: %s - Not reading DynaPKCP tag at 0x%lx",__FUNCTION__,ftell(file1)-8);
    return 0;
  }
  int01 = wdlpass2_readInt (file1);
  uncompressedsize = wdlpass2_readInt (file1);
  seeklen = wdlpass2_readInt (file1);
  int04 = wdlpass2_readInt (file1);

  /* printf ("%d %d %d %d\n", int01, uncompressedsize, seeklen, int04); */
  return seeklen;
}

/**
 * callback function for libdynamite to read from file to buffer.
 * This function controls the left size so libdynamite won't get next
 * header.
 * @param buffer the pointer of buffer to write
 * @param size the maximum size of the buffer
 * @param data wdlpass1_explodeData which to obtain the information of file
 * @return the size read from file
 */
size_t wdlpass1_dynamite_callback_read(void *buffer, size_t size, void *data) {
  int ret;
  wdlpass1_explodeData* ed;
  
  ed = ((wdlpass1_explodeData*)data);
  if (ed->current + size > ed->size) {
    size = ed->size-ed->current;
  }
  if (size <= 0) {
    return 0;
  }
  ret = fread(buffer,1,size,ed->inputFile);
  ed->current += ret;
  return ret;
}

/**
 * callback function for libdynamite to write to file from buffer.
 * This function just write toe buffer to the file
 * @param buffer the pointer of input buffer
 * @param size of the buffer
 * @param data wdlpass1_explodeData which to obtain the information of file
 * @return the size actually write to file
 */
size_t wdlpass1_dynamite_callback_write(void *buffer, size_t size, void *data) {
  size_t ret;
  ret = fwrite(buffer,1,size,((wdlpass1_explodeData*)data)->outputFile);
  return ret;
}

/**
 * decompress wdl file and output to a file.
 * It will return some information from the input file.
 * @param outputfile the descriptor of the output file
 * @param infilename the filename of the input file
 * @return some information retrived from the input file.
 */
wdlpass1_fileheader* wdlpass1_dec_file(FILE *outputfile,const char *infilename) {
  FILE *file1=NULL;
  char header[7];
  char header_property_tag[5];
  int buf_len,i=0,forwardlen,tmp1;
  wdlpass1_explodeData ed;
  wdlpass1_fileheader* ret=NULL;

  file1 = fopen (infilename, "rb");
  if (file1 == NULL)
    return NULL;

  memset(header,0,sizeof(header));
  fread (header, 1, 6, file1); /* file ID */
  if (strncmp(header,"DDoc",4)!=0) {
    return NULL;
  }
  ret = (wdlpass1_fileheader*)malloc(sizeof(wdlpass1_fileheader));
  memset(ret,0,sizeof(wdlpass1_fileheader));
  strcpy(ret->headtag,header);
  ret->version = wdlpass2_readShort(file1);
  /* read properties */
  memset(header_property_tag,0,sizeof(header_property_tag));
  fread(header_property_tag,1,4,file1);
  for (i=0 ; i<3 ; i++) {
    tmp1 = wdlpass2_readInt(file1);
    if (strcmp(header_property_tag,"font")==0) {
      ret->fontvalue[i] = tmp1;
    }
  }
  fread(header_property_tag,1,4,file1);
  for (i=0 ; i<3 ; i++) {
    tmp1 = wdlpass2_readInt(file1);
    if (strcmp(header_property_tag,"indx")==0) {
      ret->fontvalue[i] = tmp1;
    }
  }
  fread(header_property_tag,1,4,file1);
  for (i=0 ; i<3 ; i++) {
    tmp1 = wdlpass2_readInt(file1);
    if (strcmp(header_property_tag,"name")==0) {
      ret->fontvalue[i] = tmp1;
    }
  }
  fread(header_property_tag,1,4,file1);
  for (i=0 ; i<3 ; i++) {
    tmp1 = wdlpass2_readInt(file1);
    if (strcmp(header_property_tag,"pape")==0) {
      ret->fontvalue[i] = tmp1;
    }
  }
  fread(header_property_tag,1,4,file1);
  for (i=0 ; i<3 ; i++) {
    tmp1 = wdlpass2_readInt(file1);
    if (strcmp(header_property_tag,"stru")==0) {
      ret->fontvalue[i] = tmp1;
    }
  }
  fread(header_property_tag,1,4,file1);
  for (i=0 ; i<3 ; i++) {
    tmp1 = wdlpass2_readInt(file1);
    if (strcmp(header_property_tag,"thum")==0) {
      ret->fontvalue[i] = tmp1;
    }
  }
  /* unknown data */
  fseek (file1, 50 , SEEK_CUR);
  forwardlen = wdlpass2_readInt(file1);
  forwardlen -= 38;
  fseek (file1, forwardlen, SEEK_CUR);
  
  memset(&ed,0,sizeof(ed));
  ed.inputFile = file1;
  ed.outputFile = outputfile;
  

  for (i=0 ; (!feof (file1)) ; i++) {
    buf_len = wdlpass1_readDynaPKCP (file1);
    if (buf_len>0) {
      ed.current=0;
      ed.size=buf_len;
      dynamite_explode(wdlpass1_dynamite_callback_read,wdlpass1_dynamite_callback_write,&ed);
    } else {
      break;
    }
  }
  
  fclose(file1);
  fflush(outputfile);
  return ret;
}

/**
 * decompress wdl file and output to a file.
 * It will return some information from the input file.
 * @param outfilename the filename of the output file
 * @param infilename the filename of the input file
 * @return some information retrived from the input file.
 */
wdlpass1_fileheader* wdlpass1_dec(const char *outfilename,const char *infilename) {
  wdlpass1_fileheader *ret=NULL;
  FILE *outputfile=NULL;
  outputfile = fopen(outfilename,"w");
  ret = wdlpass1_dec_file(outputfile,infilename);
  fclose(outputfile);
  return ret;
}

