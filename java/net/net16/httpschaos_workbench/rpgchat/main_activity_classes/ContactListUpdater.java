package net.net16.httpschaos_workbench.rpgchat.main_activity_classes;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.httpschaos_workbench.rpgchat.R;

/**
 * Created by Daniel on 18/04/2018.
 *
 */

public class ContactListUpdater implements ValueEventListener {
    private Activity activity;

    public ContactListUpdater(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final ArrayAdapter<String> contactListAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1);
        ListView contactList = activity.findViewById(R.id.contact_list);
        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

            final String[] res = {dataSnapshot1.getKey(), ""};
            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Users/" + res[0] + "/Public/Status");
            statusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    res[1] = dataSnapshot.getValue(String.class);
                    contactListAdapter.add(res[0] + "\t" + res[1]);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Users/" + user.getDisplayName() + "/Private/Contacts/" + res[0]);
                    statusRef.setValue(res[1]);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        contactList.setAdapter(contactListAdapter);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(activity, "Erro: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
