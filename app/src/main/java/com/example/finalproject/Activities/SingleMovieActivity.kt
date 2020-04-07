package com.example.finalproject.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.APIViewModel
import com.example.finalproject.Data.Movie
import com.example.finalproject.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_single_movie.*

class SingleMovieActivity() :  AppCompatActivity() {

    private lateinit var movie: Movie
    private val moviePath = "https://image.tmdb.org/t/p/w500"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_movie)
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

        // if they cancel, stop the activity
        singleMovieCancelButton.setOnClickListener {
            finish()
        }

        writeReviewButton.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("id", movie.id);
            startActivity(intent)
        }
    }

    private fun loadInMovie(){
        Picasso.get().load(moviePath + movie.poster_path).into(singleMoviePoster)
        singleMovieTitle.text = movie.title
        singleMovieDate.text = movie.release_date
        singleMovieSummary.text = movie.overview

    }
}