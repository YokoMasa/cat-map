package com.masaworld.catmap.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.masaworld.catmap.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        WebView webView = findViewById(R.id.license_webview);
        webView.loadData(loadHtml(), "text/html", "utf-8");
    }

    private String loadHtml() {
        InputStream is = getResources().openRawResource(R.raw.license);
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        try {
            StringBuilder sb = new StringBuilder();
            String line = bf.readLine();

            while (line != null) {
                sb.append(line);
                line = bf.readLine();
            }
            return sb.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return "Error occurred";
        } finally {
            try {
                is.close();
            } catch (IOException e) {}
        }
    }
}
