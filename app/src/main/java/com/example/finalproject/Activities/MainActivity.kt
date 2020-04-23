package com.example.finalproject.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finalproject.Adapters.SigninTabsAdapter
import com.example.finalproject.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set up tabView for login/signup
        val fragAdapter =
            SigninTabsAdapter(supportFragmentManager)
        viewPager.adapter = fragAdapter

        tab_layout.setupWithViewPager(viewPager)
    }
}
