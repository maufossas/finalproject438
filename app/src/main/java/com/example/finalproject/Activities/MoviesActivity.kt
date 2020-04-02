package com.example.finalproject.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finalproject.Adapters.MovieTabsAdapter
import com.example.finalproject.R
import kotlinx.android.synthetic.main.activity_main.*

class MoviesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        //set up tabView
        val fragAdapter =
            MovieTabsAdapter(supportFragmentManager)
        viewPager.adapter = fragAdapter

        tab_layout.setupWithViewPager(viewPager)
    }
}
