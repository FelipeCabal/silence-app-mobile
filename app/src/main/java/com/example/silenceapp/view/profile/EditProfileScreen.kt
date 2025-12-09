@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.silenceapp.view.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.silenceapp.viewmodel.AuthViewModel
import com.example.silenceapp.viewmodel.SearchViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.silenceapp.R
import com.example.silenceapp.data.local.entity.UserEntity
import com.example.silenceapp.data.remote.response.ProfileResponse
import com.example.silenceapp.util.toShortDate
import com.example.silenceapp.viewmodel.FirebaseViewModel
import com.example.silenceapp.viewmodel.UserViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController, 
    authViewModel: AuthViewModel, 
    userViewModel: UserViewModel,
    searchViewModel: SearchViewModel
){

    val firebaseViewModel: FirebaseViewModel = viewModel()
    var profile by remember { mutableStateOf<ProfileResponse?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploadingImage by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        Log.d("EditProfileScreen", "🔄 Starting to load profile...")
        try {
            authViewModel.getProfile { p ->
                Log.d("EditProfileScreen", "📥 Profile callback received: ${p?.nombre ?: "null"}")
                isLoading = false
                if (p != null) {
                    profile = p
                    profileImageUrl = p.imagen?.firstOrNull()
                    loadError = null
                    Log.d("EditProfileScreen", "✅ Profile loaded successfully")
                } else {
                    loadError = "Error al cargar el perfil. Por favor, inicia sesión nuevamente."
                    Log.e("EditProfileScreen", "❌ Profile is null")
                }
            }
        } catch (e: Exception) {
            Log.e("EditProfileScreen", "💥 Exception loading profile", e)
            isLoading = false
            loadError = "Error al cargar el perfil: ${e.message}"
        }
    }
    LaunchedEffect(Unit) {
        authViewModel.loadToken { token ->
            Log.d("EditProfileScreen_TOKEN", "Token: ${if (token.isNotEmpty()) "exists (${token.take(20)}...)" else "EMPTY"}")
        }
    }

    // Launcher para seleccionar imagen de galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Mostrar preview temporalmente sin subir aún
            profileImageUrl = it.toString()
        }
    }

    var isEditingName by remember { mutableStateOf(false) }
    var isEditingEmail by remember { mutableStateOf(false) }
    var isEditingSexo by remember { mutableStateOf(false) }
    var isEditingPais by remember { mutableStateOf(false) }
    var isEditingFechaNto by remember { mutableStateOf(false) }

    var expandedSexo by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    // Mostrar loading o error
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (loadError != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = loadError!!,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigateUp() }) {
                    Text("Volver")
                }
            }
        }
        return
    }

    if (profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se pudo cargar el perfil")
        }
        return
    }

    var name by remember(profile) { mutableStateOf(profile!!.nombre) }
    var email by remember(profile) { mutableStateOf(profile!!.email) }
    var sexo by remember(profile) { mutableStateOf(profile!!.sexo) }
    var fechaNto by remember(profile) { mutableStateOf(profile!!.fechaNto) }
    var pais by remember(profile) { mutableStateOf(profile!!.pais) }
    
    // Detectar si hubo cambios
    val hasChanges = remember(name, email, sexo, fechaNto, pais, selectedImageUri, profile) {
        name != profile!!.nombre || 
        email != profile!!.email ||
        sexo != profile!!.sexo ||
        fechaNto != profile!!.fechaNto ||
        pais != profile!!.pais ||
        selectedImageUri != null
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
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = "Editar Perfil",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(selectedImageUri ?: profileImageUrl ?: R.drawable.avatar_placeholder)
                        .crossfade(true)
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.avatar_placeholder)
                        .build(),
                    contentDescription = "Profile Picture",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
                
                // Icono de cámara para indicar que es clickeable
                if (!isUploadingImage) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Cambiar foto",
                            modifier = Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Nombre completo", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { isEditingName = !isEditingName }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit name")
            }
        }

        if (isEditingName) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Correo", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { isEditingEmail = !isEditingEmail }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar correo")
            }
        }

        if (isEditingEmail) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        } else {
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Pais", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { isEditingPais = !isEditingPais }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Pais")
            }
        }

        if (isEditingPais) {
            OutlinedTextField(
                value = pais,
                onValueChange = { pais = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        } else {
            Text(
                text = pais,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sexo", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { isEditingSexo = !isEditingSexo }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Sexo")
            }
        }

        if (isEditingSexo) {
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
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
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
        } else {
            Text(
                text = sexo,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Fecha de Nacimiento", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { isEditingFechaNto = !isEditingFechaNto }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Fecha de Nacimiento")
            }
        }

        if (isEditingFechaNto) {
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
        } else {
            Text(
                text = fechaNto.toShortDate(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val p = profile!!

                if (selectedImageUri != null) {
                    isUploadingImage = true
                    firebaseViewModel.uploadImage(selectedImageUri!!, folder = "profiles/${p.id}") { response ->
                        isUploadingImage = false
                        if (response != null && response.success) {
                            val updatedProfile = UserEntity(
                                remoteId = p.id,
                                nombre = name,
                                email = email,
                                fechaNto = fechaNto,
                                sexo = sexo,
                                pais = pais,
                                imagen = response.data.url
                            )
                            userViewModel.updateUserProfile(updatedProfile) { success ->
                                if (success) {
                                    isEditingName = false
                                    isEditingEmail = false
                                    isEditingSexo = false
                                    isEditingPais = false
                                    isEditingFechaNto = false
                                    selectedImageUri = null
                                    profileImageUrl = response.data.url
                                    Log.d("EditProfile", "Perfil actualizado con nueva imagen")
                                }
                            }
                        } else {
                            Log.e("EditProfile", "Error al subir imagen")
                        }
                    }
                } else {
                    val updatedProfile = UserEntity(
                        remoteId = p.id,
                        nombre = name,
                        email = email,
                        fechaNto = fechaNto,
                        sexo = sexo,
                        pais = pais,
                        imagen = profileImageUrl
                    )
                    userViewModel.updateUserProfile(updatedProfile) { success ->
                        if (success) {
                            isEditingName = false
                            isEditingEmail = false
                            isEditingSexo = false
                            isEditingPais = false
                            isEditingFechaNto = false
                            Log.d("EditProfile", "Perfil actualizado")
                        }
                    }
                }
            },
            enabled = hasChanges && !isUploadingImage,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isUploadingImage) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...")
                }
            } else {
                Text("Guardar")
            }
        }

        Button(onClick = {
            // Limpiar datos del SearchViewModel
            searchViewModel.clearData()
            
            // Hacer logout
            authViewModel.logout()
            
            // Navegar a login
            navController.navigate("login")
        }) {
            Text("Logout")
        }
    }
}