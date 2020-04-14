package com.example.finalproject.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finalproject.Activities.MainActivity

import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.fragment_account_info.*

class AccountInfoFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var numReviews = 0
    private var numRatings = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_info, container, false)
    }

    override fun onStart() {
        super.onStart()

        db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.setFirestoreSettings(settings)

        auth = FirebaseAuth.getInstance()

        //TODO: figure out why this is not working/displaying the users name
        accountName.text = "Hello, " + FirebaseAuth.getInstance().currentUser!!.displayName!!

        db.collection("users").document(auth.currentUser!!.email!!).get().addOnSuccessListener {
            if(it.contains("ratedMovies")){
                val ratings = it.get("ratedMovies") as ArrayList<Int>
                numRatings = ratings.size
            }
            if(it.contains("reviewedMovies")){
                val reviews = it.get("reviewedMovies") as ArrayList<Int>
                numReviews = reviews.size
            }
            ratedMovies.text = "You have rated " + numRatings + " movies"
            reviewedMovies.text = "You have reviewed " + numReviews + " movies"
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Button to logout
        LogoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this.context, MainActivity::class.java)
            startActivity(intent)
        }


    }

}
