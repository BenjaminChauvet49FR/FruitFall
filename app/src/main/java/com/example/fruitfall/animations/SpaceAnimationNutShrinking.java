package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.Constants;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class SpaceAnimationNutShrinking extends SpaceAnimation {

    // TODO Soon here will be ingredients and other stuff... generalization with image will be needed

    public SpaceAnimationNutShrinking(int x, int y) {
        super(x, y, Constants.NUMBER_FRAMES_DESTRUCTION_FORANIM);
    }

    @Override
    protected void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
            canvas.drawBitmap(view.getBitmapImageNut(),
                    rectSource, rotatedShrinkedRect(rectDestination, this.ratio()), paint);
    }

    private Rect rotatedShrinkedRect(Rect rectDest, float ratio) {
        return new Rect(
                (int) (rectDest.left + Pix.wInnerSpace*ratio/2),
                (int) (rectDest.top + Pix.hInnerSpace*ratio/2),
                (int) (rectDest.right - Pix.wInnerSpace*ratio/2),
                (int) (rectDest.bottom - Pix.hInnerSpace*ratio/2)
        );
    }

}
