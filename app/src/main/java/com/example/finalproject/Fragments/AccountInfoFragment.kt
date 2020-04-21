package com.example.finalproject.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.finalproject.APIViewModel
import com.example.finalproject.Activities.LookForFavoritesActivity
import com.example.finalproject.Activities.MainActivity

import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account_info.*

class AccountInfoFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var numReviews = 0
    private var numRatings = 0
    private var name = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_info, container, false)
    }

    override fun onStart() {
        super.onStart()

        auth = FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.setFirestoreSettings(settings)

        db.collection("users").document(auth.currentUser!!.email!!).get().addOnSuccessListener {
            if(it.contains("ratedMovies")){
                val ratings = it.get("ratedMovies") as ArrayList<Int>
                numRatings = ratings.size
            }
            if(it.contains("reviewedMovies")){
                val reviews = it.get("reviewedMovies") as ArrayList<Int>
                numReviews = reviews.size
            }
            if(it.contains("Name")){
                val person = it.get("Name") as String
                name = person
            }
            if(it.contains("Favorites")){
                val favoriteMovies = it.get("Favorites") as ArrayList<String>
                val howMany = favoriteMovies.size
                if(howMany == 3){
                    addMoviesMessage.text = " Your favorite movies: "
                }
                addMoviePosters(favoriteMovies)
            }
            ratedMovies.text = "You have rated " + numRatings + " movies"
            reviewedMovies.text = "You have reviewed " + numReviews + " movies"
            accountName.text = "Hello, " + name
        }

        firstFavorite.setOnClickListener {
            updateFavorite(0)
        }

        secondFavorite.setOnClickListener{
            updateFavorite(1)
        }

        thirdFavorite.setOnClickListener {
            updateFavorite(2)
        }
    }

    private fun updateFavorite(poster : Int){
        var intent = Intent(this.context, LookForFavoritesActivity::class.java)
        intent.putExtra("posterToUpdate", poster)
        startActivity(intent)
    }

    private fun addMoviePosters(favoriteMovies : ArrayList<String>){
        //Loads movie posters into image view
        for(i in 0 until favoriteMovies.size){
            if(i == 0){
                if(favoriteMovies[i] != null && favoriteMovies[i].isNotEmpty()) {
                    Picasso.get().load("https://image.tmdb.org/t/p/w500" + favoriteMovies[i]).into(firstFavorite)
                }
            }
            if(i == 1){
                if(favoriteMovies[i] != null && favoriteMovies[i].isNotEmpty()) {
                    Picasso.get().load("https://image.tmdb.org/t/p/w500" + favoriteMovies[i]).into(secondFavorite)
                }
            }
            if(i == 2){
                if(favoriteMovies[i] != null && favoriteMovies[i].isNotEmpty()) {
                    Picasso.get().load("https://image.tmdb.org/t/p/w500" + favoriteMovies[i]).into(thirdFavorite)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Buttons for adding favorite images

        //Button to logout
        LogoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this.context, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
