package com.sma.lab10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sma.lab10.ui.Payment;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private final static String PREFS_SETTINGS = "prefs_settings";
    private final static String TAG_MONTH = "current_month";
    private SharedPreferences prefsUser;
    private int currentMonth;
    private List<Payment> payments = new ArrayList<>();
    FirebaseDatabase database = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // named preference file
        prefsUser = getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        currentMonth = prefsUser.getInt(TAG_MONTH, -1);
        if (currentMonth == -1)
            currentMonth = Month.monthFromTimestamp(AppState.getCurrentTimeDate());
        AppState.get().setCurrentMonth(currentMonth);

        TextView tStatus = findViewById(R.id.tStatus);
        Button bPrevious = findViewById(R.id.bPrevious);
        Button bNext = findViewById(R.id.bNext);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        ListView listPayments = findViewById(R.id.listPayments);

        tStatus.setText(Month.intToMonthName(currentMonth));

        if (AppState.get().getDatabaseReference() == null && !AppState.isNetworkAvailable(this)) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        AppState.get().setDatabaseReference(databaseReference);

        if (!AppState.isNetworkAvailable(this)) {
            // has local storage already
            if (AppState.get().hasLocalStorage(this)) {
                List<Payment> allPayments = AppState.get().loadFromLocalBackup(this, Month.intToMonthName(currentMonth));
                List<Payment> newPayments = new ArrayList<>(allPayments);
                newPayments.removeAll(allPayments);    // removes duplicate items, obtaining only the new ones
                if (newPayments.size() > 0) {
                    payments.addAll(newPayments);    // adds the new payment items to the payments arraylist
                }
            } else {
                Toast.makeText(this, "This app needs an internet connection!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        final PaymentAdapter adapter = new PaymentAdapter(this, R.layout.item_payment, payments);
        listPayments.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppState.get().setCurrentPayment(null);
                startActivity(new Intent(getApplicationContext(), AddPaymentActivity.class));
            }
        });

        listPayments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppState.get().setCurrentPayment(payments.get(i));
                startActivity(new Intent(getApplicationContext(), AddPaymentActivity.class));
            }
        });

        databaseReference.child("wallet").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    if (currentMonth == Month.monthFromTimestamp(Objects.requireNonNull(snapshot.getKey()))) {
                        String costString;
                        String name = null;
                        String timestamp;
                        String type = null;
                        double cost = -1;    // cost cannot be negative

                        if (snapshot.child("cost").exists()) {
                            costString = Objects.requireNonNull(snapshot.child("cost").getValue()).toString();
                            cost = Double.parseDouble(costString);
                        }

                        if (snapshot.child("name").exists()) {
                            name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                        }

                        timestamp = snapshot.getKey();

                        if (snapshot.child("type").exists()) {
                            type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                        }

                        if (name != null && type != null && cost != -1) {
                            Payment payment = new Payment(cost, name, type, timestamp);
                            payments.add(payment);
                        }

                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    if (currentMonth == Month.monthFromTimestamp(Objects.requireNonNull(snapshot.getKey()))) {
                        String costString;
                        String name = null;
                        String timestamp;
                        String type = null;
                        double cost = -1;    // cost cannot be negative

                        if (snapshot.child("cost").exists()) {
                            costString = Objects.requireNonNull(snapshot.child("cost").getValue()).toString();
                            cost = Double.parseDouble(costString);
                        }

                        if (snapshot.child("name").exists()) {
                            name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                        }

                        timestamp = snapshot.getKey();

                        if (snapshot.child("type").exists()) {
                            type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                        }

                        if (name != null && type != null && cost != -1) {
                            Payment payment = new Payment(cost, name, type, timestamp);
                            payments.remove(AppState.get().getCurrentPayment());
                            payments.add(payment);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // :D
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String costString;
                String name = null;
                String timestamp;
                String type = null;
                double cost = -1;    // cost cannot be negative

                if (snapshot.child("cost").exists()) {
                    costString = Objects.requireNonNull(snapshot.child("cost").getValue()).toString();
                    cost = Double.parseDouble(costString);
                }

                if (snapshot.child("name").exists()) {
                    name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                }

                timestamp = snapshot.getKey();

                if (snapshot.child("type").exists()) {
                    type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                }

                if (name != null && type != null && cost != -1) {
                    Payment payment = new Payment(cost, name, type, timestamp);
                    payments.remove(payment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                adapter.notifyDataSetChanged();
            }
        });

        bPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMonth = (currentMonth - 1) % 12;
                SharedPreferences.Editor editor = prefsUser.edit();
                editor.putInt(TAG_MONTH, currentMonth).apply();
                recreate();
            }
        });

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMonth = (currentMonth + 1) % 12;
                SharedPreferences.Editor editor = prefsUser.edit();
                editor.putInt(TAG_MONTH, currentMonth).apply();
                recreate();
            }
        });
    }
}