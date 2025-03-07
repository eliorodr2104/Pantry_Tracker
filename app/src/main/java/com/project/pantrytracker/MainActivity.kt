package com.project.pantrytracker

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.common.api.ApiException
import com.project.pantrytracker.Firebase.LoginGoogle.GoogleAuthUiClient
import com.project.pantrytracker.Firebase.LoginGoogle.SignInViewModel
import com.project.pantrytracker.ui.MenuScreen
import com.project.pantrytracker.ui.SignInScreen
import com.project.pantrytracker.ui.theme.PantryTrackerTheme
import com.project.pantrytracker.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PantryTrackerTheme {
                val navController     = rememberNavController()

                val viewModelUser = viewModel<UserViewModel>()

                NavHost(
                    navController = navController,
                    startDestination = "sign_in"
                ) {

                    composable(
                        route= "sign_in"
                    ) {
                        val viewModel = viewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsState()

                        LaunchedEffect(key1 = Unit) {
                            if(googleAuthUiClient.getSignedInUser() != null) {
                                navController.navigate("profile") {
                                    popUpTo("sign_in") { inclusive = true }
                                }
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                lifecycleScope.launch {
                                    if (result.resultCode == Activity.RESULT_CANCELED) {
                                        Log.e("GoogleSignIn", "Sign-in canceled by user")
                                        return@launch
                                    }

                                    val intent = result.data ?: return@launch

                                    try {
                                        val signInResult = googleAuthUiClient.signInWithIntent(intent)
                                        viewModel.onSignInResult(signInResult)
                                    } catch (e: ApiException) {
                                        Log.e("GoogleSignIn", "Sign-in failed: ${e.message}")
                                    }
                                }
                            }
                        )

                        LaunchedEffect(
                            key1 = state.isSignInSuccessful
                        ) {
                            if (state.isSignInSuccessful) {
                                viewModelUser.createUser(googleAuthUiClient.getSignedInUser())

                                navController.navigate("profile") {
                                    popUpTo("sign_in") { inclusive = true }
                                }
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

                                navController.navigate("sign_in") {
                                    popUpTo("profile") { inclusive = true }
                                }
                            }
                        }

                        //Menù dell'app con l'aggiunta della UI per il tablet
                        MenuScreen(
                            userData = googleAuthUiClient.getSignedInUser(),
                            signOut = { googleAuthUiClient.signOut() }
                        )
                    }
                }
            }
        }
    }
}