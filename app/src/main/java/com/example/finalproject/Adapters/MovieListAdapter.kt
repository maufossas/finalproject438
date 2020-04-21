package com.example.finalproject.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Activities.SingleMovieActivity
import com.example.finalproject.Data.Movie
import com.example.finalproject.R
import com.squareup.picasso.Picasso

class MovieViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.movie_item, parent, false)) {
    private val picture : ImageView
    private val title : TextView
    private val releaseDate: TextView
    private val clickLayout: LinearLayout
    private val moviePath = "https://image.tmdb.org/t/p/w500"

    init {
        picture = itemView.findViewById(R.id.moviePoster)
        title = itemView.findViewById(R.id.movieTitle)
        releaseDate = itemView.findViewById(R.id.movieReleaseDate)
        clickLayout = itemView.findViewById(R.id.layoutToClick)
    }

    fun bind(movie: Movie, context: Context, posterToUpdate: Int) {
        title.text = movie.title
        releaseDate.text = movie.release_date
        if (movie.poster_path != null && movie.poster_path.isNotEmpty()){
            // if the picture link exists, load it in
            Picasso.get().load(moviePath + movie.poster_path).into(picture)
        }else{
            picture.setImageResource(R.drawable.no_poster)
        }
        clickLayout.setOnClickListener{
            // launch the movie activity to view more details
            val intent = Intent(context, SingleMovieActivity::class.java)
            intent.putExtra("id", movie.id)
            intent.putExtra("posterToUpdate", posterToUpdate)
            context.startActivity(intent)
        }
    }
}

//create the listener for the recycler view
class MovieListAdapter(private val list: ArrayList<Movie>?, var context: Context, var posterToUpdate: Int) : RecyclerView.Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MovieViewHolder(
            inflater,
            parent
        )
    }

    //bind the object
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val event: Movie = list!!.get(position)
        holder.bind(event, context, posterToUpdate)
    }

    //set the count
    override fun getItemCount(): Int = list!!.size

}