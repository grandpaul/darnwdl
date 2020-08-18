/* 
   WDL decompressed data parser
   Copyright (C) 2005-2006 Ying-Chun Liu (PaulLiu) 
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
#include "wpass2.h"

FILE *myout = NULL;

int parse(FILE *inputFile) {
  int c1,c2,seeklen,i,j,tmp1,tmp2,tmp3,tmp4,etpos_x,etpos_y;
  int specialstart=-1;
  char tag[3];
  
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

    if (tag[1] != '\0') {
      fprintf (myout,"Parsing %s at 0x%08x\n",tag,(unsigned int)ftell(inputFile)-2);
    } else {
      fprintf (myout,"Parsing special tag %d at 0x%08x\n",(int)tag[0],(unsigned int)ftell(inputFile)-2);
    }    
    if (strcmp(tag,"FT")==0) {
      tmp1 = wdlpass2_readInt(inputFile);
      fprintf (myout,"FT: %d\n",tmp1);
    } else if (strcmp(tag,"BC")==0) {
      tmp1 = fgetc(inputFile);
      tmp2 = fgetc(inputFile);
      tmp3 = fgetc(inputFile);
      tmp4 = fgetc(inputFile);
      fprintf (myout,"BC R: %d\n",tmp1);
      fprintf (myout,"BC G: %d\n",tmp2);
      fprintf (myout,"BC B: %d\n",tmp3);
      fprintf (myout,"BC unknown byte: %d\n",tmp4);
    } else if (strcmp(tag,"BM")==0) {
      tmp1 = wdlpass2_readShort(inputFile);
      fprintf (myout,"BM: %d\n",tmp1);
    } else if (strcmp(tag,"BH")==0) {
      tmp1 = wdlpass2_readInt(inputFile);
      fprintf (myout,"BH: %d\n",tmp1);
    } else if (strcmp(tag,"TC")==0) {
      tmp1 = fgetc(inputFile);
      tmp2 = fgetc(inputFile);
      tmp3 = fgetc(inputFile);
      tmp4 = fgetc(inputFile);
      fprintf (myout,"TC R: %d\n",tmp1);
      fprintf (myout,"TC G: %d\n",tmp2);
      fprintf (myout,"TC B: %d\n",tmp3);
      fprintf (myout,"TC unknown byte: %d\n",tmp4);
    } else if (strcmp(tag,"PN")==0) {
      tmp1 = wdlpass2_readInt(inputFile);
      fprintf (myout,"PN: %d\n",tmp1);
    } else if (strcmp(tag,"R2")==0) {
      tmp1 = wdlpass2_readShort(inputFile);
      fprintf (myout,"R2: %d\n",tmp1);
    } else if (strcmp(tag,"CT")==0) {
      tmp1 = wdlpass2_readShort(inputFile);
      fprintf (myout,"CT: %d\n",tmp1);
    } else if (strcmp(tag,"UF")==0) {
      tmp1 = wdlpass2_readShort(inputFile);
      tmp2 = wdlpass2_readInt(inputFile);
      fprintf (myout,"UF: %d %d\n",tmp1,tmp2);
    } else if (strcmp(tag,"CR")==0) {
      tmp1 = wdlpass2_readShort(inputFile);
      tmp2 = wdlpass2_readShort(inputFile);
      tmp3 = wdlpass2_readShort(inputFile);
      tmp4 = wdlpass2_readShort(inputFile);
      fprintf (myout,"CR: %d %d %d %d\n",tmp1,tmp2,tmp3,tmp4);
    } else if (strcmp(tag,"ET")==0 || strcmp(tag,"EU")==0) {
      int str_len;
      int flag01=0;
      char *str;
      seeklen = wdlpass2_readShort(inputFile);
      while (seeklen > 0) {
        etpos_x = wdlpass2_readShort(inputFile);
        etpos_y = wdlpass2_readShort(inputFile);
        fprintf (myout,"ET position: %d %d\n",etpos_x,etpos_y);
        seeklen -= 4;
        str_len = wdlpass2_readShort(inputFile);
        seeklen -= 2;
        flag01 = fgetc(inputFile);
        seeklen -= 1;
        str = malloc(sizeof(char)*(str_len+1));
        memset(str,0,sizeof(char)*str_len+1);
        fread(str,1,str_len,inputFile);
        fprintf (myout,"ET string: ");
	wdlpass2_QPoutput(myout,str,str_len);
        fprintf (myout,"\n");
        seeklen -= str_len;
        free(str);
        if (flag01 & 0x0001) {
            int i1,i2,i3,i4;
            i1=wdlpass2_readShort(inputFile);
            i2=wdlpass2_readShort(inputFile);
            seeklen -= 4;
            i3=wdlpass2_readShort(inputFile);
            i4=wdlpass2_readShort(inputFile);
            seeklen -= 4;
            fprintf (myout,"ET flag01(0x1): %d %d %d %d\n",i1,i2,i3,i4);
        }
        if (flag01 & 0x0002) {
            int i1;
            fprintf (myout,"ET flag01(0x2): ");
            for (i=0 ; i<str_len ; i++) {
              i1 = wdlpass2_readShort(inputFile);
              fprintf (myout,"%d ",i1);
              seeklen -= 2;
            }
            fprintf (myout,"\n");
        }
        if (flag01 > 3) {
            fprintf(myout,"Warning: Please report bugs: unknown ET flag01: %d\n",flag01);
            break;
        }
      }
      if (seeklen != 0) {
        fprintf(myout,"Warning: Please report bugs: ET seeklen != 0 \n");
        fseek(inputFile,seeklen,SEEK_CUR);
      }
    } else if (strcmp(tag,"FR")==0) {
      int d;
      seeklen = wdlpass2_readShort(inputFile);
      while (seeklen > 0) {
        fprintf(myout,"FR: ");
        for (i=0 ; i<4 ; i++) {
          d = wdlpass2_readShort(inputFile);
          if (i!=0) {
            fprintf(myout," ");
          }
          fprintf(myout,"%d",d);
          seeklen-=2;
        }
        fprintf(myout,"\n");
      }
      if (seeklen != 0) {
        fprintf(myout,"Warning: Please report bugs: FR seeklen != 0 \n");
        fseek(inputFile,seeklen,SEEK_CUR);
      }
    } else if (strcmp(tag,"CP")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"PL")==0) {
      int N,x1,y1;
      seeklen = wdlpass2_readShort(inputFile);
      while (seeklen > 0) {
        fprintf(myout,"PL: ");
        N = wdlpass2_readShort(inputFile);
        seeklen -= 2;
        for (i=0 ; i<N ; i++) {
          x1 = wdlpass2_readShort(inputFile);
          seeklen -= 2;
          y1 = wdlpass2_readShort(inputFile);
          seeklen -= 2;
          if (i!=0) {
            fprintf(myout,"->");
          }
          fprintf (myout,"(%d,%d)",x1,y1);
        }
        fprintf(myout,"\n");
      }
      if (seeklen != 0) {
        fprintf(myout,"Warning: Please report bugs: PL seeklen != 0 \n");
        fseek(inputFile,seeklen,SEEK_CUR);
      }
    } else if (strcmp(tag,"AP")==0) {
      int N,x1,y1;
      seeklen = wdlpass2_readShort(inputFile);
      while (seeklen > 0) {
        fprintf(myout,"AP: ");
        N = wdlpass2_readShort(inputFile);
        seeklen -= 2;
        for (i=0 ; i<N ; i++) {
          x1 = wdlpass2_readShort(inputFile);
          seeklen -= 2;
          y1 = wdlpass2_readShort(inputFile);
          seeklen -= 2;
          if (i!=0) {
            fprintf(myout,"->");
          }
          fprintf (myout,"(%d,%d)",x1,y1);
        }
        fprintf(myout,"\n");
      }
      if (seeklen != 0) {
        fprintf(myout,"Warning: Please report bugs: AP seeklen != 0 \n");
        fseek(inputFile,seeklen,SEEK_CUR);
      }
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
      int x,y,w,h;
      int unknown_data_01[12];
      int src_width_short;
      int src_height_short;
      int unknown_data_02[8];
      int src_width;
      int src_height;
      int unknown_data_03[8];
      int graph_data_len;
      int unknown_int_1;
      int unknown_int_2;
      int unknown_int_3;
      int unknown_int_4;
      int graph_data_len_2;
      int old_seeklen = 0;
      int ppm_newline=0;
      int currentPos=0;
      seeklen = wdlpass2_readShort(inputFile);
      old_seeklen = seeklen;
      x = wdlpass2_readShort(inputFile);
      y = wdlpass2_readShort(inputFile);
      w = wdlpass2_readShort(inputFile);
      h = wdlpass2_readShort(inputFile);
      seeklen-=8;
      fprintf(myout,"SD dest_x,dest_y: %d %d\n",x,y);
      fprintf(myout,"SD dest_width,dest_height: %d %d\n",w,h);
      for (i=0 ; i<4 ; i++) {
        unknown_data_01[i] = fgetc(inputFile);
        seeklen -= 1;
        fprintf (myout,"SD unknown_data_01[%d]: %d\n",i,unknown_data_01[i]);
      }
      src_width_short = wdlpass2_readShort(inputFile);
      seeklen -= 2;
      fprintf (myout,"SD src_width_short: %d\n",src_width_short);
      src_height_short = wdlpass2_readShort(inputFile);
      seeklen -= 2;
      fprintf (myout,"SD src_height_short: %d\n",src_height_short);
      for (i=0 ; i<8 ; i++) {
        unknown_data_02[i] = fgetc(inputFile);
        seeklen -= 1;
        fprintf (myout,"SD unknown_data_02[%d]: %d\n",i,unknown_data_02[i]);
      }
      src_width = wdlpass2_readInt(inputFile);
      seeklen-=4;
      fprintf (myout,"SD src_width: %d\n",src_width);
      src_height = wdlpass2_readInt(inputFile);
      seeklen-=4;
      fprintf (myout,"SD src_height: %d\n",src_height);
      for (i=0 ; i<8 ; i++) {
        unknown_data_03[i] = fgetc(inputFile);
        seeklen -= 1;
        fprintf (myout,"SD unknown_data_03[%d]: %d\n",i,unknown_data_03[i]);
      }
      graph_data_len = wdlpass2_readInt(inputFile);
      seeklen-=4;
      fprintf (myout,"SD graph_data_len: %d\n",graph_data_len);
      unknown_int_1 = wdlpass2_readInt(inputFile);
      seeklen-=4;
      fprintf (myout,"SD unknwon_int_1: %d\n",unknown_int_1);
      unknown_int_2 = wdlpass2_readInt(inputFile);
      seeklen-=4;
      fprintf (myout,"SD unknwon_int_2: %d\n",unknown_int_2);
      unknown_int_3 = wdlpass2_readInt(inputFile);
      seeklen-=4;
      fprintf (myout,"SD unknwon_int_3: %d\n",unknown_int_3);
      unknown_int_4 = wdlpass2_readInt(inputFile);
      seeklen-=4;
      fprintf (myout,"SD unknwon_int_4: %d\n",unknown_int_4);
      if (unknown_int_3 != 0) {
        fseek(inputFile,1024,SEEK_CUR);
        seeklen -= 1024;
      }
      graph_data_len_2 = wdlpass2_readInt(inputFile);
      seeklen-=4;
      fprintf (myout,"SD graph_data_len_2: %d\n",graph_data_len_2);
      currentPos = ftell(inputFile);
      fprintf (myout,"SD graph data: \n");
      fprintf (myout,"P3\n# unknown.ppm\n%d %d\n255\n",src_width,src_height);
      for (i=0 ; i<src_height ; i++) {
        for (j=0 ; j<src_width ; j++) {
          int r=0,g=0,b=0;
          r= fgetc(inputFile); seeklen --;
          g= fgetc(inputFile); seeklen --;
          b= fgetc(inputFile); seeklen --;
          fprintf(myout,"%3d %3d %3d  ",r,g,b);
          ppm_newline++;
          if (ppm_newline>5) {
            fprintf(myout,"\n");
            ppm_newline=0;
          }
        }
        if ( (src_width*3)%4 != 0) {
          for (j=0 ; j<4 - ((src_width*3)%4); j++) {
            fgetc(inputFile); seeklen--;
          }
        }
      }
      if (ppm_newline != 0) {
        fprintf(myout,"\n");
        ppm_newline=0;
      }
      fprintf (myout,"SD seek remain: %d\n",seeklen);
      if (old_seeklen != 0) {
	if (seeklen != 0) {
	  fprintf(myout,"Warning: SD seeklen != 0\n");
          fseek(inputFile,seeklen,SEEK_CUR);
        }
      } else {
        fprintf(myout,"SD original seeklen=0\n");
        fseek(inputFile,currentPos,SEEK_SET);
        fseek(inputFile,graph_data_len_2,SEEK_CUR);
      }
    } else if (strcmp(tag,"SX")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"EP")==0) {
      seeklen = wdlpass2_readShort(inputFile);
      fseek(inputFile,seeklen,SEEK_CUR);
    } else if (strcmp(tag,"UT")==0) {
      int bindata_len,utpos_x,utpos_y;
      int flag01=0;
      char *bindata;
      seeklen = wdlpass2_readShort(inputFile);
      while (seeklen > 0) {
        utpos_x = wdlpass2_readShort(inputFile);
        utpos_y = wdlpass2_readShort(inputFile);
        fprintf (myout,"UT position: %d %d\n",utpos_x,utpos_y);
        seeklen -= 4;
        bindata_len = wdlpass2_readShort(inputFile);
        seeklen -= 2;
        flag01 = fgetc(inputFile);
        seeklen -= 1;
        bindata = malloc(sizeof(char)*(bindata_len*2+1));
        memset(bindata,0,sizeof(char)*bindata_len*2+1);
        fread(bindata,1,bindata_len*2,inputFile);
        fprintf (myout,"UT string: ");
	wdlpass2_QPoutput(myout,bindata,bindata_len*2);
        fprintf (myout,"\n");
        seeklen -= bindata_len*2;
        free(bindata);
        if (flag01 & 0x0001) {
            int i1,i2,i3,i4;
            i1=wdlpass2_readShort(inputFile);
            i2=wdlpass2_readShort(inputFile);
            seeklen -= 4;
            i3=wdlpass2_readShort(inputFile);
            i4=wdlpass2_readShort(inputFile);
            seeklen -= 4;
            fprintf (myout,"UT flag01(0x1): %d %d %d %d\n",i1,i2,i3,i4);
        }
        if (flag01 & 0x0002) {
            int i1;
            fprintf (myout,"UT flag01(0x2): ");
            for (i=0 ; i<bindata_len ; i++) {
              i1 = wdlpass2_readShort(inputFile);
              fprintf (myout,"%d ",i1);
              seeklen -= 2;
            }
            fprintf (myout,"\n");
        }
        if (flag01 > 3) {
            fprintf(myout,"Warning: Please report bugs: unknown UT flag01: %d\n",flag01);
            break;
        }
      }
      if (seeklen != 0) {
        fprintf(myout,"Warning: Please report bugs: UT seeklen != 0 \n");
        fseek(inputFile,seeklen,SEEK_CUR);
      }
    } else if (tag[1]=='\0') {
      if (specialstart == -1) {
        specialstart = ftell(inputFile)-2;
      }
      fprintf (myout,"special tag index: %ld\n",ftell(inputFile)-2-specialstart);
      seeklen = wdlpass2_readInt(inputFile);
      if (tag[0]=='\x01') {
	int sp01size;
	int unknownint[16];
	char fontstr[32+1];
	memset(fontstr,0,sizeof(fontstr));
	sp01size = wdlpass2_readSignedShort(inputFile);
	seeklen-=2;
	fprintf (myout,"\\1\\0 size: %d\n",sp01size);
	for (i=0 ; i<16 ; i++) {
	  unknownint[i] = fgetc(inputFile);
	  seeklen -= 1;
          fprintf (myout,"\\1\\0 unknown_data[%d]: %d\n",i,unknownint[i]);
        }
	fread(fontstr,1,32,inputFile);
	seeklen -= 32;
	fprintf(myout,"\\1\\0 font: ");
	wdlpass2_QPoutput(myout,fontstr,strlen(fontstr));
	fprintf(myout,"\n");
	if (seeklen != 0) {
	  fprintf(myout,"Warning: \\1\\0 seeklen = %d != 0\n",seeklen);
	  fseek(inputFile,seeklen,SEEK_CUR);
	}
      }
      else if (tag[0]=='\x02') {
	int unknown_short_01;
	int unknownint2[11];
	int r,g,b;
	unknown_short_01 = wdlpass2_readShort(inputFile);
	seeklen-=2;
        fprintf (myout,"\\2\\0 unknown_short_01: %d\n",unknown_short_01);
        r = fgetc(inputFile);
        g = fgetc(inputFile);
        b = fgetc(inputFile);
	seeklen -= 3;
	fprintf(myout,"\\2\\0 r g b: %d %d %d\n",r,g,b);
	for (i=0 ; i<11 ; i++) {
	  unknownint2[i] = fgetc(inputFile);
	  seeklen -= 1;
          fprintf (myout,"\\2\\0 unknown_data_2[%d]: %d\n",i,unknownint2[i]);
        }
	if (seeklen != 0) {
	  fprintf(myout,"Warning: \\2\\0 seeklen = %d != 0\n",seeklen);
	  fseek(inputFile,seeklen,SEEK_CUR);
	}
      }
      else if (tag[0]=='\x03') {
	int unknownint1[6];
	int unknownint2[7];
	int r,g,b;
	for (i=0 ; i<6 ; i++) {
	  unknownint1[i] = fgetc(inputFile);
	  seeklen -= 1;
          fprintf (myout,"\\3\\0 unknown_data_1[%d]: %d\n",i,unknownint1[i]);
        }
        r = fgetc(inputFile);
        g = fgetc(inputFile);
        b = fgetc(inputFile);
	seeklen -= 3;
	fprintf(myout,"\\3\\0 r g b: %d %d %d\n",r,g,b);
	for (i=0 ; i<7 ; i++) {
	  unknownint2[i] = fgetc(inputFile);
	  seeklen -= 1;
          fprintf (myout,"\\3\\0 unknown_data_2[%d]: %d\n",i,unknownint2[i]);
        }
	if (seeklen != 0) {
	  fprintf(myout,"Warning: \\3\\0 seeklen = %d != 0\n",seeklen);
	  fseek(inputFile,seeklen,SEEK_CUR);
	}
      } else {
        fseek(inputFile,seeklen,SEEK_CUR);
      }
    } else {
      fprintf(myout,"Warning: Please report bugs: Unknown tag %s\n",tag);
      fseek(inputFile,-1,SEEK_CUR);
    }
  }
  return 0;
}      
    

int main(int argc,char *argv[]) {
  FILE *inputFile;
  
  myout = stdout;
  
  if (argc < 2) {
    printf("Usage: %s <input>\n",argv[0]);
    return 0;
  }
  inputFile = fopen(argv[1],"rb");
  if (inputFile==NULL) {
    fprintf(myout,"Cannot open file %s\n",argv[1]);
    return 0;
  }
  parse(inputFile);
  return 0;
}
