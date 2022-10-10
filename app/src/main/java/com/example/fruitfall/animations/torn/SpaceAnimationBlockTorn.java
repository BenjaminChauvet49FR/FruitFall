package com.example.fruitfall.animations.torn;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.Constants;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.animations.SpaceAnimation;

public abstract class SpaceAnimationBlockTorn extends SpaceAnimation {

    public SpaceAnimationBlockTorn(int x, int y) {
        super(x, y, Constants.NUMBER_FRAMES_DESTRUCTION_FORANIM);
    }

    @Override
    protected void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        paint.setAlpha( (int) (255*(1-this.ratio())) );
        // NOTE : rectSource is supposed to start at 0,0
        int pixSourceX2 = rectSource.right;
        int pixSourceY2 = rectSource.bottom;
        int pixSourceXHalf = pixSourceX2/2;
        int pixSourceYHalf = pixSourceY2/2;
        int pixX1 = rectDestination.left;
        int pixX2 = rectDestination.right;
        int pixY1 = rectDestination.top;
        int pixY2 = rectDestination.bottom;
        int pixHalfW = rectDestination.width()/2;
        int pixHalfH = rectDestination.height()/2;
        int pixXExtremeQuad; // ExtremeQuad = furthest extremity of this quadrant
        int pixYExtremeQuad;
        float pixXDistance = rectDestination.width()*3*ratio()/2;
        float pixYDistance = rectDestination.height()*3*ratio()/2;

        // Left up quadrant
        rectSource.right = pixSourceXHalf;
        rectSource.bottom = pixSourceYHalf;
        pixXExtremeQuad = (int) (pixX1-pixXDistance);
        pixYExtremeQuad = (int) (pixY1-pixYDistance);
        rectDestination.set(pixXExtremeQuad, pixYExtremeQuad, pixXExtremeQuad+pixHalfW, pixYExtremeQuad+pixHalfH);
        canvas.drawBitmap(this.getImage(view), rectSource, rectDestination, paint);

        // Right up quadrant
        rectSource.left = pixSourceXHalf;
        rectSource.right = pixSourceX2;
        pixXExtremeQuad = (int) (pixX2+pixXDistance);
        rectDestination.left = pixXExtremeQuad-pixHalfW;
        rectDestination.right = pixXExtremeQuad;
        canvas.drawBitmap(this.getImage(view), rectSource, rectDestination, paint);

        // Right down quadrant
        rectSource.top = pixSourceYHalf;
        rectSource.bottom = pixSourceY2;
        pixYExtremeQuad = (int) (pixY2+pixYDistance);
        rectDestination.top = pixYExtremeQuad-pixHalfH;
        rectDestination.bottom = pixYExtremeQuad;
        canvas.drawBitmap(this.getImage(view), rectSource, rectDestination, paint);

        // Left down quadrant
        rectSource.left = 0;
        rectSource.right = pixSourceXHalf;
        pixXExtremeQuad = (int) (pixX1-pixXDistance);
        rectDestination.left = pixXExtremeQuad;
        rectDestination.right = pixXExtremeQuad+pixHalfW;
        canvas.drawBitmap(this.getImage(view), rectSource, rectDestination, paint);

        // Back to normal
        rectSource.set(0, 0, pixSourceX2, pixSourceY2); // Note : optimizable but who cares...
        rectDestination.set(pixX1, pixY1, pixX2, pixY2);
        paint.setAlpha(255);
    }

    public abstract Bitmap getImage(MyCanvasView view);
    
}
