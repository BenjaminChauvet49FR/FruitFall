package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.MyCanvasView;

public class HostageLock extends SpaceFiller {

    // The space mustn't be another hostage lock
    private final SpaceFiller space;
    private int count;

    public HostageLock(SpaceFiller space, int count) {
        super();
        this.space = space;
        this.count = count; // TODO change count to level or so ? Do some uniformism...
    }

    public SpaceFiller getHostage() {
        return this.space;
    }

    public int getCount() {
        return this.count;
    }

    public int getIdFruit() {
        return this.space.getIdFruit();
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
    public void paint(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint, boolean isInPause) {
        space.paint(view, canvas, rectSource, rectDestination, paint, isInPause);
        drawInLevels(canvas, view.getBitmapImageHostageLocks(), this.count, rectSource, rectDestination, paint, (float)0.25);
        if (isInPause) {
            this.paintPips(view, canvas, rectDestination, paint, this.count, view.getColorDotHostage());
        }
    }

    @Override
    public void downgrade() {
        count--;
    }

    @Override
    public boolean outtaHere() {return count <= 0;}
}
