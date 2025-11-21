package com.example.silenceapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.ui.components.imagecapture.ImageCaptureEvent
import com.example.silenceapp.ui.components.imagecapture.ImageCaptureState
import com.example.silenceapp.ui.components.imagecapture.PermissionType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de captura de imágenes
 * Maneja la lógica de estados, permisos y navegación entre cámara/galería
 */
class ImageCaptureViewModel : ViewModel() {

    private val _state = MutableStateFlow<ImageCaptureState>(ImageCaptureState.Idle)
    val state: StateFlow<ImageCaptureState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onEvent(event: ImageCaptureEvent) {
        when (event) {
            is ImageCaptureEvent.CameraButtonClicked -> {
                _state.value = ImageCaptureState.ShowingOptions
            }
            is ImageCaptureEvent.CameraOptionSelected -> {
                _state.value = ImageCaptureState.RequestingCameraPermission
            }
            is ImageCaptureEvent.GalleryOptionSelected -> {
                _state.value = ImageCaptureState.RequestingGalleryPermission
            }
            is ImageCaptureEvent.ImageSelected -> {
                _state.value = ImageCaptureState.ImageCaptured(event.uri)
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.ImageCaptured(event.uri))
                }
            }
            is ImageCaptureEvent.PermissionResult -> {
                handlePermissionResult(event.granted, event.type)
            }
            is ImageCaptureEvent.DismissDialog -> {
                _state.value = ImageCaptureState.Idle
            }
        }
    }

    private fun handlePermissionResult(granted: Boolean, type: PermissionType) {
        if (granted) {
            viewModelScope.launch {
                when (type) {
                    PermissionType.CAMERA -> {
                        _navigationEvent.emit(NavigationEvent.OpenCamera)
                    }
                    PermissionType.GALLERY -> {
                        _navigationEvent.emit(NavigationEvent.OpenGallery)
                    }
                }
            }
            _state.value = ImageCaptureState.Idle
        } else {
            _state.value = ImageCaptureState.Error("Permiso denegado")
        }
    }

    sealed class NavigationEvent {
        object OpenCamera : NavigationEvent()
        object OpenGallery : NavigationEvent()
        data class ImageCaptured(val uri: Uri) : NavigationEvent()
    }
}