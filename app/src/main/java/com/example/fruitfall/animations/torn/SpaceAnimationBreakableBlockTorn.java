package com.example.fruitfall.animations.torn;

import android.graphics.Bitmap;

import com.example.fruitfall.MyCanvasView;

public class SpaceAnimationBreakableBlockTorn extends SpaceAnimationBlockTorn {
    public SpaceAnimationBreakableBlockTorn(int x, int y) {
        super(x, y);
    }

    @Override
    public Bitmap getImage(MyCanvasView view) {
        return view.getBitmapImageBreakableBlocks()[0];
    }

}
