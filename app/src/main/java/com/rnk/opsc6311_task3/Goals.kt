package com.rnk.opsc6311_task3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*

class Goals : AppCompatActivity() {

    //Declaring variables(The IIE,2023)
    private lateinit var database: DatabaseReference
    private lateinit var categoriesList: MutableList<String>
    private lateinit var loggedInUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)
        supportActionBar?.hide()

        //Getting the logged-in user's username(Android Knowledge,2023)
        loggedInUser = Login.loggedInUser

        //Getting an instance of the Firebase Realtime Database reference(Mamo,2021)
        database = FirebaseDatabase.getInstance().reference
        categoriesList = mutableListOf()

        //Declaring and initializing views(The IIE,2023)
        val spinnerSetGoal = findViewById<Spinner>(R.id.spinnerSetGoal)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val btnSetGoal = findViewById<Button>(R.id.btnSetGoal)
        val btnBack4 = findViewById<Button>(R.id.btnBack4)

        //Initializing goal list(The IIE,2023)
        val goalList = ArrayList<Int>(10)
        //Setting values in array list(GeeksforGeeks, 2022)
        goalList.addAll(1..10)

        //The CustomSpinnerAdapter objects is created, which is a type of adapter that works with the list views to provide views
        //for the items in the array list(Chugh,2022)
        val adapter1 = CustomSpinnerAdapter(this,goalList.map { it.toString() })
        //The spinnerSetGoal object is used to display a list of numbers in the spinner(Chugh,2022)
        spinnerSetGoal.adapter = adapter1

        //The CustomSpinnerAdapter objects is created, which is a type of adapter that works with the list views to provide views
        //for the items in the array list(Chugh,2022)
        val adapter2 = CustomSpinnerAdapter(this,categoriesList)
        //The spinnerCategory object is used to display the list of categories(Chugh,2022)
        spinnerCategory.adapter = adapter2

        //Retrieving the selected category from the intent(The IIE,2023)
        val selectedCategory = intent.getStringExtra("category")

        //Retrieving categories from Firebase database(Mamo,2021)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val categoriesRef = database.reference.child("categories")

        //Checking if the user is logged in and has categories(Android Knowledge,2023)
        if (loggedInUser != null) {
            val categoriesRef = database.reference.child("categories")
            categoriesRef.orderByChild("username").equalTo(loggedInUser)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            //User has no categories, show AlertDialog prompting to add a category(Chugh,2022)
                            val builder = AlertDialog.Builder(this@Goals)
                            builder.setTitle("Add Category")
                                .setMessage("You need to add a category before adding a movie.")
                                .setPositiveButton("Add Category") { _, _ ->
                                    //Redirecting to AddCategory activity(Chugh,2022)
                                    val intent = Intent(this@Goals, AddCategory::class.java)
                                    startActivity(intent)
                                }
                                .setNegativeButton("Cancel") { dialog, _ ->
                                    //Closing the dialog and finish the current activity(Chugh,2022)
                                    dialog.dismiss()
                                    finish()
                                }
                                .setCancelable(false)
                                .show()
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                        Toast.makeText(this@Goals, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        //Fetching categories from database(chaitanyamunje,2021)
        categoriesRef.orderByChild("username").equalTo(loggedInUser).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                categoriesList.clear()

                //Looping through each child snapshot under "categories" node(chaitanyamunje,2021)
                for (categorySnapshot in dataSnapshot.children) {
                    val categoryName = categorySnapshot.child("categoryName").value as? String
                    if (categoryName != null) {
                        categoriesList.add(categoryName)
                    }
                }

                //Notifying the adapter that the data set has changed(Chugh,2022)
                adapter2.notifyDataSetChanged()

                //Finding the index of the selected category in the adapter(The IIE,2023)
                val updatedCategoryIndex = categoriesList.indexOf(selectedCategory)

                //Setting the selection of the spinner to the selected category(The IIE,2023)
                spinnerCategory.setSelection(updatedCategoryIndex)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                Toast.makeText(this@Goals, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })

        //Button click listener to set a goal(The IIE,2023)
        btnSetGoal.setOnClickListener {
            val selectedGoal = spinnerSetGoal.selectedItem.toString().toInt()
            val selectedCategory2 = spinnerCategory.selectedItem.toString()

            //Checking if goal already exists for the category(The IIE,2023)
            hasGoalForCategory(selectedCategory2) { hasGoal ->
                if (hasGoal) {
                    showErrorDialog("Goal for the category already exists!")
                } else {
                    addGoal(selectedGoal, selectedCategory2, loggedInUser)
                }
            }
        }


        //Button click listener to go back(The IIE,2023)
        btnBack4.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
        }
    }

    //Function to add a new goal(The IIE,2023)
    private fun addGoal(goals: Int, category: String, username: String) {
        database.child("goals").child(category).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Goal for the category already exists(Chugh,2022)
                    showErrorDialog("Goal for the category already exists")
                } else {
                    //Adding the new goal(The IIE,2023)
                    val goalData = hashMapOf(
                        "goals" to goals,
                        "category" to category,
                        "username" to username
                    )
                    val newGoalRef = database.child("goals").push() // Generate a unique key for the goal
                    newGoalRef.setValue(goalData).addOnSuccessListener {
                            Toast.makeText(this@Goals, "User successfully set a goal for a category", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@Goals, GoalProgress::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { error ->
                            showErrorDialog("Failed to add the goal: ${error.message}")
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                Toast.makeText(this@Goals, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Function to check if a goal already exists(The IIE,2023)
    private fun hasGoalForCategory(category: String, callback: (Boolean) -> Unit) {
        //Querying the database to check if any category name matches the new category name for the logged-in user(Coelho,M)
        database.child("goals").orderByChild("username").equalTo(loggedInUser).addListenerForSingleValueEvent(object : ValueEventListener {
                //This function is triggered when the data is successfully retrieved(Coelho,M)
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var hasGoal = false

                    //Iterating through each child snapshot under the "goals" node(chaitanyamunje,2021)
                    for (goalSnapshot in dataSnapshot.children) {
                        val goalCategory = goalSnapshot.child("category").value as? String
                        //Checking if the goal category matches the specified category(chaitanyamunje,2021)
                        if (goalCategory == category) {
                            hasGoal = true
                            break
                        }
                    }
                    //Invoking the callback with the result indicating if a goal exists for the category(chaitanyamunje,2021)
                    callback(hasGoal)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    //Displaying a toast message if an error occurs while querying the database(The IIE,2023)
                    Toast.makeText(this@Goals, "Error occurred", Toast.LENGTH_SHORT).show()
                }
            })
    }
    //Function to show error dialog(Chugh,2022)
    private fun showErrorDialog(errorMessage: String) {
        //Creating an AlertDialog using AlertDialog.Builder(Chugh,2022)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Error") //Setting the title of the dialog
            .setMessage(errorMessage) //Setting the error message to be displayed(Chugh,2022)
            .setPositiveButton("OK") { dialog, _ ->
                //Positive button click listener, dismiss the dialog when clicked(Chugh,2022)
                dialog.dismiss()
            }
            .create()

        alertDialog.show() //Showing the error dialog(Chugh,2022)
    }
}
