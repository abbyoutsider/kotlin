package com.example.focusspirit.ui.gallery

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.focusspirit.databinding.FragmentGalleryBinding


class GalleryFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private val CAMERA_REQUEST_CODE = 100
    private val CAMERA_PERMISSION_CODE = 101
    private var buttonSaveImage: Button? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        binding.buttonOpenCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            } else {
                openCamera()
            }
        }

        buttonSaveImage = binding.buttonSaveImage


        buttonSaveImage!!.setOnClickListener { saveImageToGallery() }
        return root
    }

    override fun onResume() {
        super.onResume()
        stepCounterSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val steps = event.values[0]
            binding.stepCount.text = "Today's Steps: $steps"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle changes in sensor accuracy if needed
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(imageBitmap)
        }
    }

    private fun saveImageToGallery() {
        val imageView: ImageView = binding.imageView
        imageView.buildDrawingCache() // Important to build the cache before getting the bitmap
        val image = imageView.drawingCache
        if (image != null) {
            // Save the image
            MediaStore.Images.Media.insertImage(
                requireContext().contentResolver,
                image,
                "MyImageName",
                "Gallery Image"
            )

            // Free up memory
            imageView.destroyDrawingCache()
            Toast.makeText(requireContext(), "Image saved to Gallery", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Error saving image", Toast.LENGTH_SHORT).show()
        }
    }

}