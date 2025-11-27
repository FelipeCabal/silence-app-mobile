package com.example.silenceapp.ui.components.imagecapture

import android.net.Uri

sealed class ImageCaptureEvent {
    object CameraButtonClicked : ImageCaptureEvent()
    object CameraOptionSelected : ImageCaptureEvent()
    object GalleryOptionSelected : ImageCaptureEvent()
    data class ImageSelected(val uri: Uri) : ImageCaptureEvent()
    data class PermissionResult(val granted: Boolean, val type: PermissionType) : ImageCaptureEvent()
    object DismissDialog : ImageCaptureEvent()
}