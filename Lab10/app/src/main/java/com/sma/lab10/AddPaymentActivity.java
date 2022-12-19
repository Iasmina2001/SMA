package com.sma.lab10;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.sma.lab10.ui.Payment;
import com.sma.lab10.ui.PaymentType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class AddPaymentActivity extends AppCompatActivity {
    private Payment myPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        EditText eName = findViewById(R.id.nameValue);
        EditText eCost = findViewById(R.id.costValue);
        Button bSave = findViewById(R.id.bSave);
        Button bDelete = findViewById(R.id.bDelete);
        Spinner sType = findViewById(R.id.spinner);
        TextView tTimestamp = findViewById(R.id.tTimestamp);

        String[] types = PaymentType.getTypes();
        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, types);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sType.setAdapter(sAdapter);

        myPayment = AppState.get().getCurrentPayment();
        String timestamp = AppState.getCurrentTimeDate();

        if (myPayment != null) {
            eName.setText(myPayment.getName());
            eCost.setText(String.valueOf(myPayment.getCost()));
            tTimestamp.setText("Time of payment: " + myPayment.getTimestamp());
            try {
                sType.setSelection(Arrays.asList(types).indexOf(myPayment.getType()));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception: " + e, Toast.LENGTH_SHORT).show();
            }
        } else {
            tTimestamp.setText(timestamp);
        }

        bSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                myPayment = AppState.get().getCurrentPayment();
                String costString = eCost.getText().toString();
                String nameString = eName.getText().toString();
                String typeString = sType.getSelectedItem().toString();
                double cost = -1;
                try {
                    cost = Double.parseDouble(costString);
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Cost value is not parsable.", Toast.LENGTH_SHORT).show();
                }

                if (myPayment != null) {
                    save(myPayment.getTimestamp(), nameString, cost, typeString);
                }
                else {
                    if (costString.length() > 0 && nameString.length() > 0 && typeString.length() > 0) {
                        String timestamp = AppState.getCurrentTimeDate();
                        cost = Double.parseDouble(costString);
                        myPayment = new Payment(cost, nameString, typeString, timestamp);
                        save(timestamp, nameString, cost, typeString);
                    }
                }

                if (!AppState.isNetworkAvailable(getApplicationContext())) {
                    AppState.get().updateLocalBackup(getApplicationContext(), myPayment, true);
                }
            }
        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myPayment = AppState.get().getCurrentPayment();
                if (myPayment != null) {
                    if (!AppState.isNetworkAvailable(getApplicationContext())) {
                        AppState.get().updateLocalBackup(getApplicationContext(), myPayment, false);
                    }
                    delete(myPayment.getTimestamp());
                } else {
                    Toast.makeText(getApplicationContext(), "Payment does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void delete(String timestamp) {
        AppState.get().getDatabaseReference().child("wallet").child(timestamp).removeValue();
        AppState.get().getDatabaseReference().child("wallet").child(timestamp).keepSynced(true);
    }

    private void save(String timestamp, String name, double cost, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("cost", cost);
        map.put("type", type);
        AppState.get().getDatabaseReference().child("wallet").child(timestamp).updateChildren(map);
        AppState.get().getDatabaseReference().child("wallet").child(timestamp).keepSynced(true);
    }
}