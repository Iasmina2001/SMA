package com.sma;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button greetButton = findViewById(R.id.buttonGreet);
        Button shareButton = findViewById(R.id.buttonShare);
        Button searchButton = findViewById(R.id.buttonSearch);
        EditText eName = findViewById(R.id.editTextPersonName);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("red");
        arrayList.add("green");
        arrayList.add("blue");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String colorString = parent.getItemAtPosition(position).toString();
                switch (colorString) {
                    case "red":
                        greetButton.setTextColor(getApplication().getResources().getColor(R.color.red));
                        break;
                    case "green":
                        greetButton.setTextColor(getApplication().getResources().getColor(R.color.green));
                        break;
                    case "blue":
                        greetButton.setTextColor(getApplication().getResources().getColor(R.color.blue));
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        // to get Context
        Context context = getApplicationContext();
        // toast time duration, can also set manual value
        int duration = Toast.LENGTH_SHORT;

        greetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                String greeting = "Hello, " + eName.getText().toString() + "! :D";
                builder.setCancelable(true);
                builder.setMessage(greeting);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // message to display
                        String text = "Positive button clicked";
                        Toast toast = Toast.makeText(context, text, duration);
                        // to show the toast
                        toast.show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // message to display
                        String text = "Negative button clicked";
                        Toast toast = Toast.makeText(context, text, duration);
                        // to show the toast
                        toast.show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   shareText(eName.getText().toString());
               }
           }
        );

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOnBrowser();
            }
        });
    }

    public void searchOnBrowser() {
        String url = "https://www.google.com/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void shareText(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    /*public void onButtonShowPopupWindowClick(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        TextView textView_popup = popupView.findViewById(R.id.id_textView_popup);
        EditText eName = findViewById(R.id.editTextPersonName);
        String greeting = "Hello, " + eName.getText().toString() + "! :D";
        textView_popup.setText(greeting);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }*/
}