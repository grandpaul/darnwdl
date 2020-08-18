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
#include <setjmp.h>
#include <error.h>

#include "jpeginmemorydec.h"
#include "graphdec.h"

/** 
 * jpeg decompressor.
 * It reads the compressed data and return the uncompressed data.
 * User should use free() to free the return data if it is not used anymore.
 * The data is width*height*3 bytes whicn each 3 bytes represents R-G-B.
 *
 * @param origdata the original data need to be compressed
 * @param origdata_len the length of the original data
 * @return the uncompressed data.
 */
unsigned char * graphdec_jpeg (unsigned char *origdata,int origdata_len) {
  struct jpeg_decompress_struct cinfo;
  struct jpeginmemorydec_error_mgr jerr;
  JSAMPARRAY jpeg_buffer;
  unsigned char *buffer=NULL;
  int row_stride;
                                
  cinfo.err = jpeg_std_error(&jerr.pub);
  jerr.pub.error_exit = jpeginmemorydec_error_exit;
  if (setjmp(jerr.setjmp_buffer)) {
    jpeg_destroy_decompress(&cinfo);
    error_at_line(0,0,__FILE__,__LINE__,"Warning: JPEG decode error");
    buffer = NULL;
  } else {
    cinfo.err = (struct jpeg_error_mgr *) (&jerr);
    jpeg_create_decompress(&cinfo);
    jpeg_jpeginmemorydec_src(&cinfo,origdata,origdata_len);
    jpeg_read_header(&cinfo,TRUE);
    jpeg_start_decompress(&cinfo);
    row_stride = cinfo.output_width*cinfo.output_components;
    jpeg_buffer = (*cinfo.mem->alloc_sarray)
      ((j_common_ptr) &cinfo, JPOOL_IMAGE, row_stride, 1);
    buffer = malloc(sizeof(unsigned char)*row_stride*cinfo.output_height);
    while (cinfo.output_scanline < cinfo.output_height) {
      jpeg_read_scanlines(&cinfo, jpeg_buffer, 1);
      memcpy(buffer+(cinfo.output_height-cinfo.output_scanline)*row_stride,
	     jpeg_buffer[0],row_stride);
    }
    jpeg_finish_decompress(&cinfo);
    jpeg_destroy_decompress(&cinfo);
  }
  return buffer;
}

unsigned char * graphdec_my02 (unsigned char *origdata,int origdata_len,unsigned char *graph_palette,int width,int height) {
  unsigned char *buffer=NULL;
  return buffer;      
}
