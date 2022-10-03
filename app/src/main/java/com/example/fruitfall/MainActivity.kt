package com.example.fruitfall

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.fruitfall.level.LevelData
import com.example.fruitfall.level.LevelManager

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
                LevelManager.levelNumber--
                myView.startLevel()
            }
        }
        findViewById<Button>(R.id.buttonModeRestart).setOnClickListener {
            myView.startLevel()
        }
        findViewById<Button>(R.id.buttonModeTolerance).setOnClickListener {
            myView.setTolerance()
        }
        findViewById<Button>(R.id.buttonModeFallSpeed).setOnClickListener {
            myView.switchFallSpeed()
        }
        findViewById<Button>(R.id.buttonModeNext).setOnClickListener {
            if (LevelManager.levelNumber != LevelManager.levelLists.size-1) {
                LevelManager.levelNumber++
                myView.startLevel()
            }
        }

        findViewById<Button>(R.id.buttonPause).setOnClickListener {
            myView.setPause()
        }

        // Level menus

        // Method "levelToCategory" : a method that returns a number between 0 and (number of categories - 1) included. Supposed to be the index of category.
        // Credits :
        // General : https://stackoverflow.com/questions/13784088/setting-popupmenu-menu-items-programmatically
        // For submenus : https://www.tabnine.com/code/java/methods/android.view.SubMenu/add
        //  For lambdas : https://kotlinlang.org/docs/lambdas.html#instantiating-a-function-type

        fun setMenu(list : List<String>, myView : MyCanvasView, levelToCategory: (LevelData) -> Int) : PopupMenu {
            val generalMenu = PopupMenu(this, myView)
            val subMenus : MutableList<PopupMenu> = mutableListOf()

            // General menu part
            for ((i, categoryName) in list.withIndex()) {
                generalMenu.menu.add(i, i, i, categoryName)
                subMenus.add(PopupMenu(this, myView)) // Credits : https://stackoverflow.com/questions/37913252/kotlins-list-missing-add-remove-map-missing-put-etc
            }

            generalMenu.setOnMenuItemClickListener { item ->
                subMenus[item.itemId].show()
                true
            }

            // Sub menu part
            var cat : Int
            for ((levelNumber, level) in LevelManager.levelLists.withIndex()) {
                cat = levelToCategory(level)
                subMenus[cat].menu.add(cat, levelNumber, levelNumber, level.title)
            }

            for (subMenu in subMenus) {
                subMenu.setOnMenuItemClickListener { item ->
                    LevelManager.levelNumber = item.itemId
                    myView.startLevel()
                    true
                }
            }


            return generalMenu
        }

        // Back to inputs

        val levelToArbitraryCat : (LevelData) -> Int = { level: LevelData -> level.category }
        val menuCategory : PopupMenu = setMenu(listOf("Future levels", "Fun", "Debug"), myView, levelToArbitraryCat )

        // Deploy'em all !
        // If not deployed first, some levels may have their kind + other data not set !
        for (level in LevelManager.levelLists) {
            level.deploy()
        }

        fun levelToTypeFunction(level : LevelData) : Int {
            return when (level.goalKind) {
                GameEnums.GOAL_KIND.BASKETS -> 0
                GameEnums.GOAL_KIND.NUTS -> 1
                GameEnums.GOAL_KIND.ORDERS -> 2
                else -> -1
            }
        }
        val levelToType : (LevelData) -> Int = { level: LevelData -> levelToTypeFunction(level)}
        val menuType : PopupMenu = setMenu(listOf("Paniers", "Noix", "Commandes"), myView, levelToType )

        fun levelToNumberColoursFunction(level : LevelData) : Int {
            val colBase = level.fruitColours
            val colReduc = level.reductionFruitColours
            if (colBase == 4) {
                return 0
            }
            if (colBase == 5) {
                if (colReduc == 0) {
                    return 1
                } else if (colReduc == 1) {
                    return 3
                }
            }
            if (colBase == 6) {
                when (colReduc) {
                    0 -> {
                        return 2
                    }
                    2 -> {
                        return 4
                    }
                    1 -> {
                        return 5
                    }
                }
            }
            return 6
        }
        val levelToNumberColours : (LevelData) -> Int = { level: LevelData -> levelToNumberColoursFunction(level)}
        val menuTypeColours : PopupMenu = setMenu(listOf("4 couleurs", "5 couleurs", "6 couleurs", "5 à 4 couleurs", "6 à 4 couleurs", "6 à 5 couleurs", "Autres"), myView, levelToNumberColours )

        // Now, the general setup !
        val mainMenu = PopupMenu(this, myView)
        findViewById<Button>(R.id.buttonLevelChoice).setOnClickListener {
            mainMenu.show()
        }

        val menusNames = arrayOf("Par catégorie", "Par type", "Par nb couleurs")
        for(i in 0 until menusNames.size) {
            mainMenu.menu.add(i, i, i, menusNames[i])
        }

        val allMenus = arrayOf(menuCategory, menuType, menuTypeColours)
        mainMenu.setOnMenuItemClickListener { item ->
            allMenus[item.itemId].show()
            true
        }
    }
}