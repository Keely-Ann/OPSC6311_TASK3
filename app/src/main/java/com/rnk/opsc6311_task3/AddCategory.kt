package com.rnk.opsc6311_task3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*

class AddCategory : AppCompatActivity() {

    //Declaring variables(The IIE,2023)
    private lateinit var database: DatabaseReference
    private lateinit var txtCategoryName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        //If the supportActionBar object is not null, the hide() method is called to hide the support action bar(see Splash Screen - Android Studio,2020)
        supportActionBar?.hide()

        //Initializing variables(The IIE,2023)
        txtCategoryName = findViewById(R.id.txtCategoryName)

        //Declaring and Initializing buttons(The IIE,2023)
        val btnBack = findViewById<Button>(R.id.btnBack)
        val btnAddCategory = findViewById<Button>(R.id.btnAddCategory)

        //Button to go back to the Welcome activity(The IIE,2023)
        btnBack.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
        }

        //Getting a reference to the "categories" node in the Firebase database(Mamo,2021)
        database = FirebaseDatabase.getInstance().reference.child("categories")

        //Button to add a new category to the database(The IIE,2023)
        btnAddCategory.setOnClickListener {
            val categoryName = txtCategoryName.text.toString().trim()

            //Error handling to ensure all fields are filled in (Ali,2020)
            if (categoryName.isNotEmpty()) {
                //Checking if the category already exists(Android Knowledge,2023)
                checkCategory(categoryName) { categoryExists ->
                    if (categoryExists) {
                        Toast.makeText(this@AddCategory, "Category already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        //Adding a new category to the database(Android Knowledge,2023)
                        val categoryData = HashMap<String, String>()
                        categoryData["categoryName"] = categoryName
                        categoryData["username"] = Login.loggedInUser
                        database.push().setValue(categoryData)

                        Toast.makeText(this@AddCategory, "Category successfully added", Toast.LENGTH_SHORT).show()

                        //Redirecting the user to the Categories activity(The IIE,2023)
                        val intent = Intent(this@AddCategory, Categories::class.java)
                        startActivity(intent)

                        //Clearing the category name field(The IIE,2023)
                        txtCategoryName.setText("")
                    }
                }
            } else {
                //Displaying a toast message if the category name field is empty(The IIE,2023)
                Toast.makeText(this@AddCategory, "Enter required field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Checking if a category already exists in the database (case-insensitive)(The IIE,2023)
    private fun checkCategory(categoryName: String, callback: (Boolean) -> Unit) {
        //Querying the database to check if any category name matches the new category name for the logged-in user(Coelho,M)
        database.orderByChild("categoryName").equalTo(categoryName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Checking if any child node key (category name) matches the new category name (case-insensitive)
                //for a logged in user(chaitanyamunje,2021)
                val categoryExists = dataSnapshot.children.any { it.child("username").getValue(String::class.java) == Login.loggedInUser }
                callback(categoryExists)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Displaying a toast message if an error occurs while querying the database(The IIE,2023)
                Toast.makeText(this@AddCategory, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
