/*
 * Initial main.c file generated by Glade. Edit as required.
 * Glade will not overwrite this file.
 */

#ifdef HAVE_CONFIG_H
#  include <config.h>
#endif

#include <gtk/gtk.h>

#include "support.h"
#include "myfunc.h"

int
main (int argc, char *argv[])
{
  GtkWidget *mainWindow;
  char *xmlfilename;
  char *iconfilename;
  GdkPixbuf *icon=NULL;
  GList *iconList=NULL;
  GError *error = NULL;
  GOptionContext *context = NULL;

#ifdef ENABLE_NLS
  bindtextdomain (GETTEXT_PACKAGE, PACKAGE_LOCALE_DIR);
  bind_textdomain_codeset (GETTEXT_PACKAGE, "UTF-8");
  textdomain (GETTEXT_PACKAGE);
#endif

  gtk_set_locale ();
  gtk_init (&argc, &argv);

  add_directory (PACKAGE_DATA_DIR "/" PACKAGE "/pixmaps");
  add_directory ("./pixmaps");
  add_directory ("../pixmaps");

  /*
   * The following code was added by Glade to create one of each component
   * (except popup menus), just so that you see something after building
   * the project. Delete any components that you don't want shown initially.
   */
  add_directory(PACKAGE_DATA_DIR "/" PACKAGE);
  add_directory(".");
  add_directory("..");
  xmlfilename=find_file("darnwdl.ui");
  gtkbuilder = gtk_builder_new ();
  if (!gtk_builder_add_from_file (gtkbuilder, xmlfilename, &error))
  {
    g_warning ("Couldn't load builder file: %s", error->message);
    g_error_free (error);
  }
  g_free(xmlfilename);
  xmlfilename=NULL;
  
  iconfilename=find_file("darnwdlicon.svg");
  icon=create_pixbuf(iconfilename);
  g_free(iconfilename);
  iconfilename=NULL;

  mainWindow = GTK_WIDGET(gtk_builder_get_object (gtkbuilder, "mainWindow"));
  gtk_builder_connect_signals(gtkbuilder,NULL);
  gtk_widget_show (mainWindow);
  iconList = g_list_append(iconList,icon);
  gtk_window_set_default_icon_list(iconList);
  gtk_window_set_icon_list(GTK_WINDOW(mainWindow),iconList);
  
  
  context = g_option_context_new("darnwdl WDL reader");
  g_option_context_parse (context, &argc, &argv, &error);
  
  g_option_context_free(context);
  
  if (argc == 2 && argv[1] != '\0') {
    FILE *tempfile=NULL;
    wdlpass1_fileheader* wdlpass1header=NULL;
    myfunc_closewdlo();
    if (g_str_has_suffix(argv[1],".wdlo")) {
      myfunc_openwdlo(argv[1]);
    } else {
      tempfile = tmpfile();
      wdlpass1header = wdlpass1_dec_file(tempfile,argv[1]);
      if (wdlpass1header != NULL) {
        myfunc_openwdlo_file(tempfile);
        free (wdlpass1header);
        wdlpass1header=NULL;
      }
    }
    on_toolbuttonfirst_clicked(GTK_TOOL_BUTTON(lookup_widget(mainWindow,"toolbuttonfirst")),NULL);
  }

  gtk_main ();
  return 0;
}

