package com.theyellowmirror.sightseer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.content.Intent;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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


    }


}
