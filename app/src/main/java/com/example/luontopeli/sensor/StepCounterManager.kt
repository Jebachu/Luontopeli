package com.example.luontopeli.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StepCounterManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var stepListener: SensorEventListener? = null

    private var lastSteps: Float = -1f

    fun startStepCounting(onStep: () -> Unit) {

        if (stepSensor == null) {
            Log.e("StepCounter", "Step sensor NOT available on this device")
            return
        }

        lastSteps = -1f

        stepListener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent) {

                if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {

                    val totalSteps = event.values[0]

                    if (lastSteps < 0f) {
                        lastSteps = totalSteps
                        return
                    }

                    val stepDiff = totalSteps - lastSteps

                    if (stepDiff > 0) {
                        repeat(stepDiff.toInt()) {
                            onStep()
                        }
                    }

                    lastSteps = totalSteps
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(
            stepListener,
            stepSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    fun stopStepCounting() {
        stepListener?.let {
            sensorManager.unregisterListener(it)
        }
        stepListener = null
        lastSteps = -1f
    }

    fun stopAll() {
        stopStepCounting()
    }

    fun isStepSensorAvailable(): Boolean {
        return stepSensor != null
    }

    companion object {
        const val STEP_LENGTH_METERS = 0.74f
    }
}
