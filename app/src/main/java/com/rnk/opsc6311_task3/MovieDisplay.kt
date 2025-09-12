package com.rnk.opsc6311_task3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MovieDisplay : AppCompatActivity() {

    //Declaring variables(The IIE,2023)
    private lateinit var database: DatabaseReference
    private lateinit var adapter: MovieAdapter
    private lateinit var loggedInUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_display)

        // Getting the logged-in user's username(Android Knowledge,2023)
        loggedInUser = Login.loggedInUser

        //If the supportActionBar object is not null, the hide() method is called to hide the support action bar(see Splash Screen - Android Studio,2020)
        supportActionBar?.hide()

        //Declaring and initializing list view variable(The IIE,2023)
        val lstMovieDisplay = findViewById<RecyclerView>(R.id.lstMovieDisplay)

        //Declaring buttons(The IIE,2023)
        val btnAddMovie2 = findViewById<Button>(R.id.btnAddMovie2)
        val btnBack8 = findViewById<Button>(R.id.btnBack8)

        //Button to go to the Add Movie page(The IIE,2023)
        btnAddMovie2.setOnClickListener {
            //Retrieving the selected category from the intent(The IIE,2023)
            val selectedCategory = intent.getStringExtra("category")

            val intent = Intent(this, AddMovie::class.java)

            //Pass the selected category as an extra to the intent(The IIE,2023)
            intent.putExtra("category", selectedCategory)
            startActivity(intent)
        }

        //Button to go back to the Welcome page(The IIE,2023)
        btnBack8.setOnClickListener {
            val intent = Intent(this, Categories::class.java)
            startActivity(intent)
        }

        //Retrieving the selected category from the intent(The IIE,2023)
        val selectedCategory = intent.getStringExtra("category")

        //Finding the TextView in the layout(The IIE,2023)
        val textViewMovieDisplay = findViewById<TextView>(R.id.textViewMovieDisplay)
        textViewMovieDisplay.text = selectedCategory

        //Initializing the Firebase Realtime Database reference(Mamo,2021)
        database = FirebaseDatabase.getInstance().reference.child("movies")

        //Filtering the movies based on the selected category in lstCategory(Stefan,2022)
        val query: Query = database.orderByChild("category").equalTo(selectedCategory)

        //Creating an empty movie list(Chugh,2022)
        val movieList = ArrayList<MovieInfo>()

        //Creating the adapter(The IIE,2023)
        adapter = MovieAdapter(this, movieList)

        //Setting up the RecyclerView with a LinearLayoutManager and the adapter(Stefan,2022)
        lstMovieDisplay.layoutManager = LinearLayoutManager(this)
        lstMovieDisplay.adapter = adapter

        //Adding a divider item decoration to the RecyclerView(Stefan,2022)
        val dividerItemDecoration =
            DividerItemDecoration(lstMovieDisplay.context, LinearLayoutManager.VERTICAL)
        lstMovieDisplay.addItemDecoration(dividerItemDecoration)

        //Adding a ValueEventListener to fetch the movie data from the database(chaitanyamunje,2021)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Clearing the movieList to avoid duplicates(chaitanyamunje,2021)
                movieList.clear()

                //Looping through each snapshot in the dataSnapshot(chaitanyamunje,2021)
                for (snapshot in dataSnapshot.children) {
                    //Getting the MovieInfo object from the snapshot(chaitanyamunje,2021)
                    val movie = snapshot.getValue(MovieInfo::class.java)

                    //Checking if the movie object is not null(chaitanyamunje,2021)
                    if (movie != null && movie.username == loggedInUser) {
                        //Adding the movie to the movieList(chaitanyamunje,2021)
                        movieList.add(movie)
                    }
                }

                //Notifying the adapter that the data has changed(Chugh,2022)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                Toast.makeText(this@MovieDisplay, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
