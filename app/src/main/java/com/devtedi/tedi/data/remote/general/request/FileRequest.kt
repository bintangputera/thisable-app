package com.devtedi.tedi.data.remote.general.request

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class FileRequest(
    @SerializedName("photo")
    val photo: MultipartBody.Part,
    @SerializedName("video")
    val video: MultipartBody.Part,
    @SerializedName("files")
    val files: MultipartBody.Part
)
