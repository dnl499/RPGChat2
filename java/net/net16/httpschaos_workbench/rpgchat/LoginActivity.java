package net.net16.httpschaos_workbench.rpgchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

import net.net16.httpschaos_workbench.rpgchat.login_methods.CustomAuth;
import net.net16.httpschaos_workbench.rpgchat.login_methods.GoogleLogin;

public class LoginActivity extends AppCompatActivity{

    public static int GOOGLE_LOGIN_RC = 1;
    GoogleLogin gl;
    CustomAuth ca;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        mAuth = FirebaseAuth.getInstance();
        gl = new GoogleLogin(this, mAuth);
        ca = new CustomAuth(this, mAuth);

        SignInButton sib = findViewById(R.id.sign_in_button);
        Button loginButton = findViewById(R.id.custom_login);
        Button sigupButton = findViewById(R.id.custom_signup);
        sib.setOnClickListener(gl);
        loginButton.setOnClickListener(ca);
        sigupButton.setOnClickListener(ca);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_LOGIN_RC) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null) gl.firebaseAuthWithGoogle(account);
                else Toast.makeText(this, "Login falhou", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login pelo google falhou", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
