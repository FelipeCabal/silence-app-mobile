package com.example.silenceapp.view.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.silenceapp.viewmodel.UserViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
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

@Composable
fun EditProfileScreen(navController: NavController, viewModel: UserViewModel){

    // si quiere acceder a esta vista debe colocar aqui el correo de su usuario de pruebas
    val userEmail = "felipe@gmail.com"

    var user by remember { mutableStateOf<UserEntity?>(null) }

    var isEditingName by remember { mutableStateOf(false) }
    var isEditingDescription by remember { mutableStateOf(false) }
    var isEditingPhone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getUserByEmail(userEmail) { fetchedUser ->
            user = fetchedUser
        }
    }

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var name by remember { mutableStateOf(user!!.name) }
    var description by remember { mutableStateOf(user!!.description ?: "") }
    var phone by remember { mutableStateOf(user!!.phoneNumber) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = "Editar Perfil",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user!!.imageUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.avatar_placeholder)
                    .error(R.drawable.avatar_placeholder)
                    .build(),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

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
            Text("Descripción", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { isEditingDescription = !isEditingDescription }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit description")
            }
        }

        if (isEditingDescription) {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        } else {
            Text(
                text = description,
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
            Text("Telefono", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { isEditingPhone = !isEditingPhone }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar telefono")
            }
        }

        if (isEditingPhone) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        } else {
            Text(
                text = phone,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val updatedUser = user!!.copy(
                    name = name,
                    description = description
                )

                viewModel.updateUserProfile(updatedUser) { success ->
                    if (success) {
                        isEditingName = false
                        isEditingDescription = false
                        // podrías mostrar un snackbar o volver atrás
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Guardar")
        }
    }
}