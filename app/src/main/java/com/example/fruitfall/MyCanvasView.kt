package com.example.fruitfall

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.roundToInt
import kotlin.random.Random

private const val SPACE_UNDEFINED = -1

class MyCanvasView(context: Context) : View(context) {
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

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

    private val gh = GameHandler(bitmapImages.size)

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
        val rectSource = Rect(0, 0, 64, 64)
        val rectDest = Rect(Pix.xStartSpaces, Pix.yStartSpaces, Pix.xStartSpaces+Pix.wMainSpace, Pix.yStartSpaces+Pix.hMainSpace)

        for (y in 0 until GameHandler.FIELD_YLENGTH) {
            for (x in 0 until GameHandler.FIELD_XLENGTH) {
                canvas.drawBitmap(bitmapImages[gh.getFruit(x, y)], rectSource, rectDest, paint);
                rectDest.left += Pix.wSpace
                rectDest.right += Pix.wSpace
            }
            rectDest.left = Pix.xStartSpaces;
            rectDest.right = Pix.xStartSpaces+Pix.wMainSpace;
            rectDest.top += Pix.hSpace
            rectDest.bottom += Pix.hSpace
        }

        if (isSpaceSelected()) {
            val frame = Rect(spaceXToPixXLeft(selectedSpaceX), spaceYToPixYUp(selectedSpaceY), spaceXToPixXRight(selectedSpaceX), spaceYToPixYDown(selectedSpaceY))
            canvas.drawRect(frame, paint)
        }
        invalidate() // At the end of draw... right ? Also, how many FPS ?
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        pixMotionTouchEventX = event.x
        pixMotionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
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
        if (isSpaceSelected()) {
            if (spaceX == selectedSpaceX && spaceY == selectedSpaceY) {
                return
            } else if ( ((spaceY == selectedSpaceY) && ((spaceX == selectedSpaceX+1) || (spaceX == selectedSpaceX-1))) ||
                    ((spaceX == selectedSpaceX) && ((spaceY == selectedSpaceY+1) || (spaceY == selectedSpaceY-1))) ) {
                gh.triggerSwap(spaceX, spaceY, selectedSpaceX, selectedSpaceY)
                alreadySwappedTouchMove = true
            }
            unselect();
        } else {
            selectedSpaceX = spaceX;
            selectedSpaceY = spaceY;
        }
    }

    private fun unselect() {
        selectedSpaceX = SPACE_UNDEFINED
    }

    private fun isSpaceSelected() : Boolean {
        return (selectedSpaceX != SPACE_UNDEFINED)
    }
}