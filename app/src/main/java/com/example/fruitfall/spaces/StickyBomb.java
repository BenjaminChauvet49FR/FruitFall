package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameHandler;
import com.example.fruitfall.MyCanvasView;

public class StickyBomb extends SpaceFiller {

    public int fruitId;
    public int count;
    public boolean blocking;
    private final static RectF rectEllipse = new RectF(0, 0, 0, 0);

    public StickyBomb(int count, int fruitId) {
        this.fruitId = fruitId;
        this.count = count;
        this.blocking = (fruitId != Constants.NOT_A_FRUIT);
    }

    public static StickyBomb BlockingStickyElement(int count) {
        StickyBomb answer = new StickyBomb(count, Constants.NOT_A_FRUIT);
        answer.blocking = true;
        return answer;
    }

    @Override
    public boolean canBeSwapped() {
        return true;
    }

    @Override
    public boolean canFall() {
        return false;
    }

    @Override
    public void paint(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint, boolean isInPause) {
        int veilColor = view.getColorVeilStickyBomb3(); // Note : colours slightly transparent
        if (count == 1) {
            veilColor = view.getColorVeilStickyBomb1();
        }
        if (count == 2) {
            veilColor = view.getColorVeilStickyBomb2();
        }
        if (this.fruitId != Constants.NOT_A_FRUIT) {
            canvas.drawBitmap(view.getBitmapFruitToDrawFromIndex(this.fruitId), rectSource, rectDestination, paint);
            drawVeil(canvas, paint, veilColor, rectDestination); // TODO Make a different icon for these fruits...
        } else {
            drawVeil(canvas, paint, veilColor, rectDestination);
            if (this.blocking) {
                canvas.drawBitmap(view.getBitmapImageStopBlasts()[6], rectSource, rectDestination, paint);
                // Note : here is where I enjoy having an array of 9 pictures ! (see image stop blasts in MyCanvasView !)
            }
        }
        drawInLevels(canvas, view.getBitmapImageStickyBombs(), this.count, rectSource, rectDestination, paint, (float)0.3);
        if (isInPause) {
            this.paintPips(view, canvas, rectDestination, paint, this.count, view.getColorDotDestroyableNearby());
        }
    }

    private void drawVeil(Canvas canvas, Paint paint, int colour, Rect rectDestination) {
        paint.setColor(colour);
        rectEllipse.set(rectDestination.left, rectDestination.top, rectDestination.right, rectDestination.bottom);
        paint.setAlpha(127);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawOval(rectEllipse, paint);
        paint.setAlpha(255);
    }

    @Override
    public boolean downgradableOnAdjAlignedOrBlast() {return true;}

    @Override
    public void downgrade() {
        count--;
    }

    @Override
    public boolean outtaHere() {return count <= 0;}

    @Override
    public void triggerOnFinalDecrease(GameHandler gh) {
        gh.removeStickyBomb(this.fruitId);
    }

    @Override
    public boolean crossableByPowers() {
        return !blocking;
    }
}
