package net.net16.httpschaos_workbench.rpgchat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.net16.httpschaos_workbench.rpgchat.main_activity_classes.AddContactValueEventListener;
import net.net16.httpschaos_workbench.rpgchat.main_activity_classes.ContactSearch;

/**
 * Created by Daniel on 26/04/2018.
 *
 */

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseUser user;
    private LinearLayout root, chatFragment;
    private ListView contactList;
    private TextView chatUser, chatStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmented_main_screen);
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        root = findViewById(R.id.fragment_main_layout);
        chatFragment = findViewById(R.id.fragment_chat_screen);
        root.removeView(chatFragment);
        contactList = findViewById(R.id.contact_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    int inchWidth = (int) (metrics.widthPixels/metrics.xdpi);
                    if (inchWidth < 4) {
                        root.removeView(chatFragment);
                        LayoutInflater inflater = getLayoutInflater();
                        inflater.inflate(R.layout.chat_screen, root);
                    } else {
                        root.addView(chatFragment);
                    }
                    chatUser = findViewById(R.id.chat_username);
                    chatStatus = findViewById(R.id.chat_status);
                    chatUser.setText((String) contactList.getAdapter().getItem(i));
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void contactAcc(MenuItem i) {
        DatabaseReference nContactsRef = database.getReference("Users/" + user.getDisplayName() + "/Private/NContact");
        nContactsRef.addListenerForSingleValueEvent(new AddContactValueEventListener(this, database, user));
    }

    public void contactSearch (MenuItem i) {
        final EditText search = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Por quem procuras?")
                .setView(search)
                .setPositiveButton("Procurar", new ContactSearch(this, user, search))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
