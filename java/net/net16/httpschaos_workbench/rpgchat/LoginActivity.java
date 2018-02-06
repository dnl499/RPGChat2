package net.net16.httpschaos_workbench.rpgchat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
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

public class LoginActivity extends AppCompatActivity {

    public static int RC_SIGN_IN = 0;

    private GoogleSignInClient mGoogleSignInClient;
    private AppCompatDialog dialog;
    private FirebaseAuth mAuth;
    private ArrayAdapter<LinearLayout> contactAdpter;
    private ListView contactList;
    AlertDialog alertDialog;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        contactList = findViewById(R.id.listView0);

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
            loginSuccesfull(user);
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
                            loginSuccesfull(user);
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

    private void loginSuccesfull(FirebaseUser user) {
        TextView textView = findViewById(R.id.textView);
        textView.setText(user.getDisplayName());
        ImageView imageView = findViewById(R.id.profileImage);
        imageView.setImageURI(user.getPhotoUrl());
    }

    public Context getContext() {
        return this;
    }
}
