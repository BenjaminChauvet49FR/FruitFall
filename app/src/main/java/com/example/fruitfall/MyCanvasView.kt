package com.example.fruitfall

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.example.fruitfall.animations.SpaceAnimation
import com.example.fruitfall.introductions.Transition
import com.example.fruitfall.introductions.TransitionRandom
import com.example.fruitfall.level.LevelData
import com.example.fruitfall.level.LevelManager
import com.example.fruitfall.spaces.SpaceFiller

private const val SPACE_UNDEFINED = -1

class MyCanvasView(context: Context) : View(context) {
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private val colorBG = ResourcesCompat.getColor(resources, R.color.background, null)
    private val colorTextMain = ResourcesCompat.getColor(resources, R.color.textMain, null)
    private val colorFrameRect = ResourcesCompat.getColor(resources, R.color.frameRect, null)
    private val colorScoreFallSpace = ResourcesCompat.getColor(resources, R.color.scoreFallSpace, null)
    private val colorBasketsSpaceFG = ResourcesCompat.getColor(resources, R.color.scoreBasketsSpaceFG, null)
    private val colorNutDropsBG = ResourcesCompat.getColor(resources, R.color.scoreNutDropsSpaceBG, null)
    private val colorBasketsSpaceBG = ResourcesCompat.getColor(resources, R.color.scoreBasketsSpaceBG, null)
    private val colorEntranceWarps = ResourcesCompat.getColor(resources, R.color.entranceWarps, null)
    private val colorExitWarps = ResourcesCompat.getColor(resources, R.color.exitWarps, null)

    private val colorScoreDestructionSpace = ResourcesCompat.getColor(resources, R.color.scoreDestructionSpace, null)
    private val colorTitle = ResourcesCompat.getColor(resources, R.color.title, null)
    val colorAnimationLightning = ResourcesCompat.getColor(resources, R.color.animationLightning, null)
    val colorAnimationFire = ResourcesCompat.getColor(resources, R.color.animationFire, null)
    val colorDotStickyBomb = ResourcesCompat.getColor(resources, R.color.colorDotStickyBomb, null)
    val colorVeilStickyBomb = ResourcesCompat.getColor(resources, R.color.colorVeilStickyBomb, null)
    val colorVeilStickyBombEmpty = ResourcesCompat.getColor(resources, R.color.colorVeilStickyBombEmpty, null)  //TODO et si on pouvait se passer des setAlpha en mettant directment la transparence dans les couleurs ? (Bon, j'ai essayé, c'est compliqué, je me remettrai plus tard)
    private val colorBGSpaces = arrayOf(ResourcesCompat.getColor(resources, R.color.spaceBG1, null), ResourcesCompat.getColor(resources, R.color.spaceBG2, null))
    private val colorBGSpaceFrame = ResourcesCompat.getColor(resources, R.color.spaceBGFrame, null)
    val colorLockDuration = ResourcesCompat.getColor(resources, R.color.colorLockDuration, null)
    val colorLockHostage = ResourcesCompat.getColor(resources, R.color.colorLockHostage, null)


    private val rectSource = Rect(0, 0, Pix.resourceSide, Pix.resourceSide)
    private val rectSourceVariable = Rect(0, 0, Pix.resourceSide, Pix.resourceSide)
    private val rectDest = Rect(0, 0, 0, 0)
    private val rectDestMini = Rect(0, 0, 0, 0)
    private val rectPips = RectF(0.0.toFloat(), 0.0.toFloat(), 0.0.toFloat(), 0.0.toFloat())
    private val rectFrame = Rect(0, 0, 0, 0)

    private var pixMotionTouchEventX = 0f
    private var pixMotionTouchEventY = 0f
    private var selectedSpaceX = SPACE_UNDEFINED
    private var selectedSpaceY = SPACE_UNDEFINED
    private var introTransition : Transition = TransitionRandom()

    private var alreadySwappedTouchMove = false
    //private var touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var bitmapFruits : Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.f_orange),
        BitmapFactory.decodeResource(resources, R.drawable.f_pomme),
        BitmapFactory.decodeResource(resources, R.drawable.f_banane),
        BitmapFactory.decodeResource(resources, R.drawable.f_kiwi),
        BitmapFactory.decodeResource(resources, R.drawable.f_fraise),
        BitmapFactory.decodeResource(resources, R.drawable.f_raisin),
        BitmapFactory.decodeResource(resources, R.drawable.f_myrtille),
        BitmapFactory.decodeResource(resources, R.drawable.f_mure),
    )

    fun getBitmapImages() :  Array<Bitmap> {
        return bitmapFruits
    }

    // https://stackoverflow.com/questions/10413659/how-to-resize-image-in-android
    private fun makeResizedImage(drawRessource : Int, pixNewSize : Int) : Bitmap {
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, drawRessource), pixNewSize, pixNewSize, true)
    }

    val bitmapImageLightActive : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.light_up)
    val bitmapImageLightInactive : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.light_down)
    val bitmapImageLightPenalty : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.light_comeback)
    val bitmapAllFruits : Bitmap = makeResizedImage(R.drawable.all_fruits, Pix.squareSide)
    val bitmapImageFire = makeResizedImage(R.drawable.on_fire, Pix.squareSide)
    val bitmapImageLightH = makeResizedImage(R.drawable.lightning_h, Pix.squareSide)
    val bitmapImageLightV = makeResizedImage(R.drawable.lightning_v, Pix.squareSide)
    val bitmapImageSphereOmega : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.sphere_omega)
    val bitmapImageLocking : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.locking) // TODO celui là a besoin d'uniformisations
    val bitmapImageBreakableBlock : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.crushable)
    val bitmapImageNut : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.f_noix)
    val bitmapImageHostageLock : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.hostage_locking)
    val bitmapImageStickyBomb = makeResizedImage(R.drawable.sticky_bomb, Pix.squareSide)
    val bitmapOrderAny : Bitmap = makeResizedImage(R.drawable.losange_any, Pix.squareSide)
    val bitmapOrderMix : Bitmap = makeResizedImage(R.drawable.losange_mix, Pix.squareSide)
    val bitmapOrderSimple : Bitmap = makeResizedImage(R.drawable.losange_simple, Pix.squareSide)
    val bitmapOrderSpecial : Bitmap = makeResizedImage(R.drawable.losange_special, Pix.squareSide)
    val bitmapOrderWild : Bitmap = makeResizedImage(R.drawable.losange_wild, Pix.squareSide)
    val bitmapArrowSpawn : Bitmap = makeResizedImage(R.drawable.arrow_spawn, Pix.pauseFieldInfoSide) // TODO flèche mal dimensionnée...

    // Search for a font on my computer : https://www.pcmag.com/how-to/how-to-manage-your-fonts-in-windows
    // Font handling : https://developer.android.com/guide/topics/ui/look-and-feel/fonts-in-xml#kotlin
    @RequiresApi(Build.VERSION_CODES.O)
    val mainFont = resources.getFont(R.font.georgia)
    @RequiresApi(Build.VERSION_CODES.O)
    val scoreFont = resources.getFont(R.font.trebuc)
    // Trouble met : "Call requires API level 26 (current min is 21): android.content.res.Resources#getFont"
    // https://stackoverflow.com/questions/20279084/how-android-set-custom-font-in-canvas
    // Got myself seducted by this : https://medium.com/programming-lite/using-custom-font-as-resources-in-android-app-6331477f8f57 so I dealt with the API.

    fun getBitmapFruitToDrawFromIndex(index : Int) : Bitmap {
        return bitmapFruits[gh.getSpriteIdFromFieldIndex(index)]
    }

    private fun drawSpaceContent(x : Int, y : Int, canvas : Canvas, rectSource : Rect, rectDest : Rect, paint : Paint) {
        gh.getSpace(x, y).paintStill(this, canvas, rectSource, rectDest, paint)
    }

    private val gh = GameHandler()

    fun startLevel() {
        val ld : LevelData = LevelManager.levelLists[LevelManager.levelNumber]
        unselect() // TODO surpris que les coordonnées du curseur soient gérées ici...
        gh.start(ld)
        introTransition = ld.transition // Should be called AFTER gh.start because of the nature of deploy
        gh.gth.setRelativeTransitionLength(introTransition.relativeTransitionLength())
    }

    fun setTolerance() {
        gh.setTolerance()
    }

    fun switchFallSpeed() {
        gh.gth.switchFallSpeed()
    }

    fun setPause() {
        gh.gth.switchPause()
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
        paint.color = colorTextMain
        paint.textSize = Pix.hText
        paint.typeface = mainFont
        paint.style = Paint.Style.FILL// How to avoid awful outlined texts : https://stackoverflow.com/questions/31877417/android-draw-text-with-solid-background-onto-canvas-to-be-used-as-a-bitmap
        paint.textAlign = Paint.Align.LEFT
        // Convention for text drawing is easy : draw on fields = center, draw outside fields aligned left !
        canvas.drawText("Score : " + gh.score, Pix.xScore, Pix.yScore, paint)
        canvas.drawText("Temps : " + gh.gth.timeToDisplay + " (" + gh.elapsedMoves + ")", Pix.xTime, Pix.yTime, paint)
        drawMission(canvas)
        paint.color = colorTitle
        canvas.drawText(gh.titleAndInfos, Pix.xTitle, Pix.yTitle, paint)

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

        paint.textAlign = Paint.Align.CENTER
        if (gh.gth.isInIntro) {
            drawProgressiveCheckerboard(canvas)
        } else {
            drawAllSpaceContents(canvas)
            if (!gh.gth.pause) {
                drawSpaceAnimations(canvas, gh.gth.animations1List)// TODO devrait être dessiné APRES les cases et AVANT les contenus dans l'idéal.
                drawFallingSpaces(canvas)
                drawCursor(canvas)
                drawSwap(canvas)
                drawSpaceAnimations(canvas, gh.gth.animations2List)
            }
            if (gh.goalKind == GameEnums.GOAL_KIND.BASKETS) {
                drawBaskets(canvas)
            }
            if (gh.goalKind == GameEnums.GOAL_KIND.NUTS) {
                drawNutDrops(canvas)
            }
            if (!gh.gth.pause) {
                drawScoresOnField(canvas)
            }
        }
        invalidate() // At the end of draw... right ? Also, how many FPS ?

        //if (gh != null && gh.gth != null) { // TODO, passer ça à Java
        if (gh.gth != null) {
            gh.gth.step() // TODO Lui donner son propre processus parallèle ? Ou bien laisser dans onDraw ?
        }
    }

    private fun drawMission(canvas : Canvas) {
        // Credits for the switch : https://kotlinlang.org/docs/control-flow.html#when-expression
        when (gh.goalKind) {
            GameEnums.GOAL_KIND.BASKETS -> {
                canvas.drawText("Paniers : " + gh.basketsCount, Pix.xCommands, Pix.yCommandsText, paint)
            }
            GameEnums.GOAL_KIND.NUTS -> {
                canvas.drawText("Noix : " + gh.nutsHealthCount, Pix.xNuts, Pix.yNutText, paint)
                for (i in 0 until gh.listWaitingNutData.size) {
                    rectDest.left = Pix.xNutWaitingPicture(i).toInt()
                    rectDest.top = Pix.yNutWaitingPicture.toInt()
                    rectDest.right = rectDest.left + Pix.resourceLittleSide
                    rectDest.bottom = rectDest.top + Pix.resourceLittleSide
                    canvas.drawBitmap(bitmapImageNut, rectSource, rectDest, paint)
                    canvas.drawText(gh.listWaitingNutData.get(i).delay.toString(), Pix.xNutWaitingText(i), Pix.yNutText, paint)
                }
            }
            else -> {
                var pixWRectShrink : Int
                var pixHRectShrink : Int
                var backBitmap : Bitmap
                var frontBitmap : Bitmap
                var frontBitmap2 : Bitmap?
                for (i in 0 until gh.numberofMissions) {
                    frontBitmap2 = null
                    when (gh.kindsOfOrder[i].superKind) {
                        GameEnums.ORDER_SUPER_KIND.SIMPLE -> {
                            backBitmap = bitmapOrderSimple
                            frontBitmap = bitmapFruits[gh.getSpriteIdFromFieldIndex(gh.kindsOfOrder[i].fruitId)]
                        }
                        GameEnums.ORDER_SUPER_KIND.ANY -> {
                            backBitmap = bitmapOrderAny
                            frontBitmap = bitmapAllFruits
                        }
                        GameEnums.ORDER_SUPER_KIND.SPECIAL -> {
                            backBitmap = bitmapOrderSpecial
                            when (gh.kindsOfOrder[i]) {
                                GameEnums.ORDER_KIND.FIRE -> frontBitmap = bitmapImageFire
                                GameEnums.ORDER_KIND.OMEGA -> frontBitmap = bitmapImageSphereOmega
                                GameEnums.ORDER_KIND.LIGHTNING -> frontBitmap = bitmapImageLightH
                                else -> frontBitmap = bitmapImageLightInactive // Should not happen
                            }
                        }
                        GameEnums.ORDER_SUPER_KIND.WILD_MIX -> {
                            backBitmap = bitmapOrderWild
                            when (gh.kindsOfOrder[i]) {
                                GameEnums.ORDER_KIND.FIRE_WILD -> frontBitmap = bitmapImageFire
                                GameEnums.ORDER_KIND.OMEGA_WILD -> frontBitmap = bitmapImageSphereOmega
                                GameEnums.ORDER_KIND.LIGHTNING_WILD -> frontBitmap = bitmapImageLightH
                                else -> frontBitmap = bitmapImageLightInactive // Should not happen
                            }
                        }
                        GameEnums.ORDER_SUPER_KIND.MIX -> {
                            backBitmap = bitmapOrderMix
                            when (gh.kindsOfOrder[i]) {
                                GameEnums.ORDER_KIND.FIRE_FIRE -> {
                                    frontBitmap = bitmapImageFire
                                    frontBitmap2 = bitmapImageFire
                                }
                                GameEnums.ORDER_KIND.LIGHTNING_FIRE -> {
                                    frontBitmap = bitmapImageLightH
                                    frontBitmap2 = bitmapImageFire
                                }
                                GameEnums.ORDER_KIND.LIGHTNING_LIGHTNING -> {
                                    frontBitmap = bitmapImageLightH
                                    frontBitmap2 = bitmapImageLightH
                                }
                                GameEnums.ORDER_KIND.OMEGA_FIRE -> {
                                    frontBitmap = bitmapImageSphereOmega
                                    frontBitmap2 = bitmapImageFire
                                }
                                GameEnums.ORDER_KIND.LIGHTNING_OMEGA -> {
                                    frontBitmap = bitmapImageSphereOmega
                                    frontBitmap2 = bitmapImageLightH
                                }
                                GameEnums.ORDER_KIND.OMEGA_OMEGA -> {
                                    frontBitmap = bitmapImageSphereOmega
                                    frontBitmap2 = bitmapImageSphereOmega
                                }
                                else -> frontBitmap = bitmapAllFruits // Should not happen
                            }
                        }
                        else -> {
                            backBitmap = bitmapOrderAny
                            frontBitmap = bitmapAllFruits // Should not happen
                        }
                    }
                    rectDest.left = Pix.xCommandsKind(i).toInt()
                    rectDest.top = Pix.yCommandsKind.toInt()
                    rectDest.right = rectDest.left + Pix.resourceLittleSide
                    rectDest.bottom = rectDest.top + Pix.resourceLittleSide
                    canvas.drawBitmap(backBitmap, rectSource, rectDest, paint)
                    pixWRectShrink = (rectDest.width()*0.2).toInt()
                    pixHRectShrink = (rectDest.height()*0.2).toInt()
                    rectDest.left += pixWRectShrink
                    rectDest.top += pixHRectShrink
                    rectDest.right -= pixWRectShrink
                    rectDest.bottom -= pixHRectShrink
                    canvas.drawBitmap(frontBitmap, rectSource, rectDest, paint)
                    if (frontBitmap2 != null) {
                        rectDest.left -= 6
                        rectDest.top += 6
                        rectDest.right -= 6
                        rectDest.bottom += 6
                        canvas.drawBitmap(frontBitmap2, rectSource, rectDest, paint)
                    }
                    canvas.drawText(gh.amountsOrder[i].toString(), Pix.xCommandsAmount(i), Pix.yCommandsText, paint)
                }
            }
        }
    }

    private fun drawNutDrops(canvas: Canvas) {
        val formerAlpha = paint.alpha
        paint.color = colorNutDropsBG
        paint.alpha = 127
        paint.style = Paint.Style.FILL_AND_STROKE
        for (coors in this.gh.coorsForNutDrops) {
            val x = coors.x
            val y = coors.y
            rectDest.set(Pix.xLeftMainSpace(x), Pix.yUpMainSpace(y), Pix.xRightMainSpace(x), Pix.yDownMainSpace(y))
            canvas.drawRect(rectDest, paint)
        }
        paint.alpha = formerAlpha
    }

    private fun drawBaskets(canvas: Canvas) {
        var baskets : Int
        var nbBasketsDown : Int // Number of baskets in the bottom part of the space
        var nbBasketsUp : Int // The remaining baskets
        val pixXLeftStart = Pix.xLeftMainSpace(0) + Pix.basketSpaceBGMargin
        val pixXRightStart = pixXLeftStart + Pix.basketSpaceSide
        paint.style = Paint.Style.FILL_AND_STROKE
        val formerAlpha = paint.alpha
        rectDest.left = pixXLeftStart
        rectDest.right = pixXRightStart
        rectDest.top = Pix.yUpMainSpace(0)
        rectDest.bottom = rectDest.top + Pix.basketSpaceSide
        var pixXPip : Float
        var pixYPip : Float
        for (y in 0 until Constants.FIELD_YLENGTH) {
            for (x in 0 until Constants.FIELD_XLENGTH) {
                baskets = gh.getBaskets(x, y)
                if (baskets > 0) {
                    paint.color = colorBasketsSpaceBG
                    paint.alpha = 127
                    canvas.drawRect(rectDest, paint)
                    paint.color = colorBasketsSpaceFG
                    paint.alpha = 127
                    nbBasketsDown = baskets/2
                    nbBasketsUp = baskets-nbBasketsDown
                    pixYPip = Pix.yUpMainSpace(y) + Pix.pipPadding
                    for (i in 0 until nbBasketsUp) {
                        pixXPip = Pix.xCenter(x) - Pix.pipSide/2.toFloat() + centeringProgression(nbBasketsUp, i)*Pix.pipSide*1.5.toFloat()
                        rectPips.set(pixXPip, pixYPip, pixXPip+Pix.pipSide, pixYPip+Pix.pipSide)
                        canvas.drawOval(rectPips, paint)
                    }
                    pixYPip = Pix.yDownMainSpace(y) - Pix.pipPadding - Pix.pipSide
                    for (i in 0 until nbBasketsDown) { // Yet another affine translating
                        pixXPip = Pix.xCenter(x) - Pix.pipSide/2.toFloat() + centeringProgression(nbBasketsDown, i)*Pix.pipSide*1.5.toFloat()
                        rectPips.set(pixXPip, pixYPip, pixXPip+Pix.pipSide, pixYPip+Pix.pipSide)
                        canvas.drawOval(rectPips, paint)
                    }
                }
                rectDest.left += Pix.wSpace
                rectDest.right += Pix.wSpace
            }
            rectDest.top += Pix.hSpace
            rectDest.bottom += Pix.hSpace
            rectDest.left = pixXLeftStart
            rectDest.right = pixXRightStart
        }
        paint.alpha = formerAlpha // Useless if colour is changed after the last alpha set
    }

    // Draw scores on the field
    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawScoresOnField(canvas: Canvas) {
        if (gh.gth.shouldDrawScore()) {
            paint.color = colorScoreFallSpace
            paint.textSize = Pix.hScoreSpace
            paint.typeface = scoreFont
            paint.style = Paint.Style.FILL_AND_STROKE
            for (coors in this.gh.contributingSpacesScoreFall) {
                val x = coors.x
                val y = coors.y
                canvas.drawText("+" + this.gh.scoreFallSpace(x, y),
                    Pix.xLeftMainSpace(x).toFloat(),
                    Pix.yUpMainSpace(y) + Pix.hScoreSpace,
                    paint)
            }
            paint.color = colorScoreDestructionSpace
            paint.textSize = Pix.hScoreSpace
            paint.typeface = scoreFont
            paint.style = Paint.Style.FILL_AND_STROKE
            for (coors in this.gh.contributingSpacesScoreDestructionSpecial) {
                val x = coors.x
                val y = coors.y
                canvas.drawText("+" + this.gh.scoreDestructionSpecialSpace(x, y),
                    Pix.xLeftMainSpace(x).toFloat(),
                    Pix.yUpMainSpace(y) + Pix.hScoreSpace,
                    paint)
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
            rectDest.left = Pix.xLeftMainSpace(x1 + ratio*(x2-x1))
            rectDest.right = rectDest.left + Pix.wMainSpace
            rectDest.top = Pix.yUpMainSpace((y1 + ratio*(y2-y1)) )
            rectDest.bottom = rectDest.top + Pix.hMainSpace
            this.drawSpaceContent(x1, y1, canvas, rectSource, rectDest, paint)
            rectDest.left = Pix.xLeftMainSpace(x2 + ratio*(x1-x2))
            rectDest.right = rectDest.left + Pix.wMainSpace
            rectDest.top = Pix.yUpMainSpace(y2 + ratio*(y1-y2))
            rectDest.bottom = rectDest.top + Pix.hMainSpace
            this.drawSpaceContent(x2, y2, canvas, rectSource, rectDest, paint)
        }
    }

    // Draw the cursor
    private fun drawCursor(canvas: Canvas) {
        if (isSpaceSelected()) {
            val formerWidth = paint.strokeWidth
            paint.strokeWidth = Pix.selectionFrame
            rectFrame.set(spaceXToPixXLeft(selectedSpaceX), spaceYToPixYUp(selectedSpaceY), spaceXToPixXRight(selectedSpaceX), spaceYToPixYDown(selectedSpaceY))
            paint.style = Paint.Style.STROKE
            paint.color = colorFrameRect
            canvas.drawRect(rectFrame, paint)
            paint.strokeWidth = formerWidth
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
                        rectDest.left = Pix.xLeftMainSpace(xStartFall)
                        rectDest.right = rectDest.left + Pix.wMainSpace
                        rectDest.top = Pix.yUpMainSpace(yStartFall + ratioFall)
                        rectDest.bottom = rectDest.top + Pix.hMainSpace
                        this.drawSpaceContent(xStartFall, yStartFall, canvas, rectSource, rectDest, paint)
                    }
                }
            }
            // Draw the diagonal squeezing fruits
            for (coorsDest in gh.coorsElementsGettingFromUpperLeft) {
                rectDest.left = Pix.xLeftMainSpace(coorsDest.x - 1 + ratioFall)
                rectDest.right = rectDest.left + Pix.wMainSpace
                rectDest.top = Pix.yUpMainSpace(coorsDest.y - 1 + ratioFall)
                rectDest.bottom = rectDest.top + Pix.hMainSpace
                this.drawSpaceContent(coorsDest.x-1, coorsDest.y-1, canvas, rectSource, rectDest, paint)
            }
            for (coorsDest in gh.coorsElementsGettingFromUpperRight) {
                rectDest.left = Pix.xLeftMainSpace(coorsDest.x + 1 - ratioFall)
                rectDest.right = rectDest.left + Pix.wMainSpace
                rectDest.top = Pix.yUpMainSpace(coorsDest.y - 1 + ratioFall)
                rectDest.bottom = rectDest.top + Pix.hMainSpace
                this.drawSpaceContent(coorsDest.x+1, coorsDest.y-1, canvas, rectSource, rectDest, paint)
            }

            // Draw the spawning fruits
            var xEndFall : Int
            var yEndFall : Int
            for (coors in gh.spawningFruitsCoors) {
                xEndFall = coors.x
                yEndFall = coors.y
                drawBotFruitInSpawnSpace(xEndFall, yEndFall, gh.spawn(xEndFall, yEndFall), pixYSplitImgSrc, pixYSplitImgDst, canvas)
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
                    desiredThreshold = introTransition.getProgressThreshold(x, y)
                    progressiveIntro = gh.gth.ratioProgressiveIntroSpaces(desiredThreshold)
                    ghostSquare = ((1-progressiveIntro)*Pix.ghostSquareMargin).toInt()
                    rectVar.set(rectDest.left - ghostSquare, rectDest.top - ghostSquare, rectDest.right + ghostSquare, rectDest.bottom + ghostSquare)
                    paint.color = colorBGSpaces[(x + y) % 2]
                    paint.style = Paint.Style.FILL
                    paint.alpha = (255.0*progressiveIntro).toInt() // Important : must be placed AFTER setColor otherwise it is returned to 255
                    canvas.drawRect(rectVar, paint)
                    paint.color = colorBGSpaceFrame
                    paint.style = Paint.Style.STROKE
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
    // Draws different things depending on whether the game is paused or not
    private fun drawAllSpaceContents(canvas : Canvas) {
        paint.strokeWidth = Pix.backgroundFrame
        var outCoors : SpaceCoors?
        val pixXStart1 = Pix.xStartSpaces
        val pixYStart1 = Pix.yStartSpaces
        val pixXStart2 = Pix.xStartField
        val pixYStart2 = Pix.yStartField
        val outsideTeleportersCoors = ArrayList<SpaceCoors>()
        rectDest.set(pixXStart1, pixYStart1, pixXStart1+Pix.wMainSpace, pixYStart1+Pix.hMainSpace)
        val rectDestSpace = Rect(pixXStart2, pixYStart2, pixXStart2+Pix.wSpace, pixYStart2+ Pix.hSpace)
        for (y in 0 until Constants.FIELD_YLENGTH) {
            for (x in 0 until Constants.FIELD_XLENGTH) {
                if (gh.isASpace(x, y)) {
                    // Warning C/P : from draw checkerboard
                    paint.color = colorBGSpaces[(x + y) % 2]
                    paint.style = Paint.Style.FILL
                    canvas.drawRect(rectDestSpace, paint)
                    paint.color = colorBGSpaceFrame
                    paint.style = Paint.Style.STROKE
                    canvas.drawRect(rectDestSpace, paint)
                }
                if (!gh.gth.pause) {
                    if (gh.gth.hasStillSpace(x, y)) {
                        this.drawSpaceContent(x, y, canvas, rectSource, rectDest, paint)
                    }
                } else {
                    if (gh.shouldSpawnFruit(x, y)) {
                        rectDestMini.set(Pix.xCenter(x)-bitmapArrowSpawn.width/2, Pix.yUpMainSpace(y)-bitmapArrowSpawn.height/2, Pix.xCenter(x)+bitmapArrowSpawn.width/2, Pix.yUpMainSpace(y)+bitmapArrowSpawn.height/2)
                        canvas.drawBitmap(bitmapArrowSpawn, rectSource, rectDestMini, paint)
                    }
                    outCoors = gh.getDestination(x, y)
                    if (outCoors != null) {
                        paint.color = colorEntranceWarps
                        paint.textSize = Pix.hTextTeleporters
                        paint.style = Paint.Style.FILL_AND_STROKE
                        outsideTeleportersCoors.add( SpaceCoors(outCoors.x, outCoors.y))
                        canvas.drawText(outsideTeleportersCoors.size.toString(),
                            Pix.xCenter(x).toFloat(),
                            Pix.yDownMainSpace(y).toFloat(), // TODO Int, float, double... un jour il faudra faire du ménage
                            paint)
                    }
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

        if (gh.gth.pause) {
            paint.color = colorExitWarps
            // If not for this side trick, a teleporter exit could be overdrawn easily by spaces, which are drawn along as contents in reading order
            for (i in 0 until outsideTeleportersCoors.size) {
                canvas.drawText((i+1).toString(),
                    Pix.xCenter(outsideTeleportersCoors[i].x).toFloat(),
                    Pix.yUpMainSpace(outsideTeleportersCoors[i].y).toFloat(),
                    paint)
            } // TODO existe-il un moyen intelligent d'organiser la numérotation des téléporteurs ?
            // Dans un niveau à quadrants pour l'instant les fruits entrent dans l'ordre dans la paire 1, puis la 9 puis la 5... ça n'a pas tellement de sens ! Si ce n'est le respect du simple ordre lexicographique.
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
        rectDest.left = Pix.xLeftMainSpace(xSpaceTarget)
        rectDest.right = rectDest.left + Pix.wMainSpace
        rectDest.top = Pix.yUpMainSpace(ySpaceTarget) + pixYSplitImgDst
        rectDest.bottom = Pix.yUpMainSpace(ySpaceTarget + 1)
        rectSourceVariable.top = 0
        rectSourceVariable.bottom = pixYSplitImgSrc
        drawSpaceContent(xSpaceToDraw, ySpaceToDraw, canvas, rectSourceVariable, rectDest, paint)
    }

    private fun drawBotImageInTopSpace(xSpaceToDraw : Int, ySpaceToDraw : Int, xSpaceTarget : Int, ySpaceTarget : Int, pixYSplitImgSrc : Int, pixYSplitImgDst : Int, canvas : Canvas) {
        rectDest.left = Pix.xLeftMainSpace(xSpaceTarget)
        rectDest.right = rectDest.left + Pix.wMainSpace
        rectDest.top = Pix.yUpMainSpace(ySpaceTarget)
        rectDest.bottom = rectDest.top + pixYSplitImgDst
        rectSourceVariable.top = pixYSplitImgSrc
        rectSourceVariable.bottom = Pix.resourceSide
        drawSpaceContent(xSpaceToDraw, ySpaceToDraw, canvas, rectSourceVariable, rectDest, paint)
    }

    // Note : it should be possible to factorize this with the previous !
    private fun drawBotFruitInSpawnSpace(xSpaceTarget : Int, ySpaceTarget : Int, space : SpaceFiller, pixYSplitImgSrc : Int, pixYSplitImgDst : Int, canvas : Canvas) {
        rectDest.left = Pix.xLeftMainSpace(xSpaceTarget)
        rectDest.right = rectDest.left + Pix.wMainSpace
        rectDest.top = Pix.yUpMainSpace(ySpaceTarget)
        rectDest.bottom = rectDest.top + pixYSplitImgDst
        rectSourceVariable.top = pixYSplitImgSrc
        rectSourceVariable.bottom = Pix.resourceSide
        space.paintStill(this, canvas, rectSourceVariable, rectDest, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
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

    // Shifting 0, 1, 2, .. (n-1) (because there are n items) to n terms equally split around 0
    // 0 1 2 3 -> -1.5 -0.5 0.5 1.5. Affine translating
    fun centeringProgression(numberTerms : Int, index : Int) : Float {
        return -(numberTerms-1)/2.toFloat() + index
    }

/*companion object {
        @kotlin.jvm.JvmField
        var bitmapFruits: Array<Bitmap> = arrayOf(
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