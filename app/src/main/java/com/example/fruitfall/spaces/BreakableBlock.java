package com.example.fruitfall.spaces;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class BreakableBlock extends SpaceFiller {

    private int count;

    public BreakableBlock(int count) {
        this.count = count;
    }

    public void paint(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint, boolean inPause) {
        Pix.adaptRectForFullSpace(rectDestination);
        drawInLevels(canvas, view.getBitmapImageBreakableBlocks(), this.count, rectSource, rectDestination, paint, (float)0.4);
        Pix.adaptRectForInnerSpace(rectDestination);
        if (inPause) {
            paintPips(view, canvas, rectDestination, paint, this.count, view.getColorDotDestroyableNearby());
        }
    }

    @Override
    public boolean canBeSwapped() {
        return false;
    }

    @Override
    public boolean canFall() {
        return false;
    }

    @Override
    public boolean downgradableOnAdjAlignedOrBlast() {return true;}

    @Override
    public void downgrade() {
        count--;
    }

    @Override
    public boolean outtaHere() {return count <= 0;}


}
