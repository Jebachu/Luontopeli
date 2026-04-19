package com.example.luontopeli.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext


class StepCounterManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val gyroSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private var stepListener: SensorEventListener? = null
    private var gyroListener: SensorEventListener? = null

    fun startStepCounting(onStep: () -> Unit) {

        stepListener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    onStep()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        stepSensor?.let {
            sensorManager.registerListener(
                stepListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stopStepCounting() {
        stepListener?.let {
            sensorManager.unregisterListener(it)
        }
        stepListener = null
    }

    fun startGyroscope(onRotation: (x: Float, y: Float, z: Float) -> Unit) {

        gyroListener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
                    onRotation(
                        event.values[0],
                        event.values[1],
                        event.values[2]
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        gyroSensor?.let {
            sensorManager.registerListener(
                gyroListener,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stopGyroscope() {
        gyroListener?.let {
            sensorManager.unregisterListener(it)
        }
        gyroListener = null
    }

    fun stopAll() {
        stopStepCounting()
        stopGyroscope()
    }

    fun isStepSensorAvailable(): Boolean {
        return stepSensor != null
    }

    companion object {
        const val STEP_LENGTH_METERS = 0.74f
    }
}