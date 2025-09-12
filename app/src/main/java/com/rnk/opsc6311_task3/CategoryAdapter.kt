package com.rnk.opsc6311_task3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CategoryAdapter(context: Context, resource: Int, objects: MutableList<CategoryInfo>) :
    ArrayAdapter<CategoryInfo>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //Getting the current view or inflate a new one if it doesn't exist(Stefan,2022)
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.category_item, parent, false)

        //Getting the Category object for the current position(Stefan,2022)
        val category = getItem(position)

        //Finding the TextViews within the layout(Stefan,2022)
        val txtCategoryName = view.findViewById<TextView>(R.id.txtCategoryName)
        val txtMovieCount = view.findViewById<TextView>(R.id.txtMovieCount)

        //Setting the category name and movie count on the TextViews(Stefan,2022)
        txtCategoryName.text = category?.name
        txtMovieCount.text = category?.movieCount.toString()

        return view
    }
}
