package com.anuranbarman.gamedunia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by anuran on 5/11/16.
 */

public class Welcome extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        SharedPreferences sharedPreferences=getSharedPreferences("mypref", Context.MODE_PRIVATE);
        final boolean isLogged=sharedPreferences.getBoolean("isLogged",false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isLogged){
                    Intent intent=new Intent(Welcome.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent=new Intent(Welcome.this,Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }
}
