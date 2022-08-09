package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.fruitfall.Constants;
import com.example.fruitfall.MyCanvasView;
import com.example.fruitfall.Pix;
import com.example.fruitfall.SpaceCoors;

import java.util.List;

public class SpaceAnimationOmegaFruit extends SpaceAnimation {

    private ThreeBeam beam;
    private List<SpaceCoors> destinations;

    public SpaceAnimationOmegaFruit(int x, int y, List<SpaceCoors> destinations, int colourOut, int colourMid, int colourIn) {
        super(x, y, Constants.NUMBER_FRAMES_DESTRUCTION_FORANIM);
        this.destinations = destinations;
        this.beam = new ThreeBeam(colourOut, colourMid, colourIn);
    }

    @Override
    protected void drawProtected(MyCanvasView view, Canvas canvas, Rect rectSource, Rect rectDestination, Paint paint) {
        int pixX = Pix.xCenter(x);
        int pixY = Pix.yCenter(y);
        float pseudoRatio = (float) Math.min(1.0, this.ratio() * 1.4);
        paint.setStyle(Paint.Style.FILL);
        for (SpaceCoors coors : this.destinations) {
            this.beam.draw(canvas, pixX, pixY,
                    (int) (pixX + (Pix.xCenter(coors.x)-pixX)*pseudoRatio),
                    (int) (pixY + (Pix.yCenter(coors.y)-pixY)*pseudoRatio), paint);
        }
    }
}
