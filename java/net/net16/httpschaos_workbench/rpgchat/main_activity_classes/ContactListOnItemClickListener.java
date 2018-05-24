package net.net16.httpschaos_workbench.rpgchat.main_activity_classes;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.net16.httpschaos_workbench.rpgchat.R;
import net.net16.httpschaos_workbench.rpgchat.chat_classes.ReceiveMessage;
import net.net16.httpschaos_workbench.rpgchat.chat_classes.SendMessage;

/**
 * Created by Daniel on 29/04/2018.
 *
 */

public class ContactListOnItemClickListener implements AdapterView.OnItemClickListener {

    private Activity activity;
    private LinearLayout chat;

    public ContactListOnItemClickListener(Activity activity, LinearLayout chat) {
        this.activity = activity;
        this.chat = chat;
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
        String contact = adapterView.getItemAtPosition(i).toString().split("\t")[0];
        LinearLayout root = activity.findViewById(R.id.fragment_main_layout);
        LinearLayout chatFragment = chat;
        ImageView mainPicture = activity.findViewById(R.id.main_pic);
        SelectedContact.setSelection(i);
        final int picWidth = mainPicture.getWidth();
        final int picHeight = mainPicture.getHeight();
        final DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int inchWidth = (int) (metrics.widthPixels/metrics.xdpi);
        if (inchWidth < 4) {
            root.removeAllViews();
            root.addView(chatFragment);
        } else {
            root.addView(chatFragment);
            chatFragment.setWeightSum(0);
        }
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert user != null;
        assert user.getDisplayName() != null;
        DatabaseReference messageRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Messages/" + contact);
        messageRef.orderByKey().limitToLast(100).addValueEventListener(new ReceiveMessage(activity, metrics));

        ImageButton send = activity.findViewById(R.id.chat_send);
        final EditText chatInput = activity.findViewById(R.id.chat_input);
        send.setOnClickListener(new SendMessage(chatInput, contact));
        TextView chatUser = activity.findViewById(R.id.chat_username);
        final TextView chatStatus = activity.findViewById(R.id.chat_status);
        final ImageView chatPicture = activity.findViewById(R.id.chat_pic);

        chatUser.setText(contact);
        chatStatus.setText(((String) adapterView.getItemAtPosition(i)).split("\t")[1]);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference profilePicRef = storage.getReference("Users/" + contact + "/Profile Picture");
        profilePicRef.getBytes((long) 10485760).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                chatPicture.setImageBitmap(bmp);
                chatPicture.setAdjustViewBounds(true);
                chatPicture.setMaxWidth(picWidth);
                chatPicture.setMaxHeight(picHeight);
                chatPicture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(chatStatus.getContext(), "Erro: " + e.getMessage() + "\n" + profilePicRef.getPath(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
