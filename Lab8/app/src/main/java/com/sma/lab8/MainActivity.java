package com.sma.lab8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sma.lab8.ui.Payment;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    // firebase
    private int currentMonth;
    private List<Payment> payments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tStatus = findViewById(R.id.tStatus);
        Button bPrevious = findViewById(R.id.bPrevious);
        Button bNext = findViewById(R.id.bNext);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        ListView listPayments = findViewById(R.id.listPayments);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        final PaymentAdapter adapter = new PaymentAdapter(this, R.layout.item_payment, payments);
        listPayments.setAdapter(adapter);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewPayment.class));
            }
        });

        databaseReference.child("wallet").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String costString = null;
                String name = null;
                String timestamp = null;
                String type = null;
                double cost = -1;    // cost cannot be negative

                if (snapshot.child("cost").exists()) {
                    costString = Objects.requireNonNull(snapshot.child("cost").getValue()).toString();
                    cost = Double.parseDouble(costString);
                }

                if (snapshot.child("name").exists()) {
                    name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                }

                if (snapshot.child("timestamp").exists()) {
                    timestamp = Objects.requireNonNull(snapshot.child("timestamp").getValue()).toString();
                }

                if (snapshot.child("timestamp").exists()) {
                    type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                }

                if (costString != null && name != null && timestamp != null && type != null && cost != -1) {
                    Payment payment = new Payment(timestamp, cost, name, type);
                    payments.add(payment);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}