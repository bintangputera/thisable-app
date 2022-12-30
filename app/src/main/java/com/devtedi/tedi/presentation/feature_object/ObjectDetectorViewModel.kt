package com.devtedi.tedi.presentation.feature_object

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtedi.tedi.factory.ObjectDetectorStore
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

/**
 *
 * Kelas ini digunakan sebagai ViewModel dari ObjectDetectionFragment
 *
 */
class ObjectDetectionViewModel : ViewModel() {

    // Instance dari yoloV5 Model
    private val _yolov5TFLiteDetector: MutableLiveData<YOLOv5ModelCreator> = MutableLiveData()
    val yolov5TFLiteDetector: LiveData<YOLOv5ModelCreator> = _yolov5TFLiteDetector

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSoundOn: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSoundOn: LiveData<Boolean> = _isSoundOn

    // Untuk mengaktifkan/menonaktifkan suara
    fun toggleSoundOnOff() {
        _isSoundOn.value = isSoundOn.value?.let { !it }
    }

    // Jika sudah tidak digunakan/lifecycle ViewModel berakhir, hapus instance dari model
    fun closeModel() {
        yolov5TFLiteDetector.value?.close()
    }

    // Inisialisasi model yoloV5
    fun initModel(modelName: String, filePath: File, context: Context) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val model = ObjectDetectorStore(context).createModel(
                    modelName, filePath).apply {
                    addGpuDelegate()
                    initialModel(context)
                }
                _yolov5TFLiteDetector.postValue(model)
                _isLoading.postValue(false)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}