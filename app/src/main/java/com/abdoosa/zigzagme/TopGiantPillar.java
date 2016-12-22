package com.abdoosa.zigzagme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class TopGiantPillar extends Pillar {
    static Bitmap topGiantPillar;

    public TopGiantPillar(Context context) {
        super(context);
        // Setting topPillar image ressource to R.drawable.top
        this.setImageResource(R.drawable.topgiant);

        topGiantPillar = BitmapFactory.decodeResource(getResources(), R.drawable.giantpillar);
    }

    public int getGiantPillarWidth() {
        return topGiantPillar.getWidth();
    }

    public int getGiantPillarHeight() {
        return topGiantPillar.getHeight();
    }
}
