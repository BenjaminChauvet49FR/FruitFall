package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class DelayedLock extends SpaceFiller {

    private int count;

    @Override
    public boolean canBeSwapped() {
        return false;
    }

    @Override
    public boolean canFall() {
        return false;
    }

    public DelayedLock(int count) {
        this.count = count;
    }

    @Override
    public void paint(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint, boolean isInPause) {
        Pix.adaptRectForFullSpace(rectDestination);
        canvas.drawBitmap(view.getBitmapImageLocking(), rectSource, rectDestination, paint);
        Pix.adaptRectForInnerSpace(rectDestination);
        paint.setColor(view.getColorLockDuration());
        paint.setTextSize(Pix.hTextLockDuration);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        // Remember : text has been centered !
        canvas.drawText(String.valueOf(this.count),
                (rectDestination.left + rectDestination.right)/(float)2, // 551551 Pas parfait ! (+ faire une fonction de draw dans une case comme ça avec Nut)
                (rectDestination.top + rectDestination.bottom + paint.getTextSize())/2, // rectDestination = where the space needs to be drawn
                paint);
    }

    @Override
    public void downgrade() {
        count--;
    }

    @Override
    public boolean outtaHere() {return count <= 0;}
}
