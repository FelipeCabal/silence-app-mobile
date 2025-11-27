package com.example.silenceapp.ui.components.imagecapture

import android.net.Uri

sealed class ImageCaptureState {
    object Idle : ImageCaptureState()
    object ShowingOptions : ImageCaptureState()
    object RequestingCameraPermission : ImageCaptureState()
    object RequestingGalleryPermission : ImageCaptureState()
    data class ImageCaptured(val uri: Uri) : ImageCaptureState()
    data class Error(val message: String) : ImageCaptureState()
}