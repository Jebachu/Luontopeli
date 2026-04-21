package com.example.luontopeli.viewmodel

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luontopeli.data.local.entity.NatureSpot
import com.example.luontopeli.data.repository.NatureSpotRepository
import com.example.luontopeli.location.LocationManager
import com.example.luontopeli.ml.PlantClassifier
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
    private val locationManager: LocationManager,
    private val classifier: PlantClassifier
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _navigateBack = MutableStateFlow(false)
    val navigateBack = _navigateBack.asStateFlow()

    fun takePhoto(context: Context, imageCapture: ImageCapture) {
        _isLoading.value = true

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())

        val dir = File(context.filesDir, "photos").apply { mkdirs() }
        val file = File(dir, "IMG_$timestamp.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    viewModelScope.launch {
                        val uri = Uri.fromFile(file)
                        val labels = classifier.classify(uri)
                        val bestLabel = labels.maxByOrNull { it.confidence }?.text ?: "Unknown"

                        val location = locationManager.currentLocation.value
                        val lat = location?.latitude ?: 0.0
                        val lon = location?.longitude ?: 0.0

                        val spot = NatureSpot(
                            name = bestLabel,
                            latitude = lat,
                            longitude = lon,
                            imagePath = file.absolutePath
                        )

                        repository.insertSpot(spot)

                        _isLoading.value = false
                        _navigateBack.value = true
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    _isLoading.value = false
                }
            }
        )
    }

    fun resetNavigation() {
        _navigateBack.value = false
    }
}
