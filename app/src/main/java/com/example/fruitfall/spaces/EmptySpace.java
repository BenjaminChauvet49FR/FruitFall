package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.MyCanvasView;

public class EmptySpace extends SpaceFiller {

    @Override
    public boolean canBeSwapped() {
        return false;
    }

    @Override
    public boolean canFall() {
        return true;
    }

    @Override
    public void paintStill(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {}
}
