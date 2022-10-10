package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.GameEnums;
import com.example.fruitfall.MyCanvasView;

public class OmegaSphere extends SpaceFiller {

    @Override
    public boolean canBeSwapped() {
        return true;
    }

    @Override
    public boolean canFall() {
        return true;
    }

    @Override
    public void paint(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint, boolean isInPause) {
        if (!isInPause) {
            canvas.drawBitmap(view.getBitmapImageSphereOmega(), rectSource, rectDestination, paint);
        }
    }

    @Override
    public GameEnums.FRUITS_POWER getPower() {return GameEnums.FRUITS_POWER.OMEGA_SPHERE;}
}
