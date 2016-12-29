package com.abdoosa.zigzagme;

import android.content.Context;
import android.widget.ImageView;


public class Pillar extends ImageView {
    int xOffset, yOffset;


    // Getting the width and the height of the screen
    int devWidth = GameStart.devWidth;
    int devHeight = GameStart.devHeight;

    public Pillar(Context context) {
        super(context);
        // set the image source for the view to pillar
        this.setImageResource(R.drawable.pillar);
    }

    // Setting the first pillar to the appropriate position
    public void setFirstPillar(){
        this.setX((float) (devWidth / 2.3));
        this.setY((float) (devHeight*0.4));
    }

    // Generating the new x position for the pillar
    public void setNewPillarX(float x) {
        // Setting x and y offsets
        xOffset = Top.getPillarDemiWidth();
        yOffset = Top.getPillarDemiHeight();

        int random = (int) (Math.random() * 2);

        if (random == 1) {
            if (x > devWidth - Top.getPillarDemiWidth()*4)
                this.setX(x - xOffset);
            else
                this.setX(x + xOffset);
        } else {
            if (x < Top.getPillarDemiWidth()*4)
                this.setX(x + xOffset);
            else
                this.setX(x - xOffset);
        }
    }

    // Checking whether the pillar is in the bottom of the screen
    public boolean isLow(){
        return this.getY() > devHeight;
    }

    // Generating the new y position for the pillar
    public void pillarNewY(float y) {
        this.setY(y-yOffset);
    }

    // Checking whether the pillar is in the bottom of the screen
    public boolean isInBottom(){
        return  this.getY() > devHeight*0.3;
    }



}
