package com.example.movieclubone.Feature.SignIn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun CreateClub(navController: NavHostController) {
    var clubName by remember { mutableStateOf("") }
    var clubId by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Club Name")
        BasicTextField(
            value = clubName,
            onValueChange = { clubName = it },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Club ID")
        BasicTextField(
            value = clubId,
            onValueChange = { clubId = it },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("HomePage")
                // Here you can add the logic to handle club creation
            }
        ) {
            Text("Create Club")
        }
    }
}
