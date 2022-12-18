package com.sma.lab8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sma.lab8.ui.Payment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class NewPayment extends AppCompatActivity {
    private String typeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_payment);

        EditText eCost = findViewById(R.id.costValue);
        EditText eName = findViewById(R.id.nameValue);
        Button bAddPayment = findViewById(R.id.bAddPayment);
        Spinner spinner = findViewById(R.id.spinner);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("entertainment");
        arrayList.add("food");
        arrayList.add("taxes");
        arrayList.add("travel");
        arrayList.add("other");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeString = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        bAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String costString = eCost.getText().toString();
                String nameString = eName.getText().toString();

                String timestampString = getCurrentTimeDate();
                if (costString.length() > 0 && nameString.length() > 0 && typeString.length() > 0 && timestampString.length() > 0) {
                    try {
                        double cost = Double.parseDouble(costString);
                        Payment payment = new Payment(timestampString, cost, nameString, typeString);
                        databaseReference.child("wallet").addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                databaseReference.child("wallet").child(timestampString).setValue(payment);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Cost value is not parsable.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public static String getCurrentTimeDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    public void writeNewPayment(String userId, Payment payment) {

        // mDatabase.child("users").child(userId).setValue(user);
    }
}