package com.rnk.opsc6311_task3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.*

class Categories : AppCompatActivity() {

    //Declaring variables(The IIE,2023)
    private lateinit var database: DatabaseReference
    private lateinit var categoryList: MutableList<CategoryInfo>
    private lateinit var loggedInUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        //If the supportActionBar object is not null, the hide() method is called to hide the support action bar(see Splash Screen - Android Studio,2020)
        supportActionBar?.hide()

        //Getting the logged-in user's username(Android Knowledge,2023)
        loggedInUser = Login.loggedInUser

        //Declaring and Initializing variables(The IIE,2023)
        val lstCategories = findViewById<ListView>(R.id.lstCategories)
        val btnAddCategory2 = findViewById<Button>(R.id.btnAddCategory2)
        val btnDeleteCategory = findViewById<Button>(R.id.btnDeleteCategory)
        val btnBack6 = findViewById<Button>(R.id.btnBack6)

        //Getting a reference to the "categories" node in the Firebase database(Mamo,2021)
        database = FirebaseDatabase.getInstance().reference
        categoryList = mutableListOf()

        //Creating an adapter to display the categories in the ListView(GeeksforGeeks,2022)
        val adapter = CategoryAdapter(this@Categories, R.layout.category_item, categoryList)
        lstCategories.adapter = adapter

        //Button to add a new category(The IIE,2023)
        btnAddCategory2.setOnClickListener {
            val intent = Intent(this, AddCategory::class.java)
            startActivity(intent)
        }

        //Button to delete a category(The IIE,2023)
        btnDeleteCategory.setOnClickListener {
            val intent = Intent(this, DeleteCategory::class.java)
            startActivity(intent)
        }

        //Button to navigate back to the Welcome activity(The IIE,2023)
        btnBack6.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
        }

        //Retrieving categories from the Firebase Realtime Database(chaitanyamunje,2021)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                categoryList.clear()

                //Looping through each category in the "categories" node(chaitanyamunje,2021)
                for (categorySnapshot in dataSnapshot.child("categories").children) {
                    val categoryName = categorySnapshot.child("categoryName").value as? String
                    val categoryUser = categorySnapshot.child("username").value as? String

                    //Checking if the category exists for the logged-in user(Android Knowledge,2023)
                    if (categoryName != null && categoryUser == loggedInUser) {
                        val movieCount = dataSnapshot.child("movies").children.count { movieSnapshot ->
                            val movieCategory = movieSnapshot.child("category").value as? String
                            val movieUser = movieSnapshot.child("username").value as? String
                            movieCategory == categoryName && movieUser == loggedInUser
                        }

                        categoryName?.let {
                            //Creating a new Category object with the category name and movie count(The IIE,2023)
                            val category = CategoryInfo(categoryName, movieCount)

                            //Adding the category to the categoryList(The IIE,2023)
                            categoryList.add(category)
                        }
                    }
                }

                //Notifying the adapter that the data has changed(Chugh,2022)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                Toast.makeText(this@Categories, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })

        //Handling item click in the ListView(Chugh,2022)
        lstCategories.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categoryList[position].name.split(" ")[0]
            val intent = Intent(this, MovieDisplay::class.java)
            intent.putExtra("category", selectedCategory)
            startActivity(intent)
        }
    }
}
