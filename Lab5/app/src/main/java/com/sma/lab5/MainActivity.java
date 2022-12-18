package com.sma.lab5;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final int NOTIFICATION_ID = 1234;
    PowerConnectionReceiver powerConnectionReceiver;
    public TextView textView;
    Intent batteryStatusIntent;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        powerConnectionReceiver = new PowerConnectionReceiver();

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatusIntent = this.registerReceiver(powerConnectionReceiver, intentFilter);

        context = getApplicationContext();
        // Create an explicit intent for an Activity in your app
        Intent newIntent = new Intent(context, MainActivity.class);
        newIntent.putExtra("status", "chargingStatus");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_IMMUTABLE);

        final String channelID = "my channel id";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, "my channel name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Charging status changed!")
                .setContentText("chargingStatus")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(MainActivity.NOTIFICATION_ID, mBuilder.build());
    }
}