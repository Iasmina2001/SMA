package com.sma.lab4;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

// background service class loads the image from url into activity_image.xml
public class ImageIntentService extends IntentService {

    public static volatile boolean continueBackgroundService = true;

    public ImageIntentService() {
        super("ImageIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String param = intent.getStringExtra(WebsearchActivity.EXTRA_URL);
            handleDownloadAction(param);
        }
        if (!continueBackgroundService)
            stopSelf();
    }

    /**
     * Handle action in the provided background thread with the provided parameters.
     */
    private void handleDownloadAction(String url) {
        // start task on separate thread
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

            // simulate longer job ...
            Thread.sleep(5000);

            ((MyApplication) getApplicationContext()).setBitmap(bmp);
            // start second activity to show result
            Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (IOException e) {
            Log.e("IOException: ", e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.e("InterruptedException: ", e.getMessage());
            e.printStackTrace();
        }
    }
}
