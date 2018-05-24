package net.net16.httpschaos_workbench.rpgchat.chat_classes;

import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Daniel on 24/05/2018.
 *
 */

public class SendMessage implements View.OnClickListener {

    private EditText chatInput;
    private String contact;
    private FirebaseDatabase database;
    private FirebaseUser user;

    public SendMessage(EditText chatInput, String contact) {
        this.chatInput = chatInput;
        this.contact = contact;
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        String a = chatInput.getText().toString().replace("\t", " ");
        DatabaseReference sendRef = database.getReference("Users/" + contact + "/Private/Messages/" + user.getDisplayName() + "/" + String.valueOf((-1)*System.currentTimeMillis()));
        sendRef.setValue(a);
        chatInput.setText("");
        DatabaseReference messageRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Messages/" + contact + "/" + String.valueOf((-1)*System.currentTimeMillis()));
        messageRef.setValue(a + "\tme");
    }
}
