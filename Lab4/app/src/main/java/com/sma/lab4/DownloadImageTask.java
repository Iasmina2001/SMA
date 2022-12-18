package com.sma.lab4;

import static com.sma.lab4.ForegroundImageService.STOPFOREGROUND_ACTION;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private Context context;
    private Bitmap myBitmap;

    public DownloadImageTask(@NonNull Context context) {
        this.context = context;
        // Toast.makeText(context.getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        myBitmap = downloadImage(urls[0]);
        return myBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        // save bitmap result in application class
        ((MyApplication) context.getApplicationContext()).setBitmap(result);
        // send intent to stop foreground service
        Intent stopForegroundIntent = new Intent(context.getApplicationContext(), ForegroundImageService.class);
        stopForegroundIntent.setAction(STOPFOREGROUND_ACTION);
        context.startService(stopForegroundIntent);
    }

    private Bitmap downloadImage(String url) {
        try {
            String longURL = URLTools.getLongUrl(url);
            Bitmap bmp = null;
            try {
                InputStream in = new URL(longURL).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}