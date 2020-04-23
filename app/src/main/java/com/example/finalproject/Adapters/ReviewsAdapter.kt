package com.example.finalproject.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R

// adapter for viewing the reviews for a movie
class ReviewViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.review_item, parent, false)) {
    private val reviewText : TextView

    init {
        reviewText = itemView.findViewById(R.id.reviewText)
    }

    fun bind(string: String) {
        reviewText.text = string
    }
}

//create the listener for the recycler view
class ReviewListAdapter(private val list: ArrayList<String>?) : RecyclerView.Adapter<ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ReviewViewHolder(inflater, parent)
    }

    //bind the object
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(list!![position])
    }

    //set the count
    override fun getItemCount(): Int = list!!.size
}