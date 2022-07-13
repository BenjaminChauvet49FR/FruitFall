package com.example.fruitfall

import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.example.fruitfall.animations.SpaceAnimation
import com.example.fruitfall.animations.SpaceAnimationFruitShrinking
import com.example.fruitfall.level.LevelManager
import kotlin.math.roundToInt

private const val SPACE_UNDEFINED = -1

class MyCanvasView(context: Context) : View(context) {
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
    private val rectSource = Rect(0, 0, 64, 64)
    private val rectDest = Rect(0, 0, 0, 0)
    private val rectFrame = Rect(0, 0, 0, 0)

    private var pixMotionTouchEventX = 0f
    private var pixMotionTouchEventY = 0f
    private var selectedSpaceX = SPACE_UNDEFINED
    private var selectedSpaceY = SPACE_UNDEFINED


    private var alreadySwappedTouchMove = false
    //private var touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
    
    private val bitmapImages : Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.f1),
        BitmapFactory.decodeResource(resources, R.drawable.f2),
        BitmapFactory.decodeResource(resources, R.drawable.f3),
        BitmapFactory.decodeResource(resources, R.drawable.f4),
        BitmapFactory.decodeResource(resources, R.drawable.f5),
        BitmapFactory.decodeResource(resources, R.drawable.f6),
        BitmapFactory.decodeResource(resources, R.drawable.f7),
        BitmapFactory.decodeResource(resources, R.drawable.f8),
    )

    // Search for a font on my computer : https://www.pcmag.com/how-to/how-to-manage-your-fonts-in-windows
    // Font handling : https://developer.android.com/guide/topics/ui/look-and-feel/fonts-in-xml#kotlin
    @RequiresApi(Build.VERSION_CODES.O)
    val mainFont = resources.getFont(R.font.georgia)
    @RequiresApi(Build.VERSION_CODES.O)
    val scoreFont = resources.getFont(R.font.trebuc)
    // Trouble met : "Call requires API level 26 (current min is 21): android.content.res.Resources#getFont"
    // https://stackoverflow.com/questions/20279084/how-android-set-custom-font-in-canvas
    // Got myself seducted by this : https://medium.com/programming-lite/using-custom-font-as-resources-in-android-app-6331477f8f57 so I dealt with the API.

    private fun getBitmapFruitToDrawFromIndex(index : Int) : Bitmap {
        return bitmapImages[gh.getRandomFruit(index)]
    }

    private fun getBitmapToDrawFromCoors(x : Int, y : Int) : Bitmap {
        return bitmapImages[gh.getRandomFruitFromCoors(x, y)]
    }

    private val bitmapImageLightActive = BitmapFactory.decodeResource(resources, R.drawable.light_up)
    private val bitmapImageLightInactive = BitmapFactory.decodeResource(resources, R.drawable.light_down)

    private val gh = GameHandler()

    fun startLevel() {
        gh.initializeGrid(LevelManager.levelLists[LevelManager.levelNumber])
    }

    // Set up the paint with which to draw.
    private val paint = Paint().apply { // Great details here : https://developer.android.com/codelabs/advanced-android-kotlin-training-canvas#4
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = Pix.selectionFrame // default: Hairline-width (really thin)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle() // If not for this, memory leak !
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        // Draw score
        paint.setColor(Colours.scoreMain);
        paint.setTextSize(Pix.hScore);
        paint.setTypeface(mainFont)
        paint.setStyle(Paint.Style.FILL)// How to avoid awful outlined texts : https://stackoverflow.com/questions/31877417/android-draw-text-with-solid-background-onto-canvas-to-be-used-as-a-bitmap
        canvas.drawText("Score : " + gh.getScore(), Pix.xScore, Pix.yScore, paint);

        var animation : SpaceAnimation?
        rectDest.set(Pix.xStartSpaces, Pix.yStartSpaces, Pix.xStartSpaces+Pix.wMainSpace, Pix.yStartSpaces+Pix.hMainSpace)
        // Draw the in-place fruits or the on-space animations
        for (y in 0 until Constants.FIELD_YLENGTH) {
            for (x in 0 until Constants.FIELD_XLENGTH) {
                if (gh.gth.hasStillFruit(x, y)) {
                    canvas.drawBitmap(getBitmapToDrawFromCoors(x, y), rectSource, rectDest, paint)
                } else {
                    animation = gh.gth.getAnimation(x, y)
                    if (animation != null && animation.shouldBeDrawn()) {
                        if (animation is SpaceAnimationFruitShrinking) {
                            canvas.save() // Note : it should be possible to make all rotations at once.
                            canvas.rotate(animation.ratio() * Constants.MAX_ANGLE_IN_DEGREES, rectDest.exactCenterX(), rectDest.exactCenterY()) // https://www.tabnine.com/code/java/methods/android.graphics.Canvas/rotate
                            canvas.drawBitmap(getBitmapFruitToDrawFromIndex(animation.imageFruit),
                                rectSource, rotatedShrinkedRect(rectDest, animation.ratio()), paint)
                            canvas.restore()
                        }
                        animation.progress()
                    }
                }
                rectDest.left += Pix.wSpace
                rectDest.right += Pix.wSpace
            }
            rectDest.left = Pix.xStartSpaces
            rectDest.right = Pix.xStartSpaces+Pix.wMainSpace
            rectDest.top += Pix.hSpace
            rectDest.bottom += Pix.hSpace
        }

        // Draw the falling fruits
        if (gh.gth.isInFall) {
            var xStartFall : Int
            var yStartFall : Int
            val ratioFall = gh.gth.ratioToCompletionFall()
            for (coors in gh.fallingFruitsCoors) {
                xStartFall = coors.x
                yStartFall = coors.y
                if (gh.hasFruit(xStartFall, yStartFall) && gh.isNotDestroyedBeforeFall(xStartFall, yStartFall)) {
                    rectDest.left = Pix.xStartSpaces + xStartFall * Pix.wSpace
                    rectDest.right = rectDest.left + Pix.wMainSpace
                    rectDest.top = (Pix.yStartSpaces + (yStartFall + ratioFall) * Pix.hSpace).toInt()
                    rectDest.bottom = rectDest.top + Pix.hMainSpace
                    canvas.drawBitmap(getBitmapToDrawFromCoors(xStartFall, yStartFall), rectSource, rectDest, paint)
                }
            }

            // Draw the spawning fruits
            var xEndFall : Int
            var yEndFall : Int
            for (coors in gh.spawningFruitsCoors) {
                xEndFall = coors.x
                yEndFall = coors.y
                rectDest.left = Pix.xStartSpaces + xEndFall * Pix.wSpace
                rectDest.right = rectDest.left + Pix.wMainSpace
                rectDest.top = (Pix.yStartSpaces + (yEndFall + ratioFall -1) * Pix.hSpace).toInt()
                rectDest.bottom = rectDest.top + Pix.hMainSpace
                canvas.drawBitmap(getBitmapFruitToDrawFromIndex(gh.spawn(xEndFall, yEndFall)), rectSource, rectDest, paint)
            }
        }


        // Draw the cursor
        if (isSpaceSelected()) {
            rectFrame.set(spaceXToPixXLeft(selectedSpaceX), spaceYToPixYUp(selectedSpaceY), spaceXToPixXRight(selectedSpaceX), spaceYToPixYDown(selectedSpaceY))
            paint.setStyle(Paint.Style.STROKE)
            paint.setColor(Colours.frameRect)
            canvas.drawRect(rectFrame, paint)
        }

        // Draw the currently swapping fruits
        if (gh.gth.isInSwap) {
            val x1 = gh.gth.xSwap1
            val x2 = gh.gth.xSwap2
            val y1 = gh.gth.ySwap1
            val y2 = gh.gth.ySwap2
            val ratio = gh.gth.ratioToCompletionSwap()
            // TODO créer des fonctions de pix...
            rectDest.left = (Pix.xStartSpaces + (x1 + ratio*(x2-x1)) * Pix.wSpace).toInt()
            rectDest.right = rectDest.left + Pix.wMainSpace
            rectDest.top = (Pix.yStartSpaces + (y1 + ratio*(y2-y1)) * Pix.hSpace).toInt()
            rectDest.bottom = rectDest.top + Pix.hMainSpace
            canvas.drawBitmap(getBitmapToDrawFromCoors(x1, y1), rectSource, rectDest, paint)
            rectDest.left = (Pix.xStartSpaces + (x2 + ratio*(x1-x2)) * Pix.wSpace).toInt()
            rectDest.right = rectDest.left + Pix.wMainSpace
            rectDest.top = (Pix.yStartSpaces + (y2 + ratio*(y1-y2)) * Pix.hSpace).toInt()
            rectDest.bottom = rectDest.top + Pix.hMainSpace
            canvas.drawBitmap(getBitmapToDrawFromCoors(x2, y2), rectSource, rectDest, paint)
        }




        // Draw the status light
        rectDest.left = Pix.xStartActiveLight
        rectDest.top = Pix.yStartActiveLight
        rectDest.right = rectDest.left + Pix.wActiveLight
        rectDest.bottom = rectDest.top + Pix.hActiveLight
        if (gh.gth.isActive) {
            canvas.drawBitmap(bitmapImageLightActive, rectSource, rectDest, paint)
        } else { // TODO Pour l'instant l'échange incorrect ne laisse pas de temps s'écouler, créer une nouvelle icône...
            canvas.drawBitmap(bitmapImageLightInactive, rectSource, rectDest, paint)
        }

        // Draw scores on the field
        if (gh.gth.shouldDrawScore()) {
            paint.setColor(Colours.frameRect);
            paint.setTextSize(Pix.hScoreSpace);
            paint.setTypeface(scoreFont)
            paint.setStyle(Paint.Style.FILL_AND_STROKE)
            for (coors in this.gh.contributingSpacesScore) {
                val x = coors.x;
                val y = coors.y;
                canvas.drawText("+" + this.gh.scoreSpace(x, y),
                    (Pix.xStartSpaces + x * Pix.wSpace).toFloat(),
                    (Pix.yStartSpaces + y * Pix.hSpace + Pix.hScoreSpace),
                    paint);
            }
        }

        invalidate() // At the end of draw... right ? Also, how many FPS ?

        //if (gh != null && gh.gth != null) { // TODO, passer ça à Java
        if (gh.gth != null) {
            gh.gth.step() // TODO Lui donner son propre processus parallèle ? Ou bien laisser dans onDraw ?
        }
    }

    private fun rotatedShrinkedRect(rectDest: Rect, ratio: Float): Rect {
        return Rect(
            (rectDest.left + Pix.wMainSpace*ratio/2).roundToInt(),
            (rectDest.top + Pix.hMainSpace*ratio/2).roundToInt(),
            (rectDest.right - Pix.wMainSpace*ratio/2).roundToInt(),
            (rectDest.bottom - Pix.hMainSpace*ratio/2).roundToInt()
        )

    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        pixMotionTouchEventX = event.x
        pixMotionTouchEventY = event.y
        if (gh.gth.isActive) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> touchStart()
                MotionEvent.ACTION_MOVE -> touchMove()
                MotionEvent.ACTION_UP -> touchUp()
            }
        }
        return true // Note : here is why true should be returned... Or it seems https://stackoverflow.com/questions/3756383/what-is-meaning-of-boolean-value-returned-from-an-event-handling-method-in-andro#:~:text=true%20means%20you%20consumed%20the,that%20though%3B%20see%20my%20answer.
    }

    private fun touchStart() {
        alreadySwappedTouchMove = false
        val currentSpaceX = pixXToSpaceX(pixMotionTouchEventX)
        val currentSpaceY = pixYToSpaceY(pixMotionTouchEventY)
        testActionSwap(currentSpaceX, currentSpaceY)

    }

    private fun touchMove() {
        if (!alreadySwappedTouchMove) {
            val currentSpaceX = pixXToSpaceX(pixMotionTouchEventX)
            val currentSpaceY = pixYToSpaceY(pixMotionTouchEventY)
            testActionSwap(currentSpaceX, currentSpaceY)
        }
    }

    private fun touchUp() {

    }

    private fun pixXToSpaceX(pixX : Float): Int {
        return ((pixX.toDouble()-Pix.wGap/2-Pix.xStartSpaces)/Pix.wSpace).toInt()
    }

    private fun pixYToSpaceY(pixY : Float): Int {
        return ( (pixY.toDouble()-Pix.hGap/2-Pix.yStartSpaces)/Pix.hSpace).toInt()
    }

    private fun spaceXToPixXLeft(spaceX : Int) : Int {
        return Pix.xStartSpaces + Pix.wSpace * spaceX
    }

    private fun spaceXToPixXRight(spaceX : Int) : Int {
        return Pix.xStartSpaces + Pix.wSpace * (spaceX + 1) - Pix.wGap
    }

    private fun spaceYToPixYUp(spaceY : Int) : Int {
        return Pix.yStartSpaces + Pix.hSpace * spaceY
    }

    private fun spaceYToPixYDown(spaceY : Int) : Int {
        return Pix.yStartSpaces + Pix.hSpace * (spaceY + 1) - Pix.wGap
    }


    // True if two different spaces are being selected
    private fun testActionSwap(spaceX : Int, spaceY : Int) {
        if (spaceX >= 0 && spaceX < Constants.FIELD_XLENGTH && spaceY >= 0 && spaceY < Constants.FIELD_YLENGTH) {
            if (!gh.hasSelectionnableFruit(spaceX, spaceY)) {
                return
            }
            if (isSpaceSelected()) {
                if (spaceX == selectedSpaceX && spaceY == selectedSpaceY) {
                    return
                } else if ( ((spaceY == selectedSpaceY) && ((spaceX == selectedSpaceX+1) || (spaceX == selectedSpaceX-1))) ||
                    ((spaceX == selectedSpaceX) && ((spaceY == selectedSpaceY+1) || (spaceY == selectedSpaceY-1))) ) {
                    gh.inputSwap(spaceX, spaceY, selectedSpaceX, selectedSpaceY)
                    alreadySwappedTouchMove = true
                }
                unselect()
            } else {
                selectedSpaceX = spaceX
                selectedSpaceY = spaceY
            }
        }
    }

    private fun unselect() {
        selectedSpaceX = SPACE_UNDEFINED
    }

    private fun isSpaceSelected() : Boolean {
        return (selectedSpaceX != SPACE_UNDEFINED)
    }
}