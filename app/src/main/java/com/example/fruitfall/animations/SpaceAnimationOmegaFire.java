package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.Constants;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class SpaceAnimationOmegaFire extends SpaceAnimation {

    public SpaceAnimationOmegaFire(int x, int y) {
        super(x, y, Constants.NUMBER_FRAMES_DESTRUCTION_FORANIM);
        this.needRecenter = false;
    }

    @Override
    protected void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(view.getColorAnimationFire());
        rectDestination.set(Pix.pixXLeftMainSpace(x -1), Pix.pixYUpMainSpace(y-1), Pix.pixXRightMainSpace(x +1), Pix.pixYDownMainSpace(y +1));
        canvas.drawRect(rectDestination, paint);
    }

}
