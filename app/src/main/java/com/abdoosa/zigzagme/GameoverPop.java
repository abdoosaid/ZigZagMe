package com.abdoosa.zigzagme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class GameoverPop extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the layout for the game over pop screen
        setContentView(R.layout.gameoverpop);

        final Bundle bundle = getIntent().getExtras();
        String[] data = bundle.getStringArray("key");
        bundle.putStringArray("key", data);

    }

}
