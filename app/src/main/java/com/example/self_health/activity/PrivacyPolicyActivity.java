package com.example.self_health.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.self_health.R;

/**
 * Created by pc on 11/13/2016.
 */

public class PrivacyPolicyActivity extends AppCompatActivity{

    private  WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_polacy);
        mWebView = (WebView) findViewById(R.id.webView1);
        mWebView.loadUrl("https://www.google.com/policies/privacy/");

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());
    }


}
