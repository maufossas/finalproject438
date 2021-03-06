package com.example.finalproject.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.APIViewModel
import com.example.finalproject.Adapters.WatchlistAdapter
import com.example.finalproject.Data.Movie
import com.example.finalproject.R

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.fragment_watchlist.*

// displays the users watchlist, allows to view movies or delete them
class WatchlistFragment : Fragment() {

    private var movieList = ArrayList<Movie>()
    private var movieIDs = ArrayList<Long>()
    lateinit var viewModel : APIViewModel
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter : WatchlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_watchlist, container, false)
    }

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(this).get(APIViewModel::class.java)

        auth = FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.setFirestoreSettings(settings)

        // set up adapter and recycler view
        adapter = WatchlistAdapter(movieList, this.context!!, this)
        watchlistRecyclerView.adapter = adapter
        watchlistRecyclerView.layoutManager = LinearLayoutManager(this.context)

        val uid = auth.currentUser!!.email!!

        // access movies from watchlist
        db.collection("users").document(uid).get().addOnSuccessListener {
            if (it.contains("watchlist")){
                val ids = it.get("watchlist") as ArrayList<Long>
                movieIDs.clear()
                movieIDs.addAll(ids)
                val ints = ArrayList<Int>()
                for (id in ids){
                    ints.add(id.toInt())
                }
                viewModel.getByIDList(ints)
                viewModel.movieList.observe(this, Observer {
                    movieList.clear()
                    movieList.addAll(it)
                    adapter.notifyDataSetChanged()
                })
            }
        }
    }

    // if user deletes a movie, remove it
    fun deleteMovie(movie: Movie){
        // only do 1 call if removed (should prevent spamming the button)
        if(movieList.remove(movie)){
            val uid = auth.currentUser!!.email!!
            movieIDs.remove(movie.id.toLong())
            db.collection("users").document(uid).update("watchlist", movieIDs)
            adapter.notifyDataSetChanged()
        }
    }

}
