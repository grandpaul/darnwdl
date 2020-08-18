## Process this file with automake to produce Makefile.in

INCLUDES = \
	-DPACKAGE_DATA_DIR=\""$(datadir)"\" \
	-DPACKAGE_LOCALE_DIR=\""$(prefix)/$(DATADIRNAME)/locale"\" \
	@PACKAGE_CFLAGS@

bin_PROGRAMS = darnwdl wdldec wdlpass2 wdl2txt

darnwdldir = $(pkgdatadir)
darnwdl_SOURCES = \
	main.c \
	support.c support.h \
	callbacks.c callbacks.h \
	myfunc.c myfunc.h wpass1.c wpass1.h wpass2.c wpass2.h \
	jpeginmemorydec.c jpeginmemorydec.h graphdec.c graphdec.h \
	pagerender.c pagerender.h fasterrender.c fasterrender.h

darnwdl_LDADD = @PACKAGE_LIBS@ $(INTLLIBS)
darnwdl_LDFLAGS = -ldynamite -export-dynamic
darnwdl_DATA = ../darnwdl.glade ../darnwdl.ui

wdldec_SOURCES = wdldec.c wpass2.c wpass2.h wpass1.c wpass1.h
wdldec_LDADD =
wdldec_LDFLAGS = -g -ldynamite

wdlpass2_SOURCES = pass2.c wpass2.c wpass2.h
wdlpass2_LDADD =

wdl2txt_SOURCES = wdl2txt.c wpass2.c wpass2.h wpass1.c wpass1.h
wdl2txt_LDFLAGS = -ldynamite
wdl2txt_LDADD = @PACKAGE_LIBS@ $(INTLLIBS)
