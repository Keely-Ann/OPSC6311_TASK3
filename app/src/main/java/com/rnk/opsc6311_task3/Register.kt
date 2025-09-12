package com.rnk.opsc6311_task3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //If the supportActionBar object is not null, the hide() method is called to hide the support action bar(see Splash Screen - Android Studio,2020)
        supportActionBar?.hide()

        // Write a message to the database(Android Knowledge,2023)
        var database = Firebase.database
        var reference = database.getReference("Name")

        //Declaring variables(The IIE,2023)
        val txtRegisterUsername = findViewById<TextView>(R.id.txtRegisterUsername)
        val txtRegisterPassword = findViewById<TextView>(R.id.txtRegisterPassword)

        //Declaring buttons(The IIE,2023)
        val btnBack = findViewById<Button>(R.id.btnRegisterBack)
        val btnRegister2 = findViewById<Button>(R.id.btnRegister2)

        //Button to go back to the main screen(The IIE,2023)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //Button when user register(Android Knowledge,2023)
        btnRegister2.setOnClickListener(View.OnClickListener {

            //Validation for text fields to ensure its not left emptied(Android Knowledge,2023)
            if(txtRegisterUsername.text.toString().isEmpty()){
                txtRegisterUsername.error="This is a required field!"
            }
            else if(txtRegisterPassword.text.toString().isEmpty()){
                txtRegisterPassword.error="This is a required field!"
            }
            else{
                //Get an instance of the Firebase database and reference the "register" node(Android Knowledge,2023)
                database = FirebaseDatabase.getInstance()
                reference = database.getReference("users")

                //Get the text entered in the username and password fields(Android Knowledge,2023)
                val username: String = txtRegisterUsername.text.toString()
                val password: String = txtRegisterPassword.text.toString()

                //Check if username already exists in the database(Android Knowledge,2023)
                reference.child(username).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //Username already exists, show error message(Android Knowledge,2023)
                            txtRegisterUsername.error = "Username already exists!"
                            txtRegisterUsername.requestFocus()
                        } else {
                            //UserDetails object with the entered username and password(Android Knowledge,2023)
                            val userDetails = UserDetails(username, password)

                            // Save the user details to the database under the username node(Android Knowledge,2023)
                            reference.child(username).setValue(userDetails)

                            //Displaying a success message using a Toast and redirecting the user to the Login page(Android Knowledge,2023)
                            Toast.makeText(this@Register, "You have registered successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@Register, Login::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                        Toast.makeText(this@Register, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        })
    }
}