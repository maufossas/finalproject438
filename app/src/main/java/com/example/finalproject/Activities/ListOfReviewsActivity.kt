package com.example.finalproject.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Adapters.ReviewListAdapter
import com.example.finalproject.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_list_of_reviews.*
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.fragment_trending.*
import kotlinx.android.synthetic.main.fragment_watchlist.*
import java.math.BigDecimal
import java.math.RoundingMode

class ListOfReviewsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    var reviewList : ArrayList<String> = ArrayList<String>()
    var ratingList : ArrayList<Int> = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_reviews)

        db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.setFirestoreSettings(settings)
    }

    override fun onStart() {
        super.onStart()

        val movieId = intent.getIntExtra("id", -1)
        val movieTitle = intent.getStringExtra("title")
        movieReviewsTitle.text = movieTitle

        val recyclerView = findViewById<RecyclerView>(R.id.reviewsRecyclerView)
        val reviewAdapter = ReviewListAdapter(reviewList)
        recyclerView.adapter = reviewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("movies").document(movieId.toString()).get().addOnSuccessListener {
            //If our document exists
            if(it.exists()){
                if(it.contains("reviews")){
                    reviewList = it.get("reviews") as ArrayList<String>
                    reviewAdapter.notifyDataSetChanged()
                } else{
                    reviewList.add("This movie does not yet have any reviews")
                    reviewAdapter.notifyDataSetChanged()
                }
                //Create the reviews array
                if(it.contains("ratings")){
                    ratingList = it.get("ratings") as ArrayList<Int>
                    var bd = BigDecimal.valueOf(ratingList.average())
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    averageRating.text = "Average rating: " + bd.toString()
                }
            }
        }

        returnToMovie.setOnClickListener {
            finish()
         }
    }
}
