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
#include "jpeglib.h"
#include "jerror.h"
#include "jpeginmemorydec.h"

typedef struct jpeginmemorydec_source_mgr_s {
  struct jpeg_source_mgr orig;
  
  unsigned char *data;
  int data_len;
  unsigned char *current_data;
} jpeginmemorydec_source_mgr;

typedef jpeginmemorydec_source_mgr * jpeginmemorydec_source_ptr;

/**
 * jpeginmemorydec source module's init_source callback function
 * It set the things before reading. It won't fill the buffer.
 * @param cinfo the pointer of decompress info structure
 */
void jpeginmemorydec_init_source (j_decompress_ptr cinfo) {
  jpeginmemorydec_source_ptr src;
  src = (jpeginmemorydec_source_ptr)(cinfo->src);
  src->orig.next_input_byte = NULL;
  src->orig.bytes_in_buffer=0;
  src->current_data = src->data;
}

/**
 * jpeginmemorydec source module's fill_input_buffer callback function
 * It puts just one byte to the buffer.
 * @param cinfo the pointer of decompress info structure
 * @return always true
 */
boolean jpeginmemorydec_fill_input_buffer (j_decompress_ptr cinfo) {
  jpeginmemorydec_source_ptr src;
  src = (jpeginmemorydec_source_ptr)(cinfo->src);
  src->orig.next_input_byte = src->current_data;
  src->current_data++;
  src->orig.bytes_in_buffer=1;
  return TRUE;
}  

/**
 * jpeginmemorydec source module's skip_input_data callback function
 * It skip num_bytes bytes and fill 1 byte into the buffer
 * @param cinfo the pointer of decompress info structure
 * @param num_bytes skip bytes
 */
void jpeginmemorydec_skip_input_data(j_decompress_ptr cinfo,long num_bytes) {
  jpeginmemorydec_source_ptr src;
  src = (jpeginmemorydec_source_ptr)(cinfo->src);
  src->current_data += num_bytes;
  src->orig.next_input_byte = src->current_data;
  src->orig.bytes_in_buffer = 1;
}  

/**
 * jpeginmemorydec source module's term_source callback function
 * this function does nothing
 * @param cinfo the pointer of decompress info structure
 */
void jpeginmemorydec_term_source (j_decompress_ptr cinfo) {
  jpeginmemorydec_source_ptr src;
  src = (jpeginmemorydec_source_ptr)(cinfo->src);
}

/**
 * This function specify a source module (reading from memory) to cinfo.
 * It will guide jpeg library to read from the given data in memory.
 * @param cinfo the pointer of decompress info structure
 * @param data the data need to be decompress
 * @param data_len the length of data
 */
void jpeg_jpeginmemorydec_src (j_decompress_ptr cinfo, unsigned char * data, int data_len) {
  jpeginmemorydec_source_ptr src;
  
  if (cinfo->src==NULL) {
    cinfo->src = (struct jpeg_source_mgr *) 
      (*cinfo->mem->alloc_small) ((j_common_ptr) cinfo, JPOOL_PERMANENT,
				  sizeof(jpeginmemorydec_source_mgr));
  }
  src = (jpeginmemorydec_source_ptr) cinfo->src;
  src->orig.init_source = jpeginmemorydec_init_source;
  src->orig.fill_input_buffer = jpeginmemorydec_fill_input_buffer;
  src->orig.skip_input_data = jpeginmemorydec_skip_input_data;
  src->orig.resync_to_restart = jpeg_resync_to_restart;
  src->orig.term_source = jpeginmemorydec_term_source;
  src->data = data;
  src->data_len = data_len;
}

/**
 * jpeginmemorydec error module's error_exit callback function
 * It jumps to the given jump_buffer if error happens.
 * @param cinfo the j_common pointer
 */
void jpeginmemorydec_error_exit(j_common_ptr cinfo) {
  jpeginmemorydec_error_ptr myerr = (jpeginmemorydec_error_ptr) cinfo->err;
  (*cinfo->err->output_message) (cinfo);
  longjmp(myerr->setjmp_buffer, 1);
}
