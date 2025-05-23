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
import androidx.compose.ui.util.doubleFromBits
import androidx.navigation.NavController
import com.example.healthbuddyapp.AuthState
import com.example.healthbuddyapp.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


@Composable
fun SleepPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    var sleepDuration by remember { mutableStateOf("") }
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val database = FirebaseDatabase.getInstance().getReference("users/$userId/sleepLogs")

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {//User Home Page
        Text(text = "Sleep Log", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        //Sleep log inputs
        OutlinedTextField(
            value = sleepDuration,
            onValueChange = { newText ->
                if (newText.all { it.isDigit() || it == '.' }) {
                    sleepDuration = newText
                } },
            label = { Text(text = "Sleep Duration") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Save Button for user inputs
        Button(onClick = {
            // Logic to save or process the activity log
            if (sleepDuration.isNotBlank()) {

                val sleepHours = sleepDuration.toDoubleOrNull()
                if (sleepHours != null)
                     {
                    //send data to ViewModel for RTDB
                    authViewModel.saveSleep(sleepHours)
                    //clear fields to enter new sleep data
                    sleepDuration = ""

                }
                Toast.makeText(context, "Sleep Duration Logged", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Save Sleep Duration")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            authViewModel.logout()
        }) {
            Text(text = "Logout")
        }
    }
}
