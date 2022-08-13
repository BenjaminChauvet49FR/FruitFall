package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.Constants;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class SpaceAnimationLightning extends SpaceAnimation {

    boolean isHorizontal;
    int extremeCoor1, extremeCoor2;

    public SpaceAnimationLightning(int x, int y, boolean isHorizontal) {
        super(x, y, Constants.NUMBER_FRAMES_ANIMATION_LIGHTNING);
        this.isHorizontal = isHorizontal;
        this.needRecenter = false;
        extremeCoor1 = 0;
        if (this.isHorizontal) {
            extremeCoor2 = Constants.FIELD_XLENGTH-1;
        } else {
            extremeCoor2 = Constants.FIELD_YLENGTH-1;
        }
    }

    @Override
    protected void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(view.getColorAnimationLightning());
        float ratio = ratio();
        float ratioSpeed = (float) Math.min(1.0, ratio * 1.4);
        if (this.isHorizontal) {
            rectDestination.set(
                    Pix.xLeftMainSpace((float)(x-((x+0.25-extremeCoor1)*ratioSpeed))),
                    Pix.yUpMainSpace((float) (y + 0.35 * ratio)),
                    Pix.rightMainSpace((float)(x+(extremeCoor2+0.25-x)*ratioSpeed)),
                    Pix.yDownMainSpace((float) (y - 0.35 * ratio))
            );
        } else {
            rectDestination.set(
                    Pix.xLeftMainSpace((float)(x + 0.35 * ratio)),
                    Pix.yUpMainSpace((float)(y-((y+0.25-extremeCoor1)*ratioSpeed)) ),
                    Pix.rightMainSpace((float)(x - 0.35 * ratio)),
                    Pix.yDownMainSpace((float)(y+(extremeCoor2+0.25-y)*ratioSpeed))

            );
        }
        canvas.drawRect(rectDestination, paint);
    }
}
