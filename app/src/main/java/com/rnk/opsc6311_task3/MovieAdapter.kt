package com.rnk.opsc6311_task3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream

class MovieAdapter(private val context: Context, private val movieList: ArrayList<MovieInfo>) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    //Called when RecyclerView needs a new ViewHolder for creating item views(Stefan,2022)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflating the layout for the movie_list_item(Stefan,2022)
        val itemView = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false)
        return ViewHolder(itemView)
    }

    //Called by RecyclerView to display the data at a specified position(Stefan,2022)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieInfo = movieList[position]

        //Decoding the Base64 string into a Bitmap
        val decodedByteArray = Base64.decode(movieInfo.imageBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)

        //Creating a BitmapDrawable from the decoded Bitmap and setting it to the ImageView(Stefan,2022)
        val drawable = BitmapDrawable(context.resources, bitmap)
        holder.imageView.setImageDrawable(drawable)

        //Setting the name, description, category, and date to their TextViews(Stefan,2022)
        holder.txtImageName.text = movieInfo.name
        holder.txtDescription.text = movieInfo.description
        holder.txtCategory.text = movieInfo.category
        holder.txtDate.text = movieInfo.date

        holder.editButton.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                //Handling edit button click for the movie at the position(Stefan,2022)
                val movie = movieList[position]
                showEditDialog(movie)
            }
        }

        holder.deleteButton.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                //Handling delete button click for the movie at the position(Stefan,2022)
                val movie = movieList[position]
                showDeleteConfirmationDialog(movie)
            }
        }
    }

    //Returning the total number of items in the data set(Stefan,2022)
    override fun getItemCount(): Int {
        return movieList.size
    }

    //ViewHolder class that holds references to the views for efficient recycling(Stefan,2022)
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val txtImageName: TextView = itemView.findViewById(R.id.txtImageName)
        val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
        val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    //Function to show delete confirmation dialog(Chugh,2022)
    private fun showDeleteConfirmationDialog(movie: MovieInfo) {
        //Creating an AlertDialogBuilder instance(Chugh,2022)
        val alertDialogBuilder = AlertDialog.Builder(context)

        //Setting the title of the dialog(Chugh,2022)
        alertDialogBuilder.setTitle("Confirm Action")

        //Setting the message shown in the dialog(Chugh,2022)
        alertDialogBuilder.setMessage("Do you want to delete or edit this movie?")

        //Setting the positive button (Delete) and its click listener(Chugh,2022)
        alertDialogBuilder.setPositiveButton("Delete") { dialog, which ->
            //Calling the deleteMovie function passing the selected movie(Chugh,2022)
            deleteMovie(movie)
        }
        //Setting the neutral button (Cancel) and its click listener(Chugh,2022)
        alertDialogBuilder.setNeutralButton("Cancel") { dialog, which ->
            //Dismiss the dialog when the cancel button is clicked
            dialog.dismiss()
        }
        //Creating the AlertDialog from the AlertDialogBuilder(Chugh,2022)
        val alertDialog = alertDialogBuilder.create()
        //Show the AlertDialog(Chugh,2022)
        alertDialog.show()
    }

    //Function to delete movie from database(chaitanyamunje,2021)
    private fun deleteMovie(movie: MovieInfo) {
        //Getting an instance of the FirebaseDatabase(Mamo,2021)
        val database = FirebaseDatabase.getInstance()

        //Getting a reference to the "movies" node in the database(Mamo,2021)
        val moviesRef = database.getReference("movies")

        //Finding the movie in the Firebase database using its unique key or ID(chaitanyamunje,2021)
        val query = moviesRef.orderByChild("name").equalTo(movie.name)

        //Executing the query to find the matching movie(chaitanyamunje,2021)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Iterating through the children of the query result(chaitanyamunje,2021)
                for (childSnapshot in dataSnapshot.children) {
                    //Deleting the movie from the Firebase database(chaitanyamunje,2021)
                    childSnapshot.ref.removeValue()
                }
                //Deleting the movie from the local list(chaitanyamunje,2021)
                val position = movieList.indexOf(movie)
                if (position != -1) {
                    movieList.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handling the error if the deletion operation is canceled(Android Knowledge,2023)
            }
        })
    }

    //Function to show edit dialog(Chugh,2022)
    private fun showEditDialog(movie: MovieInfo) {
        //Creating an AlertDialogBuilder instance(Chugh,2022)
        val editDialogBuilder = AlertDialog.Builder(context)

        //Setting the title of the dialog(Chugh,2022)
        editDialogBuilder.setTitle("Edit Movie")

        //Inflating the custom layout for the edit dialog(Stefan,2022)
        val editView = LayoutInflater.from(context).inflate(R.layout.edit_movie_dialog, null)

        //Getting references to the EditText views in the custom layout(Stefan,2022)
        val etName = editView.findViewById<EditText>(R.id.etName)
        val etDescription = editView.findViewById<EditText>(R.id.etDescription)
        val etCategory = editView.findViewById<EditText>(R.id.etCategory)
        val etDate = editView.findViewById<EditText>(R.id.etDate)
        val imageView = editView.findViewById<ImageView>(R.id.imageView)

        //Setting the current movie information in the edit fields(Stefan,2022)
        etName.setText(movie.name)
        etDescription.setText(movie.description)
        etCategory.setText(movie.category)
        etDate.setText(movie.date)

        //Decoding the Base64 string into a Bitmap(jww,2018)
        val decodedByteArray = Base64.decode(movie.imageBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)

        //Setting the decoded Bitmap to the ImageView(jww,2018)
        imageView.setImageBitmap(bitmap)

        //Setting the custom view for the edit dialog(Chugh,2022)
        editDialogBuilder.setView(editView)

        //Setting the positive button (Save) and its click listener(Chugh,2022)
        editDialogBuilder.setPositiveButton("Save") { dialog, which ->
            //Getting the updated movie information from the EditText fields(Mamo,2021)
            val newName = etName.text.toString()
            val newDescription = etDescription.text.toString()
            val newCategory = etCategory.text.toString()
            val newDate = etDate.text.toString()

            //Converting the updated image from the ImageView to a Base64 string(The IIE,2023)
            val updatedBitmap = (imageView.drawable as BitmapDrawable).bitmap
            val outputStream = ByteArrayOutputStream()
            updatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val updatedImageByteArray = outputStream.toByteArray()
            val updatedImageBase64 = Base64.encodeToString(updatedImageByteArray, Base64.DEFAULT)

            //Updating the movie information in the Firebase database(chaitanyamunje,2021)
            val database = FirebaseDatabase.getInstance()
            val moviesRef = database.getReference("movies")

            //Creating a query to find the movie to be updated(chaitanyamunje,2021)
            val query = moviesRef.orderByChild("name").equalTo(movie.name)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Iterating through the children of the query result(chaitanyamunje,2021)
                    for (childSnapshot in dataSnapshot.children) {
                        //Updating the movie fields in the Firebase database(chaitanyamunje,2021)
                        childSnapshot.ref.child("name").setValue(newName)
                        childSnapshot.ref.child("description").setValue(newDescription)
                        childSnapshot.ref.child("category").setValue(newCategory)
                        childSnapshot.ref.child("date").setValue(newDate)
                        childSnapshot.ref.child("imageBase64").setValue(updatedImageBase64)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //Handling the error if the update operation is canceled(Android Knowledge,2023)
                }
            })

            //Updating the movie information in the local list(chaitanyamunje,2021)
            val position = movieList.indexOf(movie)
            val loggedInUser = Login.loggedInUser
            if (position != -1) {
                //Creating an updated movie instance with the new information(chaitanyamunje,2021)
                val updatedMovie = MovieInfo(newName, updatedImageBase64, newDescription, newCategory, newDate, loggedInUser)

                //Replacing the old movie with the updated movie in the local list(chaitanyamunje,2021)
                movieList[position] = updatedMovie

                //Notifying any relevant UI components about the change(Chugh,2022)
                notifyItemChanged(position)
            }
        }
        //Setting the negative button (Cancel) and its click listener(Chugh,2022)
        editDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            //Dismiss the dialog when the cancel button is clicked(Chugh,2022)
            dialog.dismiss()
        }

        //Creating the edit dialog from the AlertDialogBuilder(Chugh,2022)
        val editDialog = editDialogBuilder.create()

        //Show the edit dialog(Chugh,2022)
        editDialog.show()
    }
}
