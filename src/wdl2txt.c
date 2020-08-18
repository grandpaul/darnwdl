/* 
   WDL to TXT converter
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
#include <stdlib.h>
#include <string.h>

#include <error.h>
#include <unistd.h>

#include <glib.h>

#include "wpass1.h"
#include "wpass2.h"

int usage(int argc,char *argv[]) {
  printf ("Usage: %s in.wdl\n",argv[0]);
  return 0;
}

int main(int argc,char *argv[]) {
  FILE *tempfile=NULL;
  int currentX=-1;
  wdlpass1_fileheader *wdlpass1header=NULL;
  wdloIndex *wdli=NULL,*i=NULL;
  wdloET *ET=NULL,*ETi=NULL;
  wdloUT *UT=NULL,*UTi=NULL;
  
  if (argc != 2) {
    usage(argc,argv);
    return 0;
  }
  /* create temp file */
  tempfile = tmpfile();
  if (tempfile==NULL) {
    error(0,0,"Cannot open temp file");
    return 0;
  }
  /* pass1: decompress the wdl file to temp file */
  wdlpass1header = wdlpass1_dec_file(tempfile,argv[1]);
  if (wdlpass1header == NULL) {
    error(0,0,"Decompressing error");
    return 0;
  }
  /* pass2: build the tag index of the wdlo file */
  wdli = generate_wdlo_index(tempfile);
  /* parsing txt */
  for (i=wdli; i!=NULL; i=i->next) {
    if (strcmp(i->tag,"ET")==0 
	|| strcmp(i->tag,"EU")==0) { /* big5 or gb2312 */
      ET = parse_wdlo_ET(tempfile,i);
      for (ETi = ET; ETi != NULL; ETi = ETi->next) {
        if (ETi->x < currentX) {
          printf ("\n");
        }
        currentX = ETi->x;
        printf ("%s",ETi->string);
      }
      free_wdloET(ET);
      ET=NULL;
      ETi=NULL;
    } else if (strcmp(i->tag,"UT")==0) { /* utf-16le */
      UT = parse_wdlo_UT(tempfile,i);
      for (UTi = UT; UTi != NULL; UTi = UTi->next) {
        char *cc2=NULL;
        cc2 = g_convert_with_fallback(UTi->utf16data,UTi->utf16data_len*2,"utf-8","utf-16le","?",NULL,NULL,NULL);
        if (UTi->x < currentX) {
          printf ("\n");
        }
        currentX = UTi->x;
        printf ("%s",cc2);
        g_free(cc2);
      }
      free_wdloUT(UT);
      UT=NULL;
      UTi=NULL;
    } else if (strcmp(i->tag,"R2")==0) { /* new page */
      printf ("\n");
      currentX=-1;
    }
  }
  fflush(stdout);
  
  free_wdloIndex(wdli);
  wdli=NULL;
  i=NULL;
  free(wdlpass1header);
  wdlpass1header=NULL;
  fclose(tempfile);
  tempfile=NULL;
  return 0;
}
