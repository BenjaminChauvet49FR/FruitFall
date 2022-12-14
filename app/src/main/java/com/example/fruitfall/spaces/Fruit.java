package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.GameEnums;
import com.example.fruitfall.MyCanvasView;

public class Fruit extends SpaceFiller {
    private int value;
    private final GameEnums.FRUITS_POWER power;

    public Fruit(int value) {
        this(value, GameEnums.FRUITS_POWER.NONE);
    }

    public Fruit(int value, GameEnums.FRUITS_POWER power) {
        this.value = value;
        this.power = power;
    }

    @Override
    public int getIdFruit() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
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
    public GameEnums.FRUITS_POWER getPower() {return this.power;}

    @Override
    public void paint(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint, boolean isInPause) {
        if (!isInPause) {
            canvas.drawBitmap(view.getBitmapFruitToDrawFromIndex(this.value), rectSource, rectDestination, paint);
            if (this.power == GameEnums.FRUITS_POWER.HORIZONTAL_LIGHTNING) {
                canvas.drawBitmap(view.getBitmapImageLightH(), rectSource, rectDestination, paint);
            }
            if (this.power == GameEnums.FRUITS_POWER.VERTICAL_LIGHTNING) {
                canvas.drawBitmap(view.getBitmapImageLightV(), rectSource, rectDestination, paint);
            }
            if (this.power == GameEnums.FRUITS_POWER.FIRE) {
                canvas.drawBitmap(view.getBitmapImageFire(), rectSource, rectDestination, paint);
            }
        }
    }
}
