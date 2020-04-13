package com.example.finalproject.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.APIViewModel
import com.example.finalproject.Data.Movie
import com.example.finalproject.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.ratingSpinner

class ReviewActivity : AppCompatActivity() {

    private lateinit var movie: Movie

    var ratingForMovie = -1

    private lateinit var db: FirebaseFirestore

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

        db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.setFirestoreSettings(settings)

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

        ratingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position != 0){
                    //Sets the rating for the movie
                    ratingForMovie = position
                } else{
                    ratingForMovie = -1
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Nothing
            }
        }

        submitReviewBtn.setOnClickListener {
            submitReview()
        }
    }

    private fun submitReview(){
        if(ratingForMovie == -1 && reviewInput.text.toString() == ""){
            Toast.makeText(this, "Nothing to submit, please rate or review the movie.", Toast.LENGTH_LONG).show()
        } else{
            //Then it means that they only added a written review, no text
            if(ratingForMovie == -1){
                addReview(false)
            } else{
                //Means that they only added a rating, and no review
                if(reviewInput.text.toString() == ""){
                    addRating()
                }
                //Means that they added both a review and rating
                else{
                    addReview(true)
                }
            }
            finish()
        }
    }

    private fun addReview(withRating : Boolean){
        db.collection("movies").document(movie.id.toString()).get().addOnSuccessListener {
            //If our document exists
            if(it.exists()){
                if(it.contains("reviews")){
                    val list = it.get("reviews") as ArrayList<String>
                    list.add(reviewInput.text.toString())
                    db.collection("movies").document(movie.id.toString()).update("reviews", list)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Review added!", Toast.LENGTH_SHORT).show()
                        }
                }
                //Create the reviews array
                else{
                    val list = ArrayList<String>()
                    list.add(reviewInput.text.toString())
                    db.collection("movies").document(movie.id.toString()).update("reviews", list)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Review added!", Toast.LENGTH_SHORT).show()
                        }
                }
            } else{
                val list = ArrayList<String>()
                list.add(reviewInput.text.toString())
                var map = HashMap<String, ArrayList<String>>()
                map.put("reviews", list)
                db.collection("movies").document(movie.id.toString()).set(map as Map<String, Any>).addOnSuccessListener {
                    Toast.makeText(this, "Review added!", Toast.LENGTH_SHORT).show()
                }
            }
            if(withRating){
                addRating()
            }
        }
    }

    private fun addRating(){
        db.collection("movies").document(movie.id.toString()).get().addOnSuccessListener {
            //If our document exists
            if(it.exists()){
                if(it.contains("ratingsSum") && it.contains("numOfRatings") && it.contains("ratingsAvg")){
                    var sum = it.get("ratingsSum") as Long
                    var numOfRatings = it.get("numOfRatings") as Long
                    numOfRatings++
                    sum += ratingForMovie
                    var avg = sum/numOfRatings
                    db.collection("movies").document(movie.id.toString())
                        .update("ratingsSum", sum)
                        .addOnSuccessListener {
                            //Toast.makeText(this, "Review added!", Toast.LENGTH_SHORT).show()
                        }
                    db.collection("movies").document(movie.id.toString())
                        .update("numOfRatings", numOfRatings)
                        .addOnSuccessListener {
                            //Toast.makeText(this, "Review added!", Toast.LENGTH_SHORT).show()
                        }
                    db.collection("movies").document(movie.id.toString())
                        .update("ratingsAvg", avg)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Rating added!", Toast.LENGTH_SHORT).show()
                        }

                }
                //Create the reviews array
                else{
                    var sum = ratingForMovie
                    var numOfRatings = 1
                    var avg = ratingForMovie
                    db.collection("movies").document(movie.id.toString())
                        .update("ratingsSum", sum)
                        .addOnSuccessListener {
                            //Toast.makeText(this, "Review added!", Toast.LENGTH_SHORT).show()
                        }
                    db.collection("movies").document(movie.id.toString())
                        .update("numOfRatings", numOfRatings)
                        .addOnSuccessListener {
                            //Toast.makeText(this, "Review added!", Toast.LENGTH_SHORT).show()
                        }
                    db.collection("movies").document(movie.id.toString())
                        .update("ratingsAvg", avg)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Rating added!", Toast.LENGTH_SHORT).show()
                        }
                }
            } else{
                var sum = ratingForMovie
                var numOfRatings = 1
                var avg = ratingForMovie

                var map = HashMap<String, Any>()
                map.put("ratingsSum", sum)
                map.put("numOfRatings",numOfRatings)
                map.put("ratingsAvg", avg)
                map.put("id", movie.id)

                db.collection("movies").document(movie.id.toString()).set(map as Map<String, Any>).addOnSuccessListener {
                    Toast.makeText(this, "Rating added!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
