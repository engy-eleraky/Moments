package com.example.admin.moments;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.admin.moments.navigation.NavigationActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

                Handler handler= new Handler();
                handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent mainIntent = new Intent(SplashActivity.this, NavigationActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }//run
                },3000);


    }
}
