package net.net16.httpschaos_workbench.rpgchat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity{

    public static int RC_SIGN_IN = 0;

    private GoogleSignInClient mGoogleSignInClient;
    private AppCompatDialog dialog;
    private FirebaseAuth mAuth;
    private Drawable profilePic;
    private ImageView profileImage2;
    private ImageButton imageView0, imageView1, imageView2, imageView3, imageView4;
    private TextView profileName, profileStatus;
    private EditText nameEditor, statusEditor;
    private Button nameEditorOk, statusEditorOk;
    private LinearLayout nameLayout, statusLayout;
    private FirebaseUser user;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ImageView profileImage1 = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        profileStatus = findViewById(R.id.profileStatus);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        dialog = new AppCompatDialog(this);
        dialog.setContentView(R.layout.progress_show);
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        user = mAuth.getCurrentUser();
        if (account != null) {
            dialog.dismiss();
            loginSuccesfull();
        } else {
            dialog.setContentView(R.layout.login);
            dialog.show();
        }
    }

    public void signIn(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        dialog.setContentView(R.layout.progress_show);
        dialog.show();
        imageView0 = findViewById(R.id.imageButton0);
        imageView1 = findViewById(R.id.imageButton1);
        imageView2 = findViewById(R.id.imageButton2);
        imageView3 = findViewById(R.id.imageButton3);
        imageView4 = findViewById(R.id.imageButton4);
        imageView0.setOnClickListener(new ControlButtonsOnClickListener());
        imageView1.setOnClickListener(new ControlButtonsOnClickListener());
        imageView2.setOnClickListener(new ControlButtonsOnClickListener());
        imageView3.setOnClickListener(new ControlButtonsOnClickListener());
        imageView4.setOnClickListener(new ControlButtonsOnClickListener());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            alertDialog = new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Login bem sucedido")
                                    .setPositiveButton("OK", null)
                                    .show();
                            user = mAuth.getCurrentUser();
                            loginSuccesfull();
                        } else {
                            // If sign in fails, display a message to the user.
                            alertDialog = new AlertDialog.Builder(getApplicationContext())
                                    .setTitle("Login falhou")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }

                        // ...
                    }
                });
    }

    private void loginSuccesfull() {
        UserValueEventListener nameValueEventListener = new UserValueEventListener(user, profileName);
        UserValueEventListener statusValueEventListener = new UserValueEventListener(user, profileStatus);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference statusReference = database.getReference("Status");
        statusReference.addValueEventListener(statusValueEventListener);
        DatabaseReference nameReference = database.getReference("Usuario");
        nameReference.addValueEventListener(nameValueEventListener);
    }

    public void openProfile(MenuItem i) {
        dialog.setContentView(R.layout.profile_view);
        dialog.show();
        profileImage2 = findViewById(R.id.profileImage2);
        profileImage2.setImageDrawable(profilePic);
        profileName.setText(user.getDisplayName());
    }

    public void openAbout(MenuItem i) {
    }

    public void logout(MenuItem i) {
    }

    public void setProfilePic(Drawable pic) {
        profilePic = pic;
    }

    public void changeProfileName(View v) {
    }

    public void changeStatus(View v) {
    }

    private void errMessage(String msg) {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Erro:")
                .setMessage(msg)
                .setPositiveButton("Ok", null)
                .show();
    }
}
