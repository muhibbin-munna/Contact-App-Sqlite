package com.app.contactappsp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(this, MainActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {

                    sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    startActivity(intent);
                    finish();
                }

            }
        };
        timer.start();
    }
}
