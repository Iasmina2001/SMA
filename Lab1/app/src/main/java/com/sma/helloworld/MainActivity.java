package com.sma.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonClick = (Button) findViewById(R.id.button);
        TextView tName = (TextView) findViewById(R.id.textView);
        EditText eName = (EditText) findViewById(R.id.editTextTextPersonName);

        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String greeting = "Hello, " + eName.getText().toString() + "! :D";
                tName.setText((CharSequence) greeting);
            }
        });
    }

}