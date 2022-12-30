package com.sma.lab11;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sma.lab11.ui.Payment;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private final static String PREFS_SETTINGS = "prefs_settings";
    private final static String TAG_MONTH = "current_month";
    private SharedPreferences prefsUser;
    private int currentMonth;
    private List<Payment> payments = new ArrayList<>();
    private static FirebaseAuth.AuthStateListener mAuthListener;
    public static final int SU_CODE = 101;
    public static final int SI_CODE = 106;
    private FirebaseAuth mAuth;
    ActivityResultLauncher<Intent> mStartForResult;    // -- register callback --

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        AppState.get().setDatabaseReference(databaseReference);

        TextView tStatus = findViewById(R.id.tStatus);
        Button bPrevious = findViewById(R.id.bPrevious);
        Button bNext = findViewById(R.id.bNext);
        Button bLogOut = findViewById(R.id.bLogOut);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        ListView listPayments = findViewById(R.id.listPayments);

        final PaymentAdapter adapter = new PaymentAdapter(this, R.layout.item_payment, payments);
        listPayments.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent intent = result.getData();
                        if (resultCode == SI_CODE) {
                            if (intent != null)
                            {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();
                            }
                        } else if (resultCode == SU_CODE) {
                            Toast.makeText(getApplicationContext(), "Signed up", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    AppState.get().setUserId(user.getUid());
                    attachDBListener(user.getUid(), adapter);
                } else {
                    // User is signed out
                    AppState.get().setUserId(null);
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    mStartForResult.launch(intent);
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        // named preference file
        prefsUser = getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        currentMonth = prefsUser.getInt(TAG_MONTH, -1);
        if (currentMonth == -1)
            currentMonth = Month.monthFromTimestamp(AppState.getCurrentTimeDate());
        AppState.get().setCurrentMonth(currentMonth);

        AppState.get().setCurrentPayment(null);
        // AppState.get().setUserId(null);

        tStatus.setText(Month.intToMonthName(currentMonth));

        if (AppState.get().getDatabaseReference() == null && !AppState.isNetworkAvailable(this)) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

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

        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppState.get().setCurrentPayment(null);
                startActivity(new Intent(MainActivity.this, AddPaymentActivity.class));
            }
        });

        listPayments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppState.get().setCurrentPayment(payments.get(i));
                startActivity(new Intent(MainActivity.this, AddPaymentActivity.class));
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void attachDBListener(String currentUserUID, PaymentAdapter adapter) {
        AppState.get().getDatabaseReference().child("wallet").child(currentUserUID).addChildEventListener(new ChildEventListener() {
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
                            if (!payments.contains(payment)) {
                                payments.add(payment);
                            }
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
    }
}