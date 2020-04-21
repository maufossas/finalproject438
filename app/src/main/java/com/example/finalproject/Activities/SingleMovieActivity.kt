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

    //For adding favorite movies:
    var posterToUpdate = -1

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

        apiViewModel.getByID(id.toString())

        apiViewModel.movie.observe(this, Observer {

            movie = it
            loadInMovie()
        })

        posterToUpdate = intent.getIntExtra("posterToUpdate", -1)
        //Then we sent a request to update our favorite list
        if(posterToUpdate != -1){
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

        addToFavoritesButton.setOnClickListener {
            addOrUpdateFavorites()
        }

        addToWatchlistButton.setOnClickListener {
            addToWatchlist()
        }
        // if they cancel, stop the activity
        singleMovieCancelButton.setOnClickListener {
            finish()
        }

        seeReviewsButton.setOnClickListener {
            val intent = Intent(this, ListOfReviewsActivity::class.java)
            intent.putExtra("id", movie.id)
            intent.putExtra("title", movie.title)
            startActivity(intent)
        }

        writeReviewButton.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("id", movie.id)
            startActivity(intent)
        }
    }

    private fun loadInMovie(){
        if (movie.poster_path != null && movie.poster_path.isNotEmpty()){
            // if the picture link exists, load it in
            Picasso.get().load(moviePath + movie.poster_path).into(singleMoviePoster)
        }else{
            singleMoviePoster.setImageResource(R.drawable.no_poster)
        }
        singleMovieTitle.text = movie.title
        singleMovieDate.text = movie.release_date
        singleMovieSummary.text = movie.overview
    }

    private fun addOrUpdateFavorites(){
        val uid = auth.currentUser!!.email!!
        db.collection("users").document(uid).get().addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("Favorites")) {
                    val list = it.get("Favorites") as ArrayList<String>
                    //They are expanding the size of their favorites.
                    if(list.size - 1 < posterToUpdate) {
                        list.add(movie.poster_path)
                    } else{
                        list[posterToUpdate] = movie.poster_path
                    }
                    db.collection("users").document(uid).update("Favorites", list)
                        .addOnSuccessListener {
                            val toast = Toast.makeText(
                                this,
                                "Favorites updated!",
                                Toast.LENGTH_SHORT
                            )
                            toast.show()
                        }
                } else {
                    val list = ArrayList<String>()
                    list.add(movie.poster_path)
                    db.collection("users").document(uid).update("Favorites", list)
                        .addOnSuccessListener {
                            val toast =
                                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT)
                            toast.show()
                        }
                }
            }else{
                val list = ArrayList<String>()
                list.add(movie.poster_path)
                var map = HashMap<String, ArrayList<String>>()
                map.put("Favorites", list)
                db.collection("users").document(uid).set(map as Map<String, Any>).addOnSuccessListener {
                    val toast =
                        Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
            var intent = Intent(this, MoviesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addToWatchlist(){
        val uid = auth.currentUser!!.email!!
        db.collection("users").document(uid).get().addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("watchlist")) {
                    val list = it.get("watchlist") as ArrayList<String>
                    if (list.contains(movie.id.toString())) {
                        val toast = Toast.makeText(
                            this,
                            "Movie is already in watchlist",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                    } else {
                        list.add(movie.id.toString())
                        db.collection("users").document(uid).update("watchlist", list)
                            .addOnSuccessListener {
                                val toast = Toast.makeText(
                                    this,
                                    "Added to watchlist",
                                    Toast.LENGTH_SHORT
                                )
                                toast.show()
                            }
                    }
                } else {
                    val list = ArrayList<String>()
                    list.add(movie.id.toString())
                    db.collection("users").document(uid).update("watchlist", list)
                        .addOnSuccessListener {
                            val toast =
                                Toast.makeText(this, "Added to watchlist", Toast.LENGTH_SHORT)
                            toast.show()
                        }
                }
            }else{
                val list = ArrayList<String>()
                list.add(movie.id.toString())
                var map = HashMap<String, ArrayList<String>>()
                map.put("watchlist", list)
                db.collection("users").document(uid).set(map as Map<String, Any>).addOnSuccessListener {
                    val toast =
                        Toast.makeText(this, "Added to watchlist", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }
    }
}