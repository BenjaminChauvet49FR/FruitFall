package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.Constants;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class SwapAnimationFireFire extends SpaceAnimation {
    public SwapAnimationFireFire(int x, int y) {
        super(x, y, Constants.NUMBER_FRAMES_ANIMATION_MIXED);
    }

    @Override
    protected void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(view.getColorAnimationFire());
        for (int i = 0 ; i <= 3 ; i++) {
            rectDestination.set(Pix.pixXLeftMainSpace(x -3+i), Pix.pixYUpMainSpace(y-i), Pix.pixXRightMainSpace(x +3-i), Pix.pixYDownMainSpace(y+i));
            canvas.drawRect(rectDestination, paint);
        }
    }// TODO animer plutôt que maintenir ? (cf. Fire)
}
