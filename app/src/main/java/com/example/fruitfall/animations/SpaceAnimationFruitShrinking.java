package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.Constants;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class SpaceAnimationFruitShrinking extends SpaceAnimation {

    private final int imageFruit;
    private final boolean alsoRotating;

    public SpaceAnimationFruitShrinking(int x, int y, int _imageFruit, boolean _alsoRotating) {
        super(x, y, Constants.NUMBER_FRAMES_DESTRUCTION_FORANIM);
        this.imageFruit = _imageFruit;
        this.alsoRotating = _alsoRotating;
    }

    @Override
    protected void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {

        if (this.alsoRotating) {
            canvas.save(); // Note : it should be possible to make all rotations at once.
            canvas.rotate(this.ratio() * Constants.MAX_ANGLE_IN_DEGREES, rectDestination.exactCenterX(), rectDestination.exactCenterY()); // https://www.tabnine.com/code/java/methods/android.graphics.Canvas/rotate
            canvas.drawBitmap(view.getBitmapFruitToDrawFromIndex(this.imageFruit),
                    rectSource, rotatedShrinkedRect(rectDestination, this.ratio()), paint);
            canvas.restore();
        } else {
            canvas.drawBitmap(view.getBitmapFruitToDrawFromIndex(imageFruit),
                    rectSource, rotatedShrinkedRect(rectDestination, this.ratio()), paint);
        }
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
