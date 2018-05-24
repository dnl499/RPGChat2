package net.net16.httpschaos_workbench.rpgchat.login_methods;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.net16.httpschaos_workbench.rpgchat.LoginActivity;
import net.net16.httpschaos_workbench.rpgchat.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Daniel on 16/02/2018.
 * Firebase's Google login. Pretty much is just copied and pasted from the guides, but the customized parts are commented
 */

public class GoogleLogin implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private Activity activity;
    private AlertDialog alertDialog;

    public GoogleLogin(Activity activity, FirebaseAuth mAuth) {
        this.activity = activity;
        this.mAuth = mAuth;
    }

    @Override
    public void onClick(View view) {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, LoginActivity.GOOGLE_LOGIN_RC);
        alertDialog = new AlertDialog.Builder(activity).setTitle("Carregando").setView(R.layout.loading).show();
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            alertDialog.dismiss();
                            alertDialog = new AlertDialog.Builder(activity).setTitle("Checando usuário").setView(R.layout.loading).show();
                            final FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {

                                //From here on out the code was customized.
                                DatabaseReference userValid = FirebaseDatabase.getInstance().getReference("Users/" + user.getDisplayName() + "/mail");
                                userValid.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String r = dataSnapshot.getValue(String.class);
                                        final EditText username = new EditText(activity);
                                        alertDialog.dismiss();

                                        // This AlertDialog is called when the app don't detect the presence of an correspondent mail
                                        // on the Firebase and thus assumes it is the user's first login through the google account
                                        // and asks for an username.
                                        AlertDialog d = new AlertDialog.Builder(activity)
                                                .setTitle("Logando pela primeira vez? Escolha um nome de usuário.")
                                                .setView(username)
                                                .setNegativeButton("Cancelar", null)
                                                .setPositiveButton("Pronto", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        DatabaseReference userValid = FirebaseDatabase.getInstance().getReference("Users");
                                                        userValid.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                // This loop will run through all the usernames and invalidates the
                                                                // choosen username, if there is someone already using it.
                                                                boolean valid = true;
                                                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                                                    assert user.getDisplayName() != null;
                                                                    if (user.getDisplayName().equals(dataSnapshot1.getKey())) {
                                                                        valid = false;
                                                                        break;
                                                                    }
                                                                }
                                                                if (valid) {

                                                                    alertDialog = new AlertDialog.Builder(activity).setTitle("Atualizando perfil").setView(R.layout.loading).show();
                                                                    // This will change the DisplayName to the username (It won't affect the google account, only the firebase account).
                                                                    user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(username.getText().toString()).build()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            DatabaseReference mailRef = FirebaseDatabase.getInstance().getReference("Users/" + user.getDisplayName() + "/mail");
                                                                            mailRef.setValue(user.getEmail());
                                                                            DatabaseReference uidRef = FirebaseDatabase.getInstance().getReference("Users/" + user.getDisplayName() + "/uid");
                                                                            uidRef.setValue(user.getUid());

                                                                            Intent i = new Intent("net.net16.httpschaos_workbench.rpgchat.MAIN_SCREEN");
                                                                            alertDialog.dismiss();
                                                                            activity.startActivity(i);
                                                                        }
                                                                    });

                                                                    new Thread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            // Here the profile picture will be stored in the FirebaseStorage
                                                                            // as it'll be impossible to get the URL from other users.
                                                                            try {
                                                                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                                                                StorageReference profilePic = storage.getReference("Users/" + user.getDisplayName() + "/Profile Picture");
                                                                                assert user.getPhotoUrl() != null;
                                                                                URL url = new URL(user.getPhotoUrl().toString());
                                                                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                                                                conn.setDoInput(true);
                                                                                InputStream is = conn.getInputStream();
                                                                                Bitmap img = BitmapFactory.decodeStream(is);
                                                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                                                img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                                                profilePic.putBytes(baos.toByteArray());
                                                                            } catch (MalformedURLException e) {
                                                                                e.printStackTrace();
                                                                            } catch (IOException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }).start();

                                                                } else Toast.makeText(activity, "Esse nome de usuário já existe", Toast.LENGTH_SHORT).show();
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) { Toast.makeText(activity, "Erro de Banco de dados: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show(); }
                                                        });
                                                    }
                                                })
                                                .create();
                                        if (r != null) {
                                            if (r.equals(user.getEmail())) {
                                                Intent i = new Intent("net.net16.httpschaos_workbench.rpgchat.MAIN_SCREEN");
                                                activity.startActivity(i);

                                            } else d.show();

                                        } else  d.show();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(activity, "Erro de Banco de Dados: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(activity, "A autenticação falhou.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}