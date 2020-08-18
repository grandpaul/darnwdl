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

#include <unistd.h>

#include "wpass2.h"
#include "support.h"
#include "pagerender.h"
#include "fasterrender.h"

int fasterrender_page_divide = 10;
int fasterrender_previous_data_slot = 7;

fasterrender_data* fasterrender_init(FILE *wdloFile,wdloIndex *wdloI) {
  fasterrender_data *ret=NULL;
  int i,cp;
  wdloIndex *wi;
  wdloIndex *lastCR=NULL,*lastFT=NULL,*lastPN=NULL,*lastBH=NULL,
    *lastUF=NULL,*lastTC=NULL,*lastBC=NULL;
  
  if (wdloFile == NULL || wdloI == NULL) {
    return NULL;
  }
  ret = (fasterrender_data*)malloc(sizeof(fasterrender_data));
  memset(ret,0,sizeof(fasterrender_data));
  ret->wdloFile = wdloFile;
  ret->wdloI = wdloI;
  ret->wdloSPI = get_wdlo_index_sphead (ret->wdloI);
  ret->total_pages = calculate_wdlo_number_of_tags(ret->wdloI,"R2");
  ret->div = fasterrender_page_divide;
  ret->pages_div = (wdloIndex **)malloc( ((ret->total_pages/ret->div)+1)*sizeof(wdloIndex*) );
  memset(ret->pages_div,0,((ret->total_pages/ret->div)+1)*sizeof(wdloIndex*));
  ret->previous_data = (wdloIndex ***)malloc( ((ret->total_pages/ret->div)+1)*sizeof(wdloIndex**) );
  memset(ret->previous_data,0,((ret->total_pages/ret->div)+1)*sizeof(wdloIndex**));
  for (i=0 ; i<((ret->total_pages/ret->div)+1); i++) {
    ret->previous_data[i] = (wdloIndex**) malloc( fasterrender_previous_data_slot * sizeof(wdloIndex*) );
    memset(ret->previous_data[i],0,fasterrender_previous_data_slot * sizeof(wdloIndex*));
  }
  cp=-1;
  for (wi = ret->wdloI ; wi != NULL; wi = wi->next) {
    if (cp>=ret->total_pages) {
      break;
    }
    if (strcmp(wi->tag,"CR")==0) {
      lastCR = wi;
    } else if (strcmp(wi->tag,"FT")==0) {
      lastFT = wi;
    } else if (strcmp(wi->tag,"PN")==0) {
      lastPN = wi;
    } else if (strcmp(wi->tag,"BH")==0) {
      lastBH = wi;
    } else if (strcmp(wi->tag,"UF")==0) {
      lastUF = wi;
    } else if (strcmp(wi->tag,"TC")==0) {
      lastTC = wi;
    } else if (strcmp(wi->tag,"BC")==0) {
      lastBC = wi;
    } else if (strcmp(wi->tag,"R2")==0) {
      cp++;
      if (cp%ret->div == 0 && ret->pages_div[cp/ret->div]==NULL) {
        ret->pages_div[cp/ret->div] = wi;
        ret->previous_data[cp/ret->div][0] = lastCR;
        ret->previous_data[cp/ret->div][1] = lastFT;
        ret->previous_data[cp/ret->div][2] = lastPN;
        ret->previous_data[cp/ret->div][3] = lastBH;
        ret->previous_data[cp/ret->div][4] = lastUF;
        ret->previous_data[cp/ret->div][5] = lastTC;
        ret->previous_data[cp/ret->div][6] = lastBC;
      }
    }
  }
  return ret;
}

void fasterrender_destroy(fasterrender_data *fasterdata) {
  int i;
  if (fasterdata == NULL) {
    return;
  }
  if (fasterdata->previous_data != NULL) {
    for (i=0 ; i<((fasterdata->total_pages/fasterdata->div)+1); i++) {
      if (fasterdata->previous_data[i] != NULL) {
        free(fasterdata->previous_data[i]);
        fasterdata->previous_data[i] = NULL;
      }
    }
    free(fasterdata->previous_data);
    fasterdata->previous_data = NULL;
  }
  if (fasterdata->pages_div != NULL) {
    free(fasterdata->pages_div);
    fasterdata->pages_div = NULL;
  }
  free(fasterdata);
}

GdkPixmap* fasterrender_page2pixmap(int p,fasterrender_data *fasterdata,int scaleFactor_numerator,int scaleFactor_denominator) {
  GdkPixmap* ret=NULL;
  int cp,i,slot;
  wdloIndex **prev_backup;
  wdloIndex **next_backup;
  wdloIndex *page_prev_backup=NULL;
  wdloIndex *newHead=NULL,*newCurrent=NULL;
  
  cp = p-1;
  if (cp<0) return NULL;
  if (fasterdata==NULL) return NULL;
  if (cp>=fasterdata->total_pages) return NULL;
  
  slot = cp / fasterdata->div;
  prev_backup = (wdloIndex**)malloc(fasterrender_previous_data_slot * sizeof(wdloIndex*));
  memset(prev_backup,0,fasterrender_previous_data_slot * sizeof(wdloIndex*));
  next_backup = (wdloIndex**)malloc(fasterrender_previous_data_slot * sizeof(wdloIndex*));
  memset(next_backup,0,fasterrender_previous_data_slot * sizeof(wdloIndex*));
  
  for (i=0 ; i<fasterrender_previous_data_slot; i++) {
    if (fasterdata->previous_data[slot][i]==NULL) {
      continue;
    }
    /* backup prev and next */
    prev_backup[i] = fasterdata->previous_data[slot][i]->prev;
    next_backup[i] = fasterdata->previous_data[slot][i]->next;
    /* seperate the node */
    if (prev_backup[i] != NULL) {
      prev_backup[i]->next = next_backup[i];
    }
    if (next_backup[i] != NULL) {
      next_backup[i]->prev = prev_backup[i];
    }
    fasterdata->previous_data[slot][i]->prev=NULL;
    fasterdata->previous_data[slot][i]->next=NULL;
    /* chain to our new List */
    if (newCurrent == NULL) {
      newCurrent = fasterdata->previous_data[slot][i];
      newHead = newCurrent;
    } else {
      fasterdata->previous_data[slot][i]->prev = newCurrent;
      newCurrent->next = fasterdata->previous_data[slot][i];
      newCurrent = newCurrent->next;
    }
  }
  /* chain page */
  if (newCurrent == NULL) {
    newCurrent = fasterdata->pages_div[slot];
    newHead = newCurrent;
  } else {
    newCurrent->next = fasterdata->pages_div[slot];
    if (newCurrent->next != NULL) {
      page_prev_backup = newCurrent->next->prev;
      newCurrent->next->prev = newCurrent;
    }
  }
  /* do draw */
  if (newHead != NULL) {
    ret = page2pixmap((cp%fasterdata->div)+1,fasterdata->wdloFile,newHead,fasterdata->wdloSPI,scaleFactor_numerator,scaleFactor_denominator);
  }
  /* restore page head */
  if (fasterdata->pages_div[slot] != NULL) {
    fasterdata->pages_div[slot]->prev = page_prev_backup;
  }
  /* restore chain */
  for (i=0 ; i<fasterrender_previous_data_slot; i++) {
    if (fasterdata->previous_data[slot][i]==NULL) {
      continue;
    }
    /* restore the prev and next */
    fasterdata->previous_data[slot][i]->prev = prev_backup[i];
    fasterdata->previous_data[slot][i]->next = next_backup[i];
    /* re-chain the node */
    if (prev_backup[i] != NULL) {
      prev_backup[i]->next = fasterdata->previous_data[slot][i];
    }
    if (next_backup[i] != NULL) {
      next_backup[i]->prev = fasterdata->previous_data[slot][i];
    }
  }
  if (prev_backup != NULL) {
    free(prev_backup);
    prev_backup=NULL;
  }
  if (next_backup != NULL) {
    free(next_backup);
    next_backup=NULL;
  }
  return ret;
}
