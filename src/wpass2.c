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
#include <stdlib.h>
#include <string.h>

#include <error.h>

#include "wpass2.h"

/**
 * read 4 bytes from inputfile and use little-endian to form an 32-bit integer
 * @param file1 input file descriptor
 * @return the 32-bit integer
 */
int wdlpass2_readInt (FILE * file1) {
  int a1, a2, a3, a4;
  a1 = fgetc (file1);
  a2 = fgetc (file1);
  a3 = fgetc (file1);
  a4 = fgetc (file1);
  if (a1 == EOF || a2 == EOF || a3 == EOF || a4 == EOF) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s Error",__FUNCTION__);
    return 0;
  }
  return a1 + a2 * 256 + a3 * 256 * 256 + a4 * 256 * 256 * 256;
}

/**
 * read 2 bytes from inputfile and use little-endian to form an 16-bit integer
 * @param file1 input file descriptor
 * @return the 16-bit integer
 */
int wdlpass2_readShort (FILE * file1) {
  int a1, a2;
  a1 = fgetc (file1);
  a2 = fgetc (file1);
  if (a1 == EOF || a2 == EOF ) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s Error",__FUNCTION__);
    return 0;
  }
  return a1 + a2 * 256;
}

/**
 * read 2 bytes from inputfile and use little-endian to form an 16-bit 
 * signed integer
 * @param file1 input file descriptor
 * @return the 16-bit signed integer
 */
int wdlpass2_readSignedShort (FILE * file1) {
  int a1, a2, au;
  a1 = fgetc (file1);
  a2 = fgetc (file1);
  if (a1 == EOF || a2 == EOF ) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s Error",__FUNCTION__);
    return 0;
  }
  au = a1 + a2 * 256;
  if (au > 32767) {
    au = (au-65536);
  }
  return au;
}


/**
 * Encode data by QP and output it to a file descriptor 
 * @param file1 output file descriptor
 * @param str input data
 * @param str_len the length of str
 * @return number of characters writes to output file
 */
int wdlpass2_QPoutput(FILE *file1,char *str,int str_len) {
  int i,ret=0;
  /* simple QP encoding */
  for (i=0 ; i<str_len; i++) {
    if ( ( 33 <= str[i] && str[i] <= 60)
	 ||  ( 62 <= str[i] && str[i] <= 126) ) {
      fprintf (file1,"%c",str[i]);
      ret+=1;
    } else {
      fprintf (file1,"=%02X",(((int)str[i])&0x00ff));
      ret+=3;
    }
  }
  return ret;
}

/**
 * Return the number of characters in utf-16le encoded string
 * @param str the string
 * @return the number of characters
 */
int wdlpass2_utf16le_strlen(const char *str) {
  int i,ret;
  ret=0;
  for (i=0 ; str[i]!='\0' || str[i+1]!='\0'; i+=2) {
    ret++;
  }
  return ret;
}

/**
 * generate the index list for inputFile
 *
 * @param inputFile the input wdlo file
 * @return the list of tag index
 */
wdloIndex* generate_wdlo_index (FILE *inputFile) {
  int c1,c2,seeklen;
  int specialStart=-1;
  wdloIndex *ret=NULL,*i_current=NULL,*i_new=NULL;
  char tag[3];
  
  fseek(inputFile,0,SEEK_SET);
  
  while (!feof(inputFile)) {
    c1 = fgetc(inputFile);
    if (c1==EOF) {
      break;
    }
    c2 = fgetc(inputFile);
    if (c2==EOF) {
      break;
    }
    tag[0] = c1;
    tag[1] = c2;
    tag[2] = '\0';


    /*    if (tag[1] != '\0') {
	  fprintf (stdout,"Parsing %s at 0x%08x\n",tag,ftell(inputFile)-2);
	  } else {
	  fprintf (stdout,"Parsing special tag %d at 0x%08x\n",(int)tag[0],ftell(inputFile)-2);
	  }*/
    i_new = (wdloIndex*)malloc(sizeof(wdloIndex));
    memset(i_new,0,sizeof(wdloIndex));
    memcpy(i_new->tag,tag,3);
    i_new->pos = ftell(inputFile)-2;
    if (i_current==NULL) {
      ret = i_new;
      i_current = i_new;
    } else {
      i_current->next = i_new;
      i_new->prev = i_current;
      i_current = i_new;
    }
    if (strcmp(tag,"FT")==0) {
      fseek(inputFile,4,SEEK_CUR);
    } else if (strcmp(tag,"BC")==0) {
      fseek(inputFile,4,SEEK_CUR);
    } else if (strcmp(tag,"BM")==0) {
      fseek(inputFile,2,SEEK_CUR);
    } else if (strcmp(tag,"BH")==0) {
      fseek(inputFile,4,SEEK_CUR);
    } else if (strcmp(tag,"TC")==0) {
      fseek(inputFile,4,SEEK_CUR);
    } else if (strcmp(tag,"PN")==0) {
      fseek(inputFile,4,SEEK_CUR);
    } else if (strcmp(tag,"R2")==0) {
      fseek(inputFile,2,SEEK_CUR);
    } else if (strcmp(tag,"CT")==0) {
      fseek(inputFile,2,SEEK_CUR);
    } else if (strcmp(tag,"UF")==0) {
      fseek(inputFile,6,SEEK_CUR);
    } else if (strcmp(tag,"CR")==0) {
      fseek(inputFile,8,SEEK_CUR);
    } else if (strcmp(tag,"ET")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"EU")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"FR")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"CP")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"PL")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"AP")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"AQ")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"RT")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"WP")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"XD")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"SP")==0) {
      wdlpass2_readShort(inputFile);
      seeklen = wdlpass2_readInt(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"SD")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      if (seeklen != 0) {
        fseek(inputFile,seeklen,SEEK_CUR);
      } else {
        int graph_data_len = 0,graph_data_len_2=0;
        int unknown_int_3 = 0;
        fseek(inputFile,40,SEEK_CUR);
        graph_data_len = wdlpass2_readInt(inputFile);
        fseek(inputFile,8,SEEK_CUR);
        unknown_int_3 = wdlpass2_readInt(inputFile);
        fseek(inputFile,4,SEEK_CUR);
        if (unknown_int_3 != 0) {
          fseek(inputFile,1024,SEEK_CUR);
        }
        graph_data_len_2 = wdlpass2_readInt(inputFile);
        fseek(inputFile,graph_data_len_2,SEEK_CUR);
      }
    } else if (strcmp(tag,"SX")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"EP")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"UT")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (tag[1]=='\0') {
      if (specialStart==-1) {
        specialStart = ftell(inputFile)-2;
      }
      i_new->firstSpecialTagPos = specialStart;
      seeklen = wdlpass2_readInt(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else {
      error(0,0,"Warning: Please report bugs: Unknown tag %s",tag);
      fseek(inputFile,-1,SEEK_CUR);
    }
  }
  return ret;
}

/**
 * free the whole index list
 *
 * @param head the head of index list
 */
void free_wdloIndex(wdloIndex *head) {
  wdloIndex *tmp,*i;
  if (!head) {
    return;
  }
  i = head;
  do {
    tmp = i->next;
    free(i);
    i = tmp;
  } while (i != NULL);
}

/**
 * return the beginning node for \?\0 tags
 *
 * @param idx the head of index list
 * @return the beginning node for \?\0 tags
 */
wdloIndex* get_wdlo_index_sphead (wdloIndex *idx) {
  wdloIndex *i,*ret=NULL;
  for (i=idx; i!=NULL; i=i->next) {
    ret = i;
    if (i->tag[1]=='\0') {
      break;
    }
  }
  return ret;
}

/**
 * calculate the number of tags who's ID = tagid
 * @param idx the input index linked-list need to be calculated
 * @param the tagid to be calculated
 * @return the number of tags who's ID = tagid
 */
int calculate_wdlo_number_of_tags(wdloIndex *idx,char *tagid) {
  int ret=0;
  wdloIndex *i=NULL;
  ret=0;
  for (i=idx; i!=NULL; i=i->next) {
    if (strcmp(i->tag,tagid)==0) {
      ret++;
    }
  }
  return ret;
}

/**
 * parsing the data for FT structure
 * User should use free() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is FT
 * @return the data in the FT structure
 */
wdloFT* parse_wdlo_FT (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2;
  wdloFT* ret=NULL;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  
  ret = (wdloFT*)malloc(sizeof(wdloFT));
  memset(ret,0,sizeof(wdloFT));
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  ret->index = wdlpass2_readInt(inputFile);
  return ret;
}

/**
 * parsing the data for PN structure
 * User should use free() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is PN
 * @return the data in the PN structure
 */
wdloPN* parse_wdlo_PN (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2;
  wdloPN* ret=NULL;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  
  ret = (wdloPN*)malloc(sizeof(wdloPN));
  memset(ret,0,sizeof(wdloPN));
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  ret->index = wdlpass2_readInt(inputFile);
  return ret;
}

/**
 * parsing the data for BH structure
 * User should use free() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is BH
 * @return the data in the BH structure
 */
wdloBH* parse_wdlo_BH (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2;
  wdloBH* ret=NULL;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  
  ret = (wdloBH*)malloc(sizeof(wdloBH));
  memset(ret,0,sizeof(wdloBH));
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  ret->index = wdlpass2_readInt(inputFile);
  return ret;
}

/**
 * parsing the data for UF structure
 * User should use free() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is UF
 * @return the data in the UF structure
 */
wdloUF* parse_wdlo_UF (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2;
  wdloUF* ret=NULL;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  
  ret = (wdloUF*)malloc(sizeof(wdloUF));
  memset(ret,0,sizeof(wdloUF));
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  ret->unknown_short = wdlpass2_readShort(inputFile);
  ret->index = wdlpass2_readInt(inputFile);
  return ret;
}

/**
 * parsing the data for R2 structure
 * User should use free() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is R2
 * @return the data in the R2 structure
 */
wdloR2* parse_wdlo_R2 (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2;
  wdloR2* ret=NULL;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  
  ret = (wdloR2*)malloc(sizeof(wdloR2));
  memset(ret,0,sizeof(wdloR2));
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  ret->unknown_short = wdlpass2_readShort(inputFile);
  return ret;
}

/**
 * parsing the data for BC structure
 * User should use free() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is BC
 * @return the data in the BC structure
 */
wdloBC* parse_wdlo_BC (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2;
  wdloBC* ret=NULL;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  
  ret = (wdloBC*)malloc(sizeof(wdloBC));
  memset(ret,0,sizeof(wdloBC));
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  ret->r = fgetc(inputFile);
  ret->g = fgetc(inputFile);
  ret->b = fgetc(inputFile);
  ret->unknown_byte = fgetc(inputFile);
  return ret;
}

/**
 * parsing the data for TC structure
 * User should use free() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is TC
 * @return the data in the TC structure
 */
wdloTC* parse_wdlo_TC (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2;
  wdloTC* ret=NULL;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  
  ret = (wdloTC*)malloc(sizeof(wdloTC));
  memset(ret,0,sizeof(wdloTC));
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  ret->r = fgetc(inputFile);
  ret->g = fgetc(inputFile);
  ret->b = fgetc(inputFile);
  ret->unknown_byte = fgetc(inputFile);
  return ret;
}

/**
 * parsing the data for CR structure
 * User should use free() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is CR
 * @return the data in the CR structure
 */
wdloCR* parse_wdlo_CR (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2;
  wdloCR* ret=NULL;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  
  ret = (wdloCR*)malloc(sizeof(wdloCR));
  memset(ret,0,sizeof(wdloCR));
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  ret->x1 = wdlpass2_readShort(inputFile);
  ret->y1 = wdlpass2_readShort(inputFile);
  ret->x2 = wdlpass2_readShort(inputFile);
  ret->y2 = wdlpass2_readShort(inputFile);
  return ret;
}

/**
 * parsing the data for PL structure
 * User should use free_wdlo_PL() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is PL
 * @return the linked-list of data in the PL structure
 */
wdloPL* parse_wdlo_PL (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2,seeklen,i,N;
  wdloPL *ret=NULL,*icurrent=NULL,*inew;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  seeklen = wdlpass2_readShort(inputFile);
  while (seeklen > 0) {
    N = wdlpass2_readShort(inputFile);
    seeklen -= 2;
    inew = (wdloPL*)malloc(sizeof(wdloPL));
    memset(inew,0,sizeof(wdloPL));
    inew->N = N;
    inew->x = (int *)malloc(sizeof(int)*N);
    inew->y = (int *)malloc(sizeof(int)*N);
    if (icurrent==NULL) {
      ret = inew;
      icurrent = inew;
    } else {
      icurrent->next = inew;
      icurrent = inew;
    }
    for (i=0 ; i<N ; i++) {
      inew->x[i] = wdlpass2_readShort(inputFile);
      seeklen -= 2;
      inew->y[i] = wdlpass2_readShort(inputFile);
      seeklen -= 2;
    }
  }
  if (seeklen != 0) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s seeklen = %d != 0",__FUNCTION__,seeklen);
  }
  return ret;
}

/**
 * free the linked-list of PL data
 *
 * @param head the head of the linked-list
 */
void free_wdloPL (wdloPL *head) {
  wdloPL *tmp,*i;
  if (!head) {
    return;
  }
  i = head;
  do {
    tmp = i->next;
    if (i->x != NULL) {
      free(i->x);
      i->x = NULL;
    }
    if (i->y != NULL) {
      free(i->y);
      i->y = NULL;
    }
    free(i);
    i = tmp;
  } while (i != NULL);
}

/**
 * parsing the data for AP structure
 * User should use free_wdlo_AP() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is AP
 * @return the linked-list of data in the AP structure
 */
wdloAP* parse_wdlo_AP (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2,seeklen,i,N;
  wdloAP *ret=NULL,*icurrent=NULL,*inew;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  seeklen = wdlpass2_readShort(inputFile);
  while (seeklen > 0) {
    N = wdlpass2_readShort(inputFile);
    seeklen -= 2;
    inew = (wdloAP*)malloc(sizeof(wdloAP));
    memset(inew,0,sizeof(wdloAP));
    inew->N = N;
    inew->x = (int *)malloc(sizeof(int)*N);
    inew->y = (int *)malloc(sizeof(int)*N);
    if (icurrent==NULL) {
      ret = inew;
      icurrent = inew;
    } else {
      icurrent->next = inew;
      icurrent = inew;
    }
    for (i=0 ; i<N ; i++) {
      inew->x[i] = wdlpass2_readShort(inputFile);
      seeklen -= 2;
      inew->y[i] = wdlpass2_readShort(inputFile);
      seeklen -= 2;
    }
  }
  if (seeklen != 0) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s seeklen = %d != 0",__FUNCTION__,seeklen);
  }
  return ret;
}

/**
 * free the linked-list of AP data
 *
 * @param head the head of the linked-list
 */
void free_wdloAP (wdloAP *head) {
  wdloAP *tmp,*i;
  if (!head) {
    return;
  }
  i = head;
  do {
    tmp = i->next;
    if (i->x != NULL) {
      free(i->x);
      i->x = NULL;
    }
    if (i->y != NULL) {
      free(i->y);
      i->y = NULL;
    }
    free(i);
    i = tmp;
  } while (i != NULL);
}

/**
 * parsing the data for FR structure
 * User should use free_wdlo_FR() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is FR
 * @return the linked-list of data in the FR structure
 */
wdloFR* parse_wdlo_FR (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2,seeklen;
  wdloFR *ret=NULL,*icurrent=NULL,*inew;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  seeklen = wdlpass2_readShort(inputFile);
  while (seeklen > 0) {
    inew = (wdloFR*)malloc(sizeof(wdloFR));
    memset(inew,0,sizeof(wdloFR));
    if (icurrent==NULL) {
      ret = inew;
      icurrent = inew;
    } else {
      icurrent->next = inew;
      icurrent = inew;
    }
    inew->x1 = wdlpass2_readShort(inputFile);
    seeklen -= 2;
    inew->y1 = wdlpass2_readShort(inputFile);
    seeklen -= 2;
    inew->x2 = wdlpass2_readShort(inputFile);
    seeklen -= 2;
    inew->y2 = wdlpass2_readShort(inputFile);
    seeklen -= 2;
  }
  if (seeklen != 0) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s seeklen = %d != 0",__FUNCTION__,seeklen);
  }
  return ret;
}

/**
 * free the linked-list of FR data
 *
 * @param head the head of the linked-list
 */
void free_wdloFR (wdloFR *head) {
  wdloFR *tmp,*i;
  if (!head) {
    return;
  }
  i = head;
  do {
    tmp = i->next;
    free(i);
    i = tmp;
  } while (i != NULL);
}

/**
 * parsing the data for SD structure
 * User should use free_wdlo_SD() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is SD
 * @return the data in the SD structure
 */
wdloSD* parse_wdlo_SD (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2,seeklen,old_seeklen=0,i,j,N;
  wdloSD *ret=NULL;
  unsigned char *paletteData=NULL;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  seeklen = wdlpass2_readShort(inputFile);
  old_seeklen = seeklen;
  ret = (wdloSD*)malloc(sizeof(wdloSD));
  memset(ret,0,sizeof(wdloSD));
  ret->dest_x = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->dest_y = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->dest_width = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->dest_height = wdlpass2_readShort(inputFile); seeklen -= 2;
  for (i=0; i<4 ; i++) {
    ret->unknown_bytes_1[i]=fgetc(inputFile);
    seeklen-=1;
  }
  ret->src_width_short = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->src_height_short = wdlpass2_readShort(inputFile); seeklen -= 2;
  for (i=0 ; i<8 ; i++) {
    ret->unknown_bytes_2[i]=fgetc(inputFile);
    seeklen -= 1;
  }
  ret->src_width = wdlpass2_readInt(inputFile); seeklen -=4;
  ret->src_height = wdlpass2_readInt(inputFile); seeklen -= 4;
  ret->unknown_short_1 = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->color_depth = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->unknown_short_2 = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->compression_method = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->graph_data_len = wdlpass2_readInt(inputFile); seeklen -= 4;
  ret->unknown_int_1 = wdlpass2_readInt(inputFile); seeklen -= 4;
  ret->unknown_int_2 = wdlpass2_readInt(inputFile); seeklen -= 4;
  ret->unknown_int_3 = wdlpass2_readInt(inputFile); seeklen -= 4;
  ret->unknown_int_4 = wdlpass2_readInt(inputFile); seeklen -= 4;
  if (ret->unknown_int_3 != 0) {
    paletteData = (unsigned char *)malloc(sizeof(unsigned char)*1024);
    memset(paletteData,0,1024);
    fread(paletteData,1,1024,inputFile);
    seeklen -= 1024;
  }
  ret->graph_data_len_2 = wdlpass2_readInt(inputFile); seeklen -= 4;
  N = (ret->src_width)*(ret->src_height)*3;
  ret->graph_data = (unsigned char *)malloc(sizeof(unsigned char)*N);
  if (paletteData==NULL) {
    for (i=ret->src_height-1 ; i>=0; i--) {
      for (j=0 ; j<ret->src_width ; j++) {
        int r=0,g=0,b=0;
        b = fgetc(inputFile); seeklen--;
        g = fgetc(inputFile); seeklen--;
        r = fgetc(inputFile); seeklen--;
        ret->graph_data[(i*(ret->src_width)+j)*3]= ((unsigned char)r);
        ret->graph_data[(i*(ret->src_width)+j)*3+1]= ((unsigned char)g);
        ret->graph_data[(i*(ret->src_width)+j)*3+2]= ((unsigned char)b);
      }
      if ( (ret->src_width*3)%4 != 0) {
        for (j=0; j<4-((ret->src_width*3)%4); j++) {
          fgetc(inputFile); seeklen--;
        }
      }
    }
  } else {
    for (i=ret->src_height-1 ; i>=0; i--) {
      for (j=0 ; j<ret->src_width ; j++) { 
        int cindex;
        cindex = fgetc(inputFile); seeklen--;
        ret->graph_data[(i*(ret->src_width)+j)*3]= ((unsigned char)paletteData[cindex*4+2]);
        ret->graph_data[(i*(ret->src_width)+j)*3+1]= ((unsigned char)paletteData[cindex*4+1]);
        ret->graph_data[(i*(ret->src_width)+j)*3+2]= ((unsigned char)paletteData[cindex*4]);
      }
      if ( (ret->src_width)%4 != 0) {
        for (j=0; j<4-((ret->src_width)%4); j++) {
          fgetc(inputFile); seeklen--;
        }
      }
    }
    free(paletteData);
    paletteData=NULL;
  }
  if (old_seeklen !=0 && seeklen != 0) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s seeklen = %d != 0",__FUNCTION__,seeklen);
  }
  return ret;
}

/**
 * free the SD data
 *
 * @param head the pointer of the data
 */
void free_wdloSD (wdloSD *head) {
  if (!head) {
    return;
  }
  if (head->graph_data) {
    free(head->graph_data);
    head->graph_data = NULL;
  }
  free(head);
}

/**
 * parsing the data for SP structure
 * User should use free_wdlo_SP() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is SP
 * @return the data in the SP structure
 */
wdloSP* parse_wdlo_SP (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2,seeklen=0,i;
  wdloSP *ret=NULL;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  wdlpass2_readShort(inputFile);
  seeklen = wdlpass2_readInt(inputFile);
  ret = (wdloSP*)malloc(sizeof(wdloSP));
  memset(ret,0,sizeof(wdloSP));
  ret->dest_x = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->dest_y = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->dest_width = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->dest_height = wdlpass2_readShort(inputFile); seeklen -= 2;
  for (i=0; i<4 ; i++) {
    ret->unknown_bytes_1[i]=fgetc(inputFile);
    seeklen-=1;
  }
  ret->src_width_short = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->src_height_short = wdlpass2_readShort(inputFile); seeklen -= 2;
  for (i=0 ; i<10 ; i++) {
    ret->unknown_bytes_2[i]=fgetc(inputFile);
    seeklen -= 1;
  }
  ret->src_width = wdlpass2_readInt(inputFile); seeklen -=4;
  ret->src_height = wdlpass2_readInt(inputFile); seeklen -= 4;
  ret->unknown_short_1 = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->color_depth = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->unknown_short_2 = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->compression_method = wdlpass2_readShort(inputFile); seeklen -= 2;
  ret->graph_data_len = wdlpass2_readInt(inputFile); seeklen -= 4;
  ret->unknown_int_1 = wdlpass2_readInt(inputFile); seeklen -= 4;
  ret->unknown_int_2 = wdlpass2_readInt(inputFile); seeklen -= 4;
  ret->unknown_int_3 = wdlpass2_readInt(inputFile); seeklen -= 4;
  ret->unknown_int_4 = wdlpass2_readInt(inputFile); seeklen -= 4;
  if (ret->compression_method == 1) {
    ret->graph_data_len_2 = wdlpass2_readInt(inputFile); seeklen -= 4;
  }
  if (ret->compression_method == 2 
      || ret->compression_method == 6
      || ret->compression_method == 7) {
    int n;
    n=0;
    if (ret->color_depth == 1) {
      ret->graph_palette = malloc(sizeof(unsigned char)*4*2);
      n = fread(ret->graph_palette,1,4*2,inputFile);
    } else if (ret->color_depth == 8) {
      ret->graph_palette = malloc(sizeof(unsigned char)*4*256);
      n = fread(ret->graph_palette,1,4*256,inputFile);
    }
    seeklen -= n;
    ret->graph_data_len_2 = wdlpass2_readInt(inputFile); seeklen -= 4;
  }
  if (ret->graph_data_len_2 > 0) {
    int n=0;
    ret->graph_data = (unsigned char *)malloc(sizeof(unsigned char)*ret->graph_data_len_2);
    n = fread(ret->graph_data,1,ret->graph_data_len_2,inputFile);
    seeklen -= n;
  } 
  if (seeklen != 0) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s seeklen = %d != 0",__FUNCTION__,seeklen);
  }
  return ret;
}

/**
 * free the SP data
 *
 * @param head the pointer of the data
 */
void free_wdloSP (wdloSP *head) {
  if (!head) {
    return;
  }
  if (head->graph_data) {
    free(head->graph_data);
    head->graph_data = NULL;
  }
  if (head->graph_palette) {
    free(head->graph_palette);
    head->graph_palette = NULL;
  }
  free(head);
}

/**
 * parsing the data for ET structure
 * User should use free_wdlo_ET() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is ET
 * @return the linked-list of data in the ET structure
 */
wdloET* parse_wdlo_ET (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2,seeklen,i;
  wdloET *ret=NULL,*icurrent=NULL,*inew;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  seeklen = wdlpass2_readShort(inputFile);
  while (seeklen > 0) {
    inew = (wdloET*)malloc(sizeof(wdloET));
    memset(inew,0,sizeof(wdloET));
    inew->x = wdlpass2_readShort(inputFile);
    seeklen -= 2;
    inew->y = wdlpass2_readShort(inputFile);
    seeklen -= 2;
    if (icurrent==NULL) {
      ret = inew;
      icurrent = inew;
    } else {
      icurrent->next = inew;
      icurrent = inew;
    }
    inew->stringlen = wdlpass2_readShort(inputFile);
    seeklen -= 2;
    inew->flag1 = fgetc(inputFile);
    seeklen -= 1;
    inew->string = (char*)malloc(sizeof(char)*( (inew->stringlen)+1 ));
    memset(inew->string,0,sizeof(char)*( (inew->stringlen)+1 ));
    fread(inew->string,1,inew->stringlen,inputFile);
    seeklen -= inew->stringlen;
    if (inew->flag1 & 0x0001) {
      inew->flag1_0x1_x1 = wdlpass2_readShort(inputFile);
      seeklen -= 2;
      inew->flag1_0x1_y1 = wdlpass2_readShort(inputFile);
      seeklen -= 2;
      inew->flag1_0x1_x2 = wdlpass2_readShort(inputFile);
      seeklen -= 2;
      inew->flag1_0x1_y2 = wdlpass2_readShort(inputFile);
      seeklen -= 2;
    }
    if (inew->flag1 & 0x0002) {
      inew->flag1_0x2_width = (int*)malloc(sizeof(int)*(inew->stringlen));
      for (i=0 ; i<inew->stringlen ; i++) {
        inew->flag1_0x2_width[i] = wdlpass2_readShort(inputFile);
        seeklen -= 2;
      }
    }
    if (inew->flag1 > 3) {
      error(0,0,"Warning: Please report bugs: unknown ET flag01: %d",inew->flag1);
    }
  }
  if (seeklen != 0) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s seeklen = %d != 0",__FUNCTION__,seeklen);
  }
  return ret;
}

/**
 * free the linked-list of ET data
 *
 * @param head the head of the linked-list
 */
void free_wdloET(wdloET *head) {
  wdloET *tmp,*i;
  if (!head) {
    return;
  }
  i = head;
  do {
    tmp = i->next;
    if (i->string != NULL) {
      free(i->string);
      i->string = NULL;
    }
    if (i->flag1_0x2_width != NULL) {
      free(i->flag1_0x2_width);
      i->flag1_0x2_width=NULL;
    }
    free(i);
    i = tmp;
  } while (i != NULL);
}

/**
 * parsing the data for UT structure
 * User should use free_wdlo_UT() function to free the returned data after use.
 *
 * @param inputFile the file descriptor of the input wdlo file
 * @param idx the node which it's tag is UT
 * @return the linked-list of data in the UT structure
 */
wdloUT* parse_wdlo_UT (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2,seeklen,i;
  wdloUT *ret=NULL,*icurrent=NULL,*inew;
  
  fseek(inputFile,idx->pos,SEEK_SET);
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  seeklen = wdlpass2_readShort(inputFile);
  while (seeklen > 0) {
    inew = (wdloUT*)malloc(sizeof(wdloUT));
    memset(inew,0,sizeof(wdloUT));
    inew->x = wdlpass2_readShort(inputFile);
    seeklen -= 2;
    inew->y = wdlpass2_readShort(inputFile);
    seeklen -= 2;
    if (icurrent==NULL) {
      ret = inew;
      icurrent = inew;
    } else {
      icurrent->next = inew;
      icurrent = inew;
    }
    inew->utf16data_len = wdlpass2_readShort(inputFile);
    seeklen -= 2;
    inew->flag1 = fgetc(inputFile);
    seeklen -= 1;
    inew->utf16data = (char*)malloc(sizeof(char)*2*( (inew->utf16data_len)+1 ));
    memset(inew->utf16data,0,sizeof(char)*2*( (inew->utf16data_len)+1 ));
    fread(inew->utf16data,2,inew->utf16data_len,inputFile);
    seeklen -= (inew->utf16data_len*2);
    if (inew->flag1 & 0x0001) {
      inew->flag1_0x1_x1 = wdlpass2_readShort(inputFile);
      seeklen -= 2;
      inew->flag1_0x1_y1 = wdlpass2_readShort(inputFile);
      seeklen -= 2;
      inew->flag1_0x1_x2 = wdlpass2_readShort(inputFile);
      seeklen -= 2;
      inew->flag1_0x1_y2 = wdlpass2_readShort(inputFile);
      seeklen -= 2;
    }
    if (inew->flag1 & 0x0002) {
      inew->flag1_0x2_width = (int*)malloc(sizeof(int)*(inew->utf16data_len));
      for (i=0 ; i<inew->utf16data_len ; i++) {
        inew->flag1_0x2_width[i] = wdlpass2_readShort(inputFile);
        seeklen -= 2;
      }
    }
    if (inew->flag1 > 3) {
      error(0,0,"Warning: Please report bugs: unknown UT flag01: %d",inew->flag1);
    }
  }
  if (seeklen != 0) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s seeklen = %d != 0",__FUNCTION__,seeklen);
  }
  return ret;
}

/**
 * free the linked-list of UT data
 *
 * @param head the head of the linked-list
 */
void free_wdloUT(wdloUT *head) {
  wdloUT *tmp,*i;
  if (!head) {
    return;
  }
  i = head;
  do {
    tmp = i->next;
    if (i->utf16data != NULL) {
      free(i->utf16data);
      i->utf16data = NULL;
    }
    if (i->flag1_0x2_width != NULL) {
      free(i->flag1_0x2_width);
      i->flag1_0x2_width=NULL;
    }
    free(i);
    i = tmp;
  } while (i != NULL);
}

wdloSP01* parse_wdlo_SP01 (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2,seeklen,origpos,ret_font_face_len;
  wdloSP01* ret=NULL;
  static char* gb2312_fonts [] = { "\xd3\xd7\xd4\xb2" /* 幼圓 */,
                    "\xcb\xce\xcc\xe5" /* 宋体 */,
                    "\xba\xda\xcc\xe5" /* 黑体 */,
                    "\xc1\xa5\xca\xe9" /* 隶书 */ };

  origpos = ftell(inputFile);  
  fseek(inputFile,idx->pos,SEEK_SET);
  
  ret = (wdloSP01*)malloc(sizeof(wdloSP01));
  memset(ret,0,sizeof(wdloSP01));
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  seeklen = wdlpass2_readInt(inputFile);
  ret->index = idx->pos - idx->firstSpecialTagPos;
  ret->size = wdlpass2_readSignedShort(inputFile); seeklen -= 2;
  fread(ret->unknown_data,1,16,inputFile); seeklen -= 16;
  if (seeklen == 64) {
    seeklen -= fread(ret->font_face,1,seeklen,inputFile);
    strcpy(ret->font_face_encoding_guess,"utf16le");
  } else if (seeklen == 32) {
    seeklen -= fread(ret->font_face,1,seeklen,inputFile);
    ret_font_face_len = strlen(ret->font_face);
    if (ret_font_face_len>=7 && strcmp(&(ret->font_face[ret_font_face_len-7]),"_GB2312")==0) {
      strcpy(ret->font_face_encoding_guess,"gb2312");
    } else {
      int i;
      strcpy(ret->font_face_encoding_guess,"big5"); /* default guess big5 */
      for (i=0 ; i<sizeof(gb2312_fonts)/sizeof(gb2312_fonts[0]); i++) {
        if (strcmp(ret->font_face,gb2312_fonts[i])==0) { /* match gb2312 fonts */
          strcpy(ret->font_face_encoding_guess,"gb2312");
          break;
        }
      }
    }
  } else {     
    seeklen -= fread(ret->font_face,1,32,inputFile);
  }
  if (seeklen != 0) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s seeklen = %d != 0",__FUNCTION__,seeklen);
  }
  fseek(inputFile,origpos,SEEK_SET);
  return ret;
}

wdloSP02* parse_wdlo_SP02 (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2,seeklen,origpos;
  wdloSP02* ret=NULL;

  origpos = ftell(inputFile);  
  fseek(inputFile,idx->pos,SEEK_SET);
  
  ret = (wdloSP02*)malloc(sizeof(wdloSP02));
  memset(ret,0,sizeof(wdloSP02));
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  seeklen = wdlpass2_readInt(inputFile);
  ret->index = idx->pos - idx->firstSpecialTagPos;
  ret->unknown_short01 = wdlpass2_readShort(inputFile);
  ret->r = fgetc(inputFile);
  ret->g = fgetc(inputFile);
  ret->b = fgetc(inputFile);
  fread(ret->unknown_data_2,1,11,inputFile);

  fseek(inputFile,origpos,SEEK_SET);
  return ret;
}

wdloSP03* parse_wdlo_SP03 (FILE *inputFile,const wdloIndex* idx) {
  int c1,c2,seeklen,origpos;
  wdloSP03* ret=NULL;

  origpos = ftell(inputFile);  
  fseek(inputFile,idx->pos,SEEK_SET);
  
  ret = (wdloSP03*)malloc(sizeof(wdloSP03));
  memset(ret,0,sizeof(wdloSP03));
  c1 = fgetc(inputFile);
  c2 = fgetc(inputFile);
  seeklen = wdlpass2_readInt(inputFile);
  ret->index = idx->pos - idx->firstSpecialTagPos;
  ret->style = wdlpass2_readShort(inputFile); seeklen -=2;
  ret->width = wdlpass2_readShort(inputFile); seeklen -=2;
  fread(ret->unknown_data_1,1,2,inputFile); seeklen -=2;
  ret->r = fgetc(inputFile); seeklen -=1;
  ret->g = fgetc(inputFile); seeklen -=1;
  ret->b = fgetc(inputFile); seeklen -=1;
  ret->unknown_char_1 = fgetc(inputFile); seeklen -= 1;
  if (seeklen > 0) {
    seeklen -= fread(ret->unknown_data_2,1,6,inputFile);
  }
  if (seeklen != 0) {
    error_at_line(0,0,__FILE__,__LINE__,"Warning: Please report bugs: %s seeklen = %d != 0",__FUNCTION__,seeklen);
  }
  fseek(inputFile,origpos,SEEK_SET);
  return ret;
}
