package com.theyellowmirror.sightseer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView login;
    private EditText email;
    private EditText password;
    private static final String TAG ="LoginActivity";
    private static final int ERROR_DIALOG_REQUEST =9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.login);
        email = findViewById(R.id.emailField);
        password = findViewById(R.id.passField);

        //checks if the user has the right google service
        //Links homeActivity with textView "Skip"- skips to main/home page
        if(isServiceOK()){
            init();
        }


        //Links registerActivity with textView "Register Here"
        Button registerLink = (Button) findViewById(R.id.signUp);
        registerLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
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

    //google service permission
    public void init(){
        Button skipBT = (Button) findViewById(R.id.skipBT);
        skipBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        Intent skipIntent = new Intent(LoginActivity.this, MenuActivity.class);
                        LoginActivity.this.startActivity(skipIntent);


            }
        });
    }
    public boolean isServiceOK(){
        Log.d(TAG,"isServiceOK: checking google service");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine, user can make map requests
            Log.d(TAG,"isServiceOK: Google Play Service is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG,"isServiceOK: an error occured, but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this, available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this,"We can't make map requests",Toast.LENGTH_SHORT).show();
        }
        return false;

    }

}
