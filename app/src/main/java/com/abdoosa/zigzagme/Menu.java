package com.abdoosa.zigzagme;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
    }
    public void beginPlay(View view){
        Intent intent = new Intent(Menu.this, GameStart.class);
        startActivity(intent);
    }
}
