package com.theyellowmirror.sightseer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView login;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login);
        email = findViewById(R.id.emailField);
        password = findViewById(R.id.passField);

        //Links registerActivity with textView "Register Here"
        TextView registerLink = findViewById(R.id.registerHereTextView);
        registerLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        //Links homeActivity with textView "Skip"- skips to main/home page
        TextView skipLink = findViewById(R.id.skipTextView);
        skipLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent skipIntent = new Intent(LoginActivity.this, MenuActivity.class);
                LoginActivity.this.startActivity(skipIntent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signIn();
            }
        });

    }
    //TODO: Implement an updateGUI to pass screen more legitimately.
    public void signIn(){
        Log.d("Strings", email.getText().toString()+password.getText().toString());

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent skipIntent = new Intent(LoginActivity.this, MenuActivity.class);
                            LoginActivity.this.startActivity(skipIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

