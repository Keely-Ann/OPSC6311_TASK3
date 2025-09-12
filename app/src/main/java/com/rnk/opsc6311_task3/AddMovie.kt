package com.rnk.opsc6311_task3

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class AddMovie : AppCompatActivity() {

    //Declaring variables(The IIE,2023)
    private lateinit var txtMovieName: EditText
    private lateinit var imageView: ImageView
    private lateinit var txtDescription: EditText
    private lateinit var spinnerSelectCategory: Spinner
    private lateinit var editTextDate2: EditText
    private lateinit var btnPickDate: Button
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var categoriesList: ArrayList<String>
    private lateinit var database: FirebaseDatabase

    //Setting desired width and height for image resizing(Suryavanshi,2021)
    private val desiredWidth = 500
    private val desiredHeight = 500

    //Setting request code for starting an activity for result(a_local_nobody,2022)
    private val REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)

        //Initializing variable(The IIE,2023)
        database = FirebaseDatabase.getInstance()

        //If the supportActionBar object is not null, the hide() method is called to hide the support action bar(see Splash Screen - Android Studio,2020)
        supportActionBar?.hide()

        //Initializing variables(The IIE,2023)
        txtMovieName = findViewById(R.id.txtMovieName)
        imageView = findViewById(R.id.imageView4)
        txtDescription = findViewById(R.id.txtDescription3)
        editTextDate2 = findViewById(R.id.editTextDate2)
        spinnerSelectCategory = findViewById(R.id.spinnerSelectCategory)

        //Declaring and Initializing buttons(The IIE,2023)
        val btnUploadImage = findViewById<Button>(R.id.btnUploadImage)
        val btnAddMovie = findViewById<Button>(R.id.btnAddMovie)
        btnPickDate = findViewById(R.id.btnPickDate)
        val btnBack3 = findViewById<Button>(R.id.btnBack3)
        // Set an OnClickListener for the button
        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        //Button to upload image(The IIE,2023)
        btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }

        categoriesList = ArrayList()

        //Using CustomSpinnerAdapter using the categoriesList as the data source and the default layout for spinner items(Chugh,2022)
        adapter = CustomSpinnerAdapter(spinnerSelectCategory.context, categoriesList)

        //Setting the adapter for the spinner to display the categories(Chugh,2022)
        spinnerSelectCategory.adapter = adapter

        //Retrieving the selected category from the intent(The IIE,2023)
        val selectedCategory = intent.getStringExtra("category")

        //Finding the index of the selected category in the adapter(The IIE,2023)
        val categoryIndex = categoriesList.indexOf(selectedCategory)

        //Setting the selection of the spinner to the selected category(The IIE,2023)
        spinnerSelectCategory.setSelection(categoryIndex)

        //Retrieving categories from Firebase database(Mamo,2021)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val categoriesRef = database.reference.child("categories")

        //Getting the logged-in user's username(Android Knowledge,2023)
        val loggedInUser = Login.loggedInUser

        //Checking if the user is logged in and has categories(Android Knowledge,2023)
        if (loggedInUser != null) {
            val categoriesRef = database.reference.child("categories")
            categoriesRef.orderByChild("username").equalTo(loggedInUser)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            //User has no categories, show AlertDialog prompting to add a category(Chugh,2022)
                            val builder = AlertDialog.Builder(this@AddMovie)
                            builder.setTitle("Add Category")
                                .setMessage("You need to add a category before adding a movie.")
                                .setPositiveButton("Add Category") { _, _ ->
                                    //Redirecting to AddCategory activity(Chugh,2022)
                                    val intent = Intent(this@AddMovie, AddCategory::class.java)
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
                        Toast.makeText(this@AddMovie, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        //This method retrieves the category names from the database and updates the spinner with the fetched categories(chaitanyamunje,2021)
        categoriesRef.orderByChild("username").equalTo(loggedInUser)
            .addListenerForSingleValueEvent(object : ValueEventListener {
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
                    adapter.notifyDataSetChanged()

                    //Finding the index of the selected category in the adapter(The IIE,2023)
                    val updatedCategoryIndex = categoriesList.indexOf(selectedCategory)

                    //Setting the selection of the spinner to the selected category(The IIE,2023)
                    spinnerSelectCategory.setSelection(updatedCategoryIndex)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //Handling error if the database operation is canceled(Android Knowledge,2023)
                }
            })

        //Inside the onCreate() method of AddMovie activity(see Splash Screen - Android Studio,2020)
        val handler = Handler()

        //Button to add a movie to the database(The IIE,2023)
        btnAddMovie.setOnClickListener {
            // Checking if all required fields are filled(Ali,2020)
            if (txtMovieName.text.toString().isEmpty() || txtDescription.text.toString()
                    .isEmpty() || spinnerSelectCategory.selectedItem.toString()
                    .isEmpty() || editTextDate2.text.toString().isEmpty()
            ) {
                Toast.makeText(this, "Enter required field", Toast.LENGTH_SHORT).show()
            } else {
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap

                // Error handling so that user uploads an image(The IIE,2023)
                if (bitmap.sameAs((resources.getDrawable(android.R.drawable.ic_menu_gallery) as BitmapDrawable).bitmap)) {
                    Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val name = txtMovieName.text.toString()
                val description = txtDescription.text.toString()
                val category = spinnerSelectCategory.selectedItem.toString()
                val date = editTextDate2.text.toString()

                //Creating a new MovieInfo object with the retrieved data(The IIE,2023)
                val movieInfo = MovieInfo.fromBitmap(bitmap)
                movieInfo.name = name
                movieInfo.description = description
                movieInfo.category = category
                movieInfo.date = date

                //Getting a reference to the "movies" node in the Firebase database(Android Knowledge,2023)
                val moviesRef = database.getReference("movies")

                //Getting the logged-in user's username(Android Knowledge,2023)
                movieInfo.username = loggedInUser

                //Generating a unique key for the movie(Android Knowledge,2023)
                val movieKey = moviesRef.push().key

                //Checking if the key was generated successfully(Android Knowledge,2023)
                if (movieKey != null) {
                    //Inserting the movieInfo object into the "movies" node with the movie name as the key(Android Knowledge,2023)
                    moviesRef.child(movieKey)
                        .setValue(movieInfo, object : DatabaseReference.CompletionListener {
                            override fun onComplete(
                                databaseError: DatabaseError?, databaseReference: DatabaseReference
                            ) {
                                if (databaseError != null) {
                                    //Error handling if the movie insertion fails(Android Knowledge,2023)
                                    Toast.makeText(this@AddMovie, "Failed to add movie: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                                } else {
                                    //Once the user successfully added a movie, they will be redirected to the MovieDisplay page(Ali,2020)
                                    val intent = Intent(this@AddMovie, MovieDisplay::class.java)

                                    //Passing the selected category as an extra to the intent(The IIE,2023)
                                    intent.putExtra("category", category)

                                    //Achievement system(Android Knowledge,2023)
                                    moviesRef.orderByChild("username").equalTo(loggedInUser)
                                        .addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                when (dataSnapshot.childrenCount) {
                                                    1L -> {
                                                        //Starter: When a user add the first movie to the app it will show a pop-up when the user adds
                                                        //their 1st movie(Chugh,2022)
                                                        val builder =
                                                            AlertDialog.Builder(this@AddMovie)
                                                        builder.setTitle("Congratulations!")
                                                        builder.setMessage("You have added your 1st movie.")
                                                        val alertDialog = builder.create()
                                                        alertDialog.window?.setBackgroundDrawable(
                                                            ColorDrawable(
                                                                ContextCompat.getColor(
                                                                    this@AddMovie, R.color.yellow
                                                                )
                                                            )
                                                        )
                                                        alertDialog.show()
                                                        showConfettiAnimation()//Displaying confetti animation(Patel,2021)

                                                        //Delay the redirection to MovieDisplay page by 5 seconds(see Splash Screen - Android Studio,2020)
                                                        handler.postDelayed({
                                                            startActivity(intent)
                                                        }, 5000)
                                                    }
                                                    3L -> {
                                                        //Collector: When a user added three movies to the app it will show a pop-up when the user adds
                                                        //their 3rd movie(Chugh,2022)
                                                        val builder =
                                                            AlertDialog.Builder(this@AddMovie)
                                                        builder.setTitle("Congratulations!")
                                                        builder.setMessage("You have added your 3rd movie.")
                                                        val alertDialog = builder.create()
                                                        alertDialog.window?.setBackgroundDrawable(
                                                            ColorDrawable(
                                                                ContextCompat.getColor(
                                                                    this@AddMovie, R.color.yellow
                                                                )
                                                            )
                                                        )
                                                        alertDialog.show()
                                                        showConfettiAnimation()//Displaying confetti animation(Patel,2021)

                                                        //Delay the redirection to MovieDisplay page by 5 seconds(see Splash Screen - Android Studio,2020)
                                                        handler.postDelayed({
                                                            startActivity(intent)
                                                        }, 5000)
                                                    }
                                                    10L -> {
                                                        //Packrat: When a user added 10 movies to the app it will show a pop-up when the user adds
                                                        //their 10th movie(Chugh,2022)
                                                        val builder =
                                                            AlertDialog.Builder(this@AddMovie)
                                                        builder.setTitle("Congratulations!")
                                                        builder.setMessage("You have added your 10th movie.")
                                                        val alertDialog = builder.create()
                                                        alertDialog.window?.setBackgroundDrawable(
                                                            ColorDrawable(
                                                                ContextCompat.getColor(
                                                                    this@AddMovie, R.color.yellow
                                                                )
                                                            )
                                                        )
                                                        alertDialog.show()
                                                        showConfettiAnimation()//Displaying confetti animation(Patel,2021)

                                                        //Delay the redirection to MovieDisplay page by 5 seconds(see Splash Screen - Android Studio,2020)
                                                        handler.postDelayed({
                                                            startActivity(intent)
                                                        }, 5000)
                                                    }
                                                    else -> {
                                                        startActivity(intent)
                                                    }
                                                }
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                                                Toast.makeText(
                                                    this@AddMovie,
                                                    "Error occurred",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        })

                                    Toast.makeText(
                                        this@AddMovie,
                                        "Movie successfully added",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    //Clearing input fields once movie is added(The IIE,2023)
                                    txtMovieName.setText("")
                                    txtDescription.setText("")
                                    editTextDate2.setText("")

                                    //Resetting the imageView to display the default system gallery icon(The IIE,2023)
                                    imageView.setImageResource(android.R.drawable.ic_menu_gallery)
                                }
                            }
                        })
                }
            }
        }

        //Button to go back to the Welcome page(The IIE,2023)
        btnBack3.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
        }
    }

    //Handles image upload results by retrieving the URI and resizing the image(a_local_nobody,2022)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Checking if the received result is for the specified request code and is successful(a_local_nobody,2022)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //Retrieving the image URI from the received data(a_local_nobody,2022)
            val imageUri: Uri? = data?.data

            //Resizing the image using the provided URI(jww,2018)
            val bitmap = imageUri?.let { uri ->
                resizeImage(uri)
            }
            //Setting the resized bitmap as the image for the ImageView(a_local_nobody,2022)
            imageView.setImageBitmap(bitmap)
        }
    }

    //Resizing the image to desired dimensions(jww,2018)
    private fun resizeImage(uri: Uri): Bitmap? {
        //Opening an input stream to read the image data from the provided URI(jww,2018)
        val imageStream = contentResolver.openInputStream(uri)

        //Creating options for decoding the image, set inJustDecodeBounds to true to retrieve image dimensions(jww,2018)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        //Decoding the image stream to retrieve the image dimensions(jww,2018)
        BitmapFactory.decodeStream(imageStream, null, options)

        //Closing the image stream(jww,2018)
        imageStream?.close()

        //Opening a new input stream to read the image data(jww,2018)
        val inputStream = contentResolver.openInputStream(uri)

        //Setting inJustDecodeBounds to false and calculate the scaling factor for resizing the image(jww,2018)
        options.inJustDecodeBounds = false
        options.inSampleSize = calculateScaleFactor(options, desiredWidth, desiredHeight)

        //Decoding the image stream with the specified options and obtain the resized bitmap(jww,2018)
        val resizedBitmap = BitmapFactory.decodeStream(inputStream, null, options)

        //Closing the input stream(jww,2018)
        inputStream?.close()

        //Returning the resized bitmap(jww,2018)
        return resizedBitmap
    }

    //Calculating the scale factor for resizing the image to maintain aspect ratio(Suryavanshi,2021)
    private fun calculateScaleFactor(options: BitmapFactory.Options, desiredWidth: Int, desiredHeight: Int): Int {
        //Retrieving the original width and height of the image from the BitmapFactory options(Suryavanshi,2021)
        val width = options.outWidth
        val height = options.outHeight

        //Initializing the scaleFactor to 1 (no scaling)(Suryavanshi,2021)
        var scaleFactor = 1

        //Checking if the original width or height is larger than the desired width or height(Suryavanshi,2021)
        if (width > desiredWidth || height > desiredHeight) {
            //Calculating the width and height ratios to determine the scaling factor(Suryavanshi,2021)
            val widthRatio = Math.round(width.toFloat() / desiredWidth.toFloat())
            val heightRatio = Math.round(height.toFloat() / desiredHeight.toFloat())

            //Choosing the smaller ratio as the scaleFactor to maintain aspect ratio(Suryavanshi,2021)
            scaleFactor = if (widthRatio < heightRatio) widthRatio else heightRatio
        }
        //Returning the calculated scaleFactor(Suryavanshi,2021)
        return scaleFactor
    }
    private fun showDatePicker() {
        //Getting the current date(chaitanyamunje,2022)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        //Creating a DatePickerDialog and set the selected date(chaitanyamunje,2022)
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            //Updating the EditText with the selected date(chaitanyamunje,2022)
            val formattedDate = formatDate(selectedYear, selectedMonth, selectedDay)
            editTextDate2.setText(formattedDate)
        }, year, month, dayOfMonth)

        //Showing the DatePickerDialog(chaitanyamunje,2022)
        datePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        //Formatting the date as desired (e.g., "YYYY-MM-DD")(chaitanyamunje,2022)
        val formattedYear = year.toString()
        val formattedMonth = (month + 1).toString().padStart(2, '0')
        val formattedDay = dayOfMonth.toString().padStart(2, '0')

        return "$formattedYear-$formattedMonth-$formattedDay"
    }

    //Function to generate random colors for the confetti(Patel,2021)
    private fun getRandomColor(): Int {
        val random = Random()
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    //Function to show confetti animation(Patel,2021)
    private fun showConfettiAnimation() {
        val confettiView = View(this@AddMovie)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )
        confettiView.layoutParams = layoutParams
        confettiView.background = ColorDrawable(Color.TRANSPARENT)
        val container = window.decorView as ViewGroup
        container.addView(confettiView)

        //Defining the duration and number of confetti particles(Patel,2021)
        val duration = 500L
        val numConfettiShowers = 10000 //Number of simultaneous confetti showers(Patel,2021)
        val delayBetweenShowers = 10L //Delaying between the start of each confetti shower(Patel,2021)

        //Generating and animate the confetti particles for each shower(Patel,2021)
        for (i in 0 until numConfettiShowers) {
            animateConfettiShower(container, duration, delayBetweenShowers * i)
        }
    }

    //Function to animate the confetti into a shower effect(Patel,2021)
    private fun animateConfettiShower(container: ViewGroup, duration: Long, delay: Long) {
        val confetti = View(this@AddMovie)
        val size =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
                .toInt()
        val layoutParams = FrameLayout.LayoutParams(size, size)

        //Randomizing the initial position of each confetti particle(Patel,2021)
        layoutParams.leftMargin = Random().nextInt(container.width - size)
        layoutParams.topMargin = -size

        confetti.layoutParams = layoutParams
        confetti.background = ColorDrawable(getRandomColor())
        container.addView(confetti)

        //Animating each confetti particle with variation(Patel,2021)
        val translationY = container.height.toFloat() + Random().nextFloat() * container.height
        val rotation = Random().nextFloat() * 720 //Random rotation between 0 and 720 degrees(Patel,2021)

        confetti.translationY = 0f //Setting initial translationY to 0(Patel,2021)

        //Starting the confetti animation with a delay(Patel,2021)
        confetti.postDelayed({
            confetti.animate().translationYBy(translationY).rotationBy(rotation).alpha(0f)
                .setDuration(duration).setInterpolator(AccelerateInterpolator()).withEndAction {
                    container.removeView(confetti)
                }
        }, delay)
    }
}
