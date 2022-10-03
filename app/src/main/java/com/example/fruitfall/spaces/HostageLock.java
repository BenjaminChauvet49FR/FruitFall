package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.MyCanvasView;

public class HostageLock extends SpaceFiller {

    // The space mustn't be another hostage lock
    private SpaceFiller space;
    private int count;

    public HostageLock(SpaceFiller space, int count) {
        super();
        this.space = space;
        this.count = count;
    }

    public SpaceFiller getHostage() {
        return this.space;
    }

    public int getCount() {
        return this.count;
    }

    public void downgrade() {
        this.count--;
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
    public void paintStill(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        space.paintStill(view, canvas, rectSource, rectDestination, paint);
        canvas.drawBitmap(view.getBitmapImageHostageLock(), rectSource, rectDestination, paint);
        this.paintPips(view, canvas, rectDestination, paint, this.count, view.getColorLockHostage());
    } // TODO change count to level ? Do some uniformism...

    @Override
    public SpaceFiller copy() {
        return null;
    }
}
