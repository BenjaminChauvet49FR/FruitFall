package com.example.fruitfall.animations.preservations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.MyCanvasView;

// An artificial class to keep the sprite of something drawn during an Omega stasis.
// (here, an hostage lock)
public class PreAnimation {

    private int x;
    private int y;

    public PreAnimation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {return x;}
    public int getY() {return y;}

    // TODO : this class should be made abstract once I have more of these.
    public void draw(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        canvas.drawBitmap(view.getBitmapImageHostageLocks()[0], rectSource, rectDestination, paint);
    }
}
