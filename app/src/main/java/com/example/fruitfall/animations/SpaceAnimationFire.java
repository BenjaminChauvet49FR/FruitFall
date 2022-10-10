package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

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
        rectDestination.set(Pix.xLeftSpace(x -2), Pix.yUpSpace(y), Pix.xRightSpace(x +2), Pix.yDownSpace(y ));
        canvas.drawRect(rectDestination, paint);
        rectDestination.set(Pix.xLeftSpace(x -1), Pix.yUpSpace(y-1), Pix.xRightSpace(x +1), Pix.yDownSpace(y +1));
        canvas.drawRect(rectDestination, paint);
        rectDestination.set(Pix.xLeftSpace(x ), Pix.yUpSpace(y-2), Pix.xRightSpace(x ), Pix.yDownSpace(y +2));
        canvas.drawRect(rectDestination, paint);
    }// TODO animer plutôt que maintenir ?
}
