package com.abdoosa.zigzagme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.widget.ImageView;


public class Ball extends ImageView {
    Bitmap bm1, bm2;
    int ballPxNum, pixelCollideNum;
    public Ball(Context context) {
        super(context);
        this.setImageResource(R.drawable.ball);
        bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.top);
        ballPxNum = getBallPxNum();
    }


    public void setFirstBallPos(float x, float y){
        this.setX(x+47);
        this.setY(y+27);
    }

    public Rect getCollisionBounds(Rect rect1, Rect rect2) {
        int left =  Math.max(rect1.left, rect2.left);
        int top =  Math.max(rect1.top, rect2.top);
        int right =  Math.min(rect1.right, rect2.right);
        int bottom = Math.min(rect1.bottom, rect2.bottom);
        return new Rect(left, top, right, bottom);
    }

    public int getBallPxNum(){
        int count=0;
        for(int i=0; i<bm1.getWidth(); i++){
            for(int j=0; j<bm1.getHeight(); j++){
                if (isFilled(bm1.getPixel(i,j)))
                    count++;
            }
        }
        return count;
    }

    public boolean isCollisionDetected(int xP, int yP) {
        int xB = (int) this.getX();
        int yB = (int) this.getY();
        pixelCollideNum = 0;
        Rect bounds1 = new Rect(xB, yB, xB+bm1.getWidth(), yB+bm1.getHeight());
        Rect bounds2 = new Rect(xP, yP, xP+bm2.getWidth(), yP+bm2.getHeight());

        if (Rect.intersects(bounds1, bounds2)) {
            Rect collisionBounds = getCollisionBounds(bounds1, bounds2);
            for (int i = collisionBounds.left; i < collisionBounds.right; i++) {
                for (int j = collisionBounds.top; j < collisionBounds.bottom; j++) {
                    int bitmap1Pixel = bm1.getPixel(i-xB, j-yB);
                    int bitmap2Pixel = bm2.getPixel(i-xP, j-yP);
                    if (isFilled(bitmap1Pixel) && isFilled(bitmap2Pixel)) {
                        pixelCollideNum++;
                    }
                }
            }
        }
        if(pixelCollideNum >= ballPxNum-160)
            return true;
        return false;
    }

    public boolean isFilled(int pixel) {
        return pixel != Color.TRANSPARENT;
    }

}