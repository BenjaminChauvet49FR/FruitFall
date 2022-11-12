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
import com.example.fruitfall.animations.preservations.PreAnimation
import com.example.fruitfall.introductions.Transition
import com.example.fruitfall.introductions.TransitionRandom
import com.example.fruitfall.level.LevelData
import com.example.fruitfall.level.LevelManager
import com.example.fruitfall.spaces.SpaceFiller
import com.example.fruitfall.structures.SpaceCoors

private const val SPACE_UNDEFINED = -1

class MyCanvasView(context: Context) : View(context) {
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private val colorBG = ResourcesCompat.getColor(resources, R.color.background, null)
    private val colorTextMain = ResourcesCompat.getColor(resources, R.color.textMain, null)
    private val colorFrameSelectRect = ResourcesCompat.getColor(resources, R.color.frameSelectRect, null)
    private val colorFrameHelpRect = ResourcesCompat.getColor(resources, R.color.frameHelpRect, null)
    private val colorScoreFallSpace = ResourcesCompat.getColor(resources, R.color.scoreFallSpace, null)
    private val colorNutDropsBG = ResourcesCompat.getColor(resources, R.color.colorNutDropsSpaceBG, null)
    private val colorDownFruitDropsBG = ResourcesCompat.getColor(resources, R.color.colorDownFruitDropsSpaceBG, null)
    private val colorBasketsSpaceBGArray = arrayOf(
        ResourcesCompat.getColor(resources, R.color.colorBasketsSpace0, null),
        ResourcesCompat.getColor(resources, R.color.colorBasketsSpace1, null),
        ResourcesCompat.getColor(resources, R.color.colorBasketsSpace2, null),
        ResourcesCompat.getColor(resources, R.color.colorBasketsSpace3, null),
        ResourcesCompat.getColor(resources, R.color.colorBasketsSpace4, null),
        ResourcesCompat.getColor(resources, R.color.colorBasketsSpace5, null)
    )
    private val levelBasketIntensity = arrayOf(0, 1, 2, 3, 3, 4, 4, 5, 5)
    private val colorEntranceWarps = ResourcesCompat.getColor(resources, R.color.entranceWarps, null)
    private val colorExitWarps = ResourcesCompat.getColor(resources, R.color.exitWarps, null)
    private val colorTextFrameFG = ResourcesCompat.getColor(resources, R.color.textInfosFG, null)
    private val colorTextFrameBG = ResourcesCompat.getColor(resources, R.color.textInfosBG, null)


    private val colorScoreDestructionSpace = ResourcesCompat.getColor(resources, R.color.scoreDestructionSpace, null)
    private val colorTitle = ResourcesCompat.getColor(resources, R.color.title, null)
    val colorAnimationLightning = ResourcesCompat.getColor(resources, R.color.animationLightning, null)
    val colorAnimationFire = ResourcesCompat.getColor(resources, R.color.animationFire, null)
    val colorDotDestroyableNearby = ResourcesCompat.getColor(resources, R.color.colorDotDestroyableNearby, null)
    val colorVeilStickyBomb1 = ResourcesCompat.getColor(resources, R.color.colorVeilStickyBomb1, null)
    val colorVeilStickyBomb2 = ResourcesCompat.getColor(resources, R.color.colorVeilStickyBomb2, null)
    val colorVeilStickyBomb3 = ResourcesCompat.getColor(resources, R.color.colorVeilStickyBomb3, null)
    private val colorBGSpaces = arrayOf(ResourcesCompat.getColor(resources, R.color.spaceBG1, null), ResourcesCompat.getColor(resources, R.color.spaceBG2, null))
    private val colorBorderLine = ResourcesCompat.getColor(resources, R.color.spaceBorderLine, null)
    val colorLockDuration = ResourcesCompat.getColor(resources, R.color.colorLockDuration, null)
    val colorDotHostage = ResourcesCompat.getColor(resources, R.color.colorDotHostage, null)

    private val colorOptionalFrameOut = ResourcesCompat.getColor(resources, R.color.optionalFrameOut, null)
    private val colorOptionalFrameIn = ResourcesCompat.getColor(resources, R.color.optionalFrameIn, null)
    private val colorCursorOptionalFruitOut = ResourcesCompat.getColor(resources, R.color.colorCursorOptionalFruitOut, null)
    private val colorCursorOptionalFruitIn = ResourcesCompat.getColor(resources, R.color.colorCursorOptionalFruitIn, null)

    private val rectSource = Rect(0, 0, Pix.resourceSide, Pix.resourceSide)
    private val rectSourceVariable = Rect(0, 0, Pix.resourceSide, Pix.resourceSide)
    private val rectDest = Rect(0, 0, 0, 0)
    private val rectDestMini = Rect(0, 0, 0, 0)
    private val rectPips = Rect(0, 0, 0, 0)
    private val rectSrcBasket = Rect(0, 0, Pix.sideIconBasket, Pix.sideIconBasket)
    private val rectFrame = Rect(0, 0, 0, 0)
    private val rectInfos = Rect(0, 0, 0, 0)

    private var pixMotionTouchEventX = 0f
    private var pixMotionTouchEventY = 0f
    private var selectedSpaceX = SPACE_UNDEFINED
    private var selectedSpaceY = SPACE_UNDEFINED
    private var introTransition : Transition = TransitionRandom()

    private var alreadySwappedTouchMove = false
    //private var touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private val bitmapFruits : Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.f_fraise),
        BitmapFactory.decodeResource(resources, R.drawable.f_pomme),
        BitmapFactory.decodeResource(resources, R.drawable.f_myrtille),
        BitmapFactory.decodeResource(resources, R.drawable.f_raisin),
        BitmapFactory.decodeResource(resources, R.drawable.f_orange),
        BitmapFactory.decodeResource(resources, R.drawable.f_banane),
        BitmapFactory.decodeResource(resources, R.drawable.f_kiwi),
        BitmapFactory.decodeResource(resources, R.drawable.f_mure),
    )

    private val bitmapPhoto = BitmapFactory.decodeResource(resources, R.drawable.landscape_1)

    // Option part
    private val NO_FRUIT_TAKEN = -1
    private var startingPositionFruitTaken = NO_FRUIT_TAKEN
    private var movingPositionFruitTaken = NO_FRUIT_TAKEN

    // https://stackoverflow.com/questions/10413659/how-to-resize-image-in-android
    private fun makeResizedImage(drawRessource : Int, pixNewSize : Int) : Bitmap {
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, drawRessource), pixNewSize, pixNewSize, true)
    }



    private val bitmapImageLightActive : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.light_up)
    private val bitmapImageLightInactive : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.light_down)
    private val bitmapImageLightPenalty : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.light_comeback)
    private val bitmapAllFruits : Bitmap = makeResizedImage(R.drawable.all_fruits, Pix.squareSide)
    val bitmapImageFire = makeResizedImage(R.drawable.on_fire, Pix.squareSide)
    val bitmapImageLightH = makeResizedImage(R.drawable.lightning_h, Pix.squareSide)
    val bitmapImageLightV = makeResizedImage(R.drawable.lightning_v, Pix.squareSide)
    val bitmapImageSphereOmega : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.sphere_omega)
    val bitmapImageLocking : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.locking) // TODO celui là a besoin d'uniformisations
    val bitmapImageNut : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.f_noix)
    private val bitmapIconTime : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_clock)
    private val bitmapIconMove : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_move)
    val bitmapImageDownFruit : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.down_fruit)
    val bitmapImageHostageLocks :  Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.hostage_locking1),
        BitmapFactory.decodeResource(resources, R.drawable.hostage_locking2),
        BitmapFactory.decodeResource(resources, R.drawable.hostage_locking3) // 551551 Changer ces sprites pour avoir des bordures plus épaisses
    )
    val bitmapImageStickyBombs : Array<Bitmap> = arrayOf(
        makeResizedImage(R.drawable.sticky_bomb1, Pix.squareSide), // 551551 Sprite pas tout blanc sur le côté
        makeResizedImage(R.drawable.sticky_bomb2, Pix.squareSide),
        makeResizedImage(R.drawable.sticky_bomb3, Pix.squareSide)
    )
    val bitmapImageBreakableBlocks : Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.crushable1),
        BitmapFactory.decodeResource(resources, R.drawable.crushable2),
        BitmapFactory.decodeResource(resources, R.drawable.crushable3)
    )
    val bitmapImageStopBlasts : Array<Bitmap> = arrayOf( // TODO : trouver une solution plus pérenne... à cause de la chute des fruits je ne peux pas faire aussi simple que les 2 autres ; utile cependant pour récupérer directement une main dans le StickyBlast qui stoppe !.
        BitmapFactory.decodeResource(resources, R.drawable.stop_blast1),
        BitmapFactory.decodeResource(resources, R.drawable.stop_blast2),
        BitmapFactory.decodeResource(resources, R.drawable.stop_blast3),
        BitmapFactory.decodeResource(resources, R.drawable.stop_blast4),
        BitmapFactory.decodeResource(resources, R.drawable.stop_blast5),
        BitmapFactory.decodeResource(resources, R.drawable.stop_blast6),
        BitmapFactory.decodeResource(resources, R.drawable.stop_blast7),
        BitmapFactory.decodeResource(resources, R.drawable.stop_blast8),
        BitmapFactory.decodeResource(resources, R.drawable.stop_blast9)
    )

    private val bitmapOrderAny : Bitmap = makeResizedImage(R.drawable.losange_any, Pix.squareSide)
    private val bitmapOrderMix : Bitmap = makeResizedImage(R.drawable.losange_mix, Pix.squareSide)
    private val bitmapOrderSimple : Bitmap = makeResizedImage(R.drawable.losange_simple, Pix.squareSide)
    private val bitmapOrderSpecial : Bitmap = makeResizedImage(R.drawable.losange_special, Pix.squareSide)
    private val bitmapOrderWild : Bitmap = makeResizedImage(R.drawable.losange_wild, Pix.squareSide)
    private val bitmapArrowSpawn : Bitmap = makeResizedImage(R.drawable.arrow_spawn, Pix.sidePauseFieldInfo) // TODO flèche mal dimensionnée...
    private val bitmapBasket : Bitmap = makeResizedImage(R.drawable.basket, Pix.sideIconBasket)
    private val rectSourcePicture = Rect(0, 0, 1950, 1950)// Warning : supposes that the screen is vertical
    private val rectDestinationPicture = Rect(0, 0, Pix.canvasWidth, Pix.canvasHeight)

    enum class MODE {
        GAME,
        OPTIONS
    }
    private var scene : MODE = MODE.GAME

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
        gh.getSpace(x, y).paint(this, canvas, rectSource, rectDest, paint)
    }

    private fun drawSpaceContentPAUSED(x : Int, y : Int, canvas : Canvas, rectSource : Rect, rectDest : Rect, paint : Paint) {
        gh.getSpace(x, y).paintInPause(this, canvas, rectSource, rectDest, paint)
    }

    private val gh = GameHandler()

    fun enterOptionsMode() {
        this.scene = MODE.OPTIONS
        // TODO Attention, le jeu est toujours actif !
    }

    fun enterGameMode() {
        this.scene = MODE.GAME
        this.startLevel()
    }

    fun startLevel() {
        // Note : print seems just not to work... or I missed the standard output
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
        textAlign = Paint.Align.CENTER
    }

    private val paintFrameInfos = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL_AND_STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = Pix.backgroundFrame
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val paintTextInfos = Paint().apply {
        color = colorTextMain
        textSize = Pix.hTextFloat
        typeface = mainFont
        style = Paint.Style.FILL// How to avoid awful outlined texts : https://stackoverflow.com/questions/31877417/android-draw-text-with-solid-background-onto-canvas-to-be-used-as-a-bitmap
        textAlign = Paint.Align.LEFT
    }

    private val paintTextOptions = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = 1.toFloat()
        color = colorOptionalFrameOut
        textSize = Pix.hTextOptionalFloat
    }

    private val path = Path()

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle() // If not for this, memory leak !
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(colorBG)
    }

    // Drawing : the generics !

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
        if (scene == MODE.GAME) {
            drawGame(canvas)
        } else {
            drawOptions(canvas)
        }
        invalidate() // At the end of draw... right ? Also, how many FPS ?
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawOptions(canvas : Canvas) {
        // Draw fruits frame
        rectDest.left = Pix.xOptionalFruitFrameStart
        rectDest.right = Pix.xOptionalFruitFrameEnd
        rectDest.top = Pix.yOptionalFruitFrameStart
        rectDest.bottom = Pix.yOptionalFruitFrameEnd
        paint.style = Paint.Style.STROKE
        paint.color = colorOptionalFrameOut
        paint.strokeWidth = Pix.strokeWidthOptionalFrame
        canvas.drawRect(rectDest, paint)
        paint.style = Paint.Style.FILL
        paint.color = colorOptionalFrameIn
        canvas.drawRect(rectDest, paint)

        // Draw fruits to be swapped
        rectDest.left = Pix.xOptionalFruitStart
        for (i in 0 until Constants.RESOURCES_NUMBER_FRUITS) {
            rectDest.right = rectDest.left + Pix.wOptionalFruit
            rectDest.top = Pix.yOptionalFruitStart
            rectDest.bottom = rectDest.top + Pix.hOptionalFruit
            canvas.drawBitmap(bitmapFruits[OptionHandler.indexFruitsByPriority[i]], rectSource, rectDest, paint)
            rectDest.left += Pix.wOptionalFruit + Pix.gapOptionalFruit
        }
        // Draw cursor
        val pixXCenterCursor : Int = Pix.xOptionalFruitStart - Pix.gapOptionalFruit/2 + OptionHandler.cursorRandomness * (Pix.gapOptionalFruit + Pix.wOptionalFruit)
        val pixXLeftCursor =  pixXCenterCursor - Pix.wOptionalFruitCursor/2
        val pixXRightCursor = pixXLeftCursor + Pix.wOptionalFruitCursor
        val pixYBottomCursor = Pix.yOptionalCursor + Pix.hOptionalFruitCursor
        rectDest.left = pixXLeftCursor
        rectDest.right = pixXRightCursor
        rectDest.top = Pix.yOptionalCursor
        rectDest.bottom = pixYBottomCursor
        paint.color = colorOptionalFrameOut
        canvas.drawRect(rectDest, paint)
        paint.alpha = 127
        rectDest.left = pixXCenterCursor
        rectDest.right = Pix.xOptionalFruitFrameEnd
        rectDest.top = Pix.yOptionalFruitFrameStart
        rectDest.bottom = Pix.yOptionalFruitFrameEnd
        canvas.drawRect(rectDest, paint)
        path.reset()
        paint.strokeWidth = 1.toFloat()
        paint.color = colorCursorOptionalFruitIn
        paint.alpha = 255
        val pixHalf = (pixXRightCursor-pixXLeftCursor)/2.toFloat() // Drawing half-square rectangle !
        path.moveTo(pixXLeftCursor + pixHalf, pixYBottomCursor - pixHalf)
        path.lineTo(pixXLeftCursor - pixHalf, pixYBottomCursor + pixHalf)
        path.lineTo(pixXRightCursor + pixHalf, pixYBottomCursor + pixHalf)
        path.lineTo(pixXLeftCursor + pixHalf, pixYBottomCursor - pixHalf)
        path.close()
        canvas.drawPath(path, paint)
        path.lineTo(pixXLeftCursor - pixHalf, pixYBottomCursor + pixHalf)
        path.lineTo(pixXRightCursor + pixHalf, pixYBottomCursor + pixHalf)
        path.lineTo(pixXLeftCursor + pixHalf, pixYBottomCursor - pixHalf)
        path.close()
        paint.style = Paint.Style.STROKE
        paint.color = colorCursorOptionalFruitOut
        canvas.drawPath(path, paint)

        // TODO draw more informations about this
        // Draw mode and text frame
        paint.strokeWidth = Pix.strokeWidthOptionalFrame
        Pix.adaptRectForFrameOptional(Pix.rectMode, Pix.xWriteLineOptionalMode, Pix.yWriteLineOptionalMode, 20, Pix.yWriteLineOptionalMode)
        drawRectForText(canvas, Pix.rectMode, paint, colorOptionalFrameIn, colorOptionalFrameOut)
        canvas.drawText("Mode : " + OptionHandler.mode.toString(),
            Pix.rectMode.left.toFloat(), Pix.rectMode.bottom.toFloat() - Pix.paddingMainText, paintTextOptions) // TODO faire une fonction drawTextWithinRect
        
        // TODO additional options :
        // within a level, could available colours be newly mixed ? (eg we have red green blue X Y in a 5-level colour with a coloured fruit command, some fruits in fixed positions and some fruits in sticky bombs, could we mix which colours get where ?)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawGame(canvas : Canvas) {
        drawArena(canvas)
        if (gh.gth.isInIntro) {
            drawProgressiveCheckerboard(canvas)
        } else {
            drawAllSpaceContents(canvas)
            if (!gh.gth.pause) {
                drawPreAnimations(canvas)
                drawSpaceAnimations(canvas, gh.gth.animations1List)// TODO devrait être dessiné APRES les cases et AVANT les contenus dans l'idéal.
                drawFallingSpaces(canvas)
                drawCursor(canvas)
                drawHelp(canvas)
                drawSwap(canvas)
                drawSpaceAnimations(canvas, gh.gth.animations2List)
            }
            if (gh.goalKind == GameEnums.GOAL_KIND.BASKETS) {
                drawBaskets(canvas)
            }
            if (gh.goalKind == GameEnums.GOAL_KIND.NUTS) {
                drawNutDrops(canvas)
            }
            if (gh.goalKind == GameEnums.GOAL_KIND.DOWNFRUITS) {
                drawDownFruitDrops(canvas)
            }
            if (!gh.gth.pause) {
                drawScoresOnField(canvas)
            }
        }
        if (gh.gth != null) {
            gh.gth.step() // TODO Lui donner son propre processus parallèle ? Ou bien laisser dans onDraw ?
        }
    }

    // Drawing : around the field
    private fun drawRectForText(canvas : Canvas, rect : Rect, paint : Paint, colourIn : Int, colourOut : Int) {
        paint.color = colourOut
        paint.style = Paint.Style.STROKE
        canvas.drawRect(rect, paint)
        paint.color = colourIn
        paint.style = Paint.Style.FILL
        canvas.drawRect(rect, paint)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawArena(canvas : Canvas) {
        // Draw a picture
        canvas.drawBitmap(bitmapPhoto, rectSourcePicture, rectDestinationPicture, paintTextInfos)

        // Convention for text drawing is easy : draw on fields = center, draw outside fields aligned left !
        paintTextInfos.color = colorTextMain
        Pix.adaptRectForFrame(rectInfos, Pix.xTextScore.toInt(), Pix.yTextScore.toInt(), 15, Pix.yTextScore.toInt())
        drawRectForText(canvas, rectInfos, paintFrameInfos, colorTextFrameBG, colorTextFrameFG)
        canvas.drawText("Score : " + gh.score, Pix.xTextScore, Pix.yTextScore, paintTextInfos)

        Pix.adaptRectForFrame(rectInfos, Pix.xTimePicture, Pix.yTimeMovesLine, 15, Pix.yTimeMovesLine)
        drawRectForText(canvas, rectInfos, paintFrameInfos, colorTextFrameBG, colorTextFrameFG)
        drawIconAndText(canvas, Pix.xTimePicture, Pix.yTimeMovesLine, bitmapIconTime,"" + gh.timeToDisplay)
        drawIconAndText(canvas, Pix.xMovesPicture, Pix.yTimeMovesLine, bitmapIconMove,"" + gh.movesToDisplay)

        drawMission(canvas)

        paintTextInfos.color = colorTitle
        Pix.adaptRectForFrame(rectInfos, Pix.xTextTitle.toInt(), Pix.yTextTitle.toInt(), 45, Pix.yTextTitle.toInt())
        drawRectForText(canvas, rectInfos, paintFrameInfos, colorTextFrameBG, colorTextFrameFG)
        canvas.drawText(gh.titleAndInfos, Pix.xTextTitle, Pix.yTextTitle, paintTextInfos)

        // Draw the status light
        rectDest.left = Pix.xStartActiveLight
        rectDest.top = Pix.yStartActiveLight
        rectDest.right = rectDest.left + Pix.wActiveLight
        rectDest.bottom = rectDest.top + Pix.hActiveLight
        if (gh.gth.isActive) {
            canvas.drawBitmap(bitmapImageLightActive, rectSource, rectDest, paintTextInfos)
        } else if (gh.gth.isActivePenalty) {
            canvas.drawBitmap(bitmapImageLightPenalty, rectSource, rectDest, paintTextInfos)
        } else {
            canvas.drawBitmap(bitmapImageLightInactive, rectSource, rectDest, paintTextInfos)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawMission(canvas : Canvas) {
        // Credits for the switch : https://kotlinlang.org/docs/control-flow.html#when-expression
        Pix.adaptRectForFrame(rectInfos, Pix.xTextCommands.toInt(), Pix.yTextCommands.toInt(), 25, Pix.yTextCommands.toInt())
        drawRectForText(canvas, rectInfos, paintFrameInfos, colorTextFrameBG, colorTextFrameFG)
        when (gh.goalKind) {
            GameEnums.GOAL_KIND.BASKETS -> {
                canvas.drawText("Paniers : " + gh.basketsCount, Pix.xTextCommands, Pix.yTextCommands, paintTextInfos)
            }
            GameEnums.GOAL_KIND.NUTS -> {
                canvas.drawText("Noix : " + gh.nutsHealthCount, Pix.xTextNuts, Pix.yTextNut, paintTextInfos)
                for (i in 0 until gh.listWaitingNutData.size) {
                    rectDest.left = Pix.xNutWaitingPicture(i)
                    rectDest.top = Pix.yPictureNutWaiting
                    rectDest.right = rectDest.left + Pix.resourceLittleSide
                    rectDest.bottom = rectDest.top + Pix.resourceLittleSide
                    canvas.drawBitmap(bitmapImageNut, rectSource, rectDest, paint)
                    canvas.drawText(gh.listWaitingNutData[i].delay.toString(), Pix.xTextNutWaiting(i), Pix.yTextNut, paintTextInfos)
                }
            }
            GameEnums.GOAL_KIND.DOWNFRUITS -> {
                canvas.drawText("Fruits restants : " + gh.getdownFruitsLeftForMission(), Pix.xTextCommands, Pix.yTextCommands, paintTextInfos)
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
                    rectDest.left = Pix.xPictureCommandsKind(i)
                    rectDest.top = Pix.yPictureCommandsKind
                    rectDest.right = rectDest.left + Pix.resourceLittleSide
                    rectDest.bottom = rectDest.top + Pix.resourceLittleSide
                    canvas.drawBitmap(backBitmap, rectSource, rectDest, paint)
                    pixWRectShrink = rectDest.width()/5
                    pixHRectShrink = rectDest.height()/5
                    rectDest.left += pixWRectShrink
                    rectDest.top += pixHRectShrink
                    rectDest.right -= pixWRectShrink
                    rectDest.bottom -= pixHRectShrink
                    drawDoublePicture(canvas, frontBitmap, frontBitmap2)
                    canvas.drawText(gh.amountsOrder[i].toString(), Pix.xTextCommandsAmount(i), Pix.yTextCommands, paintTextInfos)
                }
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawIconAndText(canvas : Canvas, pixXLeftPicture : Int, pixYBottomPicture : Int, bitmap : Bitmap, text : String) {
        // TODO Fenêtre de != couleurs pour != types, nombres de fruits
        rectDest.left = pixXLeftPicture
        rectDest.top = pixYBottomPicture - Pix.iconLittleSide*3/5
        rectDest.right = rectDest.left + Pix.iconLittleSide
        rectDest.bottom = pixYBottomPicture + Pix.iconLittleSide*2/5
        canvas.drawBitmap(bitmap, rectSource, rectDest, paint)
        canvas.drawText(text, pixXLeftPicture.toFloat() + Pix.iconLittleSide + 2, pixYBottomPicture.toFloat(), paintTextInfos)
    }

    private fun drawDoublePicture(canvas : Canvas, bitmap1 : Bitmap, bitmap2 : Bitmap?) {
        canvas.drawBitmap(bitmap1, rectSource, rectDest, paint)
        if (bitmap2 != null) {
            rectDest.left -= 6
            rectDest.top += 6
            rectDest.right -= 6
            rectDest.bottom += 6
            canvas.drawBitmap(bitmap2, rectSource, rectDest, paint)
        }
    }

    // Drawing : field and spaces

    private fun drawNutDrops(canvas: Canvas) {
        val formerAlpha = paint.alpha
        paint.color = colorNutDropsBG
        paint.alpha = 127
        paint.style = Paint.Style.FILL_AND_STROKE
        for (coors in this.gh.coorsForNutDrops) {
            val x = coors.x
            val y = coors.y
            rectDest.set(Pix.xLeftSpace(x), Pix.yUpSpace(y), Pix.xRightSpace(x), Pix.yDownSpace(y))
            canvas.drawRect(rectDest, paint)
        }
        paint.alpha = formerAlpha
    }

    private fun drawDownFruitDrops(canvas: Canvas) {
        val formerAlpha = paint.alpha
        paint.color = colorDownFruitDropsBG
        paint.alpha = 127
        paint.style = Paint.Style.FILL_AND_STROKE
        for (coors in this.gh.coorsForDownFruitDrops) {
            val x = coors.x
            val y = coors.y
            rectDest.set(Pix.xLeftSpace(x), Pix.yUpSpace(y), Pix.xRightSpace(x), Pix.yDownSpace(y))
            canvas.drawRect(rectDest, paint)
        }
        paint.alpha = formerAlpha
    }

    private fun drawBaskets(canvas: Canvas) {
        var baskets : Int
        var nbBasketsDown : Int // Number of baskets in the bottom part of the space
        var nbBasketsUp : Int // The remaining baskets
        val pixXLeftStart = Pix.getXLeftFirstInnerSpace()
        val pixXRightStart = pixXLeftStart + Pix.wInnerSpace
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = Pix.basketFrameWidth
        val formerAlpha = paint.alpha
        rectDest.left = pixXLeftStart
        rectDest.right = pixXRightStart
        rectDest.top = Pix.getYUpFirstInnerSpace()
        rectDest.bottom = rectDest.top + Pix.wInnerSpace
        var pixXPip : Int
        var pixYPip : Int
        for (y in 0 until gh.yFieldLength) {
            for (x in 0 until gh.xFieldLength) {
                baskets = gh.getBaskets(x, y)
                if (baskets > 0) {
                    paint.color = colorBasketsSpaceBGArray[levelBasketIntensity[baskets]]
                    paint.alpha = 96
                    canvas.drawRect(rectDest, paint)

                    paint.alpha = 96
                    nbBasketsDown = baskets/2
                    nbBasketsUp = baskets-nbBasketsDown
                    pixYPip = Pix.yUpSpace(y) + Pix.paddingBasket
                    for (i in 0 until nbBasketsUp) {
                        pixXPip = (Pix.xCenter(x) - Pix.sideIconBasket/2.toFloat() + centeringProgression(nbBasketsUp, i)*Pix.sideIconBasket*3/2).toInt()
                        rectPips.set(pixXPip, pixYPip, pixXPip+Pix.sideIconBasket, pixYPip+Pix.sideIconBasket)
                        canvas.drawBitmap(bitmapBasket, rectSrcBasket, rectPips, paint)
                    }
                    pixYPip = Pix.yDownSpace(y) - Pix.paddingBasket - Pix.sideIconBasket
                    for (i in 0 until nbBasketsDown) { // Yet another affine translating
                        pixXPip = (Pix.xCenter(x) - Pix.sideIconBasket/2 + centeringProgression(nbBasketsDown, i)*Pix.sideIconBasket*3/2).toInt()
                        rectPips.set(pixXPip, pixYPip, pixXPip+Pix.sideIconBasket, pixYPip+Pix.sideIconBasket)
                        canvas.drawBitmap(bitmapBasket, rectSrcBasket, rectPips, paint)
                    }
                }
                readaptRectNextSpace()
            }
            readaptRectNextLineSpace(pixXLeftStart, pixXRightStart)
        }
        paint.alpha = formerAlpha // Useless if colour is changed after the last alpha set
    }

    // Draw scores on the field
    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawScoresOnField(canvas: Canvas) {
        if (gh.gth.shouldDrawScore()) {
            paint.color = colorScoreFallSpace
            paint.textSize = Pix.hTextScoreSpace
            paint.typeface = scoreFont
            paint.style = Paint.Style.FILL_AND_STROKE
            for (coors in this.gh.contributingSpacesScoreFall) {
                val x = coors.x
                val y = coors.y
                canvas.drawText("+" + this.gh.scoreFallSpace(x, y),
                    Pix.xLeftSpace(x).toFloat(),
                    Pix.yUpSpace(y) + Pix.hTextScoreSpace,
                    paint)
            }
            paint.color = colorScoreDestructionSpace
            paint.textSize = Pix.hTextScoreSpace
            paint.typeface = scoreFont
            paint.style = Paint.Style.FILL_AND_STROKE
            for (coors in this.gh.contributingSpacesScoreDestructionSpecial) {
                val x = coors.x
                val y = coors.y
                canvas.drawText("+" + this.gh.scoreDestructionSpecialSpace(x, y),
                    Pix.xLeftSpace(x).toFloat(),
                    Pix.yUpSpace(y) + Pix.hTextScoreSpace,
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
            readaptRectForOneSpaceDraw(x1 + ratio*(x2-x1), y1 + ratio*(y2-y1))
            this.drawSpaceContent(x1, y1, canvas, rectSource, rectDest, paint)
            readaptRectForOneSpaceDraw(x2 + ratio*(x1-x2), y2 + ratio*(y1-y2))
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
            paint.color = colorFrameSelectRect
            canvas.drawRect(rectFrame, paint)
            paint.strokeWidth = formerWidth
        }
    }

    // Draw the help
    private fun drawHelp(canvas: Canvas) {
        if (gh.help) {
            val formerWidth = paint.strokeWidth
            paint.strokeWidth = Pix.helpFrame
            rectFrame.set(spaceXToPixXLeft(gh.xHelp), spaceYToPixYUp(gh.yHelp), spaceXToPixXRight(gh.xHelp), spaceYToPixYDown(gh.yHelp))
            paint.style = Paint.Style.STROKE
            paint.color = colorFrameHelpRect
            canvas.drawRect(rectFrame, paint)
            paint.strokeWidth = formerWidth
            // TODO Pas de représentation pour la case adjacente !
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
            val pixYSplitImgDst = (Pix.hInnerSpace * ratioFall).toInt()
            for (coors in gh.fallingEltsCoors) {
                xStartFall = coors.x
                yStartFall = coors.y
                if (gh.isNotDestroyedBeforeFall(xStartFall, yStartFall)) {
                    outCoors = gh.getDestination(xStartFall, yStartFall)
                    if (outCoors != null) { // FallElt in teleportation
                        drawTopImageInBotSpace(xStartFall, yStartFall, coors.x, coors.y, pixYSplitImgSrc, pixYSplitImgDst, canvas)
                        drawBotImageInTopSpace(xStartFall, yStartFall, outCoors.x, outCoors.y, pixYSplitImgSrc, pixYSplitImgDst, canvas)
                    } else { // FallElt without teleportation
                        readaptRectForOneSpaceDraw(xStartFall.toFloat(), yStartFall + ratioFall) // Floats are required
                        this.drawSpaceContent(xStartFall, yStartFall, canvas, rectSource, rectDest, paint)
                    }
                }
            }
            // Draw the diagonal squeezing fruits
            for (coorsDest in gh.coorsElementsGettingFromUpperLeft) {
                readaptRectForOneSpaceDraw(coorsDest.x - 1 + ratioFall, coorsDest.y - 1 + ratioFall)
                this.drawSpaceContent(coorsDest.x-1, coorsDest.y-1, canvas, rectSource, rectDest, paint)
            }
            for (coorsDest in gh.coorsElementsGettingFromUpperRight) {
                readaptRectForOneSpaceDraw(coorsDest.x + 1 - ratioFall, coorsDest.y - 1 + ratioFall)
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
    
    private fun readaptRectForOneSpaceDraw(x : Float, y : Float) {
        rectDest.left = Pix.xLeftInnerSpace(x)
        rectDest.right = rectDest.left + Pix.wInnerSpace
        rectDest.top = Pix.yUpInnerSpace(y)
        rectDest.bottom = rectDest.top + Pix.hInnerSpace
    }

    fun readaptRectNextSpace() {
        rectDest.left += Pix.wSpace
        rectDest.right += Pix.wSpace
    }

    fun readaptRectNextLineSpace(pixXLeft : Int, pixXRight : Int) {
        rectDest.left = pixXLeft
        rectDest.right = pixXRight
        rectDest.top += Pix.hSpace
        rectDest.bottom += Pix.hSpace
    }

    private fun drawProgressiveCheckerboard(canvas : Canvas) {
        // Note : checkerboard spaces must match field areas
        paint.strokeWidth = Pix.backgroundFrame
        val pixStartLeft = Pix.getXLeftFirstSpace()-Pix.horizontalPaddingSpace/2
        val pixStartRight = pixStartLeft+Pix.wSpace
        var progressiveIntro : Float
        var desiredThreshold : Float
        rectDest.left = pixStartLeft
        rectDest.right = pixStartRight
        rectDest.top = Pix.getYUpFirstSpace()-Pix.verticalPaddingSpace/2
        rectDest.bottom = rectDest.top+Pix.hSpace
        val rectVar = Rect(0, 0, 0, 0)
        var marginGhost : Int
        for (y in 0 until gh.yFieldLength) {
            for (x in 0 until gh.xFieldLength) {
                if (gh.isASpace(x, y)) {
                    desiredThreshold = introTransition.getProgressThreshold(x, y)
                    progressiveIntro = gh.gth.ratioProgressiveIntroSpaces(desiredThreshold)
                    marginGhost = ((1-progressiveIntro)*Pix.marginGhostSpace).toInt()
                    rectVar.set(rectDest.left - marginGhost, rectDest.top - marginGhost, rectDest.right + marginGhost, rectDest.bottom + marginGhost)
                    paint.color = colorBGSpaces[(x + y) % 2]
                    paint.style = Paint.Style.FILL
                    paint.alpha = (255.0*progressiveIntro).toInt() // Important : must be placed AFTER setColor otherwise it is returned to 255
                    canvas.drawRect(rectVar, paint)
                    if (gh.gth.shouldDrawSpaceContentProgessiveIntro(desiredThreshold)) {
                        this.drawSpaceContent(x, y, canvas, rectSource, rectDest, paint)
                    }
                }
                readaptRectNextSpace()
            }
            readaptRectNextLineSpace(pixStartLeft, pixStartRight)
        }
        paint.alpha = 255
    }

    // Draw the in-place elements or the on-space animations
    // Draws different things depending on whether the game is paused or not
    private fun drawAllSpaceContents(canvas : Canvas) {
        paint.strokeWidth = Pix.borderThickness
        var outCoors : SpaceCoors?
        var pixXStart1 = Pix.getXLeftFirstSpace()
        var pixYStart1 = Pix.getYUpFirstSpace()
        var pixXStart2 = pixXStart1 + Pix.wSpace // -1 not necessary ! (see note in Pix)
        var pixYStart2 = pixYStart1 + Pix.hSpace
        val outsideTeleportersCoors = ArrayList<SpaceCoors>()
        var wallLeft : Boolean
        var wallUp : Boolean
        var wallRight : Boolean
        var wallDown : Boolean
        var cornerBlockedLU : Boolean
        var cornerBlockedRU : Boolean
        var cornerBlockedLD : Boolean
        var cornerBlockedRD : Boolean
        var x1 : Float
        var x2 : Float
        var y1 : Float
        var y2 : Float
        rectDest.set(pixXStart1, pixYStart1, pixXStart2, pixYStart2)
        // Drawing background of spaces
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 4.toFloat()
        path.reset()
        for (y in 0 until gh.yFieldLength) {
            for (x in 0 until gh.xFieldLength) {
                // Warning C/P : from draw checkerboard
                wallLeft = (x == 0 || !gh.isASpace(x-1, y))
                wallRight = (x == Constants.FIELD_XLENGTH-1 || !gh.isASpace(x+1, y))
                wallUp = (y == 0 || !gh.isASpace(x, y-1))
                wallDown = (y == Constants.FIELD_YLENGTH-1 || !gh.isASpace(x, y+1))
                cornerBlockedLU = ((x == 0) || (y == 0) || (!gh.isASpace(x - 1, y - 1)))
                cornerBlockedRU =
                    ((x == Constants.FIELD_XLENGTH - 1) || (y == 0) || (!gh.isASpace(
                        x + 1,
                        y - 1
                    )))
                cornerBlockedLD =
                    ((x == 0) || (y == Constants.FIELD_YLENGTH - 1) || (!gh.isASpace(
                        x - 1,
                        y + 1
                    )))
                cornerBlockedRD =
                    ((x == Constants.FIELD_XLENGTH - 1) || (y == Constants.FIELD_YLENGTH - 1) || (!gh.isASpace(
                        x + 1,
                        y + 1
                    )))
                if (gh.isASpace(x, y)) {
                    path.moveTo(Pix.xCenter(x).toFloat(), Pix.yUpSpace(y).toFloat()) // Note : these toFloat are justified, but cringe...
                    if (cornerBlockedRU && wallUp && wallRight) {
                        path.lineTo(rectDest.right.toFloat()-Pix.roundBorderSpace, rectDest.top.toFloat())
                        path.lineTo(rectDest.right.toFloat(), rectDest.top.toFloat()+Pix.roundBorderSpace)
                    } else {
                        path.lineTo(rectDest.right.toFloat(), rectDest.top.toFloat())
                    }
                    if (cornerBlockedRD && wallDown && wallRight) {
                        path.lineTo(rectDest.right.toFloat(), rectDest.bottom.toFloat()-Pix.roundBorderSpace)
                        path.lineTo(rectDest.right.toFloat()-Pix.roundBorderSpace, rectDest.bottom.toFloat())
                    } else {
                        path.lineTo(rectDest.right.toFloat(), rectDest.bottom.toFloat())
                    }
                    if (cornerBlockedLD && wallDown && wallLeft) {
                        path.lineTo(rectDest.left.toFloat()+Pix.roundBorderSpace, rectDest.bottom.toFloat())
                        path.lineTo(rectDest.left.toFloat(), rectDest.bottom.toFloat()-Pix.roundBorderSpace)
                    } else {
                        path.lineTo(rectDest.left.toFloat(), rectDest.bottom.toFloat())
                    }
                    if (cornerBlockedLU && wallUp && wallLeft) {
                        path.lineTo(rectDest.left.toFloat(), rectDest.top.toFloat()+Pix.roundBorderSpace)
                        path.lineTo(rectDest.left.toFloat()+Pix.roundBorderSpace, rectDest.top.toFloat())
                    } else {
                        path.lineTo(rectDest.left.toFloat(), rectDest.top.toFloat())
                    }
                    path.close()
                    paint.color = colorBGSpaces[(x + y) % 2]
                    canvas.drawPath(path, paint)
                    path.reset() // Forget this and you will know pain, and a lagging screen
                } else {
                    if (!wallUp && !wallRight) {
                        path.moveTo(rectDest.right.toFloat(), rectDest.top.toFloat())
                        path.lineTo(rectDest.right.toFloat()-Pix.roundBorderSpace, rectDest.top.toFloat())
                        path.lineTo(rectDest.right.toFloat(), rectDest.top.toFloat()+Pix.roundBorderSpace)
                        path.close()
                        paint.color = colorBGSpaces[(x + y) % 2]
                        canvas.drawPath(path, paint)
                        path.reset()
                    }
                    if (!wallDown && !wallRight) {
                        path.moveTo(rectDest.right.toFloat(), rectDest.bottom.toFloat())
                        path.lineTo(rectDest.right.toFloat()-Pix.roundBorderSpace, rectDest.bottom.toFloat())
                        path.lineTo(rectDest.right.toFloat(), rectDest.bottom.toFloat()-Pix.roundBorderSpace)
                        path.close()
                        paint.color = colorBGSpaces[(x + y) % 2]
                        canvas.drawPath(path, paint)
                        path.reset()
                    }
                    if (!wallUp && !wallLeft) {
                        path.moveTo(rectDest.left.toFloat(), rectDest.top.toFloat())
                        path.lineTo(rectDest.left.toFloat()+Pix.roundBorderSpace, rectDest.top.toFloat())
                        path.lineTo(rectDest.left.toFloat(), rectDest.top.toFloat()+Pix.roundBorderSpace)
                        path.close()
                        paint.color = colorBGSpaces[(x + y) % 2]
                        canvas.drawPath(path, paint)
                        path.reset()
                    }
                    if (!wallDown && !wallLeft) {
                        path.moveTo(rectDest.left.toFloat(), rectDest.bottom.toFloat())
                        path.lineTo(rectDest.left.toFloat()+Pix.roundBorderSpace, rectDest.bottom.toFloat())
                        path.lineTo(rectDest.left.toFloat(), rectDest.bottom.toFloat()+Pix.roundBorderSpace)
                        path.close()
                        paint.color = colorBGSpaces[(x + y) % 2]
                        canvas.drawPath(path, paint)
                        path.reset()
                    }
                }
                readaptRectNextSpace()
            }
            readaptRectNextLineSpace(pixXStart1, pixXStart2)
        }

        // Drawing edges
        rectDest.set(pixXStart1, pixYStart1, pixXStart2, pixYStart2)
        for (y in 0 until gh.yFieldLength) {
            for (x in 0 until gh.xFieldLength) {
                if (gh.isASpace(x, y)) {
                    // Warning C/P : from draw checkerboard

                    wallLeft = (x == 0 || !gh.isASpace(x-1, y))
                    wallRight = (x == Constants.FIELD_XLENGTH-1 || !gh.isASpace(x+1, y))
                    wallUp = (y == 0 || !gh.isASpace(x, y-1))
                    wallDown = (y == Constants.FIELD_YLENGTH-1 || !gh.isASpace(x, y+1))
                    cornerBlockedLU = ((x == 0) || (y == 0) || (!gh.isASpace(x-1, y-1)))
                    cornerBlockedRU = ((x == Constants.FIELD_XLENGTH-1) || (y == 0) || (!gh.isASpace(x+1, y-1)))
                    cornerBlockedLD = ((x == 0) || (y == Constants.FIELD_YLENGTH-1) || (!gh.isASpace(x-1, y+1)))
                    cornerBlockedRD = ((x == Constants.FIELD_XLENGTH-1) || (y == Constants.FIELD_YLENGTH-1) || (!gh.isASpace(x+1, y+1)))
                    paint.color = colorBorderLine
                    paint.style = Paint.Style.STROKE
                    if (wallLeft) {
                        x1 = rectDest.left.toFloat()
                        y1 = rectDest.top + Pix.borderThickness
                        x2 = x1
                        y2 = rectDest.bottom - Pix.borderThickness
                        // Always draw main line (LURD-always = always when checking left up right down)
                        // The LURD-always are like this because they can be drawn only for one wall, while the diagonal cuts could be drawn for the two walls they come from
                        canvas.drawLine(x1, y1, x2, y2, paint)
                        // Draw corner line ? If portion is straight, it LURD-always needs to be drawn
                        // Diagonal lines need to be drawn only once, starting from a left wall, depending on if there is a wall in orthogonal direction and/or a corner.
                        if (!wallUp) {
                            if (!cornerBlockedLU) {
                                drawDiagonalLine(canvas, x1, y1, -1, -1) // No wall, no corner blocked : draw diagonal line outward
                            } else {
                                canvas.drawLine(x1, rectDest.top.toFloat(), x1, y1+1, paint) // No wall, a corner blocked : straight line (LURD-always needs to be drawn)
                            }
                        } else if (!cornerBlockedLU) { // Wall, no corner blocked (squeezing) : draw diagonal line outward
                            drawDiagonalLine(canvas, x1, y1, -1, -1)
                        } else { // Wall, corner blocked : diagonal line inward
                            drawDiagonalLine(canvas, x1, y1, 1, -1)
                        }
                        if (!wallDown) { // Same as wallUp in left
                            if (!cornerBlockedLD) {
                                drawDiagonalLine(canvas, x2, y2, -1, 1)
                            } else {
                                canvas.drawLine(x1, rectDest.bottom.toFloat(), x1, y2-1, paint)
                            }
                        } else if (!cornerBlockedLD) {
                            drawDiagonalLine(canvas, x2, y2, -1, 1)
                        } else {
                            drawDiagonalLine(canvas, x2, y2, 1, 1)
                        }
                    }
                    if (wallUp) { // Much shorter since we draw the LURD-always walls
                        x1 = rectDest.left + Pix.borderThickness
                        y1 = rectDest.top.toFloat()
                        x2 = rectDest.right - Pix.borderThickness
                        y2 = y1
                        canvas.drawLine(x1, y1, x2, y2, paint)
                        if (!wallLeft && cornerBlockedLU) {
                            canvas.drawLine(rectDest.left.toFloat(), y1, x1+1, y1, paint)
                        }
                        if (!wallRight && cornerBlockedRU) {
                            canvas.drawLine(rectDest.right.toFloat(), y1, x2-1, y1, paint)
                        }
                    }
                    if (wallRight) {
                        x1 = rectDest.right.toFloat()
                        y1 = rectDest.top + Pix.borderThickness
                        x2 = x1
                        y2 = rectDest.bottom - Pix.borderThickness
                        canvas.drawLine(x1, y1, x2, y2, paint)
                        if (!wallUp) {
                            if (!cornerBlockedRU) {
                                drawDiagonalLine(canvas, x1, y1, 1, -1)
                            } else {
                                canvas.drawLine(x1, rectDest.top.toFloat(), x1, y1+1, paint)
                            }
                        } else if (!cornerBlockedRU) {
                            drawDiagonalLine(canvas, x1, y1, 1, -1)
                        } else {
                            drawDiagonalLine(canvas, x1, y1, -1, -1)
                        }
                        if (!wallDown) {
                            if (!cornerBlockedRD) {
                                drawDiagonalLine(canvas, x2, y2, 1, 1)
                            } else {
                                canvas.drawLine(x1, rectDest.bottom.toFloat(), x1, y2-1, paint)
                            }
                        } else if (!cornerBlockedRD) {
                            drawDiagonalLine(canvas, x2, y2, 1, 1)
                        } else {
                            drawDiagonalLine(canvas, x2, y2, -1, 1)
                        }
                    }
                    if (wallDown) {
                        x1 = rectDest.left + Pix.borderThickness
                        y1 = rectDest.bottom.toFloat()
                        x2 = rectDest.right - Pix.borderThickness
                        y2 = y1
                        canvas.drawLine(x1, y1, x2, y2, paint)
                        if (!wallLeft && cornerBlockedLD) {
                            canvas.drawLine(rectDest.left.toFloat(), y1, x1+1, y1, paint)
                        }
                        if (!wallRight && cornerBlockedRD) {
                            canvas.drawLine(rectDest.right.toFloat(), y1, x2-1, y1, paint)
                        }
                    }
                }
                readaptRectNextSpace()
            }
            readaptRectNextLineSpace(pixXStart1, pixXStart2)
        }

        // Drawing contents
        paint.strokeWidth = 2.toFloat() // Necessary for font size

        pixXStart1 = Pix.getXLeftFirstInnerSpace()
        pixYStart1 = Pix.getYUpFirstInnerSpace()
        pixXStart2 = pixXStart1 + Pix.wInnerSpace
        pixYStart2 = pixYStart1 + Pix.hInnerSpace
        rectDest.set(pixXStart1, pixYStart1, pixXStart2, pixYStart2)
        for (y in 0 until gh.yFieldLength) {
            for (x in 0 until gh.xFieldLength) {

                if (!gh.gth.pause) {
                    if (gh.gth.hasStillSpace(x, y)) {
                        this.drawSpaceContent(x, y, canvas, rectSource, rectDest, paint)
                    }
                } else {
                    if (gh.gth.hasStillSpace(x, y)) {
                        this.drawSpaceContentPAUSED(x, y, canvas, rectSource, rectDest, paint)
                    }
                    if (gh.shouldSpawnFruit(x, y)) {
                        rectDestMini.set(Pix.xCenter(x)-bitmapArrowSpawn.width/2, Pix.yUpSpace(y)-bitmapArrowSpawn.height/2, Pix.xCenter(x)+bitmapArrowSpawn.width/2, Pix.yUpSpace(y)+bitmapArrowSpawn.height/2)
                        canvas.drawBitmap(bitmapArrowSpawn, rectSource, rectDestMini, paint)
                    }
                    outCoors = gh.getDestination(x, y)
                    if (outCoors != null) {
                        paint.color = colorEntranceWarps
                        paint.textSize = Pix.hTextTeleporters
                        paint.style = Paint.Style.FILL_AND_STROKE
                        outsideTeleportersCoors.add(
                            SpaceCoors(
                                outCoors.x,
                                outCoors.y
                            )
                        )
                        canvas.drawText(outsideTeleportersCoors.size.toString(),
                            Pix.xCenter(x).toFloat(),
                            Pix.yDownSpace(y).toFloat(), // TODO Int, float, double... un jour il faudra faire du ménage
                            paint)
                    }
                }
                readaptRectNextSpace()
            }
            readaptRectNextLineSpace(pixXStart1, pixXStart1+Pix.wInnerSpace)
        }

        if (gh.gth.pause) {
            paint.color = colorExitWarps
            // If not for this side trick, a teleporter exit could be overdrawn easily by spaces, which are drawn along as contents in reading order
            for (i in 0 until outsideTeleportersCoors.size) {
                canvas.drawText((i+1).toString(),
                    Pix.xCenter(outsideTeleportersCoors[i].x).toFloat(),
                    Pix.yUpSpace(outsideTeleportersCoors[i].y).toFloat(),
                    paint)
            } // TODO existe-il un moyen intelligent d'organiser la numérotation des téléporteurs ?
            // Dans un niveau à quadrants pour l'instant les fruits entrent dans l'ordre dans la paire 1, puis la 9 puis la 5... ça n'a pas tellement de sens ! Si ce n'est le respect du simple ordre lexicographique.
        }
    }
    
    private fun drawDiagonalLine(canvas : Canvas, pixXSource : Float, pixYSource : Float, deltaX : Int, deltaY : Int) {
        canvas.drawLine(pixXSource, pixYSource,
            (pixXSource+deltaX*Pix.roundBorderSpace), (pixYSource+deltaY*Pix.roundBorderSpace), paint)
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

    private fun drawPreAnimations(canvas : Canvas) {
        for (sa : PreAnimation in gh.gth.preAnimationsList) {
            rectDest.left = Pix.xLeftSpace(sa.x)
            rectDest.right = Pix.xRightSpace(sa.x)
            rectDest.top = Pix.yUpSpace(sa.y)
            rectDest.bottom = Pix.yDownSpace(sa.y)
            sa.draw(this, canvas, rectSource, rectDest, paint)
        }
    }

    private fun drawTopImageInBotSpace(xSpaceToDraw : Int, ySpaceToDraw : Int, xSpaceTarget : Int, ySpaceTarget : Int, pixYSplitImgSrc : Int, pixYSplitImgDst : Int, canvas : Canvas) {
        rectDest.left = Pix.xLeftSpace(xSpaceTarget)
        rectDest.right = rectDest.left + Pix.wInnerSpace // No -1 after wInnerSpace
        rectDest.top = Pix.yUpSpace(ySpaceTarget) + pixYSplitImgDst
        rectDest.bottom = Pix.yUpSpace(ySpaceTarget + 1)
        rectSourceVariable.top = 0
        rectSourceVariable.bottom = pixYSplitImgSrc
        drawSpaceContent(xSpaceToDraw, ySpaceToDraw, canvas, rectSourceVariable, rectDest, paint)
    }

    private fun drawBotImageInTopSpace(xSpaceToDraw : Int, ySpaceToDraw : Int, xSpaceTarget : Int, ySpaceTarget : Int, pixYSplitImgSrc : Int, pixYSplitImgDst : Int, canvas : Canvas) {
        rectDest.left = Pix.xLeftSpace(xSpaceTarget)
        rectDest.right = rectDest.left + Pix.wInnerSpace
        rectDest.top = Pix.yUpSpace(ySpaceTarget)
        rectDest.bottom = rectDest.top + pixYSplitImgDst
        rectSourceVariable.top = pixYSplitImgSrc
        rectSourceVariable.bottom = Pix.resourceSide
        drawSpaceContent(xSpaceToDraw, ySpaceToDraw, canvas, rectSourceVariable, rectDest, paint)
    }

    // Note : it should be possible to factorize this with the previous !
    private fun drawBotFruitInSpawnSpace(xSpaceTarget : Int, ySpaceTarget : Int, space : SpaceFiller, pixYSplitImgSrc : Int, pixYSplitImgDst : Int, canvas : Canvas) {
        rectDest.left = Pix.xLeftInnerSpace(xSpaceTarget)
        rectDest.right = rectDest.left + Pix.wInnerSpace
        rectDest.top = Pix.yUpInnerSpace(ySpaceTarget)
        rectDest.bottom = rectDest.top + pixYSplitImgDst
        rectSourceVariable.top = pixYSplitImgSrc
        rectSourceVariable.bottom = Pix.resourceSide
        space.paint(this, canvas, rectSourceVariable, rectDest, paint)
    }

    fun getMode() : MODE {
        return this.scene
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
        } else if (scene == MODE.OPTIONS) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> touchStartOptions()
                MotionEvent.ACTION_MOVE -> touchMoveOptions()
                MotionEvent.ACTION_UP -> touchUpOptions()
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

    private fun touchStartOptions() {
        startingPositionFruitTaken = pixXToOptionalFruitXPick(pixMotionTouchEventX, pixMotionTouchEventY)
        updatePositionCursorTaken(pixXToOptionalCursorXPick(pixMotionTouchEventX, pixMotionTouchEventY))
        if (Pix.rectMode.contains(pixMotionTouchEventX.toInt(), pixMotionTouchEventY.toInt())) {
            when (OptionHandler.mode) {
                GameEnums.GAME_MODE.ACTION_SLOW -> OptionHandler.mode = GameEnums.GAME_MODE.ACTION
                GameEnums.GAME_MODE.CHILL -> OptionHandler.mode = GameEnums.GAME_MODE.ACTION_SLOW
                GameEnums.GAME_MODE.ACTION -> OptionHandler.mode = GameEnums.GAME_MODE.CHILL
            }
        }
    }

    private fun touchMoveOptions() {
        if (startingPositionFruitTaken != NO_FRUIT_TAKEN) {
            movingPositionFruitTaken = pixXToOptionalFruitXDrag(pixMotionTouchEventX, pixMotionTouchEventY)
            if (movingPositionFruitTaken != NO_FRUIT_TAKEN) {
                handleMovingFruits()
            }
            startingPositionFruitTaken = movingPositionFruitTaken
        }
        updatePositionCursorTaken(pixXToOptionalCursorXPick(pixMotionTouchEventX, pixMotionTouchEventY))
    }

    private fun touchUpOptions() {
        startingPositionFruitTaken = NO_FRUIT_TAKEN
        movingPositionFruitTaken = NO_FRUIT_TAKEN
    }

    private fun handleMovingFruits() {
        if (movingPositionFruitTaken > startingPositionFruitTaken) {
            // New = 4, old = 2 : 01234567 -> 01342567
            val tmp = OptionHandler.indexFruitsByPriority[startingPositionFruitTaken]
            for (i in startingPositionFruitTaken until movingPositionFruitTaken) {
                OptionHandler.indexFruitsByPriority[i] = OptionHandler.indexFruitsByPriority[i+1]
            }
            OptionHandler.indexFruitsByPriority[movingPositionFruitTaken] = tmp
        } else if (movingPositionFruitTaken < startingPositionFruitTaken) {
            // New = 2, old = 4 : 01234567 -> 01423567
            val tmp = OptionHandler.indexFruitsByPriority[startingPositionFruitTaken]
            for (i in movingPositionFruitTaken+1..startingPositionFruitTaken) {
                OptionHandler.indexFruitsByPriority[i] = OptionHandler.indexFruitsByPriority[i-1]
            }
            OptionHandler.indexFruitsByPriority[movingPositionFruitTaken] = tmp
        }
    }

    private fun updatePositionCursorTaken(pixXToOptionalCursorXPick : Int) {
        if (pixXToOptionalCursorXPick != NO_FRUIT_TAKEN && startingPositionFruitTaken == NO_FRUIT_TAKEN) { // So we don't accidentaly move both at the same time
            OptionHandler.cursorRandomness = pixXToOptionalCursorXPick
        }
    }

    // Tactile pix
    private fun pixXToSpaceX(pixX : Float): Int {
        return ((pixX.toDouble()-Pix.horizontalPaddingSpace-Pix.getXLeftStartField())/Pix.wSpace).toInt()
    }

    private fun pixYToSpaceY(pixY : Float): Int {
        return ( (pixY.toDouble()-Pix.verticalPaddingSpace-Pix.getYUpStartField())/Pix.hSpace).toInt()
    }

    private fun spaceXToPixXLeft(spaceX : Int) : Int {
        return Pix.getXLeftStartField() + Pix.wSpace * spaceX
    }

    private fun spaceXToPixXRight(spaceX : Int) : Int {
        return Pix.getXLeftStartField() + Pix.wSpace * (spaceX + 1)
    }

    private fun spaceYToPixYUp(spaceY : Int) : Int {
        return Pix.getYUpStartField() + Pix.hSpace * spaceY
    }

    private fun spaceYToPixYDown(spaceY : Int) : Int {
        return Pix.getYUpStartField() + Pix.hSpace * (spaceY + 1)
    }


    // True if two different spaces are being selected
    private fun testActionSwap(spaceX : Int, spaceY : Int) {
        if (spaceX >= 0 && spaceX < gh.xFieldLength && spaceY >= 0 && spaceY < gh.yFieldLength) {
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

    // First, it must be picked through the pick method. Then, it must be released. Variables are returned to NO_FRUIT_TAKEN when released.
    private fun pixXToOptionalFruitXPick(pixMotionTouchEventX: Float, pixMotionTouchEventY: Float): Int {
        if (pixMotionTouchEventY < Pix.yOptionalFruitStart || pixMotionTouchEventY > Pix.yOptionalFruitStart + Pix.hOptionalFruit) {
            return NO_FRUIT_TAKEN
        }
        val l = Math.floorDiv(pixMotionTouchEventX.toInt() - Pix.xOptionalFruitStart, Pix.wOptionalFruit + Pix.gapOptionalFruit)
        if (l < 0 || l >= Constants.RESOURCES_NUMBER_FRUITS || pixMotionTouchEventX - Pix.xOptionalFruitStart - (Pix.wOptionalFruit + Pix.gapOptionalFruit)*l > Pix.wOptionalFruit) {
            return NO_FRUIT_TAKEN
        }
        return l
    }

    private fun pixXToOptionalFruitXDrag(pixMotionTouchEventX: Float, pixMotionTouchEventY: Float): Int {
        if (pixMotionTouchEventY < Pix.yOptionalFruitFrameStart || pixMotionTouchEventY > Pix.yOptionalFruitFrameEnd
            || pixMotionTouchEventX < Pix.xOptionalFruitFrameStart || pixMotionTouchEventX > Pix.xOptionalFruitFrameEnd) {
            return NO_FRUIT_TAKEN
        }
        val pos = Math.floorDiv(pixMotionTouchEventX.toInt() - Pix.xOptionalFruitStart, Pix.wOptionalFruit + Pix.gapOptionalFruit)
        if (pos < 0) {
            return 0
        }
        if (pos >= Constants.RESOURCES_NUMBER_FRUITS) {
            return Constants.RESOURCES_NUMBER_FRUITS-1
        }
        return pos
    }


    private fun pixXToOptionalCursorXPick(pixMotionTouchEventX: Float, pixMotionTouchEventY: Float) : Int {
        if (pixMotionTouchEventY < Pix.yOptionalFruitStart + Pix.hOptionalFruit + 3 || pixMotionTouchEventY > Pix.yOptionalFruitStart + Pix.hOptionalFruit + Pix.hOptionalFruitCursor
            || pixMotionTouchEventX < Pix.xOptionalFruitFrameStart || pixMotionTouchEventX > Pix.xOptionalFruitFrameEnd) {
            return NO_FRUIT_TAKEN
        }
        // Boundary at the middle of fruits !
        // Boundary 0/1 after the half of the first fruit, at Pix.xOptionalFruitStart + width/2
        val pos = Math.floorDiv(pixMotionTouchEventX.toInt() - (Pix.xOptionalFruitStart + Pix.wOptionalFruit/2), Pix.wOptionalFruit + Pix.gapOptionalFruit) + 1
        if (pos < 0) {
            return 0
        }
        if (pos >= Constants.RESOURCES_NUMBER_FRUITS) {
            return Constants.RESOURCES_NUMBER_FRUITS-1
        }
        return pos
    }
}