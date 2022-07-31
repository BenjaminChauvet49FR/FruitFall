package com.example.fruitfall.animations;

import android.graphics.Bitmap;
import android.view.View;

import com.example.fruitfall.Constants;
import com.example.fruitfall.GameHandler;
import com.example.fruitfall.MyCanvasView;

public class SpaceAnimationOmegaSphere extends SpaceAnimationFading {

    public SpaceAnimationOmegaSphere(int x, int y) {
        super(x, y, Constants.NUMBER_FRAMES_DESTRUCTION_FORANIM);
    }

    @Override
    public Bitmap getImage(MyCanvasView view) {
        return view.getBitmapImageSphereOmega();
    }
}
