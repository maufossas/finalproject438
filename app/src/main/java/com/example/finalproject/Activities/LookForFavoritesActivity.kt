package com.example.finalproject.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.APIViewModel
import com.example.finalproject.Adapters.AddToFavoritesAdapter
import com.example.finalproject.Data.Movie
import com.example.finalproject.R
import kotlinx.android.synthetic.main.activity_look_for_favorites.*

class LookForFavoritesActivity : AppCompatActivity() {

    lateinit var viewModel: APIViewModel
    var movieList: ArrayList<Movie> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_look_for_favorites)

        // api viewmodel
        viewModel = ViewModelProvider(this).get(APIViewModel::class.java)

        cancelFavoriteSearch.setOnClickListener {
            finish()
        }

        // set up recycler view with grid layout adapter
        val recyclerView = searchForFavoriteRecyclerView
        val movieAdapter = AddToFavoritesAdapter(movieList, this)
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
                if(searchForFavorite.text.isNotEmpty()){
                    viewModel.getBySearch(searchForFavorite.text.toString())
                    changeInFilter(movieAdapter)
                }
            }
        })
    }

    private fun changeInFilter(movieAdpater : AddToFavoritesAdapter) {
        viewModel.movieList.observe(this, Observer {
            movieList.clear()
            movieList.addAll(it)
            movieAdpater.notifyDataSetChanged()
        })
    }

    fun openMovie(id : Int){
        val intent = Intent(this, SingleMovieActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("favorites", 1)
        startActivityForResult(intent, 12345)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 123456){
            finish()
        }
    }
}
