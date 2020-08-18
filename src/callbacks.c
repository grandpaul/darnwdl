#ifdef HAVE_CONFIG_H
#  include <config.h>
#endif

#include <gtk/gtk.h>
#include <gdk/gdkkeysyms.h>

#include "callbacks.h"
#include "support.h"
#include "myfunc.h"
#include "pagerender.h"

void
on_new1_activate                       (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{

}

void
on_save1_activate                      (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{

}


void
on_save_as1_activate                   (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{

}


void
on_cut1_activate                       (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{

}


void
on_copy1_activate                      (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{

}


void
on_paste1_activate                     (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{

}


void
on_delete1_activate                    (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{

}


void
on_about1_activate                     (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{

}

void
on_filechooserwidget1_selection_changed
                                        (GtkFileChooser  *filechooser,
                                        gpointer         user_data)
{
  GtkEntry *entry1;
  gchar *filename=NULL;
  filename = gtk_file_chooser_get_filename(filechooser);

  if (filename != NULL) {
    entry1 = GTK_ENTRY(lookup_widget(GTK_WIDGET(filechooser),"selectedentry1"));
    gtk_entry_set_text(entry1,filename);
  }
}


void
on_fileselectopenbutton_clicked        (GtkButton       *button,
                                        gpointer         user_data)
{
  GtkFileChooser *entry1;
  const gchar *filename=NULL;
  FILE *tempfile;
  wdlpass1_fileheader* wdlpass1header=NULL;
  
  entry1 = GTK_FILE_CHOOSER(lookup_widget(GTK_WIDGET(button),"filechooserdialog1"));
  filename = gtk_file_chooser_get_filename(entry1);
  myfunc_closewdlo();
  if (g_str_has_suffix(filename,".wdlo")) {
    myfunc_openwdlo(filename);
  } else {
    tempfile = tmpfile();
    wdlpass1header = wdlpass1_dec_file(tempfile,filename);
    if (wdlpass1header != NULL) {
      myfunc_openwdlo_file(tempfile);
      free (wdlpass1header);
      wdlpass1header=NULL;
    }
  }
  gtk_widget_hide(GTK_WIDGET(entry1));
  on_toolbuttonfirst_clicked(GTK_TOOL_BUTTON(lookup_widget(GTK_WIDGET(button),"toolbuttonfirst")),NULL);
}


void
on_toolbuttonfirst_clicked             (GtkToolButton   *toolbutton,
                                        gpointer         user_data)
{
  myfunc_setCurrentPage(1);
  myfunc_showpage(myfunc_getCurrentPage(),GTK_IMAGE(lookup_widget(GTK_WIDGET(toolbutton),"mainimage")),GTK_LAYOUT(lookup_widget(GTK_WIDGET(toolbutton),"layoutmain")));
  myfunc_updateStatus(GTK_STATUSBAR(lookup_widget(GTK_WIDGET(toolbutton),"statusbar1")),myfunc_getCurrentPage(),myfunc_getPages());
}


void
on_toolbuttonback_clicked              (GtkToolButton   *toolbutton,
                                        gpointer         user_data)
{
  int p;
  p = myfunc_getCurrentPage();
  if (p>1) {p--;}
  myfunc_setCurrentPage(p);
  myfunc_showpage(myfunc_getCurrentPage(),GTK_IMAGE(lookup_widget(GTK_WIDGET(toolbutton),"mainimage")),GTK_LAYOUT(lookup_widget(GTK_WIDGET(toolbutton),"layoutmain")));
  myfunc_updateStatus(GTK_STATUSBAR(lookup_widget(GTK_WIDGET(toolbutton),"statusbar1")),myfunc_getCurrentPage(),myfunc_getPages());
}


void
on_toolbuttonforward_clicked           (GtkToolButton   *toolbutton,
                                        gpointer         user_data)
{
  int p;
  p = myfunc_getCurrentPage();
  if (p < myfunc_getPages()) {
    p++;
  }
  myfunc_setCurrentPage(p);
  myfunc_showpage(myfunc_getCurrentPage(),GTK_IMAGE(lookup_widget(GTK_WIDGET(toolbutton),"mainimage")),GTK_LAYOUT(lookup_widget(GTK_WIDGET(toolbutton),"layoutmain")));
  myfunc_updateStatus(GTK_STATUSBAR(lookup_widget(GTK_WIDGET(toolbutton),"statusbar1")),myfunc_getCurrentPage(),myfunc_getPages());
}


void
on_toolbuttonlast_clicked              (GtkToolButton   *toolbutton,
                                        gpointer         user_data)
{
  int p;
  p = myfunc_getPages();
  myfunc_setCurrentPage(p);
  myfunc_showpage(myfunc_getCurrentPage(),GTK_IMAGE(lookup_widget(GTK_WIDGET(toolbutton),"mainimage")),GTK_LAYOUT(lookup_widget(GTK_WIDGET(toolbutton),"layoutmain")));
  myfunc_updateStatus(GTK_STATUSBAR(lookup_widget(GTK_WIDGET(toolbutton),"statusbar1")),myfunc_getCurrentPage(),myfunc_getPages());
}


void
on_toolbuttonclose_clicked             (GtkToolButton   *toolbutton,
                                        gpointer         user_data)
{
  myfunc_closewdlo();
  myfunc_setCurrentPage(1);
  myfunc_showpage(myfunc_getCurrentPage(),GTK_IMAGE(lookup_widget(GTK_WIDGET(toolbutton),"mainimage")),GTK_LAYOUT(lookup_widget(GTK_WIDGET(toolbutton),"layoutmain")));
  myfunc_updateStatus(GTK_STATUSBAR(lookup_widget(GTK_WIDGET(toolbutton),"statusbar1")),myfunc_getCurrentPage(),myfunc_getPages());
}


void
on_toolbuttonzoomin_clicked            (GtkToolButton   *toolbutton,
                                        gpointer         user_data)
{
  int p;
  p = myfunc_getScaleFactor();
  if (p>1) {p--;}
  myfunc_setScaleFactor(1,p);
  myfunc_showpage(myfunc_getCurrentPage(),GTK_IMAGE(lookup_widget(GTK_WIDGET(toolbutton),"mainimage")),GTK_LAYOUT(lookup_widget(GTK_WIDGET(toolbutton),"layoutmain")));
  myfunc_updateStatus(GTK_STATUSBAR(lookup_widget(GTK_WIDGET(toolbutton),"statusbar1")),myfunc_getCurrentPage(),myfunc_getPages());
}


void
on_toolbuttonzoomout_clicked           (GtkToolButton   *toolbutton,
                                        gpointer         user_data)
{
  int p;
  p = myfunc_getScaleFactor();
  p++;
  myfunc_setScaleFactor(1,p);
  myfunc_showpage(myfunc_getCurrentPage(),GTK_IMAGE(lookup_widget(GTK_WIDGET(toolbutton),"mainimage")),GTK_LAYOUT(lookup_widget(GTK_WIDGET(toolbutton),"layoutmain")));
  myfunc_updateStatus(GTK_STATUSBAR(lookup_widget(GTK_WIDGET(toolbutton),"statusbar1")),myfunc_getCurrentPage(),myfunc_getPages());
}


void
on_toolbuttonopen_clicked              (GtkToolButton   *toolbutton,
                                        gpointer         user_data)
{
  GtkDialog *dialog;
  dialog = GTK_DIALOG(lookup_widget(GTK_WIDGET(toolbutton),"filechooserdialog1"));
  registerMainWindow(GTK_WIDGET(toolbutton));
  gtk_dialog_run(dialog);
  gtk_widget_hide(GTK_WIDGET(dialog));
}


void
on_open1_activate                      (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
  GtkDialog *dialog;
  dialog = GTK_DIALOG(lookup_widget(GTK_WIDGET(menuitem),"filechooserdialog1"));
  registerMainWindow(GTK_WIDGET(menuitem));
  gtk_dialog_run(dialog);
  gtk_widget_hide(GTK_WIDGET(dialog));
}

void
on_toolbuttonfith_clicked           (GtkToolButton   *toolbutton,
                                        gpointer         user_data)
{
  int current_height;
  int page_height;
  int g;
  GtkImage *mainImage;
  GtkScrolledWindow *win1;
  GtkAdjustment *win1_adj;
  GdkPixmap *mainImage_pixmap=NULL;
  
  mainImage = GTK_IMAGE(lookup_widget(GTK_WIDGET(toolbutton),"mainimage"));
  win1 = GTK_SCROLLED_WINDOW(lookup_widget(GTK_WIDGET(toolbutton),"scrolledwindow1"));
  win1_adj = gtk_scrolled_window_get_vadjustment(win1);
  page_height = (int)(gtk_adjustment_get_page_size(win1_adj));
  if (page_height<=0) {
    return;
  }
  gtk_image_get_pixmap(mainImage,&mainImage_pixmap,NULL);
  if (mainImage_pixmap==NULL) {
    return;
  }
  gdk_drawable_get_size(GDK_DRAWABLE(mainImage_pixmap),NULL,&current_height);
  g = pagerender_gcd(page_height,current_height);
  myfunc_setScaleFactor(page_height/g*myfunc_getScaleFactor_numerator(),current_height/g*myfunc_getScaleFactor_denominator());
  myfunc_showpage(myfunc_getCurrentPage(),mainImage,GTK_LAYOUT(lookup_widget(GTK_WIDGET(toolbutton),"layoutmain")));
  myfunc_updateStatus(GTK_STATUSBAR(lookup_widget(GTK_WIDGET(toolbutton),"statusbar1")),myfunc_getCurrentPage(),myfunc_getPages());
}

void
on_toolbuttonfitw_clicked           (GtkToolButton   *toolbutton,
                                        gpointer         user_data)
{
  int current_width;
  int page_width;
  int g;
  GtkImage *mainImage;
  GtkScrolledWindow *win1;
  GtkAdjustment *win1_adj;
  GdkPixmap *mainImage_pixmap=NULL;
  
  mainImage = GTK_IMAGE(lookup_widget(GTK_WIDGET(toolbutton),"mainimage"));
  win1 = GTK_SCROLLED_WINDOW(lookup_widget(GTK_WIDGET(toolbutton),"scrolledwindow1"));
  win1_adj = gtk_scrolled_window_get_hadjustment(win1);
  page_width = (int)(gtk_adjustment_get_page_size(win1_adj));
  if (page_width<=0) {
    return;
  }
  gtk_image_get_pixmap(mainImage,&mainImage_pixmap,NULL);
  if (mainImage_pixmap==NULL) {
    return;
  }
  gdk_drawable_get_size(GDK_DRAWABLE(mainImage_pixmap),&current_width,NULL);
  g = pagerender_gcd(page_width,current_width);
  myfunc_setScaleFactor(page_width/g*myfunc_getScaleFactor_numerator(),current_width/g*myfunc_getScaleFactor_denominator());
  myfunc_showpage(myfunc_getCurrentPage(),mainImage,GTK_LAYOUT(lookup_widget(GTK_WIDGET(toolbutton),"layoutmain")));
  myfunc_updateStatus(GTK_STATUSBAR(lookup_widget(GTK_WIDGET(toolbutton),"statusbar1")),myfunc_getCurrentPage(),myfunc_getPages());
}

void
on_toolbuttonfitp_clicked           (GtkToolButton   *toolbutton,
                                        gpointer         user_data)
{
  int p1,p2,g;
  int current_width,current_height;
  int page_width,page_height;
  GtkImage *mainImage;
  GtkScrolledWindow *win1;
  GtkAdjustment *win1_adj,*win1_adj2;
  GdkPixmap *mainImage_pixmap=NULL;
  
  mainImage = GTK_IMAGE(lookup_widget(GTK_WIDGET(toolbutton),"mainimage"));
  win1 = GTK_SCROLLED_WINDOW(lookup_widget(GTK_WIDGET(toolbutton),"scrolledwindow1"));
  win1_adj = gtk_scrolled_window_get_hadjustment(win1);
  win1_adj2 = gtk_scrolled_window_get_vadjustment(win1);
  page_width = (int)(gtk_adjustment_get_page_size(win1_adj));
  page_height = (int)(gtk_adjustment_get_page_size(win1_adj2));
  if (page_width<=0 || page_height<=0) {
    return;
  }
  gtk_image_get_pixmap(mainImage,&mainImage_pixmap,NULL);
  if (mainImage_pixmap==NULL) {
    return;
  }
  gdk_drawable_get_size(GDK_DRAWABLE(mainImage_pixmap),&current_width,&current_height);
  p1 = (current_width)*(page_height);
  p2 = (current_height)*(page_width);
  if (p1<p2) {
    g = pagerender_gcd(page_height,current_height);
    myfunc_setScaleFactor(page_height/g*myfunc_getScaleFactor_numerator(),current_height/g*myfunc_getScaleFactor_denominator());
  } else {
    g = pagerender_gcd(page_width,current_width);
    myfunc_setScaleFactor(page_width/g*myfunc_getScaleFactor_numerator(),current_width/g*myfunc_getScaleFactor_denominator());
  }
  myfunc_showpage(myfunc_getCurrentPage(),mainImage,GTK_LAYOUT(lookup_widget(GTK_WIDGET(toolbutton),"layoutmain")));
  myfunc_updateStatus(GTK_STATUSBAR(lookup_widget(GTK_WIDGET(toolbutton),"statusbar1")),myfunc_getCurrentPage(),myfunc_getPages());
}

void
on_imagemenuitem10_activate  (GtkMenuItem *menuitem, gpointer  user_data)
{
  GtkDialog *dialog;
  dialog = GTK_DIALOG(lookup_widget(GTK_WIDGET(menuitem),"aboutdialog1"));
  gtk_dialog_run(dialog);
  gtk_widget_hide(GTK_WIDGET(dialog));
}

gboolean
on_scrolledwindow1_key_release_event (GtkWidget *widget, GdkEvent *event,
 gpointer user_data) {
  GdkEventKey *key = (GdkEventKey*)event;
  if (key->type == GDK_KEY_RELEASE) {
    if (key->keyval == GDK_KEY_Page_Down) {
      GtkScrolledWindow *scrolledWindow=NULL;
      GtkAdjustment *adj=NULL;
      gdouble current,upper,step;
      scrolledWindow = GTK_SCROLLED_WINDOW(lookup_widget(widget,"scrolledwindow1"));
      if (!scrolledWindow) {
        return FALSE;
      }
      adj = gtk_scrolled_window_get_vadjustment(scrolledWindow);
      if (!adj) {
        return FALSE;
      }
      current = gtk_adjustment_get_value(adj);
      step = gtk_adjustment_get_page_increment(adj);
      upper = gtk_adjustment_get_upper(adj)-gtk_adjustment_get_page_size(adj);
      if (current >= upper) {
        int p;
        p = myfunc_getCurrentPage();
        if (p < myfunc_getPages()) {
          gtk_adjustment_set_value(adj,gtk_adjustment_get_lower(adj));
          on_toolbuttonforward_clicked(GTK_TOOL_BUTTON(lookup_widget(widget,"toolbuttonforward")),user_data);
        }
      } else if (current+step < upper) {
        gtk_adjustment_set_value(adj,current+step);
      } else {
        gtk_adjustment_set_value(adj,upper);
      }
      return TRUE;
    } else if (key->keyval == GDK_KEY_Page_Up) {
      GtkScrolledWindow *scrolledWindow=NULL;
      GtkAdjustment *adj=NULL;
      gdouble current,lower,step;
      scrolledWindow = GTK_SCROLLED_WINDOW(lookup_widget(widget,"scrolledwindow1"));
      if (!scrolledWindow) {
        return FALSE;
      }
      adj = gtk_scrolled_window_get_vadjustment(scrolledWindow);
      if (!adj) {
        return FALSE;
      }
      current = gtk_adjustment_get_value(adj);
      step = gtk_adjustment_get_page_increment(adj);
      lower = gtk_adjustment_get_lower(adj);
      if (current <= lower) {
        int p;
        p = myfunc_getCurrentPage();
        if (p > 1) {
          if (gtk_adjustment_get_upper(adj)-gtk_adjustment_get_page_size(adj) > lower) {
            gtk_adjustment_set_value(adj,gtk_adjustment_get_upper(adj)-gtk_adjustment_get_page_size(adj));
          }
          on_toolbuttonback_clicked(GTK_TOOL_BUTTON(lookup_widget(widget,"toolbuttonback")),user_data);
        }
      } else if (current-step > lower) {
        gtk_adjustment_set_value(adj,current-step);
      } else {
        gtk_adjustment_set_value(adj,lower);
      }
      return TRUE;
    }
  }
  return FALSE;
}
