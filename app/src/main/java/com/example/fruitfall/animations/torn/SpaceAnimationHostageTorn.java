package com.example.fruitfall.animations.torn;

import android.graphics.Bitmap;

import com.example.fruitfall.MyCanvasView;

public class SpaceAnimationHostageTorn extends SpaceAnimationBlockTorn {
    public SpaceAnimationHostageTorn(int x, int y) {
        super(x, y);
    }

    @Override
    public Bitmap getImage(MyCanvasView view) {
        return view.getBitmapImageHostageLocks()[0];
    }

}
