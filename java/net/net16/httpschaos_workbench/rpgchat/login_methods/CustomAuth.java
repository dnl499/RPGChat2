package net.net16.httpschaos_workbench.rpgchat.login_methods;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.net16.httpschaos_workbench.rpgchat.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Daniel on 16/02/2018.
 * Custom Firebase Login and Signup. Here we have signup and signin algorithms
 */

public class CustomAuth implements View.OnClickListener {

    private Activity activity;
    private FirebaseAuth mAuth;
    private AlertDialog alertDialog;

    public CustomAuth(Activity activity, FirebaseAuth mAuth) {
        this.activity = activity;
        this.mAuth = mAuth;
    }

    /**
     * This method will switch according to which button called it and call the respective method.
     * The "Login" Button is for sign in and calls the login().
     * The "Cadastrar" Button is for sign up and calls the cadastrar().
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.custom_login:
                login();
                break;
            case R.id.custom_signup:
                cadastar();
                break;
        }
    }

    /**
     * This method is for the sign up. You can pretty much skip to the onClick(...) on line 68,
     * the previous lines are for setting an AlertDialog that will request the form for signing up.
     */
    private void cadastar() {
        alertDialog = new AlertDialog.Builder(activity)
                .setTitle("Cadastro")
                .setView(R.layout.signup_form)
                .setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

        // The real code starts here:

                        // Those EditTexts are from the form popped by the AlertDialog
                        EditText euser = alertDialog.findViewById(R.id.signup_user);
                        EditText epass = alertDialog.findViewById(R.id.signup_pass);
                        EditText econpass = alertDialog.findViewById(R.id.signup_conpass);
                        EditText email = alertDialog.findViewById(R.id.signup_mail);
                        alertDialog = new AlertDialog.Builder(activity).setTitle("Carregando").setView(R.layout.loading).show();

                        // The EditTexts are on the XML file so, of course, they aren't null
                        assert euser != null;
                        assert epass != null;
                        assert econpass != null;
                        assert email != null;
                        final String user = euser.getText().toString();
                        final String pass = epass.getText().toString();
                        final String conpass = econpass.getText().toString();
                        final String mail = email.getText().toString();
                        DatabaseReference userValid = FirebaseDatabase.getInstance().getReference("Users");

                        //Must be a ListenerForSingleValueEvent, or else it may keep prompting when the user logoffs.
                        userValid.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                alertDialog.dismiss();
                                alertDialog = new AlertDialog.Builder(activity).setTitle("Validando usuário").setView(R.layout.loading).show();

                                boolean valid = true;
                                // This for loops for all usernames and invalidates in case there is someone with the same
                                // username (Or the username is empty)
                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                    if (dataSnapshot1.getKey().equals(user) || user.isEmpty()) {
                                        valid = false;
                                        break;
                                    }
                                }

                                if (valid) {
                                    PassowrdValidator pv = new PassowrdValidator();
                                    if (pv.validatePassword(pass, conpass)) {
                                        mAuth.createUserWithEmailAndPassword(mail, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {

                                                alertDialog.dismiss();
                                                alertDialog = new AlertDialog.Builder(activity).setTitle("Atualizando perfil").setView(R.layout.loading).show();
                                                mAuth.signInWithEmailAndPassword(mail, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        final FirebaseUser fuser = authResult.getUser();
                                                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                                                        // Here we post the mail and uid in the Firebase database for future uses
                                                        DatabaseReference mailRef = database.getReference("Users/" + user + "/mail");
                                                        mailRef.setValue(mail);
                                                        DatabaseReference uidRef = database.getReference("Users/" + user + "/uid");
                                                        uidRef.setValue(fuser.getUid());

                                                // The DisplayName and PhotoUri are, for default, null in custom auth. But I'll be
                                                // using it later on, here we update with the default photo and the choosen username.
                                                        fuser.updateProfile(new UserProfileChangeRequest.Builder()
                                                                .setDisplayName(user)
                                                                .setPhotoUri(Uri.parse("https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909_960_720.png"))
                                                                .build()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                new Thread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        // Here the profile picture will be stored in the FirebaseStorage
                                                                        // as it'll be impossible to get the URL from other users.
                                                                        try {
                                                                            FirebaseStorage storage = FirebaseStorage.getInstance();
                                                                            StorageReference profilePic = storage.getReference("Users/" + user + "/Profile Picture");
                                                                            URL url = new URL("https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909_960_720.png");
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

                                                                // For avoiding Nullpoints in the MainScreen, the main Activity is only
                                                                // called after the profile update is complete.
                                                                Intent intent = new Intent("net.net16.httpschaos_workbench.rpgchat.MAIN_SCREEN");
                                                                activity.startActivity(intent);
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                alertDialog.dismiss();
                                                Toast.makeText(activity, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    } else {
                                        // This point is only called when the password is invalid
                                        alertDialog.dismiss();
                                        alertDialog = new AlertDialog.Builder(activity).setTitle(pv.getMessage()).setPositiveButton("Ok", null).show();
                                    }
                                } else {
                                    //This point is only called when the there's already someone with the given username.
                                    alertDialog.dismiss();
                                    alertDialog = new AlertDialog.Builder(activity).setTitle("Esse usuário já existe!").setPositiveButton("Ok", null).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(activity, "Erro no Banco de dados: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                            }
                        });

                    }
                })

                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * This one method will inflate an AlertDialog with the login form and an option for the autoLogin()
     * The autoLogin will login with the default user from the last credential from the app (This include the google account).
     * The autoLogin is the fastest way to login and the users will be advised about it.
     */
    private void login() {
        alertDialog = new AlertDialog.Builder(activity)
                .setTitle("Login")
                .setView(R.layout.login_form)
                .setPositiveButton("Login", authMail())
                .setNeutralButton("Login automático", autoLogin())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private DialogInterface.OnClickListener autoLogin() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    Intent intent = new Intent("net.net16.httpschaos_workbench.rpgchat.MAIN_SCREEN");
                    activity.startActivity(intent);
                } else {
                    alertDialog = new AlertDialog.Builder(activity)
                            .setTitle("Nenhum usuário automático detectado!")
                            .show();
                }
            }
        };
    }

    /**
     * This method contains the general custom login. With it, one can access his/hers account outside one's cellphone (If the account
     * is a custom account).
     *
     * @return Returns a OnClickListener for the login() AlertDialog.
     */
    private DialogInterface.OnClickListener authMail() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText username = alertDialog.findViewById(R.id.login_mail);
                final EditText password = alertDialog.findViewById(R.id.login_pass);

                alertDialog.dismiss();
                alertDialog = new AlertDialog.Builder(activity).setTitle("Carregando").setView(R.layout.loading).show();

                if (username != null && password != null) { // Asserts the EditTexts aren't null (They never are though).
                    // Asserts the username and passowrds aren't empty.
                    if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {

                        if (username.getText().toString().contains("@")) { // This checks wether it's an email or a username

                            // If the login is made directly with the email, it runs here.
                            mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    alertDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Intent i = new Intent("net.net16.httpschaos_workbench.rpgchat.MAIN_SCREEN");
                                        activity.startActivity(i);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(activity, "Email/senha errado.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else { // If the login is made with the username, it runs here. It'll seek the email stored in the database and login with it.
                            DatabaseReference mailRef = FirebaseDatabase.getInstance().getReference("Users/" + username.getText().toString() + "/mail");
                            mailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String user = dataSnapshot.getValue(String.class);
                                    if (user != null) {
                                        mAuth.signInWithEmailAndPassword(user, password.getText().toString()).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                alertDialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    Intent i = new Intent("net.net16.httpschaos_workbench.rpgchat.MAIN_SCREEN");
                                                    activity.startActivity(i);
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Toast.makeText(activity, "Email/senha errado.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) { Toast.makeText(activity, "Erro de Banco de dados: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show(); }
                            });
                        }

                    } else {
                        alertDialog.dismiss();
                        Toast.makeText(activity, "Senha ou nome de usuário vazio", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    alertDialog.dismiss();
                    Toast.makeText(activity, "Senha ou nome de usuário vazio", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
}