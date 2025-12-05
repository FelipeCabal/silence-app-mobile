package com.example.silenceapp.view.chat

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.silenceapp.ui.components.ChatTypeButton
import com.example.silenceapp.ui.components.ImagePickerCircle
import com.example.silenceapp.ui.theme.backgroundColor
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.postBackgroundColor
import com.example.silenceapp.ui.theme.primaryColor
import com.example.silenceapp.ui.theme.secondaryColor
import com.example.silenceapp.viewmodel.AuthViewModel
import com.example.silenceapp.viewmodel.ChatViewModel
import com.example.silenceapp.viewmodel.FirebaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val firebaseViewModel: FirebaseViewModel = viewModel()
    
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedType by remember { mutableStateOf("group") }
    
    val isLoading by chatViewModel.isLoading.collectAsState()
    val error by chatViewModel.error.collectAsState()
    var isUploadingImage by remember { mutableStateOf(false) }
    
    var showSuccessDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Volver",
                    tint = onBackgroundColor
                )
            }

            Text(
                text = "Crear ${if (selectedType == "group") "Grupo" else "Comunidad"}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor
            )

            TextButton(
                onClick = {
                    if (nombre.isNotBlank()) {

                        if (selectedImageUri != null) {
                            isUploadingImage = true
                            firebaseViewModel.uploadImage(
                                imageUri = selectedImageUri!!,
                                folder = "chats-profile/${if (selectedType == "group") "groups" else "communities"}"
                            ) { response ->
                                isUploadingImage = false
                                if (response != null && response.success) {

                                    val finalImageUrl = response.data.url
                                    if (selectedType == "group") {
                                        chatViewModel.createGroupWithAuth(nombre, descripcion, finalImageUrl) { result ->
                                            result.fold(
                                                onSuccess = { chatId ->
                                                    showSuccessDialog = true
                                                },
                                                onFailure = { }
                                            )
                                        }
                                    } else {
                                        chatViewModel.createCommunityWithAuth(nombre, descripcion, finalImageUrl) { result ->
                                            result.fold(
                                                onSuccess = { chatId ->
                                                    showSuccessDialog = true
                                                },
                                                onFailure = { }
                                            )
                                        }
                                    }
                                } else {
                                    Log.e("CreateChat", "Error al subir imagen")
                                }
                            }
                        } else {
                            // Sin imagen, crear directamente
                            if (selectedType == "group") {
                                chatViewModel.createGroupWithAuth(nombre, descripcion, "") { result ->
                                    result.fold(
                                        onSuccess = { chatId ->
                                            showSuccessDialog = true
                                        },
                                        onFailure = { /* Error manejado en ViewModel */ }
                                    )
                                }
                            } else {
                                chatViewModel.createCommunityWithAuth(nombre, descripcion, "") { result ->
                                    result.fold(
                                        onSuccess = { chatId ->
                                            showSuccessDialog = true
                                        },
                                        onFailure = { /* Error manejado en ViewModel */ }
                                    )
                                }
                            }
                        }
                    }
                },
                enabled = nombre.isNotBlank() && !isLoading && !isUploadingImage
            ) {
                if (isUploadingImage) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = primaryColor,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Subiendo...",
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "Crear",
                        color = if (nombre.isNotBlank() && !isLoading) primaryColor else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = onBackgroundColor.copy(alpha = 0.1f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Tipo de chat",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ChatTypeButton(
                    text = "Grupo",
                    isSelected = selectedType == "group",
                    onClick = { selectedType = "group" },
                    modifier = Modifier.weight(1f)
                )

                ChatTypeButton(
                    text = "Comunidad",
                    isSelected = selectedType == "community",
                    onClick = { selectedType = "community" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Imagen",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ImagePickerCircle(
                selectedImageUri = selectedImageUri,
                currentImageUrl = null,
                size = 120.dp,
                onImageClick = { imagePickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nombre *",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Nombre del ${if (selectedType == "group") "grupo" else "comunidad"}",
                        color = onBackgroundColor.copy(alpha = 0.4f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = postBackgroundColor,
                    unfocusedContainerColor = postBackgroundColor,
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = onBackgroundColor.copy(alpha = 0.2f),
                    focusedTextColor = onBackgroundColor,
                    unfocusedTextColor = onBackgroundColor
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        text = "Describe de qué trata...",
                        color = onBackgroundColor.copy(alpha = 0.4f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = postBackgroundColor,
                    unfocusedContainerColor = postBackgroundColor,
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = onBackgroundColor.copy(alpha = 0.2f),
                    focusedTextColor = onBackgroundColor,
                    unfocusedTextColor = onBackgroundColor
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(20.dp))

            error?.let { errorMsg ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMsg,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = secondaryColor)
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = "¡${if (selectedType == "group") "Grupo" else "Comunidad"} creado!",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Tu ${if (selectedType == "group") "grupo" else "comunidad"} ha sido creado exitosamente.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Aceptar", color = primaryColor)
                }
            },
            containerColor = postBackgroundColor,
            titleContentColor = onBackgroundColor,
            textContentColor = onBackgroundColor
        )
    }
}
