package com.example.fruitfall.animations.torn;

import android.graphics.Bitmap;

import com.example.fruitfall.MyCanvasView;

public class SpaceAnimationStopBlastTorn extends SpaceAnimationBlockTorn {
    public SpaceAnimationStopBlastTorn(int x, int y) {
        super(x, y);
    }

    @Override
    public Bitmap getImage(MyCanvasView view) {
        return view.getBitmapImageStopBlasts()[0];
    }

}
