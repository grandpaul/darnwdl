#ifndef __HEADER_DARNWDL_WPASS2_H__
#define __HEADER_DARNWDL_WPASS2_H__

int wdlpass2_readInt (FILE * file1);
int wdlpass2_readShort (FILE * file1);
int wdlpass2_readSignedShort (FILE * file1);
int wdlpass2_QPoutput(FILE *file1,char *str,int str_len);

typedef struct wdloIndexS {
  char tag[3];
  int pos;
  struct wdloIndexS* next;
  struct wdloIndexS* prev;
  int firstSpecialTagPos;
} wdloIndex;

typedef struct wdloFTS {
  int index;
} wdloFT;

typedef struct wdloPNS {
  int index;
} wdloPN;

typedef struct wdloBHS {
  int index;
} wdloBH;

typedef struct wdloUFS {
  int unknown_short;
  int index;
} wdloUF;

typedef struct wdloBCS {
  int r;
  int g;
  int b;
  int unknown_byte;
} wdloBC;

typedef struct wdloR2S {
  int unknown_short;
} wdloR2;

typedef struct wdloTCS {
  int r;
  int g;
  int b;
  int unknown_byte;
} wdloTC;

typedef struct wdloCRS {
  int x1;
  int y1;
  int x2;
  int y2;
} wdloCR;

typedef struct wdloPLS {
  int N;
  int *x;
  int *y;
  struct wdloPLS *next;
} wdloPL;

typedef struct wdloAPS {
  int N;
  int *x;
  int *y;
  struct wdloAPS *next;
} wdloAP;

typedef struct wdloFRS {
  int x1;
  int y1;
  int x2;
  int y2;
  struct wdloFRS *next;
} wdloFR;

typedef struct wdloUTS {
  int x;
  int y;
  int utf16data_len;
  int flag1;
  char *utf16data;
  int flag1_0x1_x1;
  int flag1_0x1_y1;
  int flag1_0x1_x2;
  int flag1_0x1_y2;
  int *flag1_0x2_width;
  struct wdloUTS *next;
} wdloUT;

typedef struct wdloETS {
  int x;
  int y;
  int stringlen;
  int flag1;
  char *string;
  int flag1_0x1_x1;
  int flag1_0x1_y1;
  int flag1_0x1_x2;
  int flag1_0x1_y2;
  int *flag1_0x2_width;
  struct wdloETS *next;
} wdloET;

typedef struct wdloSDS {
  int dest_x;
  int dest_y;
  int dest_width;
  int dest_height;
  char unknown_bytes_1[4];
  int src_width_short;
  int src_height_short;
  char unknown_bytes_2[8];
  int src_width;
  int src_height;
  int unknown_short_1;
  int color_depth;
  int unknown_short_2;
  int compression_method;
  int graph_data_len;
  int unknown_int_1;
  int unknown_int_2;
  int unknown_int_3;
  int unknown_int_4;
  int graph_data_len_2;
  unsigned char *graph_data;
} wdloSD;

typedef struct wdloSPS {
  int dest_x;
  int dest_y;
  int dest_width;
  int dest_height;
  char unknown_bytes_1[4];
  int src_width_short;
  int src_height_short;
  char unknown_bytes_2[10];
  int src_width;
  int src_height;
  int unknown_short_1;
  int color_depth;
  int unknown_short_2;
  int compression_method;
  int graph_data_len;
  int unknown_int_1;
  int unknown_int_2;
  int unknown_int_3;
  int unknown_int_4;
  int graph_data_len_2;
  unsigned char *graph_palette;
  unsigned char *graph_data;
} wdloSP;

typedef struct wdloSP01S {
  int index;
  int size;
  char unknown_data[16];
  char font_face[32*2+1];
  char font_face_encoding_guess[128];
} wdloSP01;

typedef struct wdloSP02S {
  int index;
  int unknown_short01;
  int r;
  int g;
  int b;
  char unknown_data_2[11];
} wdloSP02;

typedef struct wdloSP03S {
  int index;
  int style;
  int width;
  char unknown_data_1[2];
  int r;
  int g;
  int b;
  char unknown_char_1;
  char unknown_data_2[6];
} wdloSP03;

wdloIndex* generate_wdlo_index (FILE *inputFile);
void free_wdloIndex(wdloIndex *head);
wdloIndex* get_wdlo_index_sphead (wdloIndex *idx);
int calculate_wdlo_number_of_tags(wdloIndex *idx,char *tagid);
int wdlpass2_utf16le_strlen(const char *str);
wdloFT* parse_wdlo_FT (FILE *inputFile,const wdloIndex* idx);
wdloPN* parse_wdlo_PN (FILE *inputFile,const wdloIndex* idx);
wdloBH* parse_wdlo_BH (FILE *inputFile,const wdloIndex* idx);
wdloUF* parse_wdlo_UF (FILE *inputFile,const wdloIndex* idx);
wdloR2* parse_wdlo_R2 (FILE *inputFile,const wdloIndex* idx);
wdloCR* parse_wdlo_CR (FILE *inputFile,const wdloIndex* idx);
wdloTC* parse_wdlo_TC (FILE *inputFile,const wdloIndex* idx);
wdloBC* parse_wdlo_BC (FILE *inputFile,const wdloIndex* idx);
wdloET* parse_wdlo_ET (FILE *inputFile,const wdloIndex* idx);
void free_wdloET(wdloET *head);
wdloUT* parse_wdlo_UT (FILE *inputFile,const wdloIndex* idx);
void free_wdloUT(wdloUT *head);
wdloPL* parse_wdlo_PL (FILE *inputFile,const wdloIndex* idx);
void free_wdloPL(wdloPL *head);
wdloAP* parse_wdlo_AP (FILE *inputFile,const wdloIndex* idx);
void free_wdloAP(wdloAP *head);
wdloFR* parse_wdlo_FR (FILE *inputFile,const wdloIndex* idx);
void free_wdloFR(wdloFR *head);
wdloSD* parse_wdlo_SD (FILE *inputFile,const wdloIndex* idx);
void free_wdloSD(wdloSD *head);
wdloSP* parse_wdlo_SP (FILE *inputFile,const wdloIndex* idx);
void free_wdloSP(wdloSP *head);
wdloSP01* parse_wdlo_SP01 (FILE *inputFile,const wdloIndex* idx);
wdloSP02* parse_wdlo_SP02 (FILE *inputFile,const wdloIndex* idx);
wdloSP03* parse_wdlo_SP03 (FILE *inputFile,const wdloIndex* idx);

#endif
