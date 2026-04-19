package com.example.luontopeli.ml

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PlantClassifier {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()
    )

    suspend fun classify(uri: Uri, context: Context): List<ImageLabel> =
        suspendCancellableCoroutine { cont ->
            try {
                val image = InputImage.fromFilePath(context, uri)

                labeler.process(image)
                    .addOnSuccessListener { labels ->
                        cont.resume(labels)
                    }
                    .addOnFailureListener {
                        cont.resumeWithException(it)
                    }

            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }

    fun close() {
        labeler.close()
    }
}