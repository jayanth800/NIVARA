package com.nivara.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nivara.app.ui.theme.ChampagneGold
import com.nivara.app.ui.theme.DeepForestGreen
import com.nivara.app.ui.theme.WarmIvory

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var isSignUp by remember { mutableStateOf(false) }
    var usePhoneAuth by remember { mutableStateOf(false) }
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var showOtpScreen by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val isAuthed by viewModel.authStateFlow.collectAsState(initial = false)

    LaunchedEffect(isAuthed) {
        if (isAuthed) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NIVARA",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen,
                    letterSpacing = 3.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your Space Beyond Home",
                    fontSize = 13.sp,
                    color = ChampagneGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Error / Success feedback
                when (val state = uiState) {
                    is AuthUiState.Error -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    is AuthUiState.Success -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DeepForestGreen.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.message,
                                color = DeepForestGreen,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    else -> {}
                }

                if (showOtpScreen) {
                    Text(
                        text = "Enter Verification Code",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We have sent a 6-digit OTP to $emailOrPhone",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepForestGreen.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("6-Digit OTP") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onLoginSuccess,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Verify & Continue", color = WarmIvory, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = if (isSignUp) "Create Account" else "Welcome Back",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    if (isSignUp) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = { emailOrPhone = it },
                        label = { Text(if (usePhoneAuth) "Mobile Number" else "Email Address") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (usePhoneAuth) Icons.Default.Phone else Icons.Default.Email,
                                contentDescription = null,
                                tint = DeepForestGreen
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!usePhoneAuth) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = DeepForestGreen
                                )
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { usePhoneAuth = !usePhoneAuth }) {
                            Text(
                                text = if (usePhoneAuth) "Use Email Instead" else "Use Phone OTP",
                                color = DeepForestGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (!isSignUp && !usePhoneAuth) {
                            TextButton(onClick = { viewModel.sendPasswordReset(emailOrPhone) }) {
                                Text(
                                    text = "Forgot Password?",
                                    color = ChampagneGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (usePhoneAuth) {
                                showOtpScreen = true
                            } else {
                                if (isSignUp) {
                                    viewModel.signUp(emailOrPhone, password, name)
                                } else {
                                    viewModel.signIn(emailOrPhone, password)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(color = WarmIvory, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (isSignUp) "Sign Up" else "Login",
                                color = WarmIvory,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { viewModel.signIn("demo@nivara.app", "password123") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Sign in with Google (Demo)", color = DeepForestGreen, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = {
                        isSignUp = !isSignUp
                        viewModel.resetState()
                    }) {
                        Text(
                            text = if (isSignUp) "Already have an account? Login" else "Don't have an account? Sign Up",
                            color = DeepForestGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
