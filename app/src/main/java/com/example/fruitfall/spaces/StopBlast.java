package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.GameHandler;
import com.example.fruitfall.MyCanvasView;

public class StopBlast extends SpaceFiller {

    public int count;

    public StopBlast(int count) {
        this.count = count;
    }

    @Override
    public boolean canBeSwapped() {
        return true;
    }

    @Override
    public boolean canFall() {
        return true;
    }

    @Override
    public void paint(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint, boolean inPause) {
        drawInSequences(canvas, view.getBitmapImageStopBlasts(), this.count, rectSource, rectDestination, paint);
        if (inPause) {
            paintPips(view, canvas, rectDestination, paint, this.count, view.getColorDotDestroyableNearby());
        }
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
    }

    @Override
    public boolean crossableByPowers() {
        return false;
    }
}
