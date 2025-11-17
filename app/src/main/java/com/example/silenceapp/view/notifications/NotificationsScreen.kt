package com.example.silenceapp.view.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.silenceapp.ui.components.NotificationItem
import com.example.silenceapp.view.notifications.viewmodel.NotificationViewModel

@Composable
fun NotificationsScreen(viewModel: NotificationViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.getDataTest()
    }

    val notifications by viewModel.notifications.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onNotificationClicked = { viewModel.markAsSeen(notification.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    com.example.silenceapp.ui.theme.SilenceAppTheme {
        NotificationsScreen()
    }
}