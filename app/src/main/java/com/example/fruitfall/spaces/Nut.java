package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

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

    public void downgrade() {
        count--;
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
    public void paintStill(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        if (view.getBitmapImages() != null) {
            canvas.drawBitmap(view.getBitmapImageNut(), rectSource, rectDestination, paint);
            paint.setColor(view.getColorLockDuration());
            paint.setTextSize(Pix.hLockDuration);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            // Remember : text has been centered !
            canvas.drawText(String.valueOf(this.count),
                    (rectDestination.left + rectDestination.right)/2,
                    (rectDestination.top + rectDestination.bottom)/2, // rectDestination = where the space needs to be drawn
                    paint);
        }
    }

    @Override
    public SpaceFiller copy() {
        return null;
    }
}
