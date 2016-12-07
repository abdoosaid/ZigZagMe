package com.abdoosa.zigzagme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class Splash extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Setting a sleep delay and switch the activity to menu
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                } catch (InterruptedException e){

                }finally{
                    Intent menu = new Intent("android.intent.action.MENU");
                    startActivity(menu);
                }

            }
        };
        timer.start();
    }
}
