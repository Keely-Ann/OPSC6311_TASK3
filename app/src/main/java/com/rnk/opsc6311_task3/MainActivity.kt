package com.rnk.opsc6311_task3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //If the supportActionBar object is not null, the hide() method is called to hide the support action bar(see Splash Screen - Android Studio,2020)
        supportActionBar?.hide()

        //Declaring buttons(The IIE,2023)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        //Once clicking on the register button the user will be redirected to the register page(The IIE,2023)
        btnRegister.setOnClickListener()
        {
            val intent = Intent(this,Register::class.java)
            startActivity(intent)
        }
        //Once clicking on the login button the user will be redirected to the login page(The IIE,2023)
        btnLogin.setOnClickListener()
        {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }
    }
}