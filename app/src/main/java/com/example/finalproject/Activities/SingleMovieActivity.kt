package com.example.finalproject.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.APIViewModel
import com.example.finalproject.Data.Movie
import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_single_movie.*

class SingleMovieActivity() :  AppCompatActivity() {

    private lateinit var movie: Movie
    private val moviePath = "https://image.tmdb.org/t/p/w500"
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var watchlist = ArrayList<Long>()
    private var favorites = ArrayList<Long>()
    private var hasReviewed = false
    private var hasRated = false
    private var updating = false

    private val resultFinished = 123456

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_movie)

        auth = FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.setFirestoreSettings(settings)
    }

    override fun onStart() {
        super.onStart()

        val apiViewModel = ViewModelProvider(this).get(APIViewModel::class.java)

        val id = intent.getIntExtra("id", -1)

        apiViewModel.getByID(id)

        // load in movie from the API
        apiViewModel.movie.observe(this, Observer {
            movie = it
            loadInMovie()
            val uid = auth.currentUser!!.email!!
            // load in user information to set watchlist and favorites buttons
            db.collection("users").document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    // if movie is in the watchlist, change to "remove from watchlist"
                    if (it.contains("watchlist")) {
                        watchlist = it.get("watchlist") as ArrayList<Long>
                        if (watchlist.contains(movie.id.toLong())){
                            addToWatchlistButton.text = "Remove from watchlist"
                        }
                    }
                    // if movie is in the favorites, change to "remove from favorites"
                    if (it.contains("favorites")){
                        favorites = it.get("favorites") as ArrayList<Long>
                        if (favorites.contains(movie.id.toLong())){
                            addToFavoritesButton.text = "Remove from favorites"
                        }
                    }
                    // don't allow review page access if it has been reviewed and rated
                    if (it.contains("reviewedMovies")){
                        if ((it.get("reviewedMovies") as ArrayList<Int>).contains(movie.id)){
                            hasReviewed = true
                        }
                    }
                    if (it.contains("ratedMovies")){
                        if ((it.get("ratedMovies") as ArrayList<Int>).contains(movie.id)){
                            hasRated = true
                        }
                    }
                    // update their recent movies to reflect this new movie page being visited
                    if (it.contains("recentMovies")){
                        val recents = it.get("recentMovies") as ArrayList<Long>
                        recents.remove(movie.id.toLong())
                        recents.add(0, movie.id.toLong())
                        while(recents.size > 10){
                            recents.removeAt(10)
                        }
                        db.collection("users").document(uid).update("recentMovies", recents)
                    }else{
                        val recents = ArrayList<Int>()
                        recents.add(movie.id)
                        db.collection("users").document(uid).update("recentMovies", recents)
                    }
                }
                // only allow database-related calls once database has been accessed
                addToFavoritesButton.setOnClickListener {
                    updateFavorites()
                }

                addToWatchlistButton.setOnClickListener {
                    updateWatchlist()
                }

                // if they haven't already reviewed + rated, allows access to review + rating page
                writeReviewButton.setOnClickListener {
                    if (hasReviewed && hasRated){
                        val toast = Toast.makeText(
                            this,
                            "You have already reviewed this movie",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                    }else{
                        val intent = Intent(this, ReviewActivity::class.java)
                        intent.putExtra("id", movie.id)
                        intent.putExtra("title", movie.title)
                        startActivity(intent)
                    }
                }
            }

            // option to see reviews for the movie (doesn't require user info loaded)
            seeReviewsButton.setOnClickListener {
                val intent = Intent(this, ListOfReviewsActivity::class.java)
                intent.putExtra("id", movie.id)
                intent.putExtra("title", movie.title)
                startActivity(intent)
            }

        })

        // if this is for the favorites list, change the button layout
        val doFavorites = intent.getIntExtra("favorites", -1)
        if(doFavorites != -1){
            singleMovieCancelButton.text = "Cancel"
            seeReviewsButton.visibility = View.GONE
            addToWatchlistButton.visibility = View.GONE
            writeReviewButton.visibility = View.GONE
            addToFavoritesButton.visibility = View.VISIBLE
        } else{
            singleMovieCancelButton.text = "Back to home"
            seeReviewsButton.visibility = View.VISIBLE
            addToWatchlistButton.visibility = View.VISIBLE
            writeReviewButton.visibility = View.VISIBLE
            addToFavoritesButton.visibility = View.GONE
        }

        // if they cancel, stop the activity
        singleMovieCancelButton.setOnClickListener {
            finish()
        }

    }

    // fills page with movie info
    private fun loadInMovie(){
        if (movie.poster_path != null && movie.poster_path.isNotEmpty()){
            // if the picture link exists, load it in
            Picasso.get().load(moviePath + movie.poster_path).into(singleMoviePoster)
        }else{
            singleMoviePoster.setImageResource(R.drawable.no_poster_2)
        }
        singleMovieTitle.text = movie.title
        singleMovieDate.text = movie.release_date
        singleMovieSummary.text = movie.overview
    }

    // if they hit add/remove from favorites, updates and lets the LookForFavoritesActivity know that it has been chosen
    private fun updateFavorites(){
        val uid = auth.currentUser!!.email!!
        if (!updating){ // disallow simultaneous presses (not a perfect lock, but is better than nothing)
            updating = true
            // if we remove it, update database then return
            if(favorites.remove(movie.id.toLong())){
                db.collection("users").document(uid).update("favorites", favorites).addOnSuccessListener {
                    val toast = Toast.makeText(
                        this,
                        "Removed from favorites",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                    updating = false
                    setResult(resultFinished, intent)
                    finish()
                }
            }else{
                // if it wasn't removed, then we must be adding it, so update and return
                favorites.add(movie.id.toLong())
                db.collection("users").document(uid).update("favorites", favorites).addOnSuccessListener {
                    val toast = Toast.makeText(
                        this,
                        "Added to favorites",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                    updating = false
                    setResult(resultFinished, intent)
                    finish()
                }
            }
        }
    }

    // similar to the favorites, just add or remove the current movie from the watchlist (no return)
    private fun updateWatchlist(){
        if (!updating){ // disallow simultaneous presses (not a perfect lock, but is better than nothing)
            val uid = auth.currentUser!!.email!!
            updating = true
            if(watchlist.remove(movie.id.toLong())){
                db.collection("users").document(uid).update("watchlist", watchlist).addOnSuccessListener {
                    val toast = Toast.makeText(
                        this,
                        "Removed from watchlist",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                    addToWatchlistButton.text = "Add to watchlist"
                    updating = false
                }
            }else{
                watchlist.add(movie.id.toLong())
                db.collection("users").document(uid).update("watchlist", watchlist).addOnSuccessListener {
                    val toast = Toast.makeText(
                        this,
                        "Added to watchlist",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                    addToWatchlistButton.text = "Remove from watchlist"
                    updating = false
                }
            }
        }
    }
}