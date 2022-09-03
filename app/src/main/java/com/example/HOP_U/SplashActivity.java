package com.example.HOP_U;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{ Thread.sleep(1000); }
        catch (InterruptedException e) { e.printStackTrace(); }
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}
