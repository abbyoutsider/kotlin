package com.example.swapsense.ui.Dashboard

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.swapsense.databinding.FragmentDashboardBinding
import kotlin.math.sqrt


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // TextViews to display sensor data
    private val binding get() = _binding!!

    private lateinit var magneticView: TextView
    private lateinit var accelerometerView: TextView
    private lateinit var proximityView: TextView
    private lateinit var gyroscopeView: TextView
    private lateinit var lightView: TextView
    private lateinit var stepCounterView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO
        // Initialize TextViews from the layout
        magneticView = binding.textViewMagnetic
        accelerometerView = binding.textViewAccelerometer
        proximityView = binding.textViewProximity
        gyroscopeView = binding.textViewGyroscope
        lightView = binding.textViewLight
        stepCounterView = binding.textViewStepCounter
        // My device does not support pressure sensor

        // TODO

        // Get the SensorManager instance
        val sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Get a list of all available sensors
        val allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

        // Log sensor information
        for (sensor in allSensors) {
            Log.d("DashboardFragment", "Sensor: " + sensor.name)
        }

        // TODO
        // Get sensor instances
        val magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // TODO
        // Check if Sensors available
        // Toast Message if Sensor not available
        if (magneticSensor == null) {
            Toast.makeText(requireActivity(), "Magnetic sensor not available", Toast.LENGTH_SHORT).show();
        }

        // TODO
        // Define SensorEventListeners
        val magneticListener: SensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Get sensor data is stored in the event.values array.
                val magneticFieldX = event.values[0]
                val magneticFieldY = event.values[1]
                val magneticFieldZ = event.values[2]

                val magnitude = sqrt(((magneticFieldX * magneticFieldX) +
                        (magneticFieldY * magneticFieldY) + (magneticFieldZ * magneticFieldZ)).toDouble())

                // Update magnetic field values on UI
                magneticView.text = String.format("Magnetic: %.2f", magnitude)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Handle sensor accuracy changes
                val accuracyString = when (accuracy) {
                    SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Low Accuracy"
                    SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Medium Accuracy"
                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "High Accuracy"
                    else -> "Unreliable"
                }
//                magneticView.text = String.format("Magnetic: %s, %s", magneticView.text, accuracyString)
            }
        }



        val accelerometerListener: SensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val accelerometerX = event.values[0]
                val accelerometerY = event.values[1]
                val accelerometerZ = event.values[2]
                val magnitude = sqrt(((accelerometerX * accelerometerX) +
                        (accelerometerY * accelerometerY) + (accelerometerZ * accelerometerZ)).toDouble())

                // Update accelerometer values on UI
                accelerometerView.text = String.format("Acceleration: %.2f", magnitude)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

        }

        val proximityListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val proximity = event.values[0]
                // Update proximity value on UI
                proximityView.text = String.format("Proximity: %.2f", proximity)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

        }


        val gyroscopeListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val gyroscopeX = event.values[0]
                val gyroscopeY = event.values[1]
                val gyroscopeZ = event.values[2]
                val magnitude = sqrt(((gyroscopeX * gyroscopeX) +
                        (gyroscopeY * gyroscopeY) + (gyroscopeZ * gyroscopeZ)).toDouble())
                // Update gyroscope values on UI
                gyroscopeView.text = String.format("Gyro Value: %.2f", magnitude)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Handle sensor accuracy changes
            }
        }

        val lightListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val light = event.values[0]
                // Update light value on UI
                lightView.text = String.format("Light: %.2f", light)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Handle sensor accuracy changes
            }
        }

        val stepCounterListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val steps = event.values[0]
                // Update step counter value on UI
                stepCounterView.text = String.format("Steps: %.0f", steps)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Handle sensor accuracy changes
            }
        }

        // TODO
        // Register listeners
        if (magneticSensor != null) {
            sensorManager.registerListener(magneticListener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (accelerometerSensor != null ){
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (proximitySensor != null) {
            sensorManager.registerListener(proximityListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (gyroscopeSensor !=null){
            sensorManager.registerListener(gyroscopeListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (lightSensor !=null){
            sensorManager.registerListener(lightListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (stepCounterSensor !=null){
            sensorManager.registerListener(stepCounterListener, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // TODO
    // checkPermission for the SENSORS
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.BODY_SENSORS), SENSOR_PERMISSION_REQUEST_CODE)
        }
    }

    // TODO
    // Callback for the result from requesting permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            SENSOR_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                } else {
                    // Permission denied
                }
                return
            }
            else -> {
                // Ignore all other requests
            }
        }
    }

    // TODO
    // Declare Request codes for permissions

    private val SENSOR_PERMISSION_REQUEST_CODE = 711

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
