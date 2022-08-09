package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.example.fruitfall.Constants;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class SwapAnimationFireElectric extends SpaceAnimation {

    private int finalXLeft;
    private int finalXRight;
    private int finalYUp;
    private int finalYDown;
    private int finalXLU;
    private int finalYLU;
    private int finalXRD;
    private int finalYRD;
    private int finalXRU;
    private int finalYRU;
    private int finalXLD;
    private int finalYLD;
    // Some distances for lu, ru, rd, du

    public SwapAnimationFireElectric(int x, int y) {
        super(x, y, Constants.NUMBER_FRAMES_ANIMATION_MIXED);
        finalXLeft = 0;
        finalXRight = Constants.FIELD_XLENGTH-1;
        finalYUp = 0;
        finalYDown = Constants.FIELD_YLENGTH-1;
        finalXLU = x;
        finalYLU = y;
        while(finalXLU > 0 && finalYLU > 0) {
            finalXLU--;
            finalYLU--;
        }
        finalXRD = x;
        finalYRD = y;
        while(finalXRD < Constants.FIELD_XLENGTH-1 && finalYRD < Constants.FIELD_YLENGTH-1) {
            finalXRD++;
            finalYRD++;
        }
        finalXLD = x;
        finalYLD = y;
        while(finalXLD > 0 && finalYLD < Constants.FIELD_YLENGTH-1) {
            finalXLD--;
            finalYLD++;
        }
        finalXRU = x;
        finalYRU = y;
        while(finalXRU < Constants.FIELD_XLENGTH-1 && finalYRU > 0) {
            finalXRU++;
            finalYRU--;
        }
    }

    @Override
    protected void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        // TODO les huit directions à des timings différents ?
;        // TODO ah, et les blocages aussi
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(view.getColorAnimationLightning());
        float ratio = ratio();
        float ratioSpeed = (float) Math.min(1.0, ratio * 1.4);
        // Horizontal part
        rectDestination.set(
            Pix.pixXLeftMainSpace((float)(x-((x+0.25-finalXLeft)*ratioSpeed))),
            Pix.pixYUpMainSpace((float) (y + 0.35 * ratio)),
            Pix.pixXRightMainSpace((float)(x+(finalXRight+0.25-x)*ratioSpeed)),
            Pix.pixYDownMainSpace((float) (y - 0.35 * ratio))
        );
        canvas.drawRect(rectDestination, paint);
        // Vertical part
        rectDestination.set(
                    Pix.pixXLeftMainSpace((float)(x + 0.35 * ratio)),
                    Pix.pixYUpMainSpace((float)(y-((y+0.25-finalYUp)*ratioSpeed)) ),
                    Pix.pixXRightMainSpace((float)(x - 0.35 * ratio)),
                    Pix.pixYDownMainSpace((float)(y+(finalYDown+0.25-y)*ratioSpeed))

        );
        canvas.drawRect(rectDestination, paint);
        // Fun part
        canvas.save();
        int[] pixX = {Pix.xCenter(finalXLU), Pix.pixXLeftMainSpace(finalXLU), Pix.xCenter(finalXRD), Pix.pixXRightMainSpace(finalXRD)};
        int[] pixY = {Pix.pixYUpMainSpace(finalYLU), Pix.yCenter(finalYLU), Pix.pixYDownMainSpace(finalYRD), Pix.yCenter(finalYRD)};
        Path path = new Path();
        drawPolygonFromPath(canvas, pixX, pixY, path, paint);
        pixX = new int[]{Pix.xCenter(finalXRU), Pix.pixXRightMainSpace(finalXRU), Pix.xCenter(finalXLD), Pix.pixXLeftMainSpace(finalXLD)};
        pixY = new int[]{Pix.pixYUpMainSpace(finalYRU), Pix.yCenter(finalYRU), Pix.pixYDownMainSpace(finalYLD), Pix.yCenter(finalYLD)};
        drawPolygonFromPath(canvas, pixX, pixY, path, paint);
    }

    private void drawPolygonFromPath(Canvas canvas, int[] pixX, int[] pixY, Path path, Paint paint) {
        path.reset();
        int l = pixX.length;
        path.moveTo(pixX[l-1], pixY[l-1]);
        for (int i = 0 ; i < l ; i++) {
            path.lineTo(pixX[i], pixY[i]);
        }
        canvas.drawPath(path, paint);
    }
}
