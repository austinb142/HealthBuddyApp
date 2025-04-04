package com.example.healthbuddyapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val authManager: AuthManager = AuthManager()

    init {
        checkAuthStatus()
    }

    //check if user is authenticated
    fun checkAuthStatus() {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    //login(username, password)
    fun login(email: String, password: String) {

        //if the email or password field is empty
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            //if the user has valid email and password
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something Unexpected Happened")
                }
            }
    }

    //signup(email, password)
    fun signup(email: String, username: String, password: String) {

        //if the email or password field is empty
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email, username, and password cannot be empty")
            return
        }

        Log.d("AuthViewModel", "signUp() called")
        Log.d("AuthViewModel", "  Email: $email")
        Log.d("AuthViewModel", "  Password: $password")
        Log.d("AuthViewModel", "  Username: $username")

        //validate new user with email, username, and password
        authManager.createUser(email, username, password,
            onSuccess = {
                println("Signup successful")
                _authState.value = AuthState.Authenticated
            },
            onFailure = { exception ->
                println("Signup failure: ${exception.message}")
                _authState.value = AuthState.Unauthenticated
            })

    }

    //logout()
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
