package com.sma.lab9;

import android.annotation.SuppressLint;
import com.google.firebase.database.DatabaseReference;
import com.sma.lab9.ui.Payment;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AppState {

    private static AppState singletonObject;
    private static int currentPaymentIndex;
    private DatabaseReference databaseReference;    // reference to Firebase used for reading and writing data
    private Payment currentPayment;    // current payment to be edited or deleted

    public static synchronized AppState get() {
        if (singletonObject == null) {
            singletonObject = new AppState();
        }
        return singletonObject;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void setCurrentPayment(Payment currentPayment) {
        this.currentPayment = currentPayment;
    }

    public Payment getCurrentPayment() {
        return currentPayment;
    }

    public static String getCurrentTimeDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }
}