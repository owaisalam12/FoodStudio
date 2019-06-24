package com.core2plus.oalam.foodstudio.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showStatusBar(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start main activity
                // startActivity(new Intent(SplashActivity.this, ChooseActivity.class));
                startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                // close splash activity
                finish();
            }
        }, 1250);

    }

    public void showStatusBar(boolean isVisible) {
        if (!isVisible) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}