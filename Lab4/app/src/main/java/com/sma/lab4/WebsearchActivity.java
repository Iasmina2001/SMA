package com.sma.lab4;

import static com.sma.lab4.ForegroundImageService.STARTFOREGROUND_ACTION;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("SetJavaScriptEnabled")
public class WebsearchActivity extends AppCompatActivity {

    public static String EXTRA_URL = "";
    private WebView myWebView;
    private String url;
    Button bSearchForeground;
    Button bSearchBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        myWebView = findViewById(R.id.webview);
        myWebView.setWebViewClient(new MyCustomWebViewClient());
        WebSettings webSetting = myWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);
        myWebView.loadUrl("https://www.google.com");

        bSearchForeground = findViewById(R.id.bLoadForeground);
        bSearchBackground = findViewById(R.id.bLoadBackground);    // loads image from url in ImageActivity

        bSearchForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData abc = clipboard.getPrimaryClip();
                ClipData.Item item = abc.getItemAt(0);
                url = item.getText().toString();
                if (url.isEmpty())
                    Toast.makeText(getApplicationContext(), "Please enter URL", Toast.LENGTH_SHORT).show();
                else if (!url.contains("http"))
                    Toast.makeText(getApplicationContext(), "URL not valid. Try another.", Toast.LENGTH_SHORT).show();
                else {
                    // start foreground service to download with notification
                    Intent startIntent = new Intent(view.getContext(), ForegroundImageService.class);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startIntent.setAction(STARTFOREGROUND_ACTION);
                    startIntent.putExtra(EXTRA_URL, url);
                    startService(startIntent);
                }
            }
        });

        bSearchBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData abc = clipboard.getPrimaryClip();
                ClipData.Item item = abc.getItemAt(0);
                url = item.getText().toString();
                if (url.isEmpty())
                    Toast.makeText(getApplicationContext(), "Please enter URL", Toast.LENGTH_SHORT).show();
                else if (!url.contains("http"))
                    Toast.makeText(getApplicationContext(), "URL not valid. Try another.", Toast.LENGTH_SHORT).show();
                else
                {
                    // start background service to download in background
                    Intent intent = new Intent(view.getContext(), ImageIntentService.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(EXTRA_URL, url);
                    startService(intent);
                }
            }
        });
    }

    public class MyCustomWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            if (Uri.parse(url).toString().startsWith("http") &&
                    Uri.parse(url).toString().contains(".")) {
                // This is my URL, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another

            // Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
}