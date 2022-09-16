package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.GameEnums;
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

    public int getCount() {
        return this.count;
    }

    public void decreaseCount() {
        this.count--;
    }

    @Override
    public void paintStill(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        canvas.drawBitmap(view.getBitmapImageLocking(), rectSource, rectDestination, paint);
        paint.setColor(view.getColorLockDuration());
        paint.setTextSize(Pix.hLockDuration);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawText(String.valueOf(this.count),
                rectDestination.left + 2,
                rectDestination.bottom - Pix.hMainSpace/2, // rectDestination = where the space needs to be drawn
                paint);
    }

    @Override
    public SpaceFiller copy() { return new DelayedLock(this.count); }
}
