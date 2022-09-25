package com.example.fruitfall.spaces;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class BreakableBlock extends SpaceFiller {

    private int count;

    public BreakableBlock(int level) {
        this.count = level;
    }

    public int getCount() {
        return count;
    }

    public void downgrade() {
        count--;
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
        canvas.drawBitmap(view.getBitmapImageBreakableBlock(), rectSource, rectDestination, paint);
        this.paintPips(view, canvas, rectDestination, paint, this.count, view.getColorLockDuration());
    }

    @Override
    public SpaceFiller copy() { return new BreakableBlock(this.count); }
}
