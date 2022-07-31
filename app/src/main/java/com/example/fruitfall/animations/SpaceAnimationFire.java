package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.example.fruitfall.Constants;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class SpaceAnimationFire extends SpaceAnimation {

    public SpaceAnimationFire(int x, int y) {
        super(x, y, Constants.NUMBER_FRAMES_DESTRUCTION_FORANIM);
        this.needRecenter = false;
    }

    @Override
    protected void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(view.getColorAnimationFire());
        rectDestination.set(Pix.pixXLeftMainSpace(x -2), Pix.pixYUpMainSpace(y), Pix.pixXRightMainSpace(x +2), Pix.pixYDownMainSpace(y ));
        canvas.drawRect(rectDestination, paint);
        rectDestination.set(Pix.pixXLeftMainSpace(x -1), Pix.pixYUpMainSpace(y-1), Pix.pixXRightMainSpace(x +1), Pix.pixYDownMainSpace(y +1));
        canvas.drawRect(rectDestination, paint);
        rectDestination.set(Pix.pixXLeftMainSpace(x ), Pix.pixYUpMainSpace(y-2), Pix.pixXRightMainSpace(x ), Pix.pixYDownMainSpace(y +2));
        canvas.drawRect(rectDestination, paint);
    }// TODO animer plutôt que maintenir ?
}
