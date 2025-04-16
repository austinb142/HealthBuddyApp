package com.example.healthbuddyapp.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healthbuddyapp.AuthState
import com.example.healthbuddyapp.AuthViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val userProfile = authViewModel.userProfile.observeAsState()

    var showBMI by remember { mutableStateOf(false) }

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Authenticated) {
            authViewModel.getUserProfile()
        } else if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("signup") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){//User Home Page

        userProfile.value?.let { user ->
            Text(
                text = "Welcome to Health Buddy!, ${user.username}!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Email: ${user.email}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))

            //if user has previously calculated BMI
            //show BMI and allow user to recalculate
            if (user.bmi != null && !showBMI) {
                Text(text = "BMI: ${String.format("%.2f", user.bmi)}", fontSize = 18.sp)
                Button(onClick = {
                    showBMI = true
                }) {
                    Text(text = "Recalculate your BMI")
                }
            } else if (showBMI || user.bmi == null)
            {
                BMIcalculator(authViewModel = authViewModel,
                    userBMI = user.bmi,
                    onBmiCalculated = {
                        showBMI = false
                    })
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Displays the most recent activity entry on users profile.
            if (user.activityLog.isNotEmpty()) {
                val recentActivity = user.activityLog.first()
                Text(text =
                    "Recent Activity:",
                    fontSize = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Name: ${recentActivity.activityName}," +
                        " Length: ${recentActivity.activityLength} minutes," +
                        " Details: ${recentActivity.activityDetails}",
                    fontSize = 16.sp
                )
            } else {
                Text(text = "No recent activity found")
            }
            TextButton(onClick = {
                navController.navigate("activity")
            }) {
                Text(text = "Activity Tracker")
            }

            Spacer(modifier = Modifier.height(16.dp))

            //displays most recent diet entry on users profile
            if (user.dietLog.isNotEmpty()) {
                val recentDiet = user.dietLog.first()
                Text(text =
                    "Recent Diet:",
                    fontSize = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Meal: ${recentDiet.mealType}," +
                        " Calories: ${recentDiet.calories}," +
                        " Details: ${recentDiet.description}",
                    fontSize = 16.sp
                )
            } else {
                Text(text = "No recent Diet found")
            }
            TextButton(onClick = {
                navController.navigate("Diet")
            }) {
                Text(text = "Diet Tracker")
            }

            Spacer(modifier = Modifier.height(16.dp))

            //displays most recent sleep entry on users profile
            if (user.sleepLog.isNotEmpty()) {
                val recentSleep = user.sleepLog.first()
                Text(text =
                    "Recent Sleep:",
                    fontSize = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Duration: ${recentSleep.Hours} hours,",
                    fontSize = 16.sp
                )
            } else {
                Text(text = "No recent sleep found")
            }
            TextButton(onClick = {
                navController.navigate("Sleep")
            }) {
                Text(text = "Sleep Tracker")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = {
            authViewModel.logout()
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "Logout")
        }
    }

}