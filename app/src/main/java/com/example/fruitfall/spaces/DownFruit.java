package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class DownFruit extends SpaceFiller {

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
        canvas.drawBitmap(view.getBitmapImageDownFruit(), rectSource, rectDestination, paint);
    }
}
