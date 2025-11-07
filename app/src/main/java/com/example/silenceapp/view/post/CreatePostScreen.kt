package com.example.silenceapp.view.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.silenceapp.R
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.primaryColor


@Composable
fun CreatePostScreen(navController: NavController){
    var message by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) } // 👈 estado del foco

    Column (
        modifier = Modifier.fillMaxSize().padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ){
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
                color = onBackgroundColor
            )
        }
        Divider(color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(top = 8.dp))
        Row (
            modifier = Modifier.padding(top = 10.dp),
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
                color = onBackgroundColor
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
                textStyle = TextStyle(color = onBackgroundColor),
                cursorBrush = SolidColor(onBackgroundColor)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
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
                modifier = Modifier.padding(2.dp)
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