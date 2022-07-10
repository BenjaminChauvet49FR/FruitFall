package com.example.fruitfall

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

// Almost everything was taken from : https://developer.android.com/codelabs/advanced-android-kotlin-training-canvas#2
// Main exception is "credits here".
// Also, I wasted some time in MyCanvasView by putting Bitmap.createBitmap in the wrong place. I had to download the main code to realize my foul.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //var linearLayout = findViewById<LinearLayout>(R.id.constraintLayoutMain)
        setContentView(R.layout.activity_main)




        // How to make a "custom view" into a layout :
        // https://stackoverflow.com/questions/10410616/how-to-add-custom-view-to-the-layout
        // Now I can separate views and layouts
        val c : LinearLayout = findViewById(R.id.canvasLayout)
        val myView: MyCanvasView = MyCanvasView(this)
        c.addView(myView)


        findViewById<Button>(R.id.buttonMode4).setOnClickListener {
            myView.startLevel(4)
        }
        findViewById<Button>(R.id.buttonMode6).setOnClickListener {
            myView.startLevel(6)
        }
        findViewById<Button>(R.id.buttonMode8).setOnClickListener {
            myView.startLevel(8)
        }

        //v.setV



    }
}