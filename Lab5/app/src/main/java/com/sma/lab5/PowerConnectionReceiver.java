package com.sma.lab5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

public class PowerConnectionReceiver extends BroadcastReceiver {

    protected Intent myBatteryStatusIntent;
    private static String textViewContent;

    @Override
    public void onReceive(Context context, Intent batteryStatusIntent) {
        this.myBatteryStatusIntent = batteryStatusIntent;
        final PendingResult pendingResult = goAsync();
        Task asyncTask = new Task(pendingResult, batteryStatusIntent, context);
        asyncTask.execute();
    }

    private static class Task extends AsyncTask<String, String, String> {

        private final PendingResult pendingResult;
        private final Intent tBatteryStatusIntent;
        private Context tContext;

        private Task(PendingResult pendingResult, Intent batteryStatusIntent, Context context) {
            this.pendingResult = pendingResult;
            this.tBatteryStatusIntent = batteryStatusIntent;
            this.tContext = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            Bundle extras = tBatteryStatusIntent.getExtras();
            String log = "";
            if (extras != null) {
                int status = tBatteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING);
                boolean batteryFull = (status == BatteryManager.BATTERY_STATUS_FULL);
                int level = tBatteryStatusIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = tBatteryStatusIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryPct = level * 100 / (float)scale;

                log = log + "scaled battery level: " + batteryPct + "%";

                textViewContent = log;
            }
            return log;
        }

        @Override
        protected void onPostExecute(String s) {
            TextView txtView = (TextView) ((Activity)tContext).findViewById(R.id.textView);
            txtView.setText(textViewContent);
            super.onPostExecute(s);
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingResult.finish();
        }
    }
}
