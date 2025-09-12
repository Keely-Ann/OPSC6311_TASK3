package com.rnk.opsc6311_task3

//Defining a data class representing category information it holds the information of a category(Mohabia,2018)
data class CategoryInfo(val name: String, val movieCount: Int) {
    // Override the toString() function to customize the string representation of the object(The IIE,2023)
    override fun toString(): String {
        // Return a string representation of the category in the format "name (movieCount)"(The IIE,2023)
        return "$name ($movieCount)"
    }
}



