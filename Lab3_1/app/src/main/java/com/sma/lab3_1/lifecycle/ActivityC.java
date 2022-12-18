package com.sma.lab3_1.lifecycle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.sma.lab3_1.R;

public class ActivityC extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c);
        Log.d("lifecycle", "onCreate C");

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
        Log.d("lifecycle", "onStart C");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("lifecycle", "onResume C");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("lifecycle", "onPause C");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("lifecycle", "onStop C");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("lifecycle", "onRestart C");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle", "onDestroy C");
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