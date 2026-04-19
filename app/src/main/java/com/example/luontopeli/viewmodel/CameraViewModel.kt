package com.example.luontopeli.viewmodel

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luontopeli.data.local.entity.NatureSpot
import com.example.luontopeli.data.repository.NatureSpotRepository
import com.example.luontopeli.location.LocationManager
import com.example.luontopeli.ml.PlantClassifier
import com.google.mlkit.vision.label.ImageLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: NatureSpotRepository,
    private val locationManager: LocationManager
) : ViewModel() {

    private val classifier = PlantClassifier()

    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath = _imagePath.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 👉 ML KIT tulos
    private val _labels = MutableStateFlow<List<ImageLabel>>(emptyList())
    val labels = _labels.asStateFlow()

    fun takePhoto(context: Context, imageCapture: ImageCapture) {

        _isLoading.value = true

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())

        val dir = File(context.filesDir, "photos").apply {
            mkdirs()
        }

        val file = File(dir, "IMG_$timestamp.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    val path = file.absolutePath
                    _imagePath.value = path

                    val location = locationManager.currentLocation.value
                    val lat = location?.latitude ?: 0.0
                    val lon = location?.longitude ?: 0.0

                    viewModelScope.launch {
                        try {
                            // 👉 MUUTOS TÄSSÄ (ei result.allLabels koska sitä ei ole)
                            val result: List<ImageLabel> =
                                classifier.classify(
                                    android.net.Uri.fromFile(file),
                                    context
                                )

                            _labels.value = result

                        } catch (e: Exception) {
                            _labels.value = emptyList()
                        }

                        saveToRoom(path, lat, lon)
                        _isLoading.value = false
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    _isLoading.value = false
                }
            }
        )
    }

    private fun saveToRoom(
        path: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {

            val bestLabel = _labels.value.firstOrNull()?.text ?: "Luontokohde"

            val spot = NatureSpot(
                name = bestLabel,
                latitude = latitude,
                longitude = longitude,
                imagePath = path
            )

            repository.insertSpot(spot)
        }
    }

    fun clear() {
        _imagePath.value = null
        _labels.value = emptyList()
    }
}