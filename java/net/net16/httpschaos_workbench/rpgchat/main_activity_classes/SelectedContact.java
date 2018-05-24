package net.net16.httpschaos_workbench.rpgchat.main_activity_classes;

import android.widget.ArrayAdapter;

/**
 * Created by Daniel on 24/05/2018.
 *
 */

public class SelectedContact {

    private static ArrayAdapter<String> adapt;
    private static String sel;
    private static String longSel;

    public static void setAdapter(ArrayAdapter<String> adapter) { adapt = adapter; }

    public static void setSelection(int position) { sel = adapt.getItem(position).split("\t")[0]; }

    public static String getSelected() { return sel; }

    public static void setLongSelection(int position) { longSel = adapt.getItem(position).split("\t")[0]; }

    public static String getLongSelelected() { return longSel;}
}
