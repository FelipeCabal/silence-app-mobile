package com.example.silenceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class UploadImageResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: ImageData
)

data class ImageData(
    @SerializedName("url")
    val url: String,
    
    @SerializedName("path")
    val path: String?,
    
    @SerializedName("folder")
    val folder: String?
)
