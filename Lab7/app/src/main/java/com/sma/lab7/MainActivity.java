package com.sma.lab7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sma.lab7.model.MonthlyExpenses;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TextView tStatus;
    private EditText eIncome, eExpenses;
    private String currentMonth;
    private ValueEventListener databaseListener;
    private final static String PREFS_SETTINGS = "prefs_settings";
    private SharedPreferences prefsUser, prefsApp;
    private String lastSearchedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tStatus = findViewById(R.id.tStatus);
        eIncome = findViewById(R.id.eIncome);
        eExpenses = findViewById(R.id.eExpenses);
        Button bUpdate = findViewById(R.id.bUpdate);

        // named preference file
        prefsUser = getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        // default prefs file for this app
        prefsApp = getPreferences(Context.MODE_PRIVATE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        Spinner spinner = findViewById(R.id.spinner);
        ArrayList<String> arrayList = new ArrayList<>();

        databaseReference.child("calendar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    String monthKey = uniqueKeySnapshot.getKey();
                    arrayList.add(monthKey);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Use shared preferences to save to current month so that when you relaunch the app, the
                // eSearch edittext will already be filled with the last searched month.
                // run -> Apply Changes and Restart Activity
                String lastSearchedMonth = prefsUser.getString("currentMonth", null);
                if (lastSearchedMonth != null)
                {
                    int spinnerPosition = arrayAdapter.getPosition(lastSearchedMonth);

                    // set the default according to value
                    spinner.setSelection(spinnerPosition);
                }

                spinner.setAdapter(arrayAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentMonth = parent.getItemAtPosition(position).toString();
                        prefsUser.edit().putString("currentMonth", currentMonth).apply();    // one-line code for writing data to preference file
                        tStatus.setText("Searching ...");
                        createNewDBListener();
                    }

                    @Override
                    public void onNothingSelected(AdapterView <?> parent) {
                    }
                });

                // notify the spinner that data may have changed
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eIncomeString = eIncome.getText().toString();
                String eExpensesString = eExpenses.getText().toString();
                float eIncomeFloat, eExpensesFloat;
                if (!eIncomeString.isEmpty() && !eExpensesString.isEmpty()) {
                    try {
                        currentMonth = spinner.getSelectedItem().toString();    // get current month name from spinner
                        prefsUser.edit().putString("currentMonth", currentMonth).apply();    // one-line code for writing data to preference file
                        eIncomeFloat = Float.parseFloat(eIncomeString);
                        eExpensesFloat = Float.parseFloat(eExpensesString);
                        updateDB(eIncomeFloat, eExpensesFloat);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Income and expenses values are not parsable.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Income and expenses fields may not be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDB(float income, float expenses) {
        if (databaseReference != null && currentMonth != null)
        {
            MonthlyExpenses monthlyExpenses = new MonthlyExpenses(currentMonth, income, expenses);
            prefsUser.edit().putString("currentMonth", currentMonth).apply();    // one-line code for writing data to preference file
            eIncome.setText(String.valueOf(income));
            eExpenses.setText(String.valueOf(expenses));
            tStatus.setText("Changed entry for " + currentMonth);
            databaseReference.child("calendar").child(currentMonth).setValue(monthlyExpenses);
        }
    }

    private void createNewDBListener() {
        // remove previous databaseListener
        if (databaseReference != null && currentMonth != null && databaseListener != null)
            databaseReference.child("calendar").child(currentMonth).removeEventListener(databaseListener);

        databaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String month;
                float income, expenses;

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MonthlyExpenses monthlyExpense = snapshot.child("calendar").child(currentMonth).getValue(MonthlyExpenses.class);

                // explicit mapping of month name from entry key
                if (monthlyExpense != null) {
                    month = snapshot.child("calendar").child(currentMonth).getKey();
                    prefsUser.edit().putString("currentMonth", month).apply();    // one-line code for writing data to preference file
                    monthlyExpense.month = month;

                    income = monthlyExpense.getIncome();
                    expenses = monthlyExpense.getExpenses();
                    eIncome.setText(String.valueOf(income));
                    eExpenses.setText(String.valueOf(expenses));
                    tStatus.setText("Found entry for " + currentMonth);
                    databaseReference.child("calendar").child(currentMonth).removeEventListener(databaseListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to read value.", Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.addValueEventListener(databaseListener);
        prefsUser.edit().putString("currentMonth", currentMonth).apply();    // one-line code for writing data to preference file


        // set new databaseListener
        assert databaseReference != null;
        databaseReference.child("calendar").child(currentMonth).addValueEventListener(databaseListener);
    }
}