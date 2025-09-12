package com.rnk.opsc6311_task3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*

class DeleteCategory : AppCompatActivity() {

    //Declaring and initializing variables(The IIE,2023)
    private lateinit var database: DatabaseReference
    private lateinit var categoryList: MutableList<String>
    private lateinit var loggedInUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_category)

        //If the supportActionBar object is not null, the hide() method is called to hide the support action bar(see Splash Screen - Android Studio,2020)
        supportActionBar?.hide()

        //Getting the logged-in user's username(Android Knowledge,2023)
        loggedInUser = Login.loggedInUser

        //Declaring and Initializing list view variable(The IIE,2023)
        val lstDeleteCategory = findViewById<ListView>(R.id.lstDeleteCategory)

        //Declaring and Initializing button(The IIE,2023)
        val btnBack7 = findViewById<Button>(R.id.btnBack7)

        //Getting a reference to the "categories" node in the Firebase database(Mamo,2021)
        database = FirebaseDatabase.getInstance().reference.child("categories")
        categoryList = mutableListOf()

        //The ArrayAdapter object is created, which is a type of adapter that works with the lstCategories list view to provide views
        //for the items in the array list(GeeksforGeeks,2022)
        val adapter = ArrayAdapter(this@DeleteCategory, android.R.layout.simple_list_item_1, categoryList)

        //The lstCategories object is used to display the list of categories(GeeksforGeeks,2022)
        lstDeleteCategory.adapter = adapter

        //Setting item click listener for the list view(The IIE,2023)
        lstDeleteCategory.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedCategory = categoryList[position]

            //Creating an alert dialog to confirm the deletion of the category(Chugh,2022)
            AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this category?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, _ ->
                    //Deleting the category with its movies and goal from the database and notify the adapter(Chugh,2022)
                    deleteMovies(selectedCategory)
                    deleteCategory(selectedCategory)
                    deleteGoal(selectedCategory)
                    adapter.notifyDataSetChanged()

                    //Creating an intent to navigate to the Categories activity once category is deleted(The IIE,2023)
                    val intent = Intent(this, Categories::class.java)
                    startActivity(intent)

                    //Displaying a toast message indicating successful deletion(The IIE,2023)
                    Toast.makeText(this, "Category successfully deleted", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { dialog, _ ->
                    //Do nothing if "No" is selected(Chugh,2022)
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        //Button to go back to the Categories page(The IIE,2023)
        btnBack7.setOnClickListener {
            val intent = Intent(this, Categories::class.java)
            startActivity(intent)
        }

        //Retrieving categories from the Firebase Realtime Database(chaitanyamunje,2021)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                categoryList.clear()

                //Looping through each category in the database and add it to the local list(chaitanyamunje,2021)
                for (categorySnapshot in dataSnapshot.children) {
                    val categoryName = categorySnapshot.child("categoryName").value as? String
                    categoryName?.let {
                        val username = categorySnapshot.child("username").value as? String
                        if (username == loggedInUser) {
                            categoryList.add(it)
                        }
                    }
                }
                //Notifying the adapter that the data has changed(Chugh,2022)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                Toast.makeText(this@DeleteCategory, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Function to delete movies for a category which is being deleted from the database(chaitanyamunje,2021)
    private fun deleteMovies(categoryName: String) {
        val categoryRef = database.child(categoryName)

        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(categorySnapshot: DataSnapshot) {
                //Removing the category from the database using the reference to the categorySnapshot(chaitanyamunje,2021)
                categorySnapshot.ref.removeValue()

                //Deleting the movies associated with the category and the logged-in user(chaitanyamunje,2021)
                val moviesRef = FirebaseDatabase.getInstance().reference.child("movies")
                val moviesQuery = moviesRef.orderByChild("category").equalTo(categoryName)

                moviesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(moviesSnapshot: DataSnapshot) {
                        //Looping through the children of the dataSnapshot (categories that match the query)(chaitanyamunje,2021)
                        for (movieSnapshot in moviesSnapshot.children) {
                            val movieCategory = movieSnapshot.child("category").value as? String
                            val movieUser = movieSnapshot.child("username").value as? String

                            if (movieCategory == categoryName && movieUser == loggedInUser) {
                                //Removing the movie from the database using the reference to the movieSnapshot(chaitanyamunje,2021)
                                movieSnapshot.ref.removeValue()
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                        Toast.makeText(this@DeleteCategory, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                Toast.makeText(this@DeleteCategory, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Function to delete category from database(chaitanyamunje,2021)
    private fun deleteCategory(categoryName: String) {
        //Creating a reference to the "categories" node in the database(chaitanyamunje,2021)
        val categoriesRef = FirebaseDatabase.getInstance().reference.child("categories")

        //Creating a query to find the category in the database based on its name and the logged-in user(chaitanyamunje,2021)
        val query = categoriesRef.orderByChild("categoryName").equalTo(categoryName)

        //Adding a ValueEventListener to the query
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Looping through the children of the dataSnapshot (categories that match the query)(chaitanyamunje,2021)
                for (categorySnapshot in dataSnapshot.children) {
                    val categoryUser = categorySnapshot.child("username").value as? String

                    if (categoryUser == loggedInUser) {
                        //Removing the category from the database using the reference to the categorySnapshot(chaitanyamunje,2021)
                        categorySnapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                Toast.makeText(this@DeleteCategory, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
    //Function to delete a goal for a category which is being deleted from the database(chaitanyamunje,2021)
    private fun deleteGoal(categoryName: String) {
        //Creating a reference to the "categories" node in the database(chaitanyamunje,2021)
        val categoriesRef = FirebaseDatabase.getInstance().reference.child("goals")

        //Creating a query to find the category in the database based on its name and the logged-in user(chaitanyamunje,2021)
        val query = categoriesRef.orderByChild("category").equalTo(categoryName)

        //Adding a ValueEventListener to the query
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Looping through the children of the dataSnapshot (categories that match the query)(chaitanyamunje,2021)
                for (categorySnapshot in dataSnapshot.children) {
                    val categoryUser = categorySnapshot.child("username").value as? String

                    if (categoryUser == loggedInUser) {
                        //Removing the goal from the database using the reference to the categorySnapshot(chaitanyamunje,2021)
                        categorySnapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                Toast.makeText(this@DeleteCategory, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
