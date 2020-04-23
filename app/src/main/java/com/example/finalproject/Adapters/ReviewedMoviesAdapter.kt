package com.example.finalproject.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Activities.SingleMovieActivity
import com.example.finalproject.Data.Movie
import com.example.finalproject.R
import com.squareup.picasso.Picasso

// adapter for movies that are viewed on the account settings page (image only, no title/date)
class ReviewedMovieViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.reviewed_movie_item, parent, false)) {
    private val picture : ImageView
    private val clickLayout: LinearLayout
    private val moviePath = "https://image.tmdb.org/t/p/w500"

    init {
        picture = itemView.findViewById(R.id.moviePoster)
        clickLayout = itemView.findViewById(R.id.layoutToClick)
    }

    // otherwise, similar bind method to the movielistadapter, just with no title/date
    fun bind(movie: Movie, context: Context) {

        if (movie.poster_path != null && movie.poster_path.isNotEmpty()){
            // if the picture link exists, load it in
            Picasso.get().load(moviePath + movie.poster_path).into(picture)
        }else{
            picture.setImageResource(R.drawable.no_poster_2)
        }
        clickLayout.setOnClickListener{
            // launch the movie activity to view more details
            val intent = Intent(context, SingleMovieActivity::class.java)
            intent.putExtra("id", movie.id)
            context.startActivity(intent)
        }
    }
}

//create the listener for the recycler view
class ReviewedMovieAdapter(private val list: ArrayList<Movie>?, var context: Context) : RecyclerView.Adapter<ReviewedMovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewedMovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ReviewedMovieViewHolder(
            inflater,
            parent
        )
    }

    //bind the object
    override fun onBindViewHolder(holder: ReviewedMovieViewHolder, position: Int) {
        val event: Movie = list!!.get(position)
        holder.bind(event, context)
    }

    //set the count
    override fun getItemCount(): Int = list!!.size

}