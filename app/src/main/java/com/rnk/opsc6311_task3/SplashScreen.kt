package com.rnk.opsc6311_task3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //If the supportActionBar object is not null, the hide() method is called to hide the support action bar (see Splash Screen - Android Studio,2020)
        supportActionBar?.hide()

        //The Handler().postDelayed() function is used to delay the execution of a block of code for a specified amount of time (see Splash Screen - Android Studio,2020)
        //In this case, it delays the execution of an intent to start a new activity for 3 seconds (see Splash Screen - Android Studio,2020).
        Handler().postDelayed({
            val intent= Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(intent)
            //Finishes the current activity using the finish() method (see Splash Screen - Android Studio,2020).
            finish()
            //Delay time of 3000 milliseconds (3 seconds). This means that the code block will be executed after a delay of 3 seconds (see Splash Screen - Android Studio,2020)
        },3000)
    }
}