package com.example.finalproject.Activities

import android.os.Bundle
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

        addToWatchlistButton.setOnClickListener {
            addToWatchlist()
        }
        // if they cancel, stop the activity
        singleMovieCancelButton.setOnClickListener {
            finish()
        }
    }

    private fun loadInMovie(){
        Picasso.get().load(moviePath + movie.poster_path).into(singleMoviePoster)
        singleMovieTitle.text = movie.title
        singleMovieDate.text = movie.release_date
        singleMovieSummary.text = movie.overview
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