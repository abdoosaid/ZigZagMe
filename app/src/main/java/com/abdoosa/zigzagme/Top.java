package com.abdoosa.zigzagme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Top extends Pillar {
    static Bitmap top;


    public Top(Context context) {
        super(context);
        // DisplayScoreAndShare topPillar image ressource to R.drawable.top
        this.setImageResource(R.drawable.top);
        top = BitmapFactory.decodeResource(getResources(), R.drawable.top);
    }

    public int getTopWidth() {
        return top.getWidth();
    }

    public int getTopHeight() {
        return top.getHeight();
    }

    static public int getPillarDemiHeight() {
        return  (int)(top.getHeight()/2.3);
    }

    static public int getPillarDemiWidth() {
        return (int)(top.getWidth()/2.28);
    }
}
