package com.abdoosa.zigzagme;

import android.content.Context;
import android.widget.ImageView;


public class Pillar extends ImageView {
    int devWidth = GameStart.devWidth;
    int devHeight = GameStart.devHeight;
    int xOffset = 53;
    int yOffset = 38;

    public Pillar(Context context) {
        super(context);
        this.setImageResource(R.drawable.pillar);
    }

    public void setFirstPillar(){
        this.setX((float) (devWidth / 2.3));
        this.setY(devHeight * 2 / 3);
    }

    public void setNewPillarX(float x) {

        int random = (int) (Math.random() * 2);

        if (random == 1) {
            if (x > devWidth - 250)
                this.setX(x - xOffset);
            else
                this.setX(x + xOffset);
        } else {
            if (x < 60)
                this.setX(x + xOffset);
            else
                this.setX(x - xOffset);
        }
    }

    public boolean isLow(){
        return this.getY() > 900f;
    }

    public void pillarNewY(float y) {
        this.setY(y-yOffset);
    }

    public boolean isInBottom(){
        return  this.getY() > devHeight*0.6;
    }



}
