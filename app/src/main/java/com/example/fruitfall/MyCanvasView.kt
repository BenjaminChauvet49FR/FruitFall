package com.example.fruitfall

import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.getColor
import com.example.fruitfall.animations.*
import com.example.fruitfall.introductions.Transition
import com.example.fruitfall.introductions.TransitionRandom
import com.example.fruitfall.introductions.TransitionUpward12121
import com.example.fruitfall.level.LevelData
import com.example.fruitfall.level.LevelManager
import kotlin.math.roundToInt

private const val SPACE_UNDEFINED = -1

class MyCanvasView(context: Context) : View(context) {
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private val colorBG = ResourcesCompat.getColor(resources, R.color.background, null)
    private val colorTextMain = ResourcesCompat.getColor(resources, R.color.textMain, null)
    private val colorFrameRect = ResourcesCompat.getColor(resources, R.color.frameRect, null)
    private val colorScoreFallSpace = ResourcesCompat.getColor(resources, R.color.scoreFallSpace, null)
    private val colorScoreDestructionSpace = ResourcesCompat.getColor(resources, R.color.scoreDestructionSpace, null)
    private val colorTitle = ResourcesCompat.getColor(resources, R.color.title, null)
    val colorAnimationLightning = ResourcesCompat.getColor(resources, R.color.animationLightning, null)
    val colorAnimationFire = ResourcesCompat.getColor(resources, R.color.animationFire, null)
    private val colorBGSpaces = arrayOf(ResourcesCompat.getColor(resources, R.color.spaceBG1, null), ResourcesCompat.getColor(resources, R.color.spaceBG2, null))
    private val colorBGSpaceFrame = ResourcesCompat.getColor(resources, R.color.spaceBGFrame, null)
    val colorLockDuration = ResourcesCompat.getColor(resources, R.color.colorLockDuration, null)


    private val rectSource = Rect(0, 0, Pix.resourceSide, Pix.resourceSide)
    private val rectSourceVariable = Rect(0, 0, Pix.resourceSide, Pix.resourceSide)
    private val rectDest = Rect(0, 0, 0, 0)
    private val rectFrame = Rect(0, 0, 0, 0)
    private val rectAnim = Rect(0, 0, 0, 0)

    private var pixMotionTouchEventX = 0f
    private var pixMotionTouchEventY = 0f
    private var selectedSpaceX = SPACE_UNDEFINED
    private var selectedSpaceY = SPACE_UNDEFINED
    private var introTransition : Transition = TransitionRandom()

    private var alreadySwappedTouchMove = false
    //private var touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var bitmapImages : Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.f1),
        BitmapFactory.decodeResource(resources, R.drawable.f2),
        BitmapFactory.decodeResource(resources, R.drawable.f3),
        BitmapFactory.decodeResource(resources, R.drawable.f4),
        BitmapFactory.decodeResource(resources, R.drawable.f5),
        BitmapFactory.decodeResource(resources, R.drawable.f6),
        BitmapFactory.decodeResource(resources, R.drawable.f7),
        BitmapFactory.decodeResource(resources, R.drawable.f8),
    )

    public fun getBitmapImages() :  Array<Bitmap> {
        return bitmapImages
    }

    // Search for a font on my computer : https://www.pcmag.com/how-to/how-to-manage-your-fonts-in-windows
    // Font handling : https://developer.android.com/guide/topics/ui/look-and-feel/fonts-in-xml#kotlin
    @RequiresApi(Build.VERSION_CODES.O)
    val mainFont = resources.getFont(R.font.georgia)
    @RequiresApi(Build.VERSION_CODES.O)
    val scoreFont = resources.getFont(R.font.trebuc)
    // Trouble met : "Call requires API level 26 (current min is 21): android.content.res.Resources#getFont"
    // https://stackoverflow.com/questions/20279084/how-android-set-custom-font-in-canvas
    // Got myself seducted by this : https://medium.com/programming-lite/using-custom-font-as-resources-in-android-app-6331477f8f57 so I dealt with the API.

    public fun getBitmapFruitToDrawFromIndex(index : Int) : Bitmap {
        return bitmapImages[gh.getRandomFruit(index)]
    }

    private fun drawSpaceContent(x : Int, y : Int, canvas : Canvas, rectSource : Rect, rectDest : Rect, paint : Paint) {
        gh.getSpace(x, y).paintStill(this, canvas, rectSource, rectDest, paint);
    }

    val bitmapImageLightActive = BitmapFactory.decodeResource(resources, R.drawable.light_up)
    val bitmapImageLightInactive = BitmapFactory.decodeResource(resources, R.drawable.light_down)
    val bitmapImageLightPenalty = BitmapFactory.decodeResource(resources, R.drawable.light_comeback)
    val bitmapImageFire= BitmapFactory.decodeResource(resources, R.drawable.on_fire)
    val bitmapImageLightH = BitmapFactory.decodeResource(resources, R.drawable.lightning_h)
    val bitmapImageLightV = BitmapFactory.decodeResource(resources, R.drawable.lightning_v)
    val bitmapImageSphereOmega = BitmapFactory.decodeResource(resources, R.drawable.sphere_omega)
    val bitmapImageLocking = BitmapFactory.decodeResource(resources, R.drawable.locking)

    private val gh = GameHandler()

    fun startLevel() {
        val ld : LevelData = LevelManager.levelLists[LevelManager.levelNumber]
        introTransition = ld.getTransition();
        gh.initializeGrid(ld)
    }

    // Set up the paint with which to draw.
    private val paint = Paint().apply { // Great details here : https://developer.android.com/codelabs/advanced-android-kotlin-training-canvas#4
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = Pix.backgroundFrame // default: Hairline-width (really thin)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle() // If not for this, memory leak !
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(colorBG)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        // Draw variables
        paint.setColor(colorTextMain);
        paint.setTextSize(Pix.hText);
        paint.setTypeface(mainFont)
        paint.setStyle(Paint.Style.FILL)// How to avoid awful outlined texts : https://stackoverflow.com/questions/31877417/android-draw-text-with-solid-background-onto-canvas-to-be-used-as-a-bitmap
        canvas.drawText("Score : " + gh.getScore(), Pix.xScore, Pix.yScore, paint);
        canvas.drawText("Temps : " + gh.gth.getTimeToDisplay(), Pix.xTime, Pix.yTime, paint);
        canvas.drawText("F : " + gh.getFruits(), Pix.xCommand1, Pix.yCommand1, paint);
        paint.setColor(colorTitle);
        canvas.drawText(gh.getTitle(), Pix.xTitle, Pix.yTitle, paint);

        // Draw the status light
        rectDest.left = Pix.xStartActiveLight
        rectDest.top = Pix.yStartActiveLight
        rectDest.right = rectDest.left + Pix.wActiveLight
        rectDest.bottom = rectDest.top + Pix.hActiveLight
        if (gh.gth.isActive) {
            canvas.drawBitmap(bitmapImageLightActive, rectSource, rectDest, paint)
        } else if (gh.gth.isActivePenalty) {
            canvas.drawBitmap(bitmapImageLightPenalty, rectSource, rectDest, paint)
        } else {
            canvas.drawBitmap(bitmapImageLightInactive, rectSource, rectDest, paint)
        }

        if (gh.gth.isInIntro()) {
            drawProgressiveCheckerboard(canvas)
        } else {
            drawAllSpaceContents(canvas)
            drawSpaceAnimations(canvas, gh.gth.animations1List)// TODO devrait être dessiné APRES les cases et AVANT les contenus dans l'idéal.
            drawFallingSpaces(canvas)
            drawCursor(canvas)
            drawSwap(canvas)
            drawSpaceAnimations(canvas, gh.gth.animations2List)
            drawScoresOnField(canvas)
        }
        invalidate() // At the end of draw... right ? Also, how many FPS ?

        //if (gh != null && gh.gth != null) { // TODO, passer ça à Java
        if (gh.gth != null) {
            gh.gth.step() // TODO Lui donner son propre processus parallèle ? Ou bien laisser dans onDraw ?
        }
    }

    // Draw scores on the field
    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawScoresOnField(canvas: Canvas) {
        if (gh.gth.shouldDrawScore()) {
            paint.setColor(colorScoreFallSpace);
            paint.setTextSize(Pix.hScoreSpace);
            paint.setTypeface(scoreFont)
            paint.setStyle(Paint.Style.FILL_AND_STROKE)
            for (coors in this.gh.contributingSpacesScoreFall) {
                val x = coors.x;
                val y = coors.y;
                canvas.drawText("+" + this.gh.scoreFallSpace(x, y),
                    Pix.pixXLeftMainSpace(x).toFloat(),
                    Pix.pixYUpMainSpace(y) + Pix.hScoreSpace,
                    paint);
            }
            paint.setColor(colorScoreDestructionSpace);
            paint.setTextSize(Pix.hScoreSpace);
            paint.setTypeface(scoreFont)
            paint.setStyle(Paint.Style.FILL_AND_STROKE)
            for (coors in this.gh.contributingSpacesScoreDestructionSpecial) {
                val x = coors.x;
                val y = coors.y;
                canvas.drawText("+" + this.gh.scoreDestructionSpecialSpace(x, y),
                    Pix.pixXLeftMainSpace(x).toFloat(),
                    Pix.pixYUpMainSpace(y) + Pix.hScoreSpace,
                    paint);
            }
        }
    }

    // Draw the currently swapping fruits
    private fun drawSwap(canvas: Canvas) {
        if (gh.gth.isInSwap) {
            val x1 = gh.gth.xSwap1
            val x2 = gh.gth.xSwap2
            val y1 = gh.gth.ySwap1
            val y2 = gh.gth.ySwap2
            val ratio = gh.gth.ratioToCompletionSwap()
            rectDest.left = Pix.pixXLeftMainSpace(x1 + ratio*(x2-x1))
            rectDest.right = rectDest.left + Pix.wMainSpace
            rectDest.top = Pix.pixYUpMainSpace((y1 + ratio*(y2-y1)) )
            rectDest.bottom = rectDest.top + Pix.hMainSpace
            this.drawSpaceContent(x1, y1, canvas, rectSource, rectDest, paint)
            rectDest.left = Pix.pixXLeftMainSpace(x2 + ratio*(x1-x2))
            rectDest.right = rectDest.left + Pix.wMainSpace
            rectDest.top = Pix.pixYUpMainSpace(y2 + ratio*(y1-y2))
            rectDest.bottom = rectDest.top + Pix.hMainSpace
            this.drawSpaceContent(x2, y2, canvas, rectSource, rectDest, paint)
        }
    }

    // Draw the cursor
    private fun drawCursor(canvas: Canvas) {
        if (isSpaceSelected()) {
            paint.strokeWidth = Pix.selectionFrame
            rectFrame.set(spaceXToPixXLeft(selectedSpaceX), spaceYToPixYUp(selectedSpaceY), spaceXToPixXRight(selectedSpaceX), spaceYToPixYDown(selectedSpaceY))
            paint.setStyle(Paint.Style.STROKE)
            paint.setColor(colorFrameRect)
            canvas.drawRect(rectFrame, paint)
        }
    }

    // Draw the falling elements
    private fun drawFallingSpaces(canvas: Canvas) {
        if (gh.gth.isInFall) {
            var xStartFall : Int
            var yStartFall : Int
            var outCoors : SpaceCoors?
            val ratioFall = gh.gth.ratioToCompletionFall()
            val antiRatioFall = 1 - ratioFall
            val pixYSplitImgSrc = (Pix.resourceSide * antiRatioFall).toInt()
            val pixYSplitImgDst = (Pix.hMainSpace * ratioFall).toInt()
            for (coors in gh.fallingEltsCoors) {
                xStartFall = coors.x
                yStartFall = coors.y
                if (gh.isNotDestroyedBeforeFall(xStartFall, yStartFall)) {
                    outCoors = gh.getDestination(xStartFall, yStartFall)
                    if (outCoors != null) { // FallElt in teleportation
                        drawTopImageInBotSpace(xStartFall, yStartFall, coors.x, coors.y, pixYSplitImgSrc, pixYSplitImgDst, canvas)
                        drawBotImageInTopSpace(xStartFall, yStartFall, outCoors.x, outCoors.y, pixYSplitImgSrc, pixYSplitImgDst, canvas)
                    } else { // FallElt without teleportation
                        rectDest.left = Pix.pixXLeftMainSpace(xStartFall)
                        rectDest.right = rectDest.left + Pix.wMainSpace
                        rectDest.top = Pix.pixYUpMainSpace(yStartFall + ratioFall)
                        rectDest.bottom = rectDest.top + Pix.hMainSpace
                        this.drawSpaceContent(xStartFall, yStartFall, canvas, rectSource, rectDest, paint)
                    }
                }
            }

            // Draw the spawning fruits
            var xEndFall : Int
            var yEndFall : Int
            for (coors in gh.spawningFruitsCoors) {
                xEndFall = coors.x
                yEndFall = coors.y
                drawBotFruitInSpawnSpace(xEndFall, yEndFall, getBitmapFruitToDrawFromIndex(gh.spawn(xEndFall, yEndFall)), pixYSplitImgSrc, pixYSplitImgDst, canvas)
            }
        }
    }

    private fun drawProgressiveCheckerboard(canvas : Canvas) {
        // Note : checkerboard spaces must match field areas
        paint.strokeWidth = Pix.backgroundFrame
        val pixStartLeft = Pix.xStartField
        val pixStartRight = pixStartLeft+Pix.wSpace-1
        var progressiveIntro : Float
        var desiredThreshold : Float
        rectDest.left = pixStartLeft
        rectDest.right = pixStartRight
        rectDest.top = Pix.yStartField
        rectDest.bottom = rectDest.top+Pix.hSpace-1
        val rectVar = Rect(0, 0, 0, 0)
        var ghostSquare : Int
        for (y in 0 until Constants.FIELD_YLENGTH) {
            for (x in 0 until Constants.FIELD_XLENGTH) {
                if (gh.isASpace(x, y)) {
                    desiredThreshold = introTransition.getProgressThreshold(x, y);
                    progressiveIntro = gh.gth.ratioProgressiveIntroSpaces(desiredThreshold)
                    ghostSquare = ((1-progressiveIntro)*Pix.ghostSquareMargin).toInt()
                    rectVar.set(rectDest.left - ghostSquare, rectDest.top - ghostSquare, rectDest.right + ghostSquare, rectDest.bottom + ghostSquare)
                    paint.setColor(colorBGSpaces[(x + y) % 2])
                    paint.setStyle(Paint.Style.FILL)
                    paint.alpha = (255.0*progressiveIntro).toInt() // Important : must be placed AFTER setColor otherwise it is returned to 255
                    canvas.drawRect(rectVar, paint)
                    paint.setColor(colorBGSpaceFrame)
                    paint.setStyle(Paint.Style.STROKE)
                    paint.alpha = (255.0*progressiveIntro).toInt()
                    canvas.drawRect(rectVar, paint)
                    if (gh.gth.shouldDrawSpaceContentProgessiveIntro(desiredThreshold)) {
                        this.drawSpaceContent(x, y, canvas, rectSource, rectDest, paint)
                    }
                }
                rectDest.left += Pix.wSpace
                rectDest.right += Pix.wSpace
            }
            rectDest.left = pixStartLeft
            rectDest.right = pixStartRight
            rectDest.top += Pix.hSpace
            rectDest.bottom += Pix.hSpace
        }
        paint.alpha = 255
    }

    // Draw the in-place elements or the on-space animations
    private fun drawAllSpaceContents(canvas : Canvas) {
        paint.strokeWidth = Pix.backgroundFrame
        var animation : SpaceAnimation?
        var ratio : Float
        val pixXStart1 = Pix.xStartSpaces
        val pixYStart1 = Pix.yStartSpaces
        val pixXStart2 = Pix.xStartField
        val pixYStart2 = Pix.yStartField
        rectDest.set(pixXStart1, pixYStart1, pixXStart1+Pix.wMainSpace, pixYStart1+Pix.hMainSpace)
        val rectDestSpace = Rect(pixXStart2, pixYStart2, pixXStart2+Pix.wSpace, pixYStart2+ Pix.hSpace)
        for (y in 0 until Constants.FIELD_YLENGTH) {
            for (x in 0 until Constants.FIELD_XLENGTH) {
                if (gh.isASpace(x, y)) {
                    // Warning C/P : from draw checkerboard
                    paint.setColor(colorBGSpaces[(x + y) % 2])
                    paint.setStyle(Paint.Style.FILL)
                    canvas.drawRect(rectDestSpace, paint)
                    paint.setColor(colorBGSpaceFrame)
                    paint.setStyle(Paint.Style.STROKE)
                    canvas.drawRect(rectDestSpace, paint)
                }
                if (gh.gth.hasStillSpace(x, y)) {
                    this.drawSpaceContent(x, y, canvas, rectSource, rectDest, paint)
                }
                rectDest.left += Pix.wSpace
                rectDest.right += Pix.wSpace
                rectDestSpace.left += Pix.wSpace
                rectDestSpace.right += Pix.wSpace
            }
            rectDest.left = pixXStart1
            rectDest.right = pixXStart1+Pix.wMainSpace
            rectDest.top += Pix.hSpace
            rectDest.bottom += Pix.hSpace
            rectDestSpace.left = pixXStart2
            rectDestSpace.right = pixXStart2+Pix.wSpace
            rectDestSpace.top += Pix.hSpace
            rectDestSpace.bottom += Pix.hSpace
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun drawSpaceAnimations(canvas : Canvas, spaceAnimations : List<SpaceAnimation>) {
        for (sa : SpaceAnimation in spaceAnimations) {
            if (sa.shouldBeDrawn()) {
                sa.draw(this, canvas, rectSource, rectDest, paint)
                sa.progress()
            }
        }
    }

    private fun drawTopImageInBotSpace(xSpaceToDraw : Int, ySpaceToDraw : Int, xSpaceTarget : Int, ySpaceTarget : Int, pixYSplitImgSrc : Int, pixYSplitImgDst : Int, canvas : Canvas) {
        rectDest.left = Pix.pixXLeftMainSpace(xSpaceTarget)
        rectDest.right = rectDest.left + Pix.wMainSpace
        rectDest.top = Pix.pixYUpMainSpace(ySpaceTarget) + pixYSplitImgDst
        rectDest.bottom = Pix.pixYUpMainSpace(ySpaceTarget + 1)
        rectSourceVariable.top = 0
        rectSourceVariable.bottom = pixYSplitImgSrc
        drawSpaceContent(xSpaceToDraw, ySpaceToDraw, canvas, rectSourceVariable, rectDest, paint)
    }

    private fun drawBotImageInTopSpace(xSpaceToDraw : Int, ySpaceToDraw : Int, xSpaceTarget : Int, ySpaceTarget : Int, pixYSplitImgSrc : Int, pixYSplitImgDst : Int, canvas : Canvas) {
        rectDest.left = Pix.pixXLeftMainSpace(xSpaceTarget)
        rectDest.right = rectDest.left + Pix.wMainSpace
        rectDest.top = Pix.pixYUpMainSpace(ySpaceTarget)
        rectDest.bottom = rectDest.top + pixYSplitImgDst
        rectSourceVariable.top = pixYSplitImgSrc
        rectSourceVariable.bottom = Pix.resourceSide
        drawSpaceContent(xSpaceToDraw, ySpaceToDraw, canvas, rectSourceVariable, rectDest, paint)
    }

    // Note : it should be possible to factorize this with the previous !
    private fun drawBotFruitInSpawnSpace(xSpaceTarget : Int, ySpaceTarget : Int, image : Bitmap, pixYSplitImgSrc : Int, pixYSplitImgDst : Int, canvas : Canvas) {
        rectDest.left = Pix.pixXLeftMainSpace(xSpaceTarget)
        rectDest.right = rectDest.left + Pix.wMainSpace
        rectDest.top = Pix.pixYUpMainSpace(ySpaceTarget)
        rectDest.bottom = rectDest.top + pixYSplitImgDst
        rectSourceVariable.top = pixYSplitImgSrc
        rectSourceVariable.bottom = Pix.resourceSide
        canvas.drawBitmap(image, rectSourceVariable, rectDest, paint)
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

    // Tactile pix
    private fun pixXToSpaceX(pixX : Float): Int {
        return ((pixX.toDouble()-Pix.wGap/2-Pix.xStartField)/Pix.wSpace).toInt()
    }

    private fun pixYToSpaceY(pixY : Float): Int {
        return ( (pixY.toDouble()-Pix.hGap/2-Pix.yStartField)/Pix.hSpace).toInt()
    }

    private fun spaceXToPixXLeft(spaceX : Int) : Int {
        return Pix.xStartField + Pix.wSpace * spaceX
    }

    private fun spaceXToPixXRight(spaceX : Int) : Int {
        return Pix.xStartField + Pix.wSpace * (spaceX + 1)
    }

    private fun spaceYToPixYUp(spaceY : Int) : Int {
        return Pix.yStartField + Pix.hSpace * spaceY
    }

    private fun spaceYToPixYDown(spaceY : Int) : Int {
        return Pix.yStartField + Pix.hSpace * (spaceY + 1)
    }


    // True if two different spaces are being selected
    private fun testActionSwap(spaceX : Int, spaceY : Int) {
        if (spaceX >= 0 && spaceX < Constants.FIELD_XLENGTH && spaceY >= 0 && spaceY < Constants.FIELD_YLENGTH) {
            if (!gh.isClickable(spaceX, spaceY)) {
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

    /*companion object {
        @kotlin.jvm.JvmField
        var bitmapImages: Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.f1),
        BitmapFactory.decodeResource(resources, R.drawable.f2),
        BitmapFactory.decodeResource(resources, R.drawable.f3),
        BitmapFactory.decodeResource(resources, R.drawable.f4),
        BitmapFactory.decodeResource(resources, R.drawable.f5),
        BitmapFactory.decodeResource(resources, R.drawable.f6),
        BitmapFactory.decodeResource(resources, R.drawable.f7),
        BitmapFactory.decodeResource(resources, R.drawable.f8),
        )
    }*/ // NOte :
        // Objectif : passer le paint aux méthodes de cases au lieu de faire des "instanceof" à répétition !
        // Difficultés rencontrées : j'ai essayé de transformer ça en Java. J'ai essayé avec "companion". Puis je me suis rendu compte que je pouvais passer cet objet en view en paramètre et c'est comme ça que j'ai continué mais ça n'a pas l'air d'être la bonne solution. Bon...
}