package com.example.silenceapp.view.posts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.silenceapp.R
import com.example.silenceapp.ui.components.imagecapture.rememberImagePickerActions
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.primaryColor
import com.example.silenceapp.viewmodel.PostViewModel
import com.example.silenceapp.viewmodel.AuthViewModel
import com.example.silenceapp.data.remote.response.ProfileResponse
import androidx.compose.ui.geometry.Size
import com.example.silenceapp.ui.theme.DimGray
import com.example.silenceapp.ui.theme.lightGray
import com.example.silenceapp.ui.theme.secondaryColor


@Composable
fun CreatePostScreen(
    navController: NavController,
    imageUri: String?,
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel = viewModel()
){
    var message by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false)}
    var isPublishing by remember { mutableStateOf(false) }
    var esAnonimo by remember { mutableStateOf(false) }
    // Mantener múltiples imágenes seleccionadas localmente
    val pickedImages = remember { mutableStateListOf<String>() }
    
    // Obtener perfil del usuario
    var userProfile by remember { mutableStateOf<ProfileResponse?>(null) }
    
    LaunchedEffect(Unit) {
        authViewModel.getProfile { profile ->
            userProfile = profile
        }
    }

    // Inicializar con la imagen recibida (si existe) sólo una vez
    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            if (!pickedImages.contains(uri)) pickedImages.add(uri)
        }
    }

    val scrollState = rememberScrollState()
    
    // Obtener acciones de cámara y galeria
    val imagePickerActions = rememberImagePickerActions(
        onImagePicked = { uri ->
            pickedImages.add(uri.toString())
        }
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ){
        Spacer(Modifier.height(6.dp))
        Row (
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "back",
                tint = primaryColor
            )
            Text(
                "Crear Publicación",
                color = onBackgroundColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Divider(color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 12.dp))
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo de la app",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    userProfile?.nombre ?: "Usuario",
                    color = onBackgroundColor,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Row(
                modifier = Modifier
                    .background(DimGray, shape = RoundedCornerShape(18.dp))
                    .clickable { esAnonimo = !esAnonimo }
                    .padding(horizontal = 8.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Anonimo?"
                )
                Icon(
                    imageVector = if (esAnonimo) Icons.Filled.ToggleOn else Icons.Filled.ToggleOff,
                    contentDescription = "PublicacionAnonima",
                    tint = if (esAnonimo) primaryColor else lightGray,
                    modifier = Modifier
                        .size(35.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.TopStart
        ) {
            // Placeholder visible solo si no hay texto y no está enfocado
            if (message.isEmpty() && !isFocused) {
                Text(
                    text = "¿Qué estás pensando?",
                )
            }
            BasicTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState -> isFocused = focusState.isFocused },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = onBackgroundColor),
                cursorBrush = SolidColor(onBackgroundColor)
            )
        }

        if (pickedImages.isNotEmpty()) {
            // Previews a ancho completo
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                pickedImages.forEachIndexed { index, uri ->
                    ImagePreviewItem(
                        uri = uri,
                        index = index,
                        onRemove = { pickedImages.removeAt(index) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            IconButton(
                onClick = { imagePickerActions.openGallery() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Galería",
                    tint = primaryColor
                )
            }

            IconButton(
                onClick = { imagePickerActions.openCamera() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Cámara",
                    tint = primaryColor,
                )
            }
        }

        Row (
            modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.End


        ){
            Button(
                onClick = {
                    if (message.isNotEmpty() || pickedImages.isNotEmpty()) {

                        val profile = userProfile
                        if (profile == null) {
                            // Si no hay usuario, volver al login
                            navController.navigate("login") {
                                popUpTo("add-post") { inclusive = true }
                            }
                            return@Button
                        }
                        
                        isPublishing = true
                        postViewModel.createPost(
                            userId = profile.id,
                            userName = profile.nombre,
                            description = message.ifEmpty { null },
                            imageUris = pickedImages.toList(),
                            esAnonimo = esAnonimo
                        ) { success ->
                            isPublishing = false
                            if (success) {
                                // Limpiar formulario y navegar al home
                                message = ""
                                pickedImages.clear()
                                navController.navigate("home") {
                                    // Limpiar el back stack para que no pueda volver a login/create-post
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                enabled = !isPublishing && (message.isNotEmpty() || pickedImages.isNotEmpty())
            ) {
                Text(if (isPublishing) "PUBLICANDO..." else "PUBLICAR")
            }
        }
    }
}
@Composable
private fun ImagePreviewItem(
    uri: String,
    index: Int,
    onRemove: () -> Unit
) {
    val painter = rememberAsyncImagePainter(uri)
    val state = painter.state
    var aspect by remember(uri) { mutableStateOf<Float?>(null) }

    LaunchedEffect(state) {
        val s = state
        if (s is AsyncImagePainter.State.Success) {
            val size = s.painter.intrinsicSize
            if (size != Size.Unspecified && size.width > 0f && size.height > 0f) {
                val a = size.width / size.height
                if (a.isFinite() && a > 0f) aspect = a
            }
        }
    }

    val imgModifier = Modifier
        .fillMaxWidth()
        .let { base -> aspect?.let { base.aspectRatio(it) } ?: base.height(220.dp) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painter,
            contentDescription = "Imagen $index",
            modifier = imgModifier,
            contentScale = ContentScale.Fit
        )
        // Botón eliminar
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Eliminar",
                tint = Color.White
            )
        }
    }
}