package com.example.healthbuddyapp.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button
import com.example.healthbuddyapp.AuthViewModel

@Composable
fun BMIcalculator(authViewModel: AuthViewModel, userBMI: Double?, onBmiCalculated: () -> Unit) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf<Double?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (kg)") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (m)") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val weightValue = weight.toDoubleOrNull()
            val heightValue = height.toDoubleOrNull()
            if (weightValue != null && heightValue != null && heightValue != 0.0 ) {
                bmi = weightValue / (heightValue * heightValue)
                authViewModel.saveBMI(bmi!!)
                authViewModel.getUserProfile()  //refresh user data
                onBmiCalculated()               //collapse calculator
            } else {
                bmi = null
            }
        }) {
            Text("Calculate BMI")
        }
        Spacer(modifier = Modifier.height(16.dp))
        bmi?.let {
            Text(text = "Your BMI is: ${String.format("%.2f", it)}")
        }
    }
}
