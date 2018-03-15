package com.example.david.testing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView skipLink = findViewById(R.id.button);
        skipLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent skipIntent = new Intent(MainActivity.this, MenuActivity.class);
                MainActivity.this.startActivity(skipIntent);
            }
        });
    }
}
