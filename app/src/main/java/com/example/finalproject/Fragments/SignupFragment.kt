package com.example.finalproject.Fragments

import android.content.ContentValues
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.finalproject.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.fragment_signup.*
import java.util.HashMap

// fragment for account creation
class SignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.firestoreSettings = settings
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onStart(){
        super.onStart()
        signupButton.setOnClickListener{signUp()}
    }


    private fun signUp() {
        // cancel if input is invalid
        if (!validateForm()) {
            return
        }
        val name = fieldName.text.toString()
        val email = fieldEmail.text.toString()
        val password = fieldPassword.text.toString()


        // [START create_user_with_email]
        // creates user, then they can login through the login tab
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    Toast.makeText(this.context, "User creation successful.",
                        Toast.LENGTH_SHORT).show()
                    fieldEmail.text.clear()
                    fieldPassword.text.clear()
                    fieldPasswordConfirm.text.clear()
                    fieldName.text.clear()

                    val userMap: MutableMap<String, Any> = HashMap()
                    userMap["Name"] = name
                    userMap["Email"] = email

                    db.collection("users").document(email).set(userMap)
                        .addOnSuccessListener(OnSuccessListener { documentReference ->
                            Toast.makeText(this.context, "Inserted into database", Toast.LENGTH_LONG)
                        })
                        .addOnFailureListener(OnFailureListener { e ->
                            Toast.makeText(this.context, "Failed to insert into database!", Toast.LENGTH_LONG)
                        })

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this.context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    // ensure all inputs are valid, and password matched confirm password
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
        val name = fieldName.text.toString()
        if (TextUtils.isEmpty(name)) {
            fieldName.error = "Required."
            valid = false
        } else {
            fieldName.error = null
        }
        val confirm = fieldPasswordConfirm.text.toString()
        if (TextUtils.isEmpty(confirm)){
            fieldPasswordConfirm.error="Required."
            valid = false
        }else if (!confirm.equals(password)){
            fieldPasswordConfirm.error="Passwords must match."
            valid = false
        }

        return valid
    }
}

