package com.example.finalproject.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.APIViewModel
import com.example.finalproject.Data.Movie
import com.example.finalproject.R
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.ratingSpinner

class ReviewActivity : AppCompatActivity() {

    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        ArrayAdapter.createFromResource(
            this,
            R.array.ratings_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            ratingSpinner.adapter = adapter
        }

        cancelBtn.setOnClickListener {
            finish();
        }
    }

    override fun onStart() {
        super.onStart()

        val apiViewModel = ViewModelProvider(this).get(APIViewModel::class.java)

        val id = intent.getIntExtra("id", -1)

        apiViewModel.getByID(id.toString())

        apiViewModel.movie.observe(this, Observer {

            movie = it
            OverallReviewHeader.text = "Review for " + movie.title
        })
    }

}
