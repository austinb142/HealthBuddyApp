package com.example.healthbuddyapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

    //UserProfile data class for storing user data on RTDB
    data class UserProfile(val email: String = "", val username: String = "", val bmi: Double? = null,
                           val activityLog: List<ActivityLog> = emptyList(),
                           val dietLog: List<DietLog> = emptyList(),
                           val sleepLog: List<SleepLog> = emptyList()
    )
    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    //function for fetching user data from RTDB
    fun getUserProfile()
    {
        val uid = auth.currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val email = snapshot.child("email").getValue(String::class.java) ?: ""
                val username = snapshot.child("username").getValue(String::class.java) ?: ""
                val bmi = snapshot.child("bmi").getValue(Double::class.java)

                //activity log
                val activitySnapshot = snapshot.child("activityLogs")
                val activityLogs = mutableListOf<ActivityLog>()
                for (dateNode in activitySnapshot.children) {
                    for (logNode in dateNode.children) {
                        val log = logNode.getValue(ActivityLog::class.java)
                        if (log != null) {
                            activityLogs.add(log)
                        }
                    }
                }

                //diet log
                val dietSnapshot = snapshot.child("dietLogs")
                val dietLogs = mutableListOf<DietLog>()
                for (dateNode in dietSnapshot.children) {
                    for (logNode in dateNode.children) {
                        val log = logNode.getValue(DietLog::class.java)
                        if (log != null) {
                            dietLogs.add(log)
                        }
                    }
                }

                //sleep log
                val sleepSnapshot = snapshot.child("sleepLogs")
                val sleepLogs = mutableListOf<SleepLog>()
                for (dateNode in sleepSnapshot.children) {
                    for (logNode in dateNode.children) {
                        val log = logNode.getValue(SleepLog::class.java)
                        if (log != null) {
                            sleepLogs.add(log)
                        }
                    }
                }

                val user = UserProfile(
                    username = username,
                    email = email,
                    bmi = bmi,
                    activityLog = activityLogs.sortedByDescending { it.timestamp },
                    dietLog = dietLogs.sortedByDescending { it.timestamp },
                    sleepLog = sleepLogs.sortedByDescending { it.timestamp }
                )

                _userProfile.postValue(user)
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
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
                _authState.value = AuthState.UsernameCreated
                //go to user profile page
                //NavController.navigate("home")


            },
            onFailure = { exception ->
                println("Signup failure: ${exception.message}")
                _authState.value = AuthState.Unauthenticated
            })

    }

    fun saveBMI(bmi: Double) {
        val uid = auth.currentUser?.uid ?: return
        val bmiRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("bmi")

        bmiRef.setValue(bmi)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "BMI saved successfully")
                getUserProfile()
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Error saving BMI", e)
            }
    }

    fun saveActivity(activityName: String, activityLength: Int, activityDetails: String) {
        val uid = auth.currentUser?.uid ?: return
        val activityRef =
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("activityLogs")

        //Use the date as the key node.
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val timeKey = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        // format the date to YY-MM-DD HH:MM:SS
        val timestamp = "$dateKey : $timeKey"

        val activityLog = mapOf(
            "activityName" to activityName,
            "activityLength" to activityLength,
            "activityDetails" to activityDetails,
            "timestamp" to timestamp

        )
        // Push under: users/{uid}/activityLogs/{dateKey}/{timeKey}
        //new activity entry transmission
        activityRef.child(dateKey).child(timeKey).setValue(activityLog)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Activity saved successfully under $dateKey/$timeKey")
                getUserProfile()        //refresh user profile to show activity
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Error saving activity", e)
            }
    }

    //save diet log to RTDB
    fun saveDiet(mealType: String, calorieCount: Int, mealDescription: String) {
        val uid = auth.currentUser?.uid ?: return
        val dietRef =
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("dietLogs")

        //Use the date as the key node.
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val timeKey = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        // format the date to YY-MM-DD HH:MM:SS
        val timestamp = "$dateKey : $timeKey"

        val dietLog = mapOf(
            "mealType" to mealType,
            "calories" to calorieCount,
            "description" to mealDescription,
            "timestamp" to timestamp
        )
        // push under: (current)user/{uid}/dietLogs/{dateKey}/{timeKey}
        dietRef.child(dateKey).child(timeKey).setValue(dietLog)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Activity saved successfully under $timestamp")
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Error saving activity", e)
            }
    }

    //save sleep()
    fun saveSleep(sleepDuration: Double) {
        val uid = auth.currentUser?.uid ?: return
        val dietRef =
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("sleepLogs")

        //Use the date as the key node.
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val timeKey = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        // format the date to YY-MM-DD HH:MM:SS
        val timestamp = "$dateKey : $timeKey"

        val sleepLog = mapOf(
            "Hours" to sleepDuration,
            "timestamp" to timestamp
        )
        // push under: (current)user/{uid}/dietLogs/{dateKey}/{timeKey}
        dietRef.child(dateKey).child(timeKey).setValue(sleepLog)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Sleep saved successfully under $timestamp")
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Error saving sleep", e)
            }
    }

    //logout()
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}

//data class for Activity
data class ActivityLog(
    val activityName: String = "",
    val activityLength: Int = 0,
    val activityDetails: String = "",
    val timestamp: String = ""
)
data class DietLog(
    val mealType: String = "",
    val calories: Int = 0,
    val description: String = "",
    val timestamp: String = ""
)
data class SleepLog(
    val Hours: Double = 0.0,
    val timestamp: String = ""
)

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    object UsernameCreated : AuthState()
}