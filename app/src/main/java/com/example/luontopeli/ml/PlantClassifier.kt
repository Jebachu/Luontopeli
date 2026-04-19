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

    private val natureKeywords = setOf(
        "plant", "flower", "tree", "leaf", "moss", "grass",
        "fungus", "mushroom", "forest", "nature"
    )

    // ✅ OPETTAJAN MALLI: palautetaan suoraan labelit
    suspend fun classify(imageUri: Uri, context: Context): List<ImageLabel> {
        return suspendCancellableCoroutine { cont ->
            try {
                val image = InputImage.fromFilePath(context, imageUri)

                labeler.process(image)
                    .addOnSuccessListener { labels ->

                        // suodatetaan luontoon liittyvät (ei pakollinen, mutta ok)
                        val filtered = labels.filter { label ->
                            natureKeywords.any { keyword ->
                                label.text.contains(keyword, ignoreCase = true)
                            }
                        }

                        // jos löytyy luontotuloksia → ne, muuten kaikki labelit
                        val result = if (filtered.isNotEmpty()) filtered else labels

                        cont.resume(result)
                    }
                    .addOnFailureListener {
                        cont.resumeWithException(it)
                    }

            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }
    }

    fun close() {
        labeler.close()
    }
}