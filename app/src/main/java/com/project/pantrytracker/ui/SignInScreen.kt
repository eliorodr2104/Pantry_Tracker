package com.project.pantrytracker.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.pantrytracker.Firebase.LoginGoogle.SignInState
import com.project.pantrytracker.R

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Surface (
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    all = 15.dp
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.sign_in_screen_removebg_preview_2),
                    contentDescription = "Image",
                    colorFilter = ColorFilter.tint(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(
                    modifier = Modifier
                        .size(10.dp)
                )

                Text(
                    text = "Set Up Account",
                    style = MaterialTheme.typography.titleLarge
                        .copy(
                            fontSize = 25.sp
                        ),
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onSignInClick,
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium

                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    all = 5.dp
                                ),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.google_sign_in),
                                contentDescription = "Google Icon Sign-in",
                                tint = Color.Unspecified
                            )

                            Spacer(
                                modifier = Modifier
                                    .size(5.dp)
                            )

                            Text(
                                text = "Sign-up with Google",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 19.sp
                                ),
                            )

                        }

                    }

                    Button(
                        onClick = onSignInClick,
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    all = 5.dp
                                ),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.apple_sign_in),
                                contentDescription = "Apple Icon Sign-in",
                                tint = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(
                                modifier = Modifier
                                    .size(5.dp)
                            )

                            Text(
                                text = "Sign-up with Apple",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 19.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                        }

                    }
                }

            }


        }
    }
}