package com.sma.lab3_1.lifecycle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sma.lab3_1.R;

public class ActivityB extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        Log.d("lifecycle", "onCreate B");

        Button buttonA = findViewById(R.id.buttonA);
        Button buttonB = findViewById(R.id.buttonB);
        Button buttonC = findViewById(R.id.buttonC);

        buttonA.setOnClickListener(this);
        buttonB.setOnClickListener(this);
        buttonC.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("lifecycle", "onStart B");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("lifecycle", "onResume B");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("lifecycle", "onPause B");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("lifecycle", "onStop B");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("lifecycle", "onRestart B");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle", "onDestroy B");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonA:
                startActivity(new Intent(this, ActivityA.class));
                break;
            case R.id.buttonB:
                startActivity(new Intent(this, ActivityB.class));
                break;
            case R.id.buttonC:
                startActivity(new Intent(this, ActivityC.class));
                break;
        }
    }
}