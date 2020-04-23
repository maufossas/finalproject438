package com.example.finalproject.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.APIViewModel
import com.example.finalproject.Activities.LookForFavoritesActivity
import com.example.finalproject.Activities.SingleMovieActivity
import com.example.finalproject.Adapters.ReviewedMovieAdapter
import com.example.finalproject.Data.Movie

import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account_info.*

// account info fragment: here you can add to and view your favorites, see which movies you have reviewed,
// see which movies you've recently accessed (the most recent 10, in order of access), and log out.
class AccountInfoFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var numReviews = 0
    private var numRatings = 0
    private var name = ""
    private var favorites = ArrayList<Movie>()
    private var reviewed = ArrayList<Movie>()
    private var recent = ArrayList<Movie>()
    private lateinit var reviewedAdapter : ReviewedMovieAdapter
    private lateinit var recentAdapter : ReviewedMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Button to logout - finish the movie activity, go back to login
        LogoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity!!.finish()
        }
    }

    override fun onStart() {
        super.onStart()

        auth = FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.setFirestoreSettings(settings)

        // set up adapters and recycler views
        reviewedAdapter = ReviewedMovieAdapter(reviewed, this.context!!)
        reviewedRecyclerView.adapter = reviewedAdapter
        reviewedRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

        recentAdapter = ReviewedMovieAdapter(recent, this.context!!)
        recentRecyclerView.adapter = recentAdapter
        recentRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

        // load in data for favorites, reviews, and recents
        resetPosters()
        loadData()

        // click listeners for the three favorites
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

    // loads in all of the important user data - # of ratings and reviews, favorites, reviews, recents
    private fun loadData(){
        db.collection("users").document(auth.currentUser!!.email!!).get().addOnSuccessListener {
            if(it.contains("ratedMovies")){
                val ratings = it.get("ratedMovies") as ArrayList<Int>
                numRatings = ratings.size
            }
            // if movies have been reviewed, get a count and load them in
            if(it.contains("reviewedMovies")){
                val reviews = it.get("reviewedMovies") as ArrayList<Int>
                val viewModel = ViewModelProvider(this).get(APIViewModel::class.java)
                viewModel.getByIDList(reviews)
                viewModel.movieList.observe(this, Observer {
                    reviewed.clear()
                    reviewed.addAll(it)
                    reviewedAdapter.notifyDataSetChanged()
                })
                numReviews = reviews.size
            }
            // if they have recently viewed movies, load them all in
            if(it.contains("recentMovies")){
                val recents = it.get("recentMovies") as ArrayList<Int>
                val viewModel = ViewModelProvider(this).get(APIViewModel::class.java)
                viewModel.getSecondIDList(recents)
                viewModel.secondMovieList.observe(this, Observer {
                    recent.clear()
                    recent.addAll(it)
                    recentAdapter.notifyDataSetChanged()
                })
            }
            // get the person's name for the hello message (it should always contain name)
            if(it.contains("Name")){
                val person = it.get("Name") as String
                name = person
            }
            // if they have set favorites, load them into the three favorites positions
            if(it.contains("favorites")){
                val favs = it.get("favorites") as ArrayList<Int>
                if (favs.size > 0){
                    val viewModel = ViewModelProvider(this).get(APIViewModel::class.java)
                    viewModel.getThirdIDList(favs)
                    viewModel.thirdMovieList.observe(this, Observer {
                        favorites.clear()
                        favorites.addAll(it)
                        resetPosters()
                        addMoviePosters()
                    })
                }
            }
            // update page text
            ratedMovies.text = "You have rated " + numRatings + " movies"
            reviewedMovies.text = "You have reviewed " + numReviews + " movies"
            accountName.text = "Hello, " + name
        }
    }

    // to be executed when a favorite is clicked on
    private fun updateFavorite(poster : Int){
        if (poster >= favorites.size){
            // if it was the "add poster" image, add a new favorite
            var intent = Intent(this.context, LookForFavoritesActivity::class.java)
            startActivity(intent)
        }else{
            // if it was on an already existing poster, load the single movie activity
            var intent = Intent(this.context, SingleMovieActivity::class.java)
            intent.putExtra("id", favorites[poster].id)
            intent.putExtra("favorites", 1)
            startActivity(intent)
        }
    }

    private fun resetPosters(){
        // default state for posters
        addMoviesMessage.text = " Click to add your favorite movies:"
        firstFavorite.setImageResource(R.drawable.add_movie)
        secondFavorite.setImageResource(R.drawable.add_movie)
        thirdFavorite.setImageResource(R.drawable.add_movie)
        firstFavorite.visibility = View.VISIBLE
        // hide additional unfilled posters
        secondFavorite.visibility = View.INVISIBLE
        thirdFavorite.visibility = View.INVISIBLE
    }

    private fun addMoviePosters(){
        // if favorites were available, load them in
        if (favorites.size >= 1){
            //Loads movie posters into image view
            if (favorites[0].poster_path != null && favorites[0].poster_path.isNotEmpty()){
                Picasso.get().load("https://image.tmdb.org/t/p/w500" + favorites[0].poster_path).into(firstFavorite)
            }else{
                firstFavorite.setImageResource(R.drawable.no_poster_2)
            }
            secondFavorite.visibility = View.VISIBLE
            if (favorites.size >= 2){
                if (favorites[1].poster_path != null && favorites[1].poster_path.isNotEmpty()){
                    Picasso.get().load("https://image.tmdb.org/t/p/w500" + favorites[1].poster_path).into(secondFavorite)
                }else{
                    secondFavorite.setImageResource(R.drawable.no_poster_2)
                }
                thirdFavorite.visibility = View.VISIBLE
                if (favorites.size >= 3){
                    if (favorites[2].poster_path != null && favorites[2].poster_path.isNotEmpty()){
                        Picasso.get().load("https://image.tmdb.org/t/p/w500" + favorites[2].poster_path).into(thirdFavorite)
                    }else{
                        thirdFavorite.setImageResource(R.drawable.no_poster_2)
                    }
                    addMoviesMessage.text = " Your favorite movies: "
                }
            }
        }
    }
}
