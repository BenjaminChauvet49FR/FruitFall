package com.example.fruitfall.animations.torn;

import android.graphics.Bitmap;

import com.example.fruitfall.MyCanvasView;

public class SpaceAnimationStickyBombTorn extends SpaceAnimationBlockTorn {
    public SpaceAnimationStickyBombTorn(int x, int y) {
        super(x, y);
    }

    @Override
    public Bitmap getImage(MyCanvasView view) {
        return view.getBitmapImageStickyBombs()[0];
    }

}