#include <gtk/gtk.h>
#include <gdk/gdkevents.h>


void
on_new1_activate                       (GtkMenuItem     *menuitem,
                                        gpointer         user_data);

void
on_open1_activate                      (GtkMenuItem     *menuitem,
                                        gpointer         user_data);

void
on_save1_activate                      (GtkMenuItem     *menuitem,
                                        gpointer         user_data);

void
on_save_as1_activate                   (GtkMenuItem     *menuitem,
                                        gpointer         user_data);

void
on_cut1_activate                       (GtkMenuItem     *menuitem,
                                        gpointer         user_data);

void
on_copy1_activate                      (GtkMenuItem     *menuitem,
                                        gpointer         user_data);

void
on_paste1_activate                     (GtkMenuItem     *menuitem,
                                        gpointer         user_data);

void
on_delete1_activate                    (GtkMenuItem     *menuitem,
                                        gpointer         user_data);

void
on_about1_activate                     (GtkMenuItem     *menuitem,
                                        gpointer         user_data);

void
on_fileopen_ok_button1_clicked         (GtkButton       *button,
                                        gpointer         user_data);

void
on_filechooserwidget1_selection_changed
                                        (GtkFileChooser  *filechooser,
                                        gpointer         user_data);

void
on_fileselectopenbutton_clicked        (GtkButton       *button,
                                        gpointer         user_data);

void
on_toolbuttonfirst_clicked             (GtkToolButton   *toolbutton,
                                        gpointer         user_data);

void
on_toolbuttonback_clicked              (GtkToolButton   *toolbutton,
                                        gpointer         user_data);

void
on_toolbuttonforward_clicked           (GtkToolButton   *toolbutton,
                                        gpointer         user_data);

void
on_toolbuttonlast_clicked              (GtkToolButton   *toolbutton,
                                        gpointer         user_data);

void
on_toolbuttonclose_clicked             (GtkToolButton   *toolbutton,
                                        gpointer         user_data);

void
on_toolbuttonzoomin_clicked            (GtkToolButton   *toolbutton,
                                        gpointer         user_data);

void
on_toolbuttonzoomout_clicked           (GtkToolButton   *toolbutton,
                                        gpointer         user_data);

void
on_toolbuttonopen_clicked              (GtkToolButton   *toolbutton,
                                        gpointer         user_data);

void
on_open1_activate                      (GtkMenuItem     *menuitem,
                                        gpointer         user_data);

gboolean
on_scrolledwindow1_key_release_event   (GtkWidget *widget, GdkEvent *event,
                                        gpointer user_data);

