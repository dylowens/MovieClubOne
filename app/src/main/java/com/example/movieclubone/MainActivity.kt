package com.example.movieclubone




import JoinClubCreateClub
import JoinClubID
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.movieclubone.ui.theme.MovieClubOneTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieclubone.ui.login.CreateAccount
import com.example.movieclubone.ui.login.CreateClub
import com.example.movieclubone.ui.login.SignIn
import com.example.movieclubone.ui.login.SignInCreateAccount


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieClubOneTheme {
                //Navigation Routing Composable
                Navigation()


            }
        }
    }
}
