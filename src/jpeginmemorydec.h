#ifndef __HEADER_DARNWDL_JPEGINMEMORYDEC_H__
#define __HEADER_DARNWDL_JPEGINMEMORYDEC_H__
#include <setjmp.h>
#include "jpeglib.h"

struct jpeginmemorydec_error_mgr {
  struct jpeg_error_mgr pub;    /* "public" fields */
  jmp_buf setjmp_buffer;        /* for return to caller */
};

typedef struct jpeginmemorydec_error_mgr * jpeginmemorydec_error_ptr;    

void jpeg_jpeginmemorydec_src(j_decompress_ptr, unsigned char *,int);
void jpeginmemorydec_error_exit(j_common_ptr cinfo);

#endif
