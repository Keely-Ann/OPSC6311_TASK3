package com.rnk.opsc6311_task3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*

class Login : AppCompatActivity() {

    //Declaring EditText variables for login username and password inputs(Android Knowledge,2023)
    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText

    //Declaring a DatabaseReference variable to store a reference to the Firebase Realtime Database(Android Knowledge,2023)
    private lateinit var reference: DatabaseReference

    //Companion object to keep track of current user logged in, in other classes(Android Knowledge,2023)
    companion object {
        var loggedInUser: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //If the supportActionBar object is not null, the hide() method is called to hide the support action bar(see Splash Screen - Android Studio,2020)
        supportActionBar?.hide()

        //Initializing variables(The IIE,2023)
        loginUsername = findViewById(R.id.txtLoginUsername)
        loginPassword = findViewById(R.id.txtLoginPassword)

        //Declaring button(The IIE,2023)
        val btnBack = findViewById<Button>(R.id.btnLoginBack)
        val btnLogin2 = findViewById<Button>(R.id.btnLogin2)

        //Button to go back to the main screen(The IIE,2023)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //Set click listener for login button(Android Knowledge,2023)
        btnLogin2.setOnClickListener {
            if (validateUsername() && validatePassword()) {
                checkUser()
            }
        }
        //Getting reference to Firebase database(Android Knowledge,2023)
        reference = FirebaseDatabase.getInstance().getReference("users")
    }

    //Function to validate username(Android Knowledge,2023)
    private fun validateUsername(): Boolean {
        val validate= loginUsername.text.toString()
        return if (validate.isEmpty()) {
            loginUsername.error = "Username cannot be empty!"
            false
        } else {
            loginUsername.error = null
            true
        }
    }

    //Function to validate password(Android Knowledge,2023)
    private fun validatePassword(): Boolean {
        val validate = loginPassword.text.toString()
        return if (validate.isEmpty()) {
            loginPassword.error = "Password cannot be empty!"
            false
        } else {
            loginPassword.error = null
            true
        }
    }

    //Function to check if user exists in the database(Android Knowledge,2023)
    private fun checkUser() {
        // Get username and password entered by user(Android Knowledge,2023)
        val userUsername = loginUsername.text.toString().trim()
        val userPassword = loginPassword.text.toString().trim()

        //Query the database to check if user exists(Android Knowledge,2023)
        val checkUserDatabase = reference.orderByChild("username").equalTo(userUsername)

        //Add listener for the query result(Android Knowledge,2023)
        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //If user exists, get the password from the database and compare it with the entered password(Android Knowledge,2023)
                    val passwordFromDB = snapshot.child(userUsername).child("password").getValue(String::class.java)
                    if (passwordFromDB == userPassword) {
                        //If passwords match, get the username from the database and the user will be redirected to the Welcome page(Android Knowledge,2023)
                        val usernameFromDB = snapshot.child(userUsername).child("username").getValue(String::class.java)

                        //Keeping track of current user logged in, in other classes(Android Knowledge,2023)
                        Login.loggedInUser = usernameFromDB ?: ""

                        val intent = Intent(this@Login, Welcome::class.java)
                        intent.putExtra("username", usernameFromDB)
                        intent.putExtra("password", passwordFromDB)
                        startActivity(intent)
                    } else {
                        //If passwords do not match, show error message(Android Knowledge,2023)
                        loginPassword.error = "Invalid Credentials!"
                        loginPassword.requestFocus()
                    }
                } else {
                    //If user does not exist, show error message(Android Knowledge,2023)
                    loginUsername.error = "User does not exist!"
                    loginUsername.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                Toast.makeText(this@Login, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
}