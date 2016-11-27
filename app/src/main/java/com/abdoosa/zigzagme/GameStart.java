package com.abdoosa.zigzagme;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class GameStart extends Activity {

    DisplayMetrics disp = new DisplayMetrics();
    RelativeLayout activity_main;
    RelativeLayout.LayoutParams layoutPrms;
    static int devHeight, devWidth;
    Pillar[] pillars = new Pillar[23];
    Top[] tops = new Top[23];
    Handler h = new Handler();
    Ball ball;
    TextView counterText, scoreShow, score1, score2, score3, score4;
    int r1, l1, counter;
    boolean ballRight=true, colDetected=true;
    int[] highScores = new int[4];

    LayoutInflater layoutInflater;
    ViewGroup popupLayout;
    PopupWindow popupWindow;

    Button showHighScores;
    TableLayout scoresTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        initiateView();
        movement();

    }


    public static void sendViewToBack(final View child){
        final ViewGroup parent = (ViewGroup) child.getParent();
        if(parent != null) {
            parent.removeView(child);
            parent.addView(child, 0);
        }

    }

    public void initiateView(){
        activity_main = (RelativeLayout) findViewById(R.id.game_layout);
        setContentView(activity_main);
        getWindowManager().getDefaultDisplay().getMetrics(disp);
        devHeight = disp.heightPixels;
        devWidth = disp.widthPixels;
        ////////////////////////////
        layoutInflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        popupLayout = (ViewGroup) layoutInflater.inflate(R.layout.gameoverpop, null);
        popupWindow = new PopupWindow(popupLayout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, false);

        showHighScores = (Button) popupLayout.findViewById(R.id.highScores);
        scoresTable = (TableLayout) popupLayout.findViewById(R.id.scoresTable);
        ////////////////////////
        scoreShow = (TextView) popupLayout.findViewById(R.id.scoreShow);
        score1 = (TextView) popupLayout.findViewById(R.id.score1);
        score2 = (TextView) popupLayout.findViewById(R.id.score2);
        score3 = (TextView) popupLayout.findViewById(R.id.score3);
        score4 = (TextView) popupLayout.findViewById(R.id.score4);
        ////////////////////////////

        activity_main.setBackgroundColor(Color.GRAY);
        counterText = (TextView) findViewById(R.id.counterText);

        pillars[0] = new Pillar(this);
        pillars[0].setFirstPillar();
        activity_main.addView(pillars[0]);
        sendViewToBack(pillars[0]);

        tops[0] = new Top(this);
        tops[0].setFirstPillar();

        for(int i=1; i<pillars.length; i++){
            pillars[i] = new Pillar(this);
            pillars[i].setNewPillarX(pillars[i-1].getX());
            pillars[i].pillarNewY(pillars[i-1].getY());
            activity_main.addView(pillars[i]);
            sendViewToBack(pillars[i]);

            tops[i] = new Top(this);
            tops[i].setX(pillars[i].getX());
            tops[i].setY(pillars[i].getY());
        }

        ball = new Ball(this);
        ball.setFirstBallPos(pillars[0].getX(), pillars[0].getY());
        activity_main.addView(ball);


    }

    public void movement(){


        final int DELAY=50;
        final MediaPlayer beep = MediaPlayer.create(this, R.raw.beep3);
        final MediaPlayer gameover = MediaPlayer.create(this, R.raw.gameover2);
        counterText.setText("0");
        counter=0;

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity_main.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_UP){
                            ballRight = !ballRight;
                            counter+=5;
                            counterText.setText(String.valueOf(counter));
                            beep.start();
                        }
                        return false;
                    }
                });

                for(int i=0; i<pillars.length; i++){
                    ////Check collision
                    r1 = (int) tops[i].getX();
                    l1 = (int) tops[i].getY();
                    if(tops[i].isInBottom()){
                        if(ball.isCollisionDetected(r1, l1)){
                            colDetected=true;
                            break;
                        }

                    }
                    colDetected=false;
                }
                if(!colDetected){
                    /// When Game Over
                    gameover.start(); //play gameover sound
                    popupWindow.showAtLocation(activity_main, Gravity.CENTER, 0, 0);
                    scoreShow.setText(String.valueOf(counter));
                    addHighScore(counter);
                    assignScores();
                    for(int i=0; i<highScores.length; Log.i("hihi", String.valueOf(highScores[i++])));
                    return;
                }

                // moving pillars through x-axis
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
                    pillars[i].setY(pillars[i].getY() + 6.5f);
                    tops[i].setY(pillars[i].getY());
                }

                // moving ball on click
                if(ballRight)
                    ball.setX(ball.getX() + 9f);
                else
                    ball.setX(ball.getX() - 9f);
                h.postDelayed(this, DELAY);
            }
        }, DELAY);
    }
    public void replay(View v){
        for(int i=0; i<pillars.length; i++){
            pillars[i].setVisibility(View.GONE);
            tops[i].setVisibility(View.GONE);
        }
        ball.setVisibility(View.GONE);
        popupWindow.dismiss();
        initiateView();
        movement();
    }

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
    private void assignScores(){
        score1.setText(String.valueOf(highScores[0]));
        score2.setText(String.valueOf(highScores[1]));
        score3.setText(String.valueOf(highScores[2]));
        score4.setText(String.valueOf(highScores[3]));
    }
    public void showScores(View v){
        if(scoresTable.getVisibility() == View.VISIBLE)
            scoresTable.setVisibility(View.INVISIBLE);
        else
            scoresTable.setVisibility(View.VISIBLE);
    }



}