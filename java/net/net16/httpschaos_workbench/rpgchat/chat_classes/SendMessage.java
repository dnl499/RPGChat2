package net.net16.httpschaos_workbench.rpgchat.chat_classes;

import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                String a = chatInput.getText().toString().replace("\t", " ");
                try {

                    a = a.replaceAll("<c:", "<font color=#");
                    a = a.replaceAll("</c>", "</font>");

                    if (a.contains("<d") && a.contains("d>")) {
                        String sub = a.substring(a.indexOf("<d")+2, a.indexOf("d>"));
                        String suba = a.substring(a.indexOf("<d")+2, a.indexOf("d>"));
                        int n;
                        boolean indv = false;
                        if (suba.contains("t")) {
                            n = Integer.parseInt(suba.split("t")[0]);
                            suba = suba.split("t")[1];
                        }
                        else if (suba.contains("i")){
                            n = Integer.parseInt(suba.split("i")[0]);
                            suba = suba.split("i")[1];
                            indv = true;
                        } else {
                            Looper.prepare();
                            Toast.makeText(chatInput.getContext(), "Código de rolagem inválido", Toast.LENGTH_SHORT).show();

                            DatabaseReference sendRef = database.getReference("Users/" + contact + "/Private/Messages/" + user.getDisplayName() + "/" + String.valueOf((-1)*System.currentTimeMillis()));
                            sendRef.setValue(a);
                            DatabaseReference messageRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Messages/" + contact + "/" + String.valueOf((-1)*System.currentTimeMillis()));
                            messageRef.setValue(a + "\tme");
                            return;
                        }
                        int f;
                        int l = -1;
                        int g = -1;
                        int c = 0;
                        int m = 0;
                        if (suba.contains(">")) {
                            g = Integer.parseInt(suba.split(">")[1]);
                            suba = suba.split(">")[0];
                            c = g;
                            suba = suba.replaceAll(">", "");
                            suba = suba.replaceAll("<", "");
                        } else if (suba.contains("<")) {
                            l = Integer.parseInt(suba.split("<")[1]);
                            suba = suba.split("<")[0];
                            c = l;
                            suba = suba.replaceAll(">", "");
                            suba = suba.replaceAll("<", "");
                        }
                        if (suba.contains("+")) {
                            m = Integer.parseInt(suba.split("[+]")[1]);
                            suba = suba.split("[+]")[0];
                        }
                        f = Integer.parseInt(suba);
                        a = a.replace("<d" + sub + "d>", DiceRoller.roll(n, f, m, (g > 0), (l > 0), indv, c));
                    }

                    DatabaseReference sendRef = database.getReference("Users/" + contact + "/Private/Messages/" + user.getDisplayName() + "/" + String.valueOf((-1)*System.currentTimeMillis()));
                    sendRef.setValue(a);
                    DatabaseReference messageRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Messages/" + contact + "/" + String.valueOf((-1)*System.currentTimeMillis()));
                    messageRef.setValue(a + "\tme");
                } catch (NumberFormatException e) {
                    Looper.prepare();
                    Toast.makeText(chatInput.getContext(), "Código de rolagem inválido", Toast.LENGTH_SHORT).show();

                    DatabaseReference sendRef = database.getReference("Users/" + contact + "/Private/Messages/" + user.getDisplayName() + "/" + String.valueOf((-1)*System.currentTimeMillis()));
                    sendRef.setValue(a);
                    DatabaseReference messageRef = database.getReference("Users/" + user.getDisplayName() + "/Private/Messages/" + contact + "/" + String.valueOf((-1)*System.currentTimeMillis()));
                    messageRef.setValue(a + "\tme");
                }
            }
        }).start();
        chatInput.setText("");
    }
}
