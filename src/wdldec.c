/* 
   WDL decompressor
   Copyright (C) 2005-2006 Ying-Chun Liu (PaulLiu)
   Copyright (C) 2006 Dan Jacobson http://jidanni.org/

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

#include "wpass1.h"
#include "wpass2.h"

int main (int argc, char *argv[]) {

  if (argc <= 1 || argc >= 4) {
    printf ("Usage: %s <input> [<output>]\n",argv[0]);
    return 0;
  }

  if (argc == 3 && argv[2] != NULL) {
    wdlpass1_dec(argv[2],argv[1]);
  } else {
    wdlpass1_dec_file(stdout,argv[1]);
  }

  return 0;
}
