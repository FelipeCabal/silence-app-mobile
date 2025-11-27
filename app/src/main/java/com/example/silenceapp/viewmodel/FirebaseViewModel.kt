package com.example.silenceapp.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.remote.response.UploadImageResponse
import com.example.silenceapp.data.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {
    
    private val store = AuthDataStore(application)
    private val api = ApiClient.firebaseService
    private val repository = FirebaseRepository(api, store, application)

    /**
     * Sube una imagen a Firebase Storage
     * @param imageUri URI de la imagen seleccionada
     * @param folder Carpeta donde guardar (opcional, default: "images")
     * @param onResult Callback con el resultado (UploadImageResponse si éxito, null si fallo)
     */
    fun uploadImage(
        imageUri: Uri,
        folder: String = "images",
        onResult: (UploadImageResponse?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("FirebaseVM", "Iniciando upload con folder: $folder")
                val response = withContext(Dispatchers.IO) {
                    repository.uploadImage(imageUri, folder)
                }
                Log.d("FirebaseVM", "Upload exitoso: ${response.data.url}")
                onResult(response)
            } catch (e: HttpException) {
                Log.e("FirebaseVM", "Error HTTP ${e.code()}: ${e.message()}")
                e.response()?.errorBody()?.string()?.let { errorBody ->
                    Log.e("FirebaseVM", "Error body: $errorBody")
                }
                e.printStackTrace()
                onResult(null)
            } catch (e: Exception) {
                Log.e("FirebaseVM", "Error: ${e.message}", e)
                e.printStackTrace()
                onResult(null)
            }
        }
    }
}
