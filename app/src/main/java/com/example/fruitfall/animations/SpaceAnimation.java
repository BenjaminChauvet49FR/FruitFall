package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.example.fruitfall.GameEnums;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public abstract class SpaceAnimation {

    protected int frameLimit;
    protected int frameCount;
    protected int x;
    protected int y;
    protected boolean needRecenter;

    protected SpaceAnimation(int x, int y, int frameLimit) {
        this.frameCount = 0;
        this.frameLimit = frameLimit;
        this.x = x;
        this.y = y;
        this.needRecenter = true;
    }

    public void progress() {
        frameCount++;
    }

    public boolean shouldBeDrawn() {
        return (frameCount < frameLimit);
    }

    public float ratio() {
        return (float) frameCount/frameLimit;
    }

    public void draw(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        if (this.needRecenter) {
            rectDestination.set(Pix.xLeftMainSpace(x), Pix.yUpMainSpace(y), Pix.xRightMainSpace(x), Pix.yDownMainSpace(y) );
        }
        drawProtected(view, canvas, rectSource, rectDestination, paint);
    }

    protected abstract void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint);
}
