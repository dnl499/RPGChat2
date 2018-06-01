package net.net16.httpschaos_workbench.rpgchat.main_activity_classes;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import net.net16.httpschaos_workbench.rpgchat.R;

/**
 * Created by Daniel on 01/06/2018.
 *
 */

public class TabManager {
    static public void createTab(String name, final Activity activity) {
        final LinearLayout tabLayout = activity.findViewById(R.id.tabs);
        Button btn = new Button(activity);
        btn.setText(name);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i <tabLayout.getChildCount(); i++) {
                    tabLayout.getChildAt(i).setEnabled(true);
                }
                view.setEnabled(false);
                ListView lv = activity.findViewById(R.id.contact_list);
                if (lv.getAdapter().getClass().equals(ArrayAdapter.class)) ArrayAdapter<String> aa = (ArrayAdapter<String>) lv.getAdapter();
            }
        });
    }
}