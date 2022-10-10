package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameEnums;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;

public class SpaceAnimationBlastUpTo8Directions extends SpaceAnimation {

    private final int finalXLeft;
    private final int finalXRight;
    private final int finalYUp;
    private final int finalYDown;
    private final int finalXLU;
    private final int finalYLU;
    private final int finalXRD;
    private final int finalYRD;
    private final int finalXRU;
    private final int finalYRU;
    private final int finalXLD;
    private final int finalYLD;
    private final boolean[] blockades = new boolean[8];
    // Some distances for lu, ru, rd, du

    public SpaceAnimationBlastUpTo8Directions(int x, int y, int[] directionsBlastRange, boolean[] blockades) {
        super(x, y, Constants.NUMBER_FRAMES_ANIMATION_MIXED);
        finalXLeft = x - directionsBlastRange[GameEnums.DIRECTIONS_BLAST.L.index()];
        finalXRight = x + directionsBlastRange[GameEnums.DIRECTIONS_BLAST.R.index()];
        finalYUp = y - directionsBlastRange[GameEnums.DIRECTIONS_BLAST.U.index()];
        finalYDown = y + directionsBlastRange[GameEnums.DIRECTIONS_BLAST.D.index()];
        finalXLU = x - directionsBlastRange[GameEnums.DIRECTIONS_BLAST.LU.index()];
        finalYLU = y - directionsBlastRange[GameEnums.DIRECTIONS_BLAST.LU.index()];
        finalXRD = x + directionsBlastRange[GameEnums.DIRECTIONS_BLAST.RD.index()];
        finalYRD = y + directionsBlastRange[GameEnums.DIRECTIONS_BLAST.RD.index()];
        finalXLD = x - directionsBlastRange[GameEnums.DIRECTIONS_BLAST.DL.index()];
        finalYLD = y + directionsBlastRange[GameEnums.DIRECTIONS_BLAST.DL.index()];
        finalXRU = x + directionsBlastRange[GameEnums.DIRECTIONS_BLAST.UR.index()];
        finalYRU = y - directionsBlastRange[GameEnums.DIRECTIONS_BLAST.UR.index()];
        for (GameEnums.DIRECTIONS_BLAST dir : GameEnums.DIRECTIONS_BLAST.values()) {
            this.blockades[dir.index()] = blockades[dir.index()];
        }
    }

    @Override
    protected void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        // TODO les huit directions à des timings différents ?
        // TODO faire plus pour les blocages des blasts diagonaux (pour l'instant les entrées diagonales du tableau de blocage ne sont pas utilisées)
        // TODO parti pris esthétique : un "plus" apparaît pour les L/R
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(view.getColorAnimationLightning());
        float ratio = ratio();
        float ratioSpeed = (float) Math.min(1.0, ratio * 1.4);
        float pixPenetrate1, pixPenetrate2;
        // Horizontal part
        pixPenetrate1 = this.blockades[GameEnums.DIRECTIONS_BLAST.L.index()] ? 0 : (float) 0.25;
        pixPenetrate2 = this.blockades[GameEnums.DIRECTIONS_BLAST.R.index()] ? 0 : (float) 0.25;
        rectDestination.set(
            Pix.xLeftSpace(x-((x+pixPenetrate1-finalXLeft)*ratioSpeed)),
            Pix.yUpSpace(y + (float)0.35 * ratio),
            Pix.xRightSpace(x+(finalXRight+pixPenetrate2-x)*ratioSpeed),
            Pix.yDownSpace(y - (float)0.35 * ratio)
        );
        canvas.drawRect(rectDestination, paint);
        // Vertical part
        pixPenetrate1 = this.blockades[GameEnums.DIRECTIONS_BLAST.U.index()] ? 0 : (float) 0.25;
        pixPenetrate2 = this.blockades[GameEnums.DIRECTIONS_BLAST.D.index()] ? 0 : (float) 0.25;
        rectDestination.set(
                    Pix.xLeftSpace(x + (float)0.35 * ratio),
                    Pix.yUpSpace(y-(y+pixPenetrate1-finalYUp)*ratioSpeed),
                    Pix.xRightSpace(x - (float)0.35 * ratio),
                    Pix.yDownSpace(y+(finalYDown+pixPenetrate2-y)*ratioSpeed)
        );
        canvas.drawRect(rectDestination, paint);
        // Fun part
        canvas.save();
        int[] pixX = {Pix.xCenter(finalXLU), Pix.xLeftSpace(finalXLU), Pix.xCenter(finalXRD), Pix.xRightSpace(finalXRD)};
        int[] pixY = {Pix.yUpSpace(finalYLU), Pix.yCenter(finalYLU), Pix.yDownSpace(finalYRD), Pix.yCenter(finalYRD)};
        Path path = new Path();
        drawPolygonFromPath(canvas, pixX, pixY, path, paint);
        pixX = new int[]{Pix.xCenter(finalXRU), Pix.xRightSpace(finalXRU), Pix.xCenter(finalXLD), Pix.xLeftSpace(finalXLD)};
        pixY = new int[]{Pix.yUpSpace(finalYRU), Pix.yCenter(finalYRU), Pix.yDownSpace(finalYLD), Pix.yCenter(finalYLD)};
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
