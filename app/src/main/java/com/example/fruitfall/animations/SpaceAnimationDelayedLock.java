package com.example.fruitfall.animations;

import android.graphics.Bitmap;

import com.example.fruitfall.Constants;
import com.example.fruitfall.MyCanvasView;

public class SpaceAnimationDelayedLock extends SpaceAnimationFading {

    public SpaceAnimationDelayedLock(int x, int y) {
        super(x, y, Constants.NUMBER_FRAMES_DESTRUCTION_FORDELAYEDLOCK);
    }

    @Override
    public Bitmap getImage(MyCanvasView view) {
        return view.getBitmapImageLocking();
    }

}
