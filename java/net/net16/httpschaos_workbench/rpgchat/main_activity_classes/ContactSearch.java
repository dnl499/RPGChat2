package net.net16.httpschaos_workbench.rpgchat.main_activity_classes;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Daniel on 18/04/2018.
 *
 */

public class ContactSearch implements DialogInterface.OnClickListener {

    private Activity activity;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private TextView search;

    public ContactSearch(Activity activity, FirebaseUser user, TextView search) {
        this.activity = activity;
        this.user = user;
        this.search = search;
        this.database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        DatabaseReference usersReference = database.getReference("Users");
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1);
                final ListView userList = new ListView(activity);

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().contains(search.getText().toString()) && !data.getKey().equals(user.getDisplayName())) usersAdapter.add(data.getKey());
                }

                final AlertDialog dg;

                    userList.setAdapter(usersAdapter);
                    dg = new AlertDialog.Builder(activity)
                            .setTitle("Resultado")
                            .setView(userList)
                            .show();

                userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {new AlertDialog.Builder(activity)
                            .setTitle("Desejas adicionar essa pessoa aos seus contatos?")
                            .setPositiveButton("Adcionar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int a) {
                                    DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference("Users/" + user.getDisplayName() + "/Private/Contacts/" + usersAdapter.getItem(i));
                                    contactRef.setValue("");

                                    DatabaseReference nContactRef = FirebaseDatabase.getInstance().getReference("Users/" + usersAdapter.getItem(i) + "/Private/NContact/" + user.getDisplayName());
                                    nContactRef.setValue("");
                                    dg.dismiss();
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                    }
                });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(activity, "Erro no banco de dados: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
}
