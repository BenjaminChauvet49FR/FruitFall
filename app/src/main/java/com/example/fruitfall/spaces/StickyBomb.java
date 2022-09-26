package com.example.fruitfall.spaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class StickyBomb extends SpaceFiller {

    public int fruitId;
    public int count;
    private static RectF rectEllipse = new RectF(0, 0, 0, 0);

    public StickyBomb(int count, int fruitId) {
        this.fruitId = fruitId;
        this.count = count;
    }

    public int getContainedFruitId() {
        return fruitId;
    }

    public int getCount() {
        return count;
    }

    public void downgrade() {
        count--;
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
    public void paintStill(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        if (this.fruitId != Constants.NOT_A_FRUIT) {
            canvas.drawBitmap(view.getBitmapFruitToDrawFromIndex(this.fruitId), rectSource, rectDestination, paint);
        }
        canvas.drawBitmap(view.getBitmapImageStickyBomb(), rectSource, rectDestination, paint);
        paint.setColor(this.fruitId != Constants.NOT_A_FRUIT ? view.getColorVeilStickyBomb() : view.getColorVeilStickyBombEmpty());
        rectEllipse.set(rectDestination.left, rectDestination.top, rectDestination.right, rectDestination.bottom);
        paint.setAlpha(127);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawOval(rectEllipse, paint);
        paintPips(view, canvas, rectDestination, paint, this.count, view.getColorDotStickyBomb());
    }

    @Override
    public SpaceFiller copy() {
        return null;
    }
}
