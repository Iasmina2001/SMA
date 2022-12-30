package com.sma.lab11;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.sma.lab11.ui.Payment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AppState {

    private static String userID;
    private Payment currentPayment;    // current payment to be edited or deleted
    private int currentMonthInt;
    private static AppState singletonObject;
    private DatabaseReference databaseReference;    // reference to Firebase used for reading and writing data

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

    public void setCurrentMonth(int month) { this.currentMonthInt = month; }

    public int getCurrentMonth() { return this.currentMonthInt; }

    public void setUserId(String uid) { userID = uid; }

    public String getUserID() { return userID; }

    public static String getCurrentTimeDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    public void updateLocalBackup(Context context, Payment payment, boolean toAdd) {
        String fileName = payment.getTimestamp();
        try {
            if (toAdd) {
                // save to file
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(payment.copy());
                os.close();
                fos.close();
            } else {
                context.deleteFile(fileName);
            }
        } catch (IOException e) {
            Toast.makeText(context, "Cannot access local data.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean hasLocalStorage(Context context) {
        return Objects.requireNonNull(context.getFilesDir().listFiles()).length > 0;
    }

    public List<Payment> loadFromLocalBackup(Context context, String month) {
        try {
            List<Payment> payments = new ArrayList<>();
            for (File file : Objects.requireNonNull(context.getFilesDir().listFiles())) {
                if (isMyFile(file.getName()))
                {
                    FileInputStream fis = context.openFileInput(file.getName());
                    ObjectInputStream is = new ObjectInputStream(fis);
                    Payment payment = (Payment) is.readObject();
                    if (Month.monthFromTimestamp(payment.getTimestamp()) == AppState.get().getCurrentMonth())
                        payments.add(payment);
                    is.close();
                    fis.close();
                }
            }
            return payments;
        } catch (IOException e) {
            Toast.makeText(context, "Cannot access local data.", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connected;
        //we are connected to a network
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }

    // My files have names of timestamps
    public boolean isMyFile(String fileName) {
        String regEx = "([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})$";

        Pattern pattern = Pattern.compile(regEx,Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(fileName);

        return matcher.matches();
    }
}