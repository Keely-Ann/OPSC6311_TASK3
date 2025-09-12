package com.rnk.opsc6311_task3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class GoalProgress : AppCompatActivity() {

    //Declaring variable(The IIE,2023)
    private lateinit var database: DatabaseReference
    private lateinit var goalList: MutableList<GoalsInfo>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalProgressAdapter
    private lateinit var loggedInUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_progress)

        //If the supportActionBar object is not null, the hide() method is called to hide the support action bar (see Splash Screen - Android Studio, 2020)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance().reference //Getting an instance of the Firebase Realtime Database reference(Mamo,2021)
        goalList = mutableListOf() //Creating an empty mutable list to store goal information(The,IIE,2023)
        recyclerView = findViewById(R.id.recyclerViewGoals) //Initializing variable(The,IIE,2023)
        recyclerView.layoutManager = LinearLayoutManager(this) //Setting the layout manager for the RecyclerView as a linear layout manager(Stefan,2022)
        adapter = GoalProgressAdapter(this, goalList) //Creating an instance of the GoalProgressAdapter, passing the activity and goalList as parameters(Stefan,2022)
        recyclerView.adapter = adapter //Setting the adapter for the RecyclerView(Chugh,2022)


        //Getting the logged-in user's username(Android Knowledge,2023)
        loggedInUser = Login.loggedInUser

        //Declaring and Initializing buttons (The IIE, 2023)
        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnBack5 = findViewById<Button>(R.id.btnBack5)

        //Button will redirect the user to the Welcome page (The IIE, 2023)
        btnHome.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent) // User will be redirected to Welcome page (The IIE, 2023)
        }

        //Button will redirect the user to the Goals page (The IIE, 2023)
        btnBack5.setOnClickListener {
            val intent = Intent(this, Goals::class.java)
            startActivity(intent) // User will be redirected to Goals page (The IIE, 2023)
        }

        //Adding a divider to the RecyclerView(Stefan,2022)
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        //Adding a value event listener to the database reference(chaitanyamunje,2021)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                goalList.clear()

                val moviesRef = dataSnapshot.child("movies")

                //Iterating through the "goals" child nodes(chaitanyamunje,2021)
                for (goalSnapshot in dataSnapshot.child("goals").children) {
                    val category = goalSnapshot.child("category").getValue(String::class.java)
                    val goal = goalSnapshot.child("goals").getValue(Int::class.java)
                    val user = goalSnapshot.child("username").value as? String

                    if (category != null && user == loggedInUser) {
                        //Counting the number of movies for the logged-in user's username in the current category(chaitanyamunje,2021)
                        val movieCount = moviesRef.children.count { movieSnapshot ->
                            val movieCategory = movieSnapshot.child("category").value as? String
                            val movieUser = movieSnapshot.child("username").value as? String

                            movieCategory == category && movieUser == loggedInUser
                        }
                        // Creating a GoalsInfo object with the category, movie count, and goal(chaitanyamunje,2021)
                        val goalInfo = GoalsInfo(category, movieCount, goal ?: 0)
                        goalList.add(goalInfo)
                    }
                }
                adapter.notifyDataSetChanged() //Notifying the adapter that the data has changed(Chugh,2022)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                Toast.makeText(this@GoalProgress, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
