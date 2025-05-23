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
fun DietPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    var mealType by remember { mutableStateOf("") }
    var calorieCount by remember { mutableStateOf("") }
    var mealDescription by remember { mutableStateOf("") }

    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {//User Home Page
        Text(text = "Diet Log", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        //Meal type input
        OutlinedTextField(
            value = mealType,
            onValueChange = { newText ->
                if (newText.all { it.isLetter() || it.isWhitespace() }) {
                    mealType = newText
                }
            },
            label = { Text(text = "Meal Type") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        //Calorie Count input
        OutlinedTextField(
            value = calorieCount,
            onValueChange = { newText ->
                if (newText.all { it.isDigit() || it == '.' }) {
                    calorieCount = newText
                }
            },
            label = { Text(text = "Calorie Count") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        //Meal Description input
        OutlinedTextField(
            value = mealDescription,
            onValueChange = { mealDescription = it },
            label = { Text(text = "Meal Description") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Save Button for user inputs
        Button(onClick = {
            // Logic to save or process the activity log
            if (mealType.isNotBlank() && calorieCount.isNotBlank() && mealDescription.isNotBlank()) {
                //  authViewModel() for storing data in RTDB
                authViewModel.saveDiet(mealType, calorieCount.toInt(), mealDescription)
                // clear the fields to enter new diet details
                mealType = ""
                calorieCount = ""
                mealDescription = ""

                Toast.makeText(context, "Meal has been logged", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Save Meal")
        }

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