package com.rnk.opsc6311_task3

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class GoalProgressAdapter(private val context: Context, private val goalList: MutableList<GoalsInfo>) :
    RecyclerView.Adapter<GoalProgressAdapter.ViewHolder>() {

    //Declaring and initializing variables(The IIE,2023)
    private var database: DatabaseReference = FirebaseDatabase.getInstance().getReference("goals")

    //Declaring variable(The IIE,2023)
    private lateinit var loggedInUser: String

    //Creating a ViewHolder for the item view(Stefan,2022)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflating the layout for the goal item view(Stefan,2022)
        val view = LayoutInflater.from(context).inflate(R.layout.goal_item, parent, false)
        return ViewHolder(view)
    }

    //Binding data to the ViewHolder(Stefan,2022)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goal = goalList[position]
        holder.bind(goal)
    }

    //Returning the number of items in the list(Stefan,2022)
    override fun getItemCount(): Int {
        return goalList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Declaring views in the goal item layout(Stefan,2022)
        private val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)
        private val txtProgress: TextView = itemView.findViewById(R.id.txtProgress)
        private val txtCompleted: TextView = itemView.findViewById(R.id.txtCompleted)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        //Binding data to the views in the item view(Stefan,2022)
        fun bind(goal: GoalsInfo) {
            //Setting the category text(Stefan,2022)
            txtCategory.text = goal.category

            //Checking if the goal is reached and update the progress bar and text(Stefan,2022)
            if (goal.movieCount >= goal.goal) {
                progressBar.progressDrawable.setColorFilter(
                    ContextCompat.getColor(context, R.color.yellow),
                    PorterDuff.Mode.SRC_IN
                )
                txtProgress.visibility = View.GONE
                txtCompleted.visibility = View.VISIBLE
            } else {
                //Setting the progress text if goal haven't been reached yet(Stefan,2022)
                val progressText = "${goal.movieCount}/${goal.goal}"
                txtProgress.text = progressText
                txtProgress.visibility = View.VISIBLE
                txtCompleted.visibility = View.GONE
            }

            //Calculating and setting the progress value for the ProgressBar(Stefan,2022)
            val progress = (goal.movieCount.toFloat() / goal.goal.toFloat() * 100).toInt()
            progressBar.progress = progress

            //Button to delete goal(The IIE,2023)
            btnDelete.setOnClickListener {
                showDeleteConfirmationDialog(goal)
            }
        }

        //Function to display confirmation dialog(Chugh,2022)
        private fun showDeleteConfirmationDialog(goal: GoalsInfo) {
            //Creating an AlertDialog to confirm the deletion(Chugh,2022)
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Confirm Delete")
            alertDialog.setMessage("Are you sure you want to delete this goal?")

            //Setting the positive button to perform the deletion(Chugh,2022)
            alertDialog.setPositiveButton("Delete") { dialog, which ->
                deleteGoalFromDatabase(goal)
            }

            //Setting the negative button to cancel the deletion(Chugh,2022)
            alertDialog.setNegativeButton("Cancel", null)

            //Show the dialog(Chugh,2022)
            alertDialog.show()
        }


        private fun deleteGoalFromDatabase(goal: GoalsInfo) {
            //Getting the logged-in user's username(Android Knowledge,2023)
            loggedInUser = Login.loggedInUser

            //Query the database to find the goal with the same category as the one to be deleted(chaitanyamunje,2021)
            val goalRef = database.orderByChild("category").equalTo(goal.category)
            goalRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        //Retrieve the goal from the snapshot(chaitanyamunje,2021)
                        val goal = snapshot.getValue(GoalsInfo::class.java)
                        //Checking if the goal exists and the username matches the logged-in user(chaitanyamunje,2021)
                        if (goal != null && goal.username == loggedInUser) {
                            //Removing the goal from the database(chaitanyamunje,2021)
                            snapshot.ref.removeValue()
                            //Deletion successful(chaitanyamunje,2021)
                            Toast.makeText(context, "Goal deleted successfully", Toast.LENGTH_SHORT).show()
                            //Removing the deleted goal from the list(chaitanyamunje,2021)
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                goalList.removeAt(position)
                                notifyItemRemoved(position)
                            }
                            return  //Exiting the loop after deleting the goal(chaitanyamunje,2021)
                        }
                    }
                    //Goal not found or user doesn't match(The IIE,2023)
                    Toast.makeText(context, "Failed to delete goal: Goal not found or user doesn't match", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //Handling the error if the onDataChange operation is canceled(Android Knowledge,2023)
                    Toast.makeText(context, "Failed to delete goal: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
