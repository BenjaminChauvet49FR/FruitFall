package com.example.fruitfall.animations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.example.fruitfall.GameHandler;
import com.example.fruitfall.MyCanvasView;

public abstract class SpaceAnimationFading extends SpaceAnimation {

    public SpaceAnimationFading(int x, int y, int durationFrames) {
        super(x, y, durationFrames);
    }

    @Override
    protected void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        paint.setAlpha( (int) (255*(1-this.ratio())) );
        canvas.drawBitmap(this.getImage(view), rectSource, rectDestination, paint);
        paint.setAlpha(255);
    }

    public abstract Bitmap getImage(MyCanvasView view);
}
