package com.example.silenceapp.view.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.silenceapp.R
import com.example.silenceapp.ui.theme.errorColor
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.primaryColor
import com.example.silenceapp.ui.theme.secondaryColor
import com.example.silenceapp.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: UserViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    var fechaNto by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var expandedSexo by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val authSuccess by viewModel.authSuccess.collectAsState()

    LaunchedEffect(authSuccess) {
        if (authSuccess) {
            message = "Usuario registrado correctamente"
            viewModel.clearAuthSuccess()
            navController.popBackStack()
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        runCatching {
                            if (millis != null) {
                                val date = java.time.Instant
                                    .ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                fechaNto = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            }
                        }.onFailure {
                            // En caso de error, evita crash y cierra el diálogo
                            fechaNto = ""
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(40.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de la app",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            "Silence App",
            style = MaterialTheme.typography.headlineLarge,
            color = primaryColor
        )

        Spacer(Modifier.height(20.dp))

        Text(
            "Registro",
            style = MaterialTheme.typography.titleLarge,
            color = onBackgroundColor
        )

        Spacer(Modifier.height(20.dp))

        // Campo Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        // Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        // Confirmación
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Repetir contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        // Dropdown Sexo
        ExposedDropdownMenuBox(
            expanded = expandedSexo,
            onExpandedChange = { expandedSexo = !expandedSexo }
        ) {
            OutlinedTextField(
                value = sexo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sexo") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedSexo,
                onDismissRequest = { expandedSexo = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Masculino") },
                    onClick = {
                        sexo = "Masculino"
                        expandedSexo = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Femenino") },
                    onClick = {
                        sexo = "Femenino"
                        expandedSexo = false
                    }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Fecha de nacimiento (abre date picker)
        OutlinedTextField(
            value = fechaNto,
            onValueChange = {},
            label = { Text("Fecha de nacimiento") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        Icons.Filled.CalendarMonth,
                        contentDescription = null
                    )
                }
            },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // País
        OutlinedTextField(
            value = pais,
            onValueChange = { pais = it },
            label = { Text("País") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        // Error message
        if (message != null) {
            Text(text = message!!, color = errorColor)
            Spacer(Modifier.height(10.dp))
        }

        // Botón de registro
        Button(
            onClick = {
                if (nombre.isEmpty() ||
                    email.isEmpty() ||
                    password.isEmpty() ||
                    confirmPassword.isEmpty() ||
                    sexo.isEmpty() ||
                    fechaNto.isEmpty() ||
                    pais.isEmpty()
                ) {
                    message = "Todos los campos son obligatorios"
                } else if (password != confirmPassword) {
                    message = "Las contraseñas no coinciden"
                } else {
                    viewModel.registerUser(
                        nombre = nombre,
                        email = email,
                        password = password,
                        sexo = sexo,
                        fechaNto = fechaNto,
                        pais = pais
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        Spacer(Modifier.height(10.dp))

        // Link a login
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("¿Ya tienes cuenta?", color = onBackgroundColor)
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Inicia sesión", color = secondaryColor)
            }
        }
    }
}
