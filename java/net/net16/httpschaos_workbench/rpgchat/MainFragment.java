package net.net16.httpschaos_workbench.rpgchat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.httpschaos_workbench.rpgchat.main_activity_classes.ContactListUpdater;
import net.net16.httpschaos_workbench.rpgchat.main_activity_classes.ImageDownloader;
import net.net16.httpschaos_workbench.rpgchat.main_activity_classes.MainInterfaceButtonsOnClickListener;

/**
 * Created by Daniel on 06/03/2018.
 * The main activity that will display the contact list, assigned rooms and the main options.
 */

public class MainFragment extends Fragment {

    private TextView status;
    private FirebaseDatabase database;
    private DatabaseReference statusRef;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.main_screen, container, true);
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView username = getActivity().findViewById(R.id.main_username);
        status = getActivity().findViewById(R.id.main_status);
        ImageView picture = getActivity().findViewById(R.id.main_pic);
        ImageView mainopt = getActivity().findViewById(R.id.main_menu);
        ImageView addcon = getActivity().findViewById(R.id.main_contact);
        ImageView croom = getActivity().findViewById(R.id.main_add_room);
        ImageView favroom = getActivity().findViewById(R.id.main_fav_room);
        ImageView sroom = getActivity().findViewById(R.id.main_search_room);

        picture.setOnClickListener(new MainInterfaceButtonsOnClickListener(getActivity()));
        status.setOnClickListener(new MainInterfaceButtonsOnClickListener(getActivity()));
        mainopt.setOnClickListener(new MainInterfaceButtonsOnClickListener(getActivity()));
        addcon.setOnClickListener(new MainInterfaceButtonsOnClickListener(getActivity()));
        croom.setOnClickListener(new MainInterfaceButtonsOnClickListener(getActivity()));
        favroom.setOnClickListener(new MainInterfaceButtonsOnClickListener(getActivity()));
        sroom.setOnClickListener(new MainInterfaceButtonsOnClickListener(getActivity()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        statusRef = database.getReference("Users/"+ user.getDisplayName()+"/Public/Status");
        username.setText(user.getDisplayName());
        if (user.getPhotoUrl() != null) {
            new Thread(new ImageDownloader(getActivity(), user.getPhotoUrl().toString(), picture)).start();
        }
        statusRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) == null) {
                    statusRef.setValue("Status");
                    status.setText("Status");

                } else {
                    String a = "" + dataSnapshot.getValue(String.class);
                    if (a.isEmpty()) {
                        statusRef.setValue("Status");
                        status.setText("Status");

                    } else status.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(status.getContext(), "Erro no banco de dados: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        DatabaseReference contactRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Contacts");
        contactRef.addValueEventListener(new ContactListUpdater(getActivity()));
    }
}