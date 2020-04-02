package com.example.finalproject.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.finalproject.Fragments.*


class MovieTabsAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                TrendingFragment();
            }
            1 -> {
                SearchFragment();
            }
            2 -> {
                WatchlistFragment();
            }
            else -> null!!
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Trending"
            1 -> "Search"
            2 -> "Watchlist"
            else -> {
                return ""
            }
        }
    }
}