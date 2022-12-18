package com.sma.lab3_2.intents;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.sma.lab3_2.R;

public class IntentFilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_filter);

        TextView label = findViewById(R.id.textView);
        Uri receivedData = getIntent().getData();
        label.setText(receivedData.toString());
    }
}