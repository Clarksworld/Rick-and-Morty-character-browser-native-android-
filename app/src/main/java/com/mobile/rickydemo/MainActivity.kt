package com.mobile.rickydemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mobile.rickydemo.ui.character_detail.CharacterDetailScreen
import com.mobile.rickydemo.ui.character_list.CharacterListScreen
import com.mobile.rickydemo.ui.splash.SplashScreen
import com.mobile.rickydemo.ui.theme.RickydemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickydemoTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
                                navController.navigate("character_list") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("character_list") {
                        CharacterListScreen(
                            onCharacterClick = { character ->
                                navController.navigate("character_detail/${character.id}")
                            }
                        )
                    }
                    composable(
                        route = "character_detail/{characterId}",
                        arguments = listOf(
                            navArgument("characterId") { type = NavType.IntType }
                        )
                    ) {
                        CharacterDetailScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
