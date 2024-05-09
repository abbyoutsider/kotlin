package com.example.swapsense.ui.Camera

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.swapsense.R

class CameraFragment  : Fragment() {

    // Lateinit is used for variables that will be initialized later.
    private lateinit var imageView: ImageView
    private lateinit var deleteButton: Button
    private lateinit var openCameraButton: Button
    private lateinit var openGalleryButton: Button
    private lateinit var saveImageButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        // Initialize the ImageView and Buttons from the layout
        imageView = view.findViewById(R.id.selected_image_view)
        deleteButton = view.findViewById(R.id.button_delete_image)
        openCameraButton = view.findViewById(R.id.button_add_image_with_camera)
        openGalleryButton = view.findViewById(R.id.button_add_image_from_gallery)
        saveImageButton = view.findViewById(R.id.button_save_image)

        // Set an onClickListener for the "Add Image With Camera" button to handle camera permission and opening the camera
        openCameraButton.setOnClickListener {
            if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } != PackageManager.PERMISSION_GRANTED) {
                // Request camera permission if not granted
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            } else {
                // Open the camera if permission is granted
                openCamera()
            }
        }

        // Set an onClickListener for the "Add Image From Gallery" button to select images from gallery
        openGalleryButton.setOnClickListener {
            if (context?.let{ ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) }!= PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), GALLERY_REQUEST_CODE)
            } else {
                openGallery()
            }
        }

        // Set an onClickListener for the "Save To Gallery" button to handle gallery permission and saving the photo
        saveImageButton.setOnClickListener {
            if (context?.let{ ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE)} != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), GALLERY_REQUEST_CODE)
            } else {
                saveImageToGallery()
            }
        }

        // Set an onClickListener for the "Delete Image" button to clear and hide the image and button
        deleteButton.setOnClickListener {
            imageView.setImageBitmap(null) // Clears the image from ImageView
            imageView.visibility = ImageView.GONE // Hides the ImageView
            deleteButton.visibility = Button.GONE // Hides the Delete Button
            saveImageButton.visibility = Button.GONE // Hides the Save Button
        }

        return view
    }

    // Function to open the camera using an implicit intent
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, IMAGE_CAPTURE_CODE)
        }
    }

    // Function to open the gallery using an implicit intent
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun saveImageToGallery() {
        imageView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(imageView.drawingCache)
        imageView.isDrawingCacheEnabled = false

        val savedImageURL = MediaStore.Images.Media.insertImage(
            requireActivity().contentResolver,
            bitmap,
            "Image Title",
            "Image Description"
        )

        Toast.makeText(requireActivity(), "Image saved to gallery.\n$savedImageURL", Toast.LENGTH_LONG).show()
    }

    // Callback for the result from requesting permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission was granted, open the camera
            openCamera()
        } else if (requestCode == GALLERY_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission was granted, save to the gallery
            openCamera()
        }else {
            // Permission was denied, handle the case
        }
    }

    // Callback for the result from capturing an image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                IMAGE_CAPTURE_CODE -> {
                    // Process and display the captured image
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(imageBitmap)
                    updateImageViewVisibility()
                }
                IMAGE_PICK_CODE -> {
                    imageView.setImageURI(data?.data)
                    updateImageViewVisibility()
                }
            }
        }
    }

    private fun updateImageViewVisibility() {
        imageView.visibility = View.VISIBLE
        deleteButton.visibility = View.VISIBLE
        saveImageButton.visibility = View.VISIBLE
    }

    companion object {
        // Request codes for camera and permissions
        private const val IMAGE_CAPTURE_CODE = 1002
        private const val CAMERA_REQUEST_CODE = 1003
        private const val IMAGE_PICK_CODE = 1004
        private const val GALLERY_REQUEST_CODE = 1005
    }
}