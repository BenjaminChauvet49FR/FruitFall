package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.GameHandler;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class Nut extends SpaceFiller {

    int count;

    public Nut(int count) {
        super();
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean canBeSwapped() {
        return true;
    }

    @Override
    public boolean canFall() {
        return true;
    }

    @Override
    public void paint(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint, boolean isInPause) {
        canvas.drawBitmap(view.getBitmapImageNut(), rectSource, rectDestination, paint);
        paint.setColor(view.getColorLockDuration());
        paint.setTextSize(Pix.hTextLockDuration);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        // Remember : text has been centered !
        canvas.drawText(String.valueOf(this.count),
                (rectDestination.left + rectDestination.right)/(float)2,
                (rectDestination.top + rectDestination.bottom + paint.getTextSize())/2, // rectDestination = where the space needs to be drawn
                paint);
    }

    @Override
    public boolean downgradableOnAdjAlignedOrBlast() {return true;}

    @Override
    public void downgrade() {
        count--;
    }

    @Override
    public boolean outtaHere() {return count <= 0;}

    @Override
    public void triggerOnActiveDecrease(GameHandler gh) {
        gh.decreaseNut();
    }
}
