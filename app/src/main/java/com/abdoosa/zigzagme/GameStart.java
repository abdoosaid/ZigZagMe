package com.abdoosa.zigzagme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class GameStart extends Activity {

    public SharedPreferences keyValues;
    public SharedPreferences.Editor keyValuesEditor;

    // Declaring different layouts and views
    RelativeLayout game_layout;
    TableLayout scoresTable, difficultyTable;
    LayoutInflater layoutInflater;
    TextView counterText, scoreShow, score1, score2, score3, score4, scoreLabel;
    ViewGroup popupLayout;
    PopupWindow popupWindow;
    Button showHighScores, difficultyBtn, level1, level2, level3, muteBtn, replayBtn;


    // Declaring different variables
    Pillar[] pillars = new Pillar[23];
    DisplayMetrics disp = new DisplayMetrics();
    Top[] tops = new Top[23];
    int[] highScores = new int[4];
    Handler h = new Handler();
    GiantPillar giantP;
    TopGiantPillar topG;
    Ball ball;
    static int devHeight, devWidth;
    int r1, l1, r2, l2, counter, level = 1, DELAY;
    boolean ballRight=true, gameOver=false, mute = false, inGiantP, firstGame=true;
    float xOffset, yOffset;
    MediaPlayer beep, gameOverBeep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On creating , the contentView is R.layout.game_layout
        // and the two methods are invoked
        setContentView(R.layout.game_layout);
        // Initializing the sounds
        beep = MediaPlayer.create(this, R.raw.beep3);
        gameOverBeep = MediaPlayer.create(this, R.raw.gameover2);
        initiateView();

    }

    @Override
    public void onBackPressed() {
        if (popupWindow.isShowing())
            moveTaskToBack(true);
        else
            popupWindow.showAtLocation(game_layout, Gravity.CENTER, 0, 0);
    }

    // switching to DisplayScoreAndShare activity
    public void movingToSharingActivity(View v){
        final Bundle bundle = getIntent().getExtras();
        String[] data = bundle.getStringArray("key");
        bundle.putStringArray("key", data);
        final Intent intent = new Intent(this, DisplayScoreAndShare.class);

        scoreLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public static void sendViewToBack(final View child){
        // This method is to send the view to back
        final ViewGroup parent = (ViewGroup) child.getParent();
        if(parent != null) {
            parent.removeView(child);
            parent.addView(child, 0);
        }

    }

    public void initiateView(){
        // Initiates the static view of the main layout of the game
        linkingXMLViews();
        settingDynamicViews();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // if is the first time to play, some views are hidden from gameover layout
                if(firstGame){
                    popupWindow.showAtLocation(game_layout, Gravity.CENTER, 0, 0);
                    scoreLabel.setVisibility(View.INVISIBLE);
                    scoreShow.setVisibility(View.INVISIBLE);
                    counterText.setVisibility(View.INVISIBLE);
                    popupLayout.setBackgroundColor(Color.argb(148,90, 0, 0));
                }
            }
        }, 2);

        // if a touch has been detected, the pillars and the ball start moving
        game_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game_layout.setOnClickListener(null);
                movement();
            }
        });
    }

    public void linkingXMLViews() {
        game_layout = (RelativeLayout) findViewById(R.id.game_layout);
        setContentView(game_layout);
        getWindowManager().getDefaultDisplay().getMetrics(disp);
        devHeight = disp.heightPixels;
        devWidth = disp.widthPixels;

        // Connecting the views and the layouts to their correspondents in XML files
        layoutInflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        popupLayout = (ViewGroup) layoutInflater.inflate(R.layout.gameoverpop, null);
        popupWindow = new PopupWindow(popupLayout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, false);

        showHighScores = (Button) popupLayout.findViewById(R.id.highScores);
        scoresTable = (TableLayout) popupLayout.findViewById(R.id.scoresTable);
        scoreShow = (TextView) popupLayout.findViewById(R.id.scoreShow);
        score1 = (TextView) popupLayout.findViewById(R.id.score1);
        score2 = (TextView) popupLayout.findViewById(R.id.score2);
        score3 = (TextView) popupLayout.findViewById(R.id.score3);
        score4 = (TextView) popupLayout.findViewById(R.id.score4);
        counterText = (TextView) findViewById(R.id.counterText);
        scoreLabel = (TextView) popupLayout.findViewById(R.id.scoreLabel);
        difficultyTable = (TableLayout) popupLayout.findViewById(R.id.difficultyTable);
        difficultyBtn = (Button) popupLayout.findViewById(R.id.difficulty);
        level1 = (Button) popupLayout.findViewById(R.id.level1Btn);
        level2 = (Button) popupLayout.findViewById(R.id.level2Btn);
        level3 = (Button) popupLayout.findViewById(R.id.level3Btn);
        muteBtn= (Button) popupLayout.findViewById(R.id.muteBtn);
        replayBtn= (Button) popupLayout.findViewById(R.id.replay);
    }

    public void settingDynamicViews() {
        // Setting the first pillar, positioning it and adding it to game_layout layout
        pillars[0] = new Pillar(this);
        pillars[0].setFirstPillar();
        game_layout.addView(pillars[0]);
        sendViewToBack(pillars[0]);


        // DisplayScoreAndShare the first topPillar and  positioning it
        tops[0] = new Top(this);
        tops[0].setFirstPillar();


        // Generating the positions for the next pillars and topPillars
        for(int i=1; i<pillars.length; i++){
            pillars[i] = new Pillar(this);
            pillars[i].setNewPillarX(pillars[i-1].getX());
            pillars[i].pillarNewY(pillars[i-1].getY());
            game_layout.addView(pillars[i]);
            sendViewToBack(pillars[i]);

            tops[i] = new Top(this);
            tops[i].setX(pillars[i].getX());
            tops[i].setY(pillars[i].getY());
        }


        //  DisplayScoreAndShare the giant pillar
        giantP = new GiantPillar(this);

        giantP.setX(tops[0].getX()-giantP.getGiantPillarWidth()/2);
        giantP.setY(tops[0].getY()+tops[0].getTopHeight()/2);
        game_layout.addView(giantP);

        // DisplayScoreAndShare the topGiant pillar
        topG = new TopGiantPillar(this);
        topG.setX(giantP.getX());
        topG.setY(giantP.getY());


        // Setting the ball, positioning it and adding it to game_layout layout
        ball = new Ball(this);
        ball.setX(giantP.getX()+giantP.getGiantPillarWidth()/2);
        ball.setY(giantP.getY()-30+giantP.getGiantPillarHeight()/2);
        game_layout.addView(ball);
    }

    public void movement(){
        // This method is for starting the game and make the views moves
        xOffset=devWidth/100;
        yOffset= (float) (xOffset*0.72);
        // modifying the x-y offsets according to the difficulty level
        if(level == 1){
            xOffset*=0.8;
            yOffset*=0.8;
        }
        else if(level == 3){
            xOffset*=1.2;
            yOffset*=1.2;
        }

        DELAY=5;
        inGiantP=true;

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                game_layout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Detecting the touch on the screen, increasing the score counter, starting the beep
                        // and inverse the direction of the ball
                        if(event.getAction() == MotionEvent.ACTION_UP){
                            ballRight = !ballRight;
                            counter+=5;
                            counterText.setText(String.valueOf(counter));
                            if(!mute)
                                beep.start();
                        }
                        return false;
                    }
                });

                CheckingIfGameOver();
                // If no collision detected (Game Over), play the gameOver sound, showing the popUp window,
                // showing the score and adding it to highscores if it's in the first 4 high scores
                if(gameOver){
                    modifyingLayoutWhenGameOver();
                    return;
                }

                MovingPillars();
                MovingBall();


                h.postDelayed(this, DELAY);
            }
        }, DELAY);
    }

    public void modifyingLayoutWhenGameOver() {
        if(!mute)
            gameOverBeep.start();
        inGiantP=false;
        firstGame=false;
        replayBtn.setText("Replay");
        popupWindow.showAtLocation(game_layout, Gravity.CENTER, 0, 0);
        counterText.setVisibility(View.INVISIBLE);
        scoreShow.setText(String.valueOf(counter));
        scoreLabel.setVisibility(View.VISIBLE);
        scoreShow.setVisibility(View.VISIBLE);
        addHighScore(counter);
        assignScores();

        keyValues = getApplicationContext().getSharedPreferences("counter", 0);
        keyValuesEditor = keyValues.edit();
        keyValuesEditor.putInt("counter", counter);
        keyValuesEditor.apply();

        counterText.setText("0");
        counter=0;
    }

    public void MovingBall() {
        // moving ball on click
        if(ballRight)
            ball.setX(ball.getX() + xOffset);
        else
            ball.setX(ball.getX() - xOffset);
    }

    public void MovingPillars() {
        // moving pillars through x-axis. If the pillar is in the bottom of the screen, a new position
        // will be generated (the topPillars follow the pillars)
        for(int i=0; i<pillars.length; i++){
            if (pillars[i].isLow()){
                if(i == 0){
                    pillars[i].setNewPillarX(pillars[pillars.length-1].getX());
                    pillars[i].pillarNewY(pillars[pillars.length-1].getY());
                    sendViewToBack(pillars[i]);
                }else{
                    pillars[i].setNewPillarX(pillars[i-1].getX());
                    pillars[i].pillarNewY(pillars[i-1].getY());
                    sendViewToBack(pillars[i]);
                }
                break;
            }
            tops[i].setX(pillars[i].getX());
            tops[i].setY(pillars[i].getY());
        }

        // moving pillar and topPillar downward
        for(int i=0; i<pillars.length; i++){
            pillars[i].setY(pillars[i].getY() + yOffset);
            tops[i].setY(pillars[i].getY());
        }
        giantP.setY(giantP.getY()+yOffset);
        topG.setY(topG.getY()+yOffset);
    }

    public void CheckingIfGameOver() {
        // Check whether the ball is in the path(means the ball collide with at least 1 topPillar)
        for(int i=0; i<pillars.length; i++){
            r1 = (int) tops[i].getX();
            l1 = (int) tops[i].getY();

            r2 = (int) giantP.getX();
            l2 = (int) giantP.getY();

            if(tops[i].isInBottom()) {
                if (ball.isInThePath(r1, l1, Top.top)) {
                    inGiantP=false;
                    gameOver = false;
                    break;
                }
            }
            if(inGiantP && ball.isInThePath(r2, l2, TopGiantPillar.topGiantPillar)) {
                gameOver = false;
                break;
            }else{
                if(inGiantP && ball.isInThePath(r1, l1, Top.top)){
                    gameOver = false;
                    break;
                }
            }

            gameOver=true;
        }
    }

    public void replay(View v){
        // When the REPLAY button is clicked this method is invoked
        // The pillars, topPillars and the ball will be invisible
        // The gameOver popup windows will closed
        // The view of the game will be reset (initiateView and movement method)
        if(firstGame){
            popupWindow.dismiss();
            // if a touch has been detected, the pillars and the ball start moving
            game_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    game_layout.setOnClickListener(null);
                    movement();
                }
            });
            return;
        }

        ball.setVisibility(View.GONE);
        counterText.setVisibility(View.VISIBLE);
        giantP.setVisibility(View.INVISIBLE);
        topG.setVisibility(View.INVISIBLE);
        popupWindow.dismiss();


        for(int i=0; i<pillars.length; i++){
            pillars[i].setVisibility(View.GONE);
            tops[i].setVisibility(View.GONE);
        }
        initiateView();

    }

    // This method takes a score and orders it in the highScores array
    // The highScores array contains just 4 high scores
    private void addHighScore(int score){
        for(int i=0; i<highScores.length; i++){
            if (score > highScores[i]){
                for (int j=highScores.length-1; j>i; j--){
                    highScores[j] = highScores[j-1];
                }
                highScores[i] = score;
                break;
            }
        }
    }

    // This method is to assign the scores to their correspondent TextViews
    private void assignScores(){
        score1.setText(String.valueOf(highScores[0]));
        score2.setText(String.valueOf(highScores[1]));
        score3.setText(String.valueOf(highScores[2]));
        score4.setText(String.valueOf(highScores[3]));
    }

    // This method is to show and hide the scoresTable when clicking on "High Scores" button
    public void showScores(View v){
        if(scoresTable.getVisibility() == View.VISIBLE)
            scoresTable.setVisibility(View.INVISIBLE);
        else {
            difficultyTable.setVisibility(View.INVISIBLE);
            scoresTable.setVisibility(View.VISIBLE);
        }
    }

    public void displayDifficulties(View v){
        if(difficultyTable.getVisibility() == View.VISIBLE)
            difficultyTable.setVisibility(View.INVISIBLE);
        else {
            scoresTable.setVisibility(View.INVISIBLE);
            difficultyTable.setVisibility(View.VISIBLE);
        }
    }


    // This method is to control the sound
    public void mute(View v){
        mute=!mute;
        if(mute)
            muteBtn.setText("UNMUTE");
        else
            muteBtn.setText("MUTE");

    }



    // This method manage the difficulty buttons styles and visibility
    public void setButtonState(View v){
        int id=v.getId();
        if(id==R.id.level1Btn){
            level=1;
            level1.setTextColor(Color.BLACK);
            level1.setTypeface(null, Typeface.BOLD);
            level2.setTextColor(Color.WHITE);
            level2.setTypeface(null, Typeface.NORMAL);
            level3.setTextColor(Color.WHITE);
            level3.setTypeface(null, Typeface.NORMAL);
            difficultyTable.setVisibility(View.INVISIBLE);
        }else if(id==R.id.level2Btn){
            level=2;
            level1.setTextColor(Color.WHITE);
            level1.setTypeface(null, Typeface.NORMAL);
            level2.setTextColor(Color.BLACK);
            level2.setTypeface(null, Typeface.BOLD);
            level3.setTextColor(Color.WHITE);
            level3.setTypeface(null, Typeface.NORMAL);
            difficultyTable.setVisibility(View.INVISIBLE);
        }else if(id== R.id.level3Btn) {
            level=3;
            level1.setTextColor(Color.WHITE);
            level1.setTypeface(null, Typeface.NORMAL);
            level2.setTextColor(Color.WHITE);
            level2.setTypeface(null, Typeface.NORMAL);
            level3.setTextColor(Color.BLACK);
            level3.setTypeface(null, Typeface.BOLD);
            difficultyTable.setVisibility(View.INVISIBLE);
        }
    }

}
