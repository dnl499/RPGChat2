package net.net16.httpschaos_workbench.rpgchat;

import android.annotation.SuppressLint;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Daniel on 08/02/2018.
 *
 * This Class will update the username in the TextView profileName and get the
 * google account name, if it wasn't set. It'll also update the user status in
 * the TextView profileStatus.
 */

public class UserValueEventListener implements ValueEventListener {

    private FirebaseUser user;
    private TextView view;

    UserValueEventListener(FirebaseUser firebaseUser, TextView textView) {
        user = firebaseUser;
        view = textView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        String value = dataSnapshot.getValue(String.class);
        assert value != null;
        if (value.isEmpty()) {
            switch (view.getId()) {
                case R.id.profileName:
                    view.setText(user.getDisplayName());
                    break;

                case R.id.profileStatus:
                    view.setText("Online");
                    break;
            }
        }
        else view.setText(value);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Erro:")
                .setMessage("Não foi possível acessar o BD nome\n" + databaseError.getMessage())
                .show();
    }
}
