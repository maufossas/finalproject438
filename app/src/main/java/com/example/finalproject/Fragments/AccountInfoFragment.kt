package com.example.finalproject.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.finalproject.Adapters.WatchlistAdapter
import com.example.finalproject.Data.Movie

import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account_info.*
import kotlinx.android.synthetic.main.fragment_watchlist.*

class AccountInfoFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var numReviews = 0
    private var numRatings = 0
    private var name = ""
    private var favorites = ArrayList<Int>()
    private var posters = ArrayList<String>()
    private var reviewed = ArrayList<Movie>()
    private lateinit var adapter : ReviewedMovieAdapter

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

        loadData()

        adapter = ReviewedMovieAdapter(reviewed, this.context!!)

        reviewedRecyclerView.adapter = adapter
        reviewedRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

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

    private fun loadData(){
        posters.clear()
        favorites.clear()
        resetPosters()
        db.collection("users").document(auth.currentUser!!.email!!).get().addOnSuccessListener {
            if(it.contains("ratedMovies")){
                val ratings = it.get("ratedMovies") as ArrayList<Int>
                numRatings = ratings.size
            }
            if(it.contains("reviewedMovies")){
                val reviews = it.get("reviewedMovies") as ArrayList<Int>
                val viewModel = ViewModelProvider(this).get(APIViewModel::class.java)
                viewModel.getByIDList(reviews)
                viewModel.movieList.observe(this, Observer {
                    reviewed.clear()
                    reviewed.addAll(it)
                    adapter.notifyDataSetChanged()
                })
                numReviews = reviews.size
            }
            if(it.contains("Name")){
                val person = it.get("Name") as String
                name = person
            }
            if(it.contains("favorites")){
                favorites = it.get("favorites") as ArrayList<Int>
                if(favorites.size == 3){
                    addMoviesMessage.text = " Your favorite movies: "
                }
                if (favorites.size > 0){
                    val viewModel = ViewModelProvider(this).get(APIViewModel::class.java)
                    viewModel.getByIDList(favorites)
                    viewModel.movieList.observe(this, Observer {
                        posters.clear()
                        for (movie in it){
                            posters.add(movie.poster_path)
                        }
                        addMoviePosters()
                    })
                }
            }
            ratedMovies.text = "You have rated " + numRatings + " movies"
            reviewedMovies.text = "You have reviewed " + numReviews + " movies"
            accountName.text = "Hello, " + name
        }
    }

    private fun updateFavorite(poster : Int){
        if (poster >= favorites.size){
            var intent = Intent(this.context, LookForFavoritesActivity::class.java)
            startActivity(intent)
        }else{
            var intent = Intent(this.context, SingleMovieActivity::class.java)
            intent.putExtra("id", favorites[poster])
            intent.putExtra("favorites", 1)
            startActivity(intent)
        }
    }

    private fun resetPosters(){
        // default state
        addMoviesMessage.text = " Click to add your favorite movies:"
        firstFavorite.setImageResource(R.drawable.add_movie)
        secondFavorite.setImageResource(R.drawable.add_movie)
        thirdFavorite.setImageResource(R.drawable.add_movie)
        firstFavorite.visibility = View.VISIBLE
        secondFavorite.visibility = View.INVISIBLE
        thirdFavorite.visibility = View.INVISIBLE
    }

    private fun addMoviePosters(){
        // if movies were available, load them in
        Log.d("updating", posters.toString())
        if (favorites.size >= 1){
            //Loads movie posters into image view
            if (posters.size >= 1 && posters[0] != null && posters[0].isNotEmpty()){
                Picasso.get().load("https://image.tmdb.org/t/p/w500" + posters[0]).into(firstFavorite)
            }else{
                firstFavorite.setImageResource(R.drawable.no_poster_2)
            }
            secondFavorite.visibility = View.VISIBLE
            if (favorites.size >= 2){
                if (posters.size >= 2 && posters[1] != null && posters[1].isNotEmpty()){
                    Picasso.get().load("https://image.tmdb.org/t/p/w500" + posters[1]).into(secondFavorite)
                }else{
                    secondFavorite.setImageResource(R.drawable.no_poster_2)
                }
                thirdFavorite.visibility = View.VISIBLE
                if (favorites.size >= 3){
                    if (posters.size >= 3 && posters[2] != null && posters[2].isNotEmpty()){
                        Picasso.get().load("https://image.tmdb.org/t/p/w500" + posters[2]).into(thirdFavorite)
                    }else{
                        thirdFavorite.setImageResource(R.drawable.no_poster_2)
                    }
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
            activity!!.finish()
        }
    }

}
