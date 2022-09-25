package com.example.fruitfall.spaces;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public abstract class SpaceFiller {
    public abstract boolean canBeSwapped();
    public abstract boolean canFall();
    public int getIdFruit() {
        return Constants.NOT_A_FRUIT;
    }
    public GameEnums.FRUITS_POWER getPower() { return GameEnums.FRUITS_POWER.NONE; }
    public abstract void paintStill(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint);
    public abstract SpaceFiller copy();

    public boolean isASpace() { return true; }
    public boolean mayDisappear() { return true; }
    private static RectF rectPips = new RectF();

    protected void paintPips(MyCanvasView view, Canvas canvas, Rect rectDestination, Paint paint, int value, int color) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(color);
        int nbDotsRight = value/2;
        int nbDotsLeft = value-nbDotsRight;
        int i;
        float pixXPip = rectDestination.left + Pix.pipPadding; // Is this pixXPip exact ?
        float pixYCenter = (rectDestination.top + rectDestination.bottom)/2; // TODO : well, really suboptimized !
        float pixYPip;
        for (i = 0 ; i < nbDotsLeft ; i++) {
            pixYPip = pixYCenter - Pix.pipSide/(float)2.0 + view.centeringProgression(nbDotsLeft, i)*Pix.pipSide*(float)1.5;
            rectPips.set(pixXPip, pixYPip, pixXPip+Pix.pipSide, pixYPip+Pix.pipSide);
            canvas.drawOval(rectPips, paint);
        }
        pixXPip = rectDestination.right - Pix.pipPadding - Pix.pipSide;
        for (i = 0 ; i < nbDotsRight ; i++) {
            pixYPip = pixYCenter - Pix.pipSide/(float)2.0 + view.centeringProgression(nbDotsRight, i)*Pix.pipSide*(float)1.5;
            rectPips.set(pixXPip, pixYPip, pixXPip+Pix.pipSide, pixYPip+Pix.pipSide);
            canvas.drawOval(rectPips, paint);
        }
    }
}
