package com.example.fruitfall.animations;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.fruitfall.Pix;

public class ThreeBeam {

    private int colourIn;
    private int colourMid;
    private int colourOut;

    public ThreeBeam(int colourOut, int colourMid, int colourIn) {
        this.colourOut = colourOut;
        this.colourMid = colourMid;
        this.colourIn = colourIn;
    }

    // Important : paint must be filled
    public void draw(Canvas c, int pixXStart, int pixYStart, int pixXEnd, int pixYEnd, Paint paint) {
        paint.setStrokeWidth(Pix.thicknessOuterBeam);
        paint.setColor(colourOut);
        c.drawLine(pixXStart, pixYStart, pixXEnd, pixYEnd, paint);
        paint.setStrokeWidth(Pix.thicknessMidBeam);
        paint.setColor(colourMid);
        c.drawLine(pixXStart, pixYStart, pixXEnd, pixYEnd, paint);
        paint.setStrokeWidth(Pix.thicknessInnerBeam);
        paint.setColor(colourIn);
        c.drawLine(pixXStart, pixYStart, pixXEnd, pixYEnd, paint);
    }

}
