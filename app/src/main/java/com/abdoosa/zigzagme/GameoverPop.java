package com.abdoosa.zigzagme;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

public class GameoverPop extends Activity{
    Button showHighScores;
    TableLayout scoresTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting the layout for the game over pop screen
        setContentView(R.layout.gameoverpop);
        showHighScores = (Button) findViewById(R.id.highScores);
        scoresTable = (TableLayout) findViewById(R.id.scoresTable);
    }

}
