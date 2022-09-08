package com.example.fruitfall

import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.fruitfall.level.LevelManager
import java.util.logging.Level

// Almost everything was taken from : https://developer.android.com/codelabs/advanced-android-kotlin-training-canvas#2
// Main exception is "credits here".
// Also, I wasted some time in MyCanvasView by putting Bitmap.createBitmap in the wrong place. I had to download the main code to realize my foul.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        LevelManager.init()


        //var linearLayout = findViewById<LinearLayout>(R.id.constraintLayoutMain)
        setContentView(R.layout.activity_main)




        // How to make a "custom view" into a layout :
        // https://stackoverflow.com/questions/10410616/how-to-add-custom-view-to-the-layout
        // Now I can separate views and layouts
        val c : LinearLayout = findViewById(R.id.canvasLayout)
        val myView = MyCanvasView(this)
        c.addView(myView)
        myView.startLevel()

        findViewById<Button>(R.id.buttonModePrev).setOnClickListener {
            if (LevelManager.levelNumber != 0) {
                LevelManager.levelNumber--;
                myView.startLevel();
            }
        }
        findViewById<Button>(R.id.buttonModeRestart).setOnClickListener {
            myView.startLevel();
        }
        findViewById<Button>(R.id.buttonModeTolerance).setOnClickListener {
            myView.setTolerance();
        }
        findViewById<Button>(R.id.buttonModeFallSpeed).setOnClickListener {
            myView.switchFallSpeed();
        }
        findViewById<Button>(R.id.buttonModeNext).setOnClickListener {
            if (LevelManager.levelNumber != LevelManager.levelLists.size-1) {
                LevelManager.levelNumber++;
                myView.startLevel();
            }
        }

        // IMPORTANT : order must be the same for items and for levels.
        // Credits : https://stackoverflow.com/questions/13784088/setting-popupmenu-menu-items-programmatically
        val menu = PopupMenu(this, myView)
        var levelNumber = 0;
        for (level in LevelManager.levelLists) {
            menu.getMenu().add(Menu.NONE, levelNumber, levelNumber, level.title)
            levelNumber++
        }
        findViewById<Button>(R.id.buttonLevelChoice).setOnClickListener {

            menu.show()
        }
        menu.setOnMenuItemClickListener { item ->
            LevelManager.levelNumber = item.itemId
            myView.startLevel()
            true
        }
    }
}