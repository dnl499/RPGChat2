package net.net16.httpschaos_workbench.rpgchat.main_activity_classes;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.net16.httpschaos_workbench.rpgchat.R;

/**
 * Created by Daniel on 26/04/2018.
 *
 */

public class MainInterfaceButtonsOnClickListener implements View.OnClickListener{

    private Activity activity;

    public MainInterfaceButtonsOnClickListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(activity, R.animator.onclick);
        set.setTarget(view);
        set.start();
        switch (view.getId()) {
            case R.id.main_pic:
                changeProfilePicture(view);
                break;
            case R.id.main_menu:
                PopupMenu menu = new PopupMenu(activity, view);
                menu.getMenuInflater().inflate(R.menu.menu_principal, menu.getMenu());
                menu.show();
                break;
            case R.id.main_contact:
                PopupMenu menu0 = new PopupMenu(activity, view);
                menu0.getMenuInflater().inflate(R.menu.contact_search, menu0.getMenu());
                menu0.show();
                break;
            case R.id.main_add_room:
                LinearLayout l0 = new LinearLayout(activity);
                EditText e0 = new EditText(activity);
                EditText e1 = new EditText(activity);
                e0.setHint("Nome da sala");
                e1.setHint("Senha da sala");
                l0.setOrientation(LinearLayout.VERTICAL);
                l0.addView(e0);
                l0.addView(e1);
                new AlertDialog.Builder(activity)
                        .setTitle("Criar sala")
                        .setView(l0)
                        .setPositiveButton("Criar sala", createRoom(e0, e1))
                        .setNegativeButton("Cancelar", null)
                        .show();
                break;
            case R.id.main_fav_room:
                searchFavoriteRooms();
                break;
            case R.id.main_search_room:
                searchRooms();
                break;
            case R.id.main_status:
                EditText edtStatus = new EditText(activity);
                edtStatus.setHint("Novo status");
                new AlertDialog.Builder(activity)
                        .setView(edtStatus)
                        .setTitle("Alterar status")
                        .setPositiveButton("Alterar", statusChanger(edtStatus))
                        .setNegativeButton("Cancelar", null)
                        .show();
                break;
            case R.id.chat_opt:
                PopupMenu menu2 = new PopupMenu(activity, view);
                menu2.getMenuInflater().inflate(R.menu.chat_menu, menu2.getMenu());
                menu2.show();
                break;
        }
    }

    private DialogInterface.OnClickListener createRoom(final EditText e0, final EditText e1) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        };
    }

    private void changeProfilePicture(View view) {
        new AlertDialog.Builder(activity)
                .setMessage("A imagem é um URL ou está no celular?")
                .setPositiveButton("URL", new InternetProfilePicture(activity, (ImageView) view))
                .setNeutralButton("Escolher do celular", new InternalImageSelector(activity, (ImageView) view))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void searchRooms() {
    }

    private void searchFavoriteRooms() {
    }

    private DialogInterface.OnClickListener statusChanger(final EditText edtStatus) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Users/" + user.getDisplayName() + "/Public/Status");
                String st = edtStatus.getText().toString();
                if (st.equals("")) st = "Status";
                statusRef.setValue(st);
            }
        };
    }
}
