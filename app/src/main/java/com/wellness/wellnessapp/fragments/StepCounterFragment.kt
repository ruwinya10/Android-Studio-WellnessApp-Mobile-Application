package com.wellness.wellnessapp.fragments

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.models.MoodEntry
import com.wellness.wellnessapp.utils.SharedPrefManager

class StepCounterFragment : Fragment(), SensorEventListener {
    private var stepCount = 0
    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private lateinit var textStepCount: TextView
    private lateinit var textShakeInfo: TextView
    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_step_counter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textStepCount = view.findViewById(R.id.text_step_count)
        textShakeInfo = view.findViewById(R.id.text_shake_info)
        sharedPrefManager = SharedPrefManager(requireContext())

        sensorManager = requireContext().getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        setupStepCounter()
        setupShakeDetection()
    }

    private fun setupStepCounter() {
        if (stepSensor == null) {
            textStepCount.text = "Step counter not available on this device"
            return
        }

        textStepCount.text = "Steps: $stepCount"
    }

    private fun setupShakeDetection() {
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let { accelerometer ->
            sensorManager?.registerListener(
                accelerometerListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        } ?: run {
            textShakeInfo.text = "Shake detection not available"
        }
    }

    private val accelerometerListener = object : SensorEventListener {
        private val shakeThreshold = 15f
        private var lastUpdate = 0L
        private var lastShake = 0L
        private var lastX = 0f
        private var lastY = 0f
        private var lastZ = 0f

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val currentTime = System.currentTimeMillis()
                if ((currentTime - lastUpdate) > 100) {
                    val timeDiff = currentTime - lastUpdate
                    lastUpdate = currentTime

                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / timeDiff * 10000

                    if (speed > shakeThreshold && (currentTime - lastShake) > 1000) {
                        lastShake = currentTime
                        onShakeDetected()
                    }

                    lastX = x
                    lastY = y
                    lastZ = z
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun onShakeDetected() {
        val quickMood = MoodEntry(
            moodEmoji = "💪",
            moodText = "Energized",
            notes = "Quick entry from shake"
        )

        val currentEntries = sharedPrefManager.getMoodEntries().toMutableList()
        currentEntries.add(0, quickMood)
        sharedPrefManager.saveMoodEntries(currentEntries)

        Toast.makeText(requireContext(), "Quick mood added: Energized! 💪", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager?.registerListener(accelerometerListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
        sensorManager?.unregisterListener(accelerometerListener)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.firstOrNull()?.toInt()?.let { steps ->
            stepCount = steps
            textStepCount.text = "Steps: $stepCount"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}