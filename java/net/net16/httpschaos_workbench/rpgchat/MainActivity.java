package net.net16.httpschaos_workbench.rpgchat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.httpschaos_workbench.rpgchat.main_activity_classes.AddContactValueEventListener;
import net.net16.httpschaos_workbench.rpgchat.main_activity_classes.ContactListOnItemClickListener;
import net.net16.httpschaos_workbench.rpgchat.main_activity_classes.ContactSearch;
import net.net16.httpschaos_workbench.rpgchat.main_activity_classes.MainInterfaceButtonsOnClickListener;
import net.net16.httpschaos_workbench.rpgchat.main_activity_classes.SelectedContact;

/**
 * Created by Daniel on 26/04/2018.
 *
 */

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseUser user;
    private Context context = this;
    private LinearLayout root, chatFragment, mainFragment;
    private ListView contactList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmented_main_screen);
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        root = findViewById(R.id.fragment_main_layout);
        chatFragment = findViewById(R.id.fragment_chat_screen);
        mainFragment = findViewById(R.id.fragment_main_screen);
        ImageView chatOpt = findViewById(R.id.chat_opt);
        chatOpt.setOnClickListener(new MainInterfaceButtonsOnClickListener(this));
        root.removeView(chatFragment);
        contactList = findViewById(R.id.contact_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        contactList.setOnItemClickListener(new ContactListOnItemClickListener(this, chatFragment));
        contactList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PopupMenu menu = new PopupMenu(context, view);
                menu.getMenuInflater().inflate(R.menu.menu_contatos, menu.getMenu());
                menu.show();
                SelectedContact.setLongSelection(i);
                return true;
            }
        });
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

    public void closeMessages(MenuItem i) {
        root.removeAllViews();
        root.addView(mainFragment);
    }

    public void changePassword (MenuItem i) {
        assert user.getEmail() != null;
        FirebaseAuth.getInstance().fetchProvidersForEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                assert task.getResult().getProviders() != null;
                if ("password".equals(task.getResult().getProviders().get(0))) {
                    final EditText apass = new EditText(root.getContext());
                    final EditText npass = new EditText(root.getContext());
                    final EditText cpass = new EditText(root.getContext());
                    LinearLayout l0 = new LinearLayout(root.getContext());
                    apass.setHint("Senha atual");
                    npass.setHint("Nova senha");
                    cpass.setHint("Repetir senha");
                    l0.setOrientation(LinearLayout.VERTICAL);
                    l0.addView(apass);
                    l0.addView(npass);
                    l0.addView(cpass);
                    new AlertDialog.Builder(root.getContext())
                            .setTitle("Alterar senha")
                            .setView(l0)
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Pronto", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (npass.getText().toString().equals(cpass.getText().toString())) {
                                        FirebaseAuth.getInstance().signInWithEmailAndPassword(user.getEmail(), apass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    user.updatePassword(npass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(root.getContext(), "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                } else Toast.makeText(root.getContext(), "Senha atual incorreta!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else Toast.makeText(root.getContext(), "Senhas diferentes!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                } else  if ("google.com".equals(task.getResult().getProviders().get(0))) Toast.makeText(root.getContext(), "Usuários de conta do google não têm senha", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void about(MenuItem i) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.about);
        ImageView cancel = dialog.findViewById(R.id.cancelAbout);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { dialog.dismiss(); }
        });
        dialog.show();
    }

    public void blocked(MenuItem i) {
        DatabaseReference blockedRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Blocked");
        blockedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayAdapter<String> blockedListAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.blocked_list);
                ListView blockedList = dialog.findViewById(R.id.blocked_list);
                ImageView cancel = dialog.findViewById(R.id.cancelBlocked);

                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    blockedListAdapter.add("" + data.getKey());
                }
                blockedList.setAdapter(blockedListAdapter);
                blockedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                        new AlertDialog.Builder(context)
                                .setTitle("Deseja desbloquear esse usuário?")
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int a) {
                                        DatabaseReference userRef  = database.getReference("Users/" + user.getDisplayName() + "/Private/Blocked/" + blockedListAdapter.getItem(i));
                                        userRef.removeValue();
                                    }
                                })
                                .setNegativeButton("Não", null)
                                .show();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { dialog.dismiss(); }
                });

                dialog.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();}
        });
    }

    public void block(MenuItem i) {
        DatabaseReference blockRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Blocked/" + SelectedContact.getSelected());
        blockRef.setValue(" ");
        DatabaseReference conRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Contacts/" + SelectedContact.getSelected());
        conRef.removeValue();
    }

    public void longBlock(MenuItem i) {
        DatabaseReference blockRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Blocked/" + SelectedContact.getLongSelelected());
        blockRef.setValue(" ");
        DatabaseReference conRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Contacts/" + SelectedContact.getLongSelelected());
        conRef.removeValue();
    }

    public void removeCon(MenuItem i) {
        DatabaseReference conRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Contacts/" + SelectedContact.getSelected());
        conRef.removeValue();
    }

    public void longRemoveCon (MenuItem i) {
        DatabaseReference conRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Contacts/" + SelectedContact.getLongSelelected());
        conRef.removeValue();
    }
}
