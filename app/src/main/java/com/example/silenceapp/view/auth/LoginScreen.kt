package com.example.silenceapp.view.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.silenceapp.ui.theme.primaryColor
import com.example.silenceapp.ui.theme.secondaryColor
import com.example.silenceapp.R
import com.example.silenceapp.ui.theme.errorColor
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.viewmodel.UserViewModel


@Composable
fun LoginScreen(navController: NavController, viewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null)  }

    Column(
        modifier = Modifier.fillMaxSize().padding(50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de la app",
            modifier = Modifier
                .size(150.dp),
            contentScale = ContentScale.Fit
        )

        Text("Silence App",
            style = MaterialTheme.typography.headlineLarge,
            color = primaryColor
        )

        Spacer(Modifier.height(20.dp))

        Text("Login",
            style = MaterialTheme.typography.titleLarge,
            color = onBackgroundColor
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(
                "Correo",
            )},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                IconButton(onClick = {
                    passwordVisible = !passwordVisible },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = image,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        modifier = Modifier.size(18.dp)

                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        if (message != null) {
            Text(
                text = message!!,
                color = errorColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor
            ),
            onClick = {
                if (email.isEmpty() || password.isEmpty()){
                    message = "Todos los campos son obligatorios"
                }else{
                    viewModel.loginUser(email, password) { success ->
                        message = if (success) "Inicio exitoso" else "Credenciales inválidas"
                        if (success) navController.navigate("add-post") }
                }

            }
        ) {
            Text(
                "Iniciar sesión",
                color = onBackgroundColor

            )
        }

        Spacer(Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¿No tienes cuenta?",
                color = onBackgroundColor,
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(
                onClick = { navController.navigate("register") },
                contentPadding = PaddingValues(4.dp)) {
                Text(
                    text = "Regístrate",
                    color = secondaryColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        TextButton(
            onClick = { navController.navigate("forgot_password") }) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.Underline
                ),
                color = onBackgroundColor
            )
        }
    }
}
