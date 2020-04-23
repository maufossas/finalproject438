package com.example.finalproject.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.finalproject.Activities.MoviesActivity

import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

// handles logins for created accounts
class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set up firebase authentication
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        // load app if they're already signed in
        if(currentUser != null){
            val intent = Intent(this.context, MoviesActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener{signIn()}
    }

    private fun signIn() {
        val email = fieldEmail.text.toString()
        val password = fieldPassword.text.toString()

        Log.d(TAG, "signIn:$email")

        // ensure input is valid
        if (validateForm()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        // clear out sign in info
                        fieldEmail.text.clear()
                        fieldPassword.text.clear()
                        val user = auth.currentUser
                        Toast.makeText(this.context, "Authentication success.",
                            Toast.LENGTH_SHORT).show()
                        val intent = Intent(this.context, MoviesActivity::class.java)
                        // launch movie app
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this.context, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    // make sure input is valid (email, password)
    private fun validateForm(): Boolean {
        var valid = true

        val email = fieldEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            fieldEmail.error = "Required."
            valid = false
        } else {
            fieldEmail.error = null
        }

        val password = fieldPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            fieldPassword.error = "Required."
            valid = false
        } else {
            fieldPassword.error = null
        }

        return valid
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}
