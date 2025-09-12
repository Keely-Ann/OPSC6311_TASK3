package com.rnk.opsc6311_task3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class Welcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        //If the supportActionBar object is not null, the hide() method is called to hide the support action bar(see Splash Screen - Android Studio,2020)
        supportActionBar?.hide()

        //Declaring buttons(The IIE,2023)
        val imageButtonCategories = findViewById<ImageButton>(R.id.imageButtonCategories)
        val imageButtonAddCategory = findViewById<ImageButton>(R.id.imageButtonAddCategory)
        val imageButtonAddMovie = findViewById<ImageButton>(R.id.imageButtonAddMovie)
        val imageButtonProgressBar = findViewById<ImageButton>(R.id.imageButtonProgressBar)
        val imageButtonGoals = findViewById<ImageButton>(R.id.imageButtonGoals)
        val imageButtonSignOut = findViewById<ImageButton>(R.id.imageButtonSignOut)

        //Button to go to the Categories page(The IIE,2023)
        imageButtonCategories.setOnClickListener {
            val intent = Intent(this,Categories::class.java)
            startActivity(intent)
        }
        //Button to go to the Add Category page(The IIE,2023)
        imageButtonAddCategory.setOnClickListener {
            val intent = Intent(this,AddCategory::class.java)
            startActivity(intent)
        }
        //Button to go to the Add Movie page(The IIE,2023)
        imageButtonAddMovie.setOnClickListener {
            val intent = Intent(this,AddMovie::class.java)
            startActivity(intent)
        }
        //Button to go to the Progress Bar page(The IIE,2023)
        imageButtonProgressBar.setOnClickListener {
            val intent = Intent(this,GoalProgress::class.java)
            startActivity(intent)
        }
        //Button to go to the Goals page(The IIE,2023)
        imageButtonGoals.setOnClickListener {
            val intent = Intent(this,Goals::class.java)
            startActivity(intent)
        }
        //Button to go back to the main screen(The IIE,2023)
        imageButtonSignOut.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}