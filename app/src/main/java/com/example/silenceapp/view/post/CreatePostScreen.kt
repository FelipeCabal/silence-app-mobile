package com.example.silenceapp.view.post

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.silenceapp.R
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.primaryColor


@Composable
fun CreatePostScreen(
    navController: NavController
                     ){
    var message by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false)}


    Column (
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ){
        Spacer(Modifier.height(6.dp))
        Row (
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)

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
            modifier = Modifier.padding(vertical = 15.dp),
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
                "Leopoldo",
                color = onBackgroundColor,
                style = MaterialTheme.typography.titleSmall
            )
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Imagen",
                tint = primaryColor

            )
            Icon(
                imageVector = Icons.Default.EmojiEmotions,
                contentDescription = "emoji",
                tint = primaryColor
            )
        }
        Row (
            modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.End


        ){
            Button(
                onClick = {},
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("PUBLICAR")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePostScreenPreview(){
    CreatePostScreen(navController = rememberNavController())
}