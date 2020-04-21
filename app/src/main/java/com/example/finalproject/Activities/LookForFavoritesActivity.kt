package com.example.finalproject.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.APIViewModel
import com.example.finalproject.Adapters.MovieListAdapter
import com.example.finalproject.Data.Movie
import com.example.finalproject.R
import kotlinx.android.synthetic.main.activity_look_for_favorites.*
import kotlinx.android.synthetic.main.fragment_search.*

class LookForFavoritesActivity : AppCompatActivity() {

    var posterToUpdate = -1

    lateinit var viewModel: APIViewModel
    var movieList: ArrayList<Movie> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_look_for_favorites)

        posterToUpdate = intent.getIntExtra("posterToUpdate", -1)


        // api viewmodel
        viewModel = ViewModelProvider(this).get(APIViewModel::class.java)

        cancelFavoriteSearch.setOnClickListener {
            finish()
        }

        // set up recycler view with grid layout adapter
        val recyclerView = searchForFavoriteRecyclerView
        val movieAdapter = MovieListAdapter(movieList, this, posterToUpdate)
        recyclerView.adapter = movieAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchForFavorite.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //DO NOTHING
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //DO NOTHING
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(searchForFavorite.text.toString().length != 0){
                    viewModel.getBySearch(searchForFavorite.text.toString())
                    changeInFilter(movieAdapter)
                }
            }
        })
    }

    private fun changeInFilter(movieAdpater : MovieListAdapter) {
        viewModel.movieList.observe(this, Observer {
            movieList.clear()
            movieList.addAll(it)
            movieAdpater.notifyDataSetChanged()
        })
    }
}
