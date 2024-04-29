package com.project.pantrytracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.auth.api.identity.Identity
import com.project.pantrytracker.Firebase.LoginGoogle.GoogleAuthUiClient
import com.project.pantrytracker.Firebase.LoginGoogle.SignInViewModel
import com.project.pantrytracker.Firebase.createUserDb
import com.project.pantrytracker.ui.MenuScreen
import com.project.pantrytracker.ui.SignInScreen
import com.project.pantrytracker.ui.theme.PantryTrackerTheme
import kotlinx.coroutines.launch

@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PantryTrackerTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "sign_in") {
                    composable("sign_in") {
                        val viewModel = viewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsState()

                        LaunchedEffect(key1 = Unit) {
                            if(googleAuthUiClient.getSignedInUser() != null) {

                                navController.navigate("profile")
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                lifecycleScope.launch {
                                    val signInResult = googleAuthUiClient.signInWithIntent(
                                        intent = result.data ?: return@launch
                                    )
                                    viewModel.onSignInResult(signInResult)
                                }
                            }
                        )

                        LaunchedEffect(
                            key1 = state.isSignInSuccessful
                        ) {
                            if (state.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Sign in successful",
                                    Toast.LENGTH_LONG
                                ).show()

                                //test
                                createUserDb(googleAuthUiClient.getSignedInUser())

                                navController.navigate("profile")
                                viewModel.resetState()
                            }
                        }

                        SignInScreen(
                            state = state,
                            onSignInClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )
                    }

                    composable("profile") {
                        LaunchedEffect(key1 = Unit) {
                            if(googleAuthUiClient.getSignedInUser() == null) {

                                navController.navigate("sign_in")
                            }
                        }

                        MenuScreen(
                            userData = googleAuthUiClient.getSignedInUser(),
                            signOut = { googleAuthUiClient.signOut() },
                            activity = this@MainActivity
                        )
                    }
                }
            }
        }
    }
}