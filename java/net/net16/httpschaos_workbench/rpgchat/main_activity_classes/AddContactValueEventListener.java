package net.net16.httpschaos_workbench.rpgchat.main_activity_classes;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Daniel on 12/04/2018.
 * This class handles the 'Aceitar contatos' menu item. It displays a Dialog listing all people
 * that sent a contact solicitation to the user and give the user the options to leave it be,
 * accept or block the other user.
 */

public class AddContactValueEventListener implements ValueEventListener {

    private Activity activity;
    private FirebaseDatabase database;
    private FirebaseUser user;

    public AddContactValueEventListener(Activity activity, FirebaseDatabase database, FirebaseUser user) {
        this.activity = activity;
        this.database = database;
        this.user = user;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final ArrayAdapter<String> contacts = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1);

        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
            contacts.add(dataSnapshot1.getKey());
        }

        final ListView contactList = new ListView(activity);
        contactList.setAdapter(contacts);
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(activity)
                        .setTitle("Deseja adcionar ou bloquear este contato?")
                        .setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int a) {
                                String uName = user.getDisplayName();
                                String contact = contacts.getItem(i);

                                DatabaseReference contactRef = database.getReference("Users/" + uName + "/Private/Contacts/" + contact);
                                contactRef.setValue("");

                                DatabaseReference nContactReference = database.getReference("Users/" + uName + "/Private/NContact/" + contact);
                                nContactReference.removeValue();
                            }
                        })
                        .setNeutralButton("Cancelar", null)
                        .setNegativeButton("Bloquear", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int a) {
                                String uName = user.getDisplayName();
                                String contact = contacts.getItem(i);

                                DatabaseReference contactRef = database.getReference("Users/" + uName + "/Private/Blocked/"+contact);
                                contactRef.setValue("");

                                DatabaseReference nContactReference = database.getReference("Users/" + uName + "/Private/NContact/" + contact);
                                nContactReference.removeValue();
                            }
                        })
                        .show();
            }
        });
        new AlertDialog.Builder(activity)
                .setTitle("Solicitações de contato")
                .setView(contactList)
                .show();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(activity, "Erro no banco de dados: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
    }

}
