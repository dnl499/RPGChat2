package net.net16.httpschaos_workbench.rpgchat;

import android.view.View;
import android.widget.PopupMenu;

/**
 * Created by Daniel on 08/02/2018.
 * This Class contains all main.xml ImageButtons' actions.
 */

public class ControlButtonsOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton0:
                openMainMenu(view);
                break;

            case R.id.imageButton1:
                addContact(view);
                break;

            case R.id.imageButton2:
                createRoom(view);
                break;

            case R.id.imageButton3:
                searchRoom(view, 0);
                break;

            case R.id.imageButton4:
                searchRoom(view, 1);
                break;
        }
    }

    private void openMainMenu(View v) {
        PopupMenu menu = new PopupMenu(v.getContext(), v);
        menu.getMenuInflater().inflate(R.menu.menu_principal, menu.getMenu());
        menu.show();
    }

    private void addContact(View v) {
    }

    private void createRoom(View v) {
    }

    private void searchRoom(View view, int i) {
    }
}
