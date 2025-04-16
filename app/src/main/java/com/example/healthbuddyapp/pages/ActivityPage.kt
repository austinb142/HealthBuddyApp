
package com.example.healthbuddyapp.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthbuddyapp.AuthState
import com.example.healthbuddyapp.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Locale



@Composable
fun ActivityPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    //State to hold the user's input
    var activityName by remember { mutableStateOf("") }
    var activityLength by remember { mutableStateOf("") }
    var activityDetails by remember { mutableStateOf("") }

    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val currentDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())}
    //activityRef establishes where in FIREBASE RTDB the activity logs will be stored
    //val activityRef = FirebaseDatabase.getInstance().getReference("users/$userId/activityLogs")

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        //User Home Page
        Text(text = "Activity Tracker", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Input for activity name/type
        OutlinedTextField(
            value = activityName,
            onValueChange = { activityName = it },
            label = { Text(text = "Activity Name/Type") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input for Activity Length
        OutlinedTextField(
            value = activityLength,
            onValueChange = { activityLength = it },
            label = { Text(text = "Activity Length (in minutes)") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input for Activity Details
        OutlinedTextField(
            value = activityDetails,
            onValueChange = { activityDetails = it },
            label = { Text(text = "Activity Details") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Save Button for user inputs
        Button(onClick = {
            // Logic to save or process the activity log
            if (activityName.isNotBlank() && activityLength.isNotBlank() && activityDetails.isNotBlank()) {
                // authviewmodel() for storing data in RTDB
                authViewModel.saveActivity(activityName, activityLength.toInt(), activityDetails)
                Toast.makeText(context, "Activity Saved", Toast.LENGTH_SHORT).show()
                // clear the fields to allow for a new activity to be entered
                activityName = ""
                activityLength = ""
                activityDetails = ""

            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Save Activity")
        }


        // Back Button
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "back")
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Logout Button
        TextButton(onClick = {
            authViewModel.logout()
        }) {
            Text(text = "Logout")
        }
    }

}
