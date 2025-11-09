package com.example.silenceapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.ui.theme.DarkGray
import com.example.silenceapp.ui.theme.MintGreen

@Composable
fun CommentCard(comment: Comment){
    Row(
        modifier = Modifier
            .padding(20.dp, 14.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ){
        if(!comment.usuario.name.isNullOrEmpty()){
            Image(
                painter = rememberAsyncImagePainter(comment.usuario.profileImage),
                contentDescription = "Foto de perfil de ${comment.usuario.name}",
                modifier = Modifier
                    .height(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = comment.usuario.name.first().uppercase(),
                color = Color.Black,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .background(
                    color = MintGreen,
                    shape = RoundedCornerShape(14.dp),

                )
                .padding(12.dp),
        ){
            Text(
                text = comment.usuario.name,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkGray
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = comment.comentario,
                color = DarkGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
