package com.example.healthbuddyapp

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class AuthManager {
    private val auth: FirebaseAuth = Firebase.auth
    private val database: DatabaseReference = Firebase.database.reference
    private val usersRef: DatabaseReference = database.child("users")

    fun createUser(
        email: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("AuthManager", "createUser() called")
        Log.d("AuthManager", "  Email: $email")
        Log.d("AuthManager", "  Password: $password")
        Log.d("AuthManager", "  Username: $username")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userId = user.uid
                        storeEmailAndUsername(userId, email, username, onSuccess, onFailure)
                    }
                } else {
                    onFailure(task.exception!!)
                }
            }
    }

    private fun storeEmailAndUsername(
        userId: String,
        email: String,
        username: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        Log.d("AuthManager", "createUser() called")
        Log.d("AuthManager", "  User ID: $userId")
        Log.d("AuthManager", "  Email: $email")
        Log.d("AuthManager", "  Username: $username")

        val userMap = mapOf(
            "email" to email,
            "username" to username
        )

        // Store the user's email and username in the "users" node in the database
        // seperate from authentication in AuthViewModel authentication credentials
        // Upon successful signup, send the username to RTDB..
        usersRef.child(userId).setValue(userMap)
            .addOnSuccessListener {
                Log.d("AuthManager", "Email and username stored successfully")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("AuthManager", "Error storing email and username", e)
                onFailure(e)
            }
    }
}