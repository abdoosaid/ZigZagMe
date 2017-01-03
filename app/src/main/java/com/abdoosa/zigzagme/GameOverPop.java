package com.abdoosa.zigzagme;

import android.app.Activity;
import android.os.Bundle;

public class GameOverPop extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // DisplayScoreAndShare the layout for the game over pop screen
        setContentView(R.layout.game_over_pop);
    }

}
