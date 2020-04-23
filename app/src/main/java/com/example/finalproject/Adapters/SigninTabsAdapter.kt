package com.example.finalproject.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.finalproject.Fragments.LoginFragment
import com.example.finalproject.Fragments.SignupFragment

// adapter for the log in / sign up fragments
class SigninTabsAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                LoginFragment();
            }
            1 -> {
                SignupFragment();
            }
            else -> null!!
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Login"
            1 -> "Sign Up"
            else -> {
                return ""
            }
        }
    }
}