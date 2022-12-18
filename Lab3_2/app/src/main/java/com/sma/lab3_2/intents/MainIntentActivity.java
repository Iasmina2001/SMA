package com.sma.lab3_2.intents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.sma.lab3_2.R;

public class MainIntentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_intent);

        Button bViewGoogle = findViewById(R.id.buttonViewGoogle);
        Button bViewTel = findViewById(R.id.buttonViewTel);
        Button bMSALaunchGoogle = findViewById(R.id.buttonMSALaunchGoogle);
        Button bMSALaunchTel = findViewById(R.id.buttonMSALaunchTel);

        String URL = "http://www.google.com";
        String httpsURL = "http://www.google.com";
        String TEL = "tel:00401213456";

        bViewGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        android.content.Intent.ACTION_VIEW,
                        Uri.parse(URL)
                );
                startActivity(i);
            }
        });

        bViewTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        android.content.Intent.ACTION_VIEW,
                        Uri.parse(TEL)
                );
                startActivity(i);
            }
        });

        bMSALaunchGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        "com.sma.lab3_2.LAUNCH",
                        Uri.parse(httpsURL)
                );
                startActivity(i);
            }
        });

        bMSALaunchTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        "com.sma.lab3_2.LAUNCH",
                        Uri.parse(TEL)
                );
                startActivity(i);
            }
        });
    }
}