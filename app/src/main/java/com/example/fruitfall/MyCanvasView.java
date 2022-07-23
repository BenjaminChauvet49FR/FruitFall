/*package com.example.fruitfall;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.fonts.Font;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.test.core.app.ApplicationProvider;

import com.example.fruitfall.animations.*;
import com.example.fruitfall.level.LevelManager;

import java.util.Arrays;


public class MyCanvasView extends View {

        private static int SPACE_UNDEFINED = -1;

        private static Resources resources; //resources = ApplicationProvider.getApplicationContext().getResources();
        // https://www.tabnine.com/code/java/methods/android.graphics.BitmapFactory/decodeResource
        public MyCanvasView(Context context) {
                super(context);
                resources = context.getResources();
        }


        public static int colorBG = ResourcesCompat.getColor(resources, R.color.background, null);
        public static int colorTextMain = ResourcesCompat.getColor(resources,R.color.textMain, null);
        public static int colorFrameRect = ResourcesCompat.getColor(resources, R.color.frameRect, null);
        public static int colorScoreFallSpace = ResourcesCompat.getColor(resources, R.color.scoreFallSpace, null);
        public static int colorScoreDestructionSpace = ResourcesCompat.getColor(resources, R.color.scoreDestructionSpace, null);
        public static int colorTitle = ResourcesCompat.getColor(resources, R.color.title, null);
        public static int colorAnimationLightning = ResourcesCompat.getColor(resources, R.color.animationLightning, null);
        public static int colorAnimationFire = ResourcesCompat.getColor(resources, R.color.animationFire, null);

        public static Bitmap bitmapImageLightActive = BitmapFactory.decodeResource(resources, R.drawable.light_up);
        public static Bitmap bitmapImageLightInactive = BitmapFactory.decodeResource(resources, R.drawable.light_down);
        public static Bitmap bitmapImageLightPenalty = BitmapFactory.decodeResource(resources, R.drawable.light_comeback);
        public static Bitmap bitmapImageFire= BitmapFactory.decodeResource(resources, R.drawable.on_fire);
        public static Bitmap bitmapImageLightH = BitmapFactory.decodeResource(resources, R.drawable.lightning_h);
        public static Bitmap bitmapImageLightV = BitmapFactory.decodeResource(resources, R.drawable.lightning_v);
        public static Bitmap bitmapImageSphereOmega = BitmapFactory.decodeResource(resources, R.drawable.sphere_omega);
        public static Bitmap[] bitmapImages = {
                BitmapFactory.decodeResource(resources, R.drawable.f1),
                BitmapFactory.decodeResource(resources, R.drawable.f2),
                BitmapFactory.decodeResource(resources, R.drawable.f3),
                BitmapFactory.decodeResource(resources, R.drawable.f4),
                BitmapFactory.decodeResource(resources, R.drawable.f5),
                BitmapFactory.decodeResource(resources, R.drawable.f6),
                BitmapFactory.decodeResource(resources, R.drawable.f7),
                BitmapFactory.decodeResource(resources, R.drawable.f8)
        };

        // Search for a font on my computer : https://www.pcmag.com/how-to/how-to-manage-your-fonts-in-windows
// Font handling : https://developer.android.com/guide/topics/ui/look-and-feel/fonts-in-xml#kotlin

        /* NOPE
        @RequiresApi(Build.VERSION_CODES.O)
        private Typeface mainFont = resources.getFont(R.font.georgia);
        @RequiresApi(Build.VERSION_CODES.O)
        private Typeface scoreFont = resources.getFont(R.font.trebuc); */
// Trouble met : "Call requires API level 26 (current min is 21): android.content.res.Resources#getFont"
// https://stackoverflow.com/questions/20279084/how-android-set-custom-font-in-canvas
// Got myself seducted by this : https://medium.com/programming-lite/using-custom-font-as-resources-in-android-app-6331477f8f57 so I dealt with the API.

        // Set up the paint with which to draw.
        /*private Paint paint = new Paint(); */
                /* Paint().apply { // Great details here : https://developer.android.com/codelabs/advanced-android-kotlin-training-canvas#4
                // Smooths out edges of what is drawn without affecting shape.
                isAntiAlias = true
                // Dithering affects how colors with higher-precision than the device are down-sampled.
                isDither = true
                style = Paint.Style.STROKE // default: FILL
                strokeJoin = Paint.Join.ROUND // default: MITER
                strokeCap = Paint.Cap.ROUND // default: BUTT
                strokeWidth = Pix.selectionFrame // default: Hairline-width (really thin)
                }*/

        /*private Rect rectSource = new Rect(0, 0, Pix.resourceSide, Pix.resourceSide);
        private Rect rectSourceVariable = new Rect(0, 0, Pix.resourceSide, Pix.resourceSide);
        private Rect rectDest = new Rect(0, 0, 0, 0);
        private Rect rectFrame = new Rect(0, 0, 0, 0);
        private Rect rectAnim = new Rect(0, 0, 0, 0);

        private Canvas extraCanvas;
        private Bitmap extraBitmap;


        private Bitmap getBitmapFruitToDrawFromIndex(int index)  {
                return bitmapImages[gh.getRandomFruit(index)];
        }

        private void drawSpaceContent(int x, int y, Canvas canvas, Rect rectSource, Rect rectDest, Paint paint) {
                if (gh.hasFruit(x, y)) {
                        Bitmap fruitImage = bitmapImages[gh.getRandomFruitFromCoors(x, y)];
                        canvas.drawBitmap(fruitImage, rectSource, rectDest, paint);
                        GameEnums.FRUITS_POWER power = gh.getFruitPowerFromCoors(x, y);
                        Bitmap powerImage = null;
                        if (power == GameEnums.FRUITS_POWER.FIRE) {
                                powerImage = bitmapImageFire;
                        } else if (power == GameEnums.FRUITS_POWER.HORIZONTAL_LIGHTNING) {
                                powerImage = bitmapImageLightH;
                        } else if (power == GameEnums.FRUITS_POWER.VERTICAL_LIGHTNING) {
                                powerImage = bitmapImageLightV;
                        }
                        if (powerImage != null) {
                                canvas.drawBitmap(powerImage, rectSource, rectDest, paint);
                        }
                } else if (gh.hasOmegaSphere(x, y)) {
                        canvas.drawBitmap(bitmapImageSphereOmega, rectSource, rectDest, paint);
                }
        }

        private GameHandler gh = new GameHandler();

        public void startLevel() {
                gh.initializeGrid(LevelManager.levelLists.get(LevelManager.levelNumber));
        }*/

        /*
        @Override 
        private void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (::extraBitmap.isInitialized()) extraBitmap.recycle() // If not for this, memory leak !
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(colorBG)
        }*/

       /*§ @RequiresApi(Build.VERSION_CODES.O)

        @Override
        protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawBitmap(extraBitmap, 0f, 0f, null);

                // Draw variables
                paint.setColor(colorTextMain);
                paint.setTextSize(Pix.hText);
               // paint.setTypeface(mainFont);
                paint.setStyle(Paint.Style.FILL);// How to avoid awful outlined texts : https://stackoverflow.com/questions/31877417/android-draw-text-with-solid-background-onto-canvas-to-be-used-as-a-bitmap
                canvas.drawText("Score : " + gh.getScore(), Pix.xScore, Pix.yScore, paint);
                canvas.drawText("Temps : " + gh.gth.getTimeToDisplay(), Pix.xTime, Pix.yTime, paint);
                canvas.drawText("F : " + gh.getFruits(), Pix.xCommand1, Pix.yCommand1, paint);
                paint.setColor(colorTitle);
                canvas.drawText(gh.getTitle(), Pix.xTitle, Pix.yTitle, paint);

                SpaceAnimation animation = null;
                rectDest.set(Pix.xStartSpaces, Pix.yStartSpaces, Pix.xStartSpaces+Pix.wMainSpace, Pix.yStartSpaces+Pix.hMainSpace);
                int x, y;
                // Draw the in-place fallElts or the on-space animations
                for (y = 0 ; y < Constants.FIELD_YLENGTH ; y++) {
                        for (x = 0 ; x < Constants.FIELD_XLENGTH ; x++) {
                                if (gh.gth.hasStillSpace(x, y)) {
                                        this.drawSpaceContent(x, y, canvas, rectSource, rectDest, paint);
                                }
                                animation = gh.gth.getAnimation(x, y);
                                if (animation != null && animation.shouldBeDrawn()) {
                                        if (animation instanceof SpaceAnimationFruitShrinking) { // TODO this type-by-type logic and images...
                                                if (((SpaceAnimationFruitShrinking) animation).isAlsoRotating()) {
                                                        canvas.save(); // Note : it should be possible to make all rotations at once.
                                                        canvas.rotate(animation.ratio() * Constants.MAX_ANGLE_IN_DEGREES, rectDest.exactCenterX(), rectDest.exactCenterY()); // https://www.tabnine.com/code/java/methods/android.graphics.Canvas/rotate
                                                        canvas.drawBitmap(getBitmapFruitToDrawFromIndex(((SpaceAnimationFruitShrinking) animation).getImageFruit()),
                                                                rectSource, shrinkedRect(rectDest, animation.ratio()), paint);
                                                        canvas.restore();
                                                } else {
                                                        canvas.drawBitmap(getBitmapFruitToDrawFromIndex(((SpaceAnimationFruitShrinking) animation).getImageFruit()),
                                                                rectSource, shrinkedRect(rectDest, animation.ratio()), paint);
                                                }
                                        } else if (animation instanceof SpaceAnimationOmegaSphere) {
                                                paint.setAlpha((int) (255*(1-animation.ratio())));
                                                canvas.drawBitmap(bitmapImageSphereOmega, rectSource, rectDest, paint);
                                                paint.setAlpha(255);
                                        } else if (animation instanceof SpaceAnimationLightning) {
                                                paint.setColor(colorAnimationLightning);
                                                //ratio = animation.ratio();
                                                if (((SpaceAnimationLightning) animation).getHorizontal()) {
                                                        rectAnim.set(
                                                                pixXLeftMainSpace(0),//x-((x+1)*ratio)),
                                                                pixYUpMainSpace(y), //(y + 0.5 * ratio).toFloat()), TODO revoir les animations
                                                                pixXRightMainSpace(Constants.FIELD_XLENGTH-1),//x+(Constants.FIELD_XLENGTH-x)*ratio),
                                                                pixYDownMainSpace(y)//(y - 0.5 * ratio).toFloat())
                                                        );
                                                } else {
                                                        rectAnim.set(
                                                                pixXLeftMainSpace(x),//(x + 0.5 * ratio).toFloat()),
                                                                pixYUpMainSpace(0),//y-((y+1)*ratio) ),
                                                                pixXRightMainSpace(x),//(x - 0.5 * ratio).toFloat()),
                                                                pixYDownMainSpace(Constants.FIELD_YLENGTH-1)//y+(Constants.FIELD_YLENGTH-y)*ratio)
                                                        );
                                                }
                                                canvas.drawRect(rectAnim, paint);
                                        } else if (animation instanceof SpaceAnimationFire) {
                                                paint.setColor(colorAnimationFire);
                                                rectAnim.set(pixXLeftMainSpace(x -2), pixYUpMainSpace(y), pixXRightMainSpace(x +2), pixYDownMainSpace(y ));
                                                canvas.drawRect(rectAnim, paint);
                                                rectAnim.set(pixXLeftMainSpace(x -1), pixYUpMainSpace(y-1), pixXRightMainSpace(x +1), pixYDownMainSpace(y +1));
                                                canvas.drawRect(rectAnim, paint);
                                                rectAnim.set(pixXLeftMainSpace(x ), pixYUpMainSpace(y-2), pixXRightMainSpace(x ), pixYDownMainSpace(y +2));
                                                canvas.drawRect(rectAnim, paint);
                                        }
                                        animation.progress();
                                }
                                rectDest.left += Pix.wSpace;
                                rectDest.right += Pix.wSpace;
                        }
                        rectDest.left = Pix.xStartSpaces;
                        rectDest.right = Pix.xStartSpaces+Pix.wMainSpace;
                        rectDest.top += Pix.hSpace;
                        rectDest.bottom += Pix.hSpace;
                }

                // Draw the falling elements
                if (gh.gth.isInFall()) {
                        int xStartFall;
                        int yStartFall;
                        SpaceCoors outCoors;
                        float ratioFall = gh.gth.ratioToCompletionFall();
                        float antiRatioFall = 1 - ratioFall;
                        int pixYSplitImgSrc = (int) (Pix.resourceSide * antiRatioFall);
                        int pixYSplitImgDst = (int) (Pix.hMainSpace * ratioFall);
                        for (SpaceCoors coors : gh.getFallingEltsCoors()) {
                                xStartFall = coors.x;
                                yStartFall = coors.y;
                                if (gh.isNotDestroyedBeforeFall(xStartFall, yStartFall)) {
                                        outCoors = gh.getDestination(xStartFall, yStartFall);
                                        if (outCoors != null) { // FallElt in teleportation
                                                drawTopImageInBotSpace(xStartFall, yStartFall, coors.x, coors.y, pixYSplitImgSrc, pixYSplitImgDst, canvas);
                                                drawBotImageInTopSpace(xStartFall, yStartFall, outCoors.x, outCoors.y, pixYSplitImgSrc, pixYSplitImgDst, canvas);
                                        } else { // FallElt without teleportation
                                                rectDest.left = pixXLeftMainSpace(xStartFall);
                                                rectDest.right = rectDest.left + Pix.wMainSpace;
                                                rectDest.top = pixYUpMainSpace(yStartFall + ratioFall);
                                                rectDest.bottom = rectDest.top + Pix.hMainSpace;
                                                this.drawSpaceContent(xStartFall, yStartFall, canvas, rectSource, rectDest, paint);
                                        }
                                }
                        }

                        // Draw the spawning fruits
                        int xEndFall, yEndFall;
                        for (SpaceCoors coors : gh.getSpawningFruitsCoors()) {
                                xEndFall = coors.x;
                                yEndFall = coors.y;
                                drawBotFruitInSpawnSpace(xEndFall, yEndFall, getBitmapFruitToDrawFromIndex(gh.spawn(xEndFall, yEndFall)), pixYSplitImgSrc, pixYSplitImgDst, canvas);
                        }
                }


                // Draw the cursor
                if (isSpaceSelected()) {
                        rectFrame.set(spaceXToPixXLeft(getSpaceXSelected()), spaceYToPixYUp(getSpaceYSelected()), spaceXToPixXRight(getSpaceXSelected()), spaceYToPixYDown(getSpaceYSelected()));
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(colorFrameRect);
                        canvas.drawRect(rectFrame, paint);
                }

                // Draw the currently swapping fruits
                if (gh.gth.isInSwap()) {
                        int x1 = gh.gth.getXSwap1();
                        int x2 = gh.gth.getXSwap2();
                        int y1 = gh.gth.getYSwap1();
                        int y2 = gh.gth.getYSwap2();
                        float ratio = gh.gth.ratioToCompletionSwap();
                        rectDest.left = pixXLeftMainSpace(x1 + ratio*(x2-x1));
                        rectDest.right = rectDest.left + Pix.wMainSpace;
                        rectDest.top = pixYUpMainSpace((y1 + ratio*(y2-y1)) );
                        rectDest.bottom = rectDest.top + Pix.hMainSpace;
                        this.drawSpaceContent(x1, y1, canvas, rectSource, rectDest, paint);
                        rectDest.left = pixXLeftMainSpace(x2 + ratio*(x1-x2));
                        rectDest.right = rectDest.left + Pix.wMainSpace;
                        rectDest.top = pixYUpMainSpace(y2 + ratio*(y1-y2));
                        rectDest.bottom = rectDest.top + Pix.hMainSpace;
                        this.drawSpaceContent(x2, y2, canvas, rectSource, rectDest, paint);
                }

                // Draw the status light
                rectDest.left = Pix.xStartActiveLight;
                rectDest.top = Pix.yStartActiveLight;
                rectDest.right = rectDest.left + Pix.wActiveLight;
                rectDest.bottom = rectDest.top + Pix.hActiveLight;
                if (gh.gth.isActive()) {
                        canvas.drawBitmap(bitmapImageLightActive, rectSource, rectDest, paint);
                } else if (gh.gth.isActivePenalty()) {
                        canvas.drawBitmap(bitmapImageLightPenalty, rectSource, rectDest, paint);
                } else {
                        canvas.drawBitmap(bitmapImageLightInactive, rectSource, rectDest, paint);
                }

                // Draw scores on the field
                if (gh.gth.shouldDrawScore()) {
                        paint.setColor(colorScoreFallSpace);
                        paint.setTextSize(Pix.hScoreSpace);
                        // NOPE  paint.setTypeface(scoreFont);
                        paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        for (SpaceCoors coors : this.gh.getContributingSpacesScoreFall()) {
                                x = coors.x;
                                y = coors.y;
                                canvas.drawText("+" + this.gh.scoreFallSpace(x, y),
                                        (float) pixXLeftMainSpace(x),pixYUpMainSpace(y) + Pix.hScoreSpace,
                                        paint);
                        }
                        paint.setColor(colorScoreDestructionSpace);
                        paint.setTextSize(Pix.hScoreSpace);
                        // NOPE paint.setTypeface(scoreFont);
                        paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        for (SpaceCoors coors : this.gh.getContributingSpacesScoreDestructionSpecial()) {
                                x = coors.x;
                                y = coors.y;
                                canvas.drawText("+" + this.gh.scoreDestructionSpecialSpace(x, y),
                                        (float) pixXLeftMainSpace(x),
                                        pixYUpMainSpace(y) + Pix.hScoreSpace,
                                        paint);
                        }
                }


                invalidate(); // At the end of draw... right ? Also, how many FPS ?

                //if (gh != null && gh.gth != null) { // TODO, passer ça à Java
                if (gh.gth != null) {
                        gh.gth.step(); // TODO Lui donner son propre processus parallèle ? Ou bien laisser dans onDraw ?
                }
        }

        private Rect shrinkedRect(Rect rectDest, float ratio) {
                return new Rect(
                        (int) (rectDest.left + Pix.wMainSpace*ratio/2),
                        (int) (rectDest.top + Pix.hMainSpace*ratio/2),
                        (int) (rectDest.right - Pix.wMainSpace*ratio/2),
                        (int) (rectDest.bottom - Pix.hMainSpace*ratio/2)
                );
        }

        private void drawTopImageInBotSpace(int xSpaceToDraw, int ySpaceToDraw, int xSpaceTarget, int ySpaceTarget, int pixYSplitImgSrc, int pixYSplitImgDst, Canvas canvas) {
                rectDest.left = pixXLeftMainSpace(xSpaceTarget);
                rectDest.right = rectDest.left + Pix.wMainSpace;
                rectDest.top = pixYUpMainSpace(ySpaceTarget) + pixYSplitImgDst;
                rectDest.bottom = pixYUpMainSpace(ySpaceTarget + 1);
                rectSourceVariable.top = 0;
                rectSourceVariable.bottom = pixYSplitImgSrc;
                drawSpaceContent(xSpaceToDraw, ySpaceToDraw, canvas, rectSourceVariable, rectDest, paint);
        }

        private void drawBotImageInTopSpace(int xSpaceToDraw, int ySpaceToDraw, int xSpaceTarget, int ySpaceTarget, int pixYSplitImgSrc, int pixYSplitImgDst, Canvas canvas) {
                rectDest.left = pixXLeftMainSpace(xSpaceTarget);
                rectDest.right = rectDest.left + Pix.wMainSpace;
                rectDest.top = pixYUpMainSpace(ySpaceTarget);
                rectDest.bottom = rectDest.top + pixYSplitImgDst;
                rectSourceVariable.top = pixYSplitImgSrc;
                rectSourceVariable.bottom = Pix.resourceSide;
                drawSpaceContent(xSpaceToDraw, ySpaceToDraw, canvas, rectSourceVariable, rectDest, paint);
        }

        // Note : it should be possible to factorize this with the previous !
        private void drawBotFruitInSpawnSpace(int xSpaceTarget, int ySpaceTarget, Bitmap image, int pixYSplitImgSrc, int pixYSplitImgDst, Canvas canvas) {
                rectDest.left = pixXLeftMainSpace(xSpaceTarget);
                rectDest.right = rectDest.left + Pix.wMainSpace;
                rectDest.top = pixYUpMainSpace(ySpaceTarget);
                rectDest.bottom = rectDest.top + pixYSplitImgDst;
                rectSourceVariable.top = pixYSplitImgSrc;
                rectSourceVariable.bottom = Pix.resourceSide;
                canvas.drawBitmap(image, rectSourceVariable, rectDest, paint);
        }

        // Draw pix
        private int pixXLeftMainSpace(float x) {
                return Pix.xStartSpaces + (int)(x * Pix.wSpace);
        }
        private int pixYUpMainSpace(float y) {
                return Pix.yStartSpaces + (int)(y * Pix.hSpace);
        }
        private int pixXRightMainSpace(float x) {
                return pixXLeftMainSpace(x) + Pix.wMainSpace;
        }
        private int pixYDownMainSpace(float y) {
                return pixYUpMainSpace(y) + Pix.hMainSpace;
        }
        private int pixXLeftMainSpace(int x) {
                return Pix.xStartSpaces + (x * Pix.wSpace);
        }
        private int pixYUpMainSpace(int y) {
                return Pix.yStartSpaces + (y * Pix.hSpace);
        }
        private int pixXRightMainSpace(int x) {
                return pixXLeftMainSpace(x) + Pix.wMainSpace;
        }
        private int pixYDownMainSpace(int y) {
                return pixYUpMainSpace(y) + Pix.hMainSpace;
        }

// --------------------------------------------------------------------
// Tactile part

        private float pixMotionTouchEventX = 0f;
        private float pixMotionTouchEventY = 0f;

        private int selectedSpaceX = SPACE_UNDEFINED;
        private int selectedSpaceY = SPACE_UNDEFINED;
        private boolean alreadySwappedTouchMove = false;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
                pixMotionTouchEventX = event.getX();
                pixMotionTouchEventY = event.getY();
                if (gh.gth.isActive()) {
                        switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN: touchStart(); break;
                                case MotionEvent.ACTION_MOVE: touchMove(); break;
                                case MotionEvent.ACTION_UP: touchUp(); break;
                        }
                }
                return true; // Note : here is why true should be returned... Or it seems https://stackoverflow.com/questions/3756383/what-is-meaning-of-boolean-value-returned-from-an-event-handling-method-in-andro#:~:text=true%20means%20you%20consumed%20the,that%20though%3B%20see%20my%20answer.
        }

        private void touchStart() {
                alreadySwappedTouchMove = false;
                int currentSpaceX = pixXToSpaceX(pixMotionTouchEventX);
                int currentSpaceY = pixYToSpaceY(pixMotionTouchEventY);
                testActionSwap(currentSpaceX, currentSpaceY);
        }

        private void touchMove() {
                if (!alreadySwappedTouchMove) {
                        int currentSpaceX = pixXToSpaceX(pixMotionTouchEventX);
                        int currentSpaceY = pixYToSpaceY(pixMotionTouchEventY);
                        testActionSwap(currentSpaceX, currentSpaceY);
                }
        }

        private void touchUp() {

        }

        // True if two different spaces are being selected
        private void testActionSwap(int spaceX, int spaceY) {
                if (spaceX >= 0 && spaceX < Constants.FIELD_XLENGTH && spaceY >= 0 && spaceY < Constants.FIELD_YLENGTH) {
                        if (!gh.isClickable(spaceX, spaceY)) {
                                return;
                        }
                        if (isSpaceSelected()) {
                                if (spaceX == selectedSpaceX && spaceY == selectedSpaceY) {
                                        return;
                                } else if ( ((spaceY == selectedSpaceY) && ((spaceX == selectedSpaceX+1) || (spaceX == selectedSpaceX-1))) ||
                                        ((spaceX == selectedSpaceX) && ((spaceY == selectedSpaceY+1) || (spaceY == selectedSpaceY-1))) ) {
                                        gh.inputSwap(spaceX, spaceY, selectedSpaceX, selectedSpaceY);
                                        alreadySwappedTouchMove = true;
                                }
                                unselect();
                        } else {
                                selectedSpaceX = spaceX;
                                selectedSpaceY = spaceY;
                        }
                }
        }

// Tactile utilitary methods

        private int pixXToSpaceX(float pixX) {
                return (int)((pixX-Pix.wGap/2-Pix.xStartSpaces)/Pix.wSpace);
        }

        private int pixYToSpaceY(float pixY) {
                return (int) ((pixY-Pix.hGap/2-Pix.yStartSpaces)/Pix.hSpace);
        }

        private int spaceXToPixXLeft(int spaceX) {
                return Pix.xStartSpaces + Pix.wSpace * spaceX;
        }

        private int spaceXToPixXRight(int spaceX) {
                return Pix.xStartSpaces + Pix.wSpace * (spaceX + 1) - Pix.wGap;
        }

        private int spaceYToPixYUp(int spaceY) {
                return Pix.yStartSpaces + Pix.hSpace * spaceY;
        }

        private int spaceYToPixYDown(int spaceY) {
                return Pix.yStartSpaces + Pix.hSpace * (spaceY + 1) - Pix.wGap;
        }

        private void unselect() {
                selectedSpaceX = SPACE_UNDEFINED;
        }

        private boolean isSpaceSelected() {
                return (selectedSpaceX != SPACE_UNDEFINED);
        }

        private int getSpaceXSelected() {
                return selectedSpaceX;
        }

        private int getSpaceYSelected() {
                return selectedSpaceY;
        }

}*/