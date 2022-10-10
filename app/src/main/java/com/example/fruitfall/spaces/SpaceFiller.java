package com.example.fruitfall.spaces;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;
import com.example.fruitfall.GameHandler;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public abstract class SpaceFiller {
    public abstract boolean canBeSwapped();
    public abstract boolean canFall();
    public int getIdFruit() {
        return Constants.NOT_A_FRUIT;
    }
    public GameEnums.FRUITS_POWER getPower() { return GameEnums.FRUITS_POWER.NONE; }
    protected abstract void paint(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint, boolean inPause);
    public void paint(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        paint(view, canvas, rectSource, rectDestination, paint, false);
    }
    public void paintInPause(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        paint(view, canvas, rectSource, rectDestination, paint, true);
    }


    public boolean isASpace() { return true; }
    public boolean mayDisappear() { return true; }

    // Downgradable when aligned contact is made
    public boolean downgradableOnAdjAlignedOrBlast() {return false;}
    // Actively downgrade
    public void downgrade() {}
    // When a fruit should disappear
    public boolean outtaHere() {return false;}
    public void triggerOnActiveDecrease(GameHandler gh) {} // Call a public method of GH.
    public void triggerOnFinalDecrease(GameHandler gh) {} // same.

    public boolean crossableByPowers() {return true;}

    private static final RectF rectPips = new RectF();

    protected void paintPips(MyCanvasView view, Canvas canvas, Rect rectDestination, Paint paint, int value, int color) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(color);
        int nbDotsRight = value/2;
        int nbDotsLeft = value-nbDotsRight;
        int i;
        float pixXPip = rectDestination.left + Pix.paddingPip; 
        float pixYCenter = (rectDestination.top + rectDestination.bottom)/(float)2.0; // TODO : well, really suboptimized !
        float pixYPip;
        for (i = 0 ; i < nbDotsLeft ; i++) {
            pixYPip = pixYCenter - Pix.sidePip/(float)2.0 + view.centeringProgression(nbDotsLeft, i)*Pix.sidePip*(float)1.5;
            rectPips.set(pixXPip, pixYPip, pixXPip+Pix.sidePip, pixYPip+Pix.sidePip);
            canvas.drawOval(rectPips, paint);
        }
        pixXPip = rectDestination.right - Pix.paddingPip - Pix.sidePip;
        for (i = 0 ; i < nbDotsRight ; i++) {
            pixYPip = pixYCenter - Pix.sidePip/(float)2.0 + view.centeringProgression(nbDotsRight, i)*Pix.sidePip*(float)1.5;
            rectPips.set(pixXPip, pixYPip, pixXPip+Pix.sidePip, pixYPip+Pix.sidePip);
            canvas.drawOval(rectPips, paint);
        }
    }



    // There must be 3 images in the stack, from lighter to darker
    // Level : 1-9 ; numbers of remaining layers
    // Shrinkside : shrinking the total length of a side for ONE layer (so 2 layers to display : using it twice !)
    public void drawInLevels(Canvas canvas, Bitmap[] bitmaps, int level, Rect rectSource, Rect rectDestination, Paint paint, float shrinkSideRatio) {
        Bitmap theImage;
        if (level == 1) {
            theImage = bitmaps[0];
        } else if (level == 2) {
            theImage = bitmaps[1];
        } else {
            theImage = bitmaps[2];
        }
        canvas.drawBitmap(theImage, rectSource, rectDestination, paint);
        if (level >= 4) {
            if (level == 4) {
                theImage = bitmaps[0];
            } else if (level == 5) {
                theImage = bitmaps[1];
            } else {
                theImage = bitmaps[2];
            }
            int pixXPenetrate = (int) (Pix.wSpace*shrinkSideRatio*0.5);
            int pixYPenetrate = (int) (Pix.hSpace*shrinkSideRatio*0.5);
            rectDestination.inset(pixXPenetrate, pixYPenetrate);
            canvas.drawBitmap(theImage, rectSource, rectDestination, paint);
            if (level >= 7) {
                if (level == 7) {
                    theImage = bitmaps[0];
                } else if (level == 8) {
                    theImage = bitmaps[1];
                } else {
                    theImage = bitmaps[2];
                }
                rectDestination.inset(pixXPenetrate, pixYPenetrate);
                canvas.drawBitmap(theImage, rectSource, rectDestination, paint);
                rectDestination.inset(-pixXPenetrate, -pixYPenetrate);
            }
            rectDestination.inset(-pixXPenetrate, -pixYPenetrate);
        }
    }

    public void drawInSequences(Canvas canvas, Bitmap[] bitmaps, int level, Rect rectSource, Rect rectDestination, Paint paint) {
        Bitmap theImage;
        if (level == 1) {
            theImage = bitmaps[0];
        } else if (level == 2) {
            theImage = bitmaps[1];
        } else {
            theImage = bitmaps[2];
        }
        canvas.drawBitmap(theImage, rectSource, rectDestination, paint);
        if (level >= 4) {
            if (level == 4) {
                theImage = bitmaps[3];
            } else if (level == 5) {
                theImage = bitmaps[4];
            } else {
                theImage = bitmaps[5];
            }
            canvas.drawBitmap(theImage, rectSource, rectDestination, paint);
            if (level >= 7) {
                if (level == 7) {
                    theImage = bitmaps[6];
                } else if (level == 8) {
                    theImage = bitmaps[7];
                } else {
                    theImage = bitmaps[8];
                }
                canvas.drawBitmap(theImage, rectSource, rectDestination, paint);
            }
        }
    }
}
