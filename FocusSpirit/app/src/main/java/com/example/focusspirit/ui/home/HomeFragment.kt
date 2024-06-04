package com.example.focusspirit.ui.home

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.focusspirit.R
import android.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.focusspirit.data.SampleData
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import android.content.pm.PackageManager
import android.widget.Toast
import com.example.focusspirit.models.Weather.WeatherResponse
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.location.Geocoder
import android.location.Location
import java.util.Locale
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.example.focusspirit.databinding.FragmentHomeBinding
import com.example.focusspirit.models.FocusSession
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var isPlaying = false
    private val LOCATION_PERMISSION_CODE = 107
    private val FILE_NAME = "focus_sessions.dat"
    private var focusSessions = mutableListOf<FocusSession>()
    private val currentUser = "User"
    private val apikey= "" // Please set the api key for testing, removed for security issues
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var focusSessionManager: FocusSessionManager
    private val btnStartText = "Start Focus"
    private val btnFocusingText = "Focusing..."
    private val btnPausedText = "Paused"
    private val musicOff = R.drawable.mute
    private val musicOn = R.drawable.music
    private var level = 0
    private val idle = 0
    private val end = 1
    private val levelPre = 2
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var musicButton: ImageView
    private val validFocusDuration =  5000//600000 //Set 510 mins for a valid focus session


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Get the HomeViewModel instance from the ViewModelProvider
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

// Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
// Get the root view of the fragment
        val root: View = binding.root

// Get the TextView that displays the level
        val textView: TextView = binding.textLevel
// Observe changes in the level LiveData in the ViewModel and update the TextView text accordingly
        homeViewModel.level.observe(viewLifecycleOwner) {
            textView.text = it
        }

// Get the last known location on the device
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLastKnownLocation()

// Get the music button from the binding
        musicButton = binding.btnMusic
// Set the initial state of the music button
        switchMusic(musicButton)

// Initialize the media player with a music resource
        mediaPlayer = MediaPlayer.create(context, R.raw.honor)
// Enable looping for the media player
        mediaPlayer.isLooping = true

// Set an OnClickListener for the music button to start or stop the music when clicked
        musicButton.setOnClickListener { v ->
            if (isPlaying) {
                stopMusic(musicButton)
            } else {
                startMusic(musicButton)
            }
        }

// Initialize the FocusSessionManager with callbacks for when the session is paused and resumed
        focusSessionManager = FocusSessionManager(
            onSessionPause = { requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) },
            onSessionResume = { requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
        )
// Load the idle animation
        loadAnimation(idle)

// Add the FocusSessionManager as an observer of the lifecycle
        lifecycle.addObserver(focusSessionManager)


        // Set an OnClickListener for the focus button
        binding.btnFocus.setOnClickListener {view ->
            // If the focus session has not started yet
            if (focusSessionManager.focusStartTime == 0L) {
                // Start the focus session
                focusSessionManager.startFocusSession()
                // Update the button text to indicate that the session is in progress
                binding.btnFocus.text = btnFocusingText
                // Load the animation corresponding to the current level
                loadAnimation(levelPre+level)
                // Keep the screen on during the focus session
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                // Show a Snackbar message indicating that the focus session has started
                Snackbar.make(view, "Focus session started", Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.btnFocus).show()
            } else if (focusSessionManager.isPaused) {
                // If the focus session is currently paused, resume it
                focusSessionManager.resumeFocusSession()
                // Update the button text to indicate that the session is in progress
                binding.btnFocus.text = btnFocusingText
                // Load the animation corresponding to the current level
                loadAnimation(levelPre+level)
                // Show a Snackbar message indicating that the focus session has resumed
                Snackbar.make(view, "Focus session resumed", Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.btnFocus).show()
            } else {
                // If the focus session is currently in progress, pause it
                focusSessionManager.pauseFocusSession()
                // Update the button text to indicate that the session is paused
                binding.btnFocus.text = btnPausedText
                // Load the idle animation
                loadAnimation(idle);
                // Allow the screen to turn off during the pause
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                // Show a Snackbar message indicating that the focus session has paused
                Snackbar.make(view, "Focus session paused", Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.btnFocus).show()
            }
        }

// Set an OnLongClickListener for the focus button to end the focus session
        binding.btnFocus.setOnLongClickListener { view ->
            // If a focus session is currently in progress
            if (focusSessionManager.focusStartTime != 0L) {
                // End the focus session and get the duration
                val focusDuration = focusSessionManager.stopFocusSession()
                // Update the button text to indicate that no session is in progress
                binding.btnFocus.text = btnStartText
                // If the focus session was shorter than the valid duration
                if(focusDuration < validFocusDuration){
                    // Load the failure animation
                    loadAnimation(end)
                    // Show a Snackbar message indicating that the session was too short to learn a skill
                    Snackbar.make(view, "Sorry, No skill has been learned.\nFocus duration: ${reformatDuration(focusDuration)} seconds.", Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.btnFocus).show()
                }else{
                    // If the focus session was long enough to learn a skill, increment the level
                    level++
                    // Update the level text
                    homeViewModel.updateLevel(level)
                    // Load the animation corresponding to the new level
                    loadAnimation(levelPre+level)
                    // Prompt the user to title and save the session
                    promptForTitleAndSaveSession(focusDuration)
                }
                // Allow the screen to turn off now that the session has ended
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                // Return true to indicate that the long click was handled
                true
            } else {
                // If no focus session is in progress, return false
                false
            }
        }

// Load the saved focus sessions
        loadFocusSessions()
// Return the root view
        return root
    }


    // This function takes the focus duration in milliseconds and returns a formatted string
// representing the duration in hours, minutes, and seconds.
    fun reformatDuration(focusDuration: Long): String {
        // Calculate the number of seconds, minutes, and hours from the focus duration
        val seconds = (focusDuration / 1000) % 60
        val minutes = (focusDuration / (1000 * 60)) % 60
        val hours = focusDuration / (1000 * 60 * 60)

        // Build and return a string representing the focus duration
        return buildString {
            if (hours > 0) {
                append("$hours hours")
            }
            if (minutes > 0) {
                if (hours > 0) append(", ")
                append("$minutes minutes")
            }
            if (seconds > 0 || (hours == 0L && minutes == 0L)) {
                if (hours > 0 || minutes > 0) append(", ")
                append("$seconds seconds")
            }
        }
    }

    // This function prompts the user to title and save the focus session.
    private fun promptForTitleAndSaveSession(focusDuration: Long) {
        // Create an AlertDialog builder
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Save Focus Session")
        // Inflate the custom layout/view
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.record_session, null)
        builder.setView(dialogView)

        // Find the EditText in the inflated layout
        val input = dialogView.findViewById<EditText>(R.id.inputTitle)

        // Set the positive button to save the focus session
        builder.setPositiveButton("OK") { dialog, which ->
            val title = input.text.toString()
            val userName = currentUser
            val session = FocusSession(userName, title, focusDuration)
            focusSessions.add(session)
            saveFocusSessions()

            Snackbar.make(binding.btnFocus, "Congrats! New skill has been learned!\nFocused duration: ${reformatDuration(focusDuration)} seconds.", Snackbar.LENGTH_LONG)
                .setAnchorView(binding.btnFocus).show()
        }
        // Set the negative button to cancel the dialog
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        // Show the dialog
        builder.show()
    }

    // This function saves the focus sessions to a file.
    private fun saveFocusSessions() {
        val file = File(requireContext().filesDir, FILE_NAME)
        ObjectOutputStream(file.outputStream()).use { it.writeObject(focusSessions) }
    }

    // This function loads the focus sessions from a file.
    private fun loadFocusSessions() {
        val file = File(requireContext().filesDir, FILE_NAME)
        if (file.exists()) {
            ObjectInputStream(file.inputStream()).use {
                @Suppress("UNCHECKED_CAST")
                focusSessions = it.readObject() as MutableList<FocusSession>
            }
        }else{
            // Load sample data
            focusSessions = SampleData.focusSessions.toMutableList()
        }
    }

    // This function loads an animation based on the given index.
    private fun loadAnimation(animaIndex: Int) {
        Glide.with(this)
            .asGif()
            .load(animationList[animaIndex])
            .into(binding.imageViewAnimation)
    }


    // This method is called when the fragment is visible to the user and actively running.
// It checks if a focus session is in progress and paused, and if so, it updates the button text and loads the idle animation.
    override fun onResume() {
        super.onResume()
        if (focusSessionManager.focusStartTime != 0L && focusSessionManager.isPaused) {
            binding.btnFocus.text = btnPausedText
            loadAnimation(0)
        }
    }

    // This method is called when the fragment is no longer resumed.
// It stops the music when the fragment is paused.
    override fun onPause() {
        super.onPause()
        stopMusic(musicButton)
    }

    // This method stops the music and updates the music button image.
    private fun stopMusic(musicButton: ImageView){
        isPlaying = false
        mediaPlayer.pause()
        switchMusic(musicButton)
    }

    // This method starts the music and updates the music button image.
    private fun startMusic(musicButton: ImageView){
        isPlaying = true
        mediaPlayer.start()
        switchMusic(musicButton)
    }

    // This method is called when the view hierarchy associated with the fragment is being removed.
// It stops the music, releases the media player resources, and nullifies the binding reference.
    override fun onDestroyView() {
        stopMusic(musicButton)
        mediaPlayer.release()
        super.onDestroyView()
        _binding = null
    }

    // This method switches the music button image based on whether the music is playing or not.
    private fun switchMusic(musicButton: ImageView){
        if(isPlaying){
            musicButton.setImageResource(musicOn)
        }else{
            musicButton.setImageResource(musicOff)
        }
    }

    private fun getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_CODE)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    val cityName = addresses?.get(0)?.locality
                    val weatherDataUrl = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&units=metric&appid=$apikey"
                    fetchWeatherData(weatherDataUrl)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastKnownLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeatherData(urlString: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val content = reader.readLine()
                    reader.close()

                    val jsonElement = JsonParser.parseString(content)
                    val weatherResponse: WeatherResponse = Gson().fromJson(jsonElement, WeatherResponse::class.java)

                    withContext(Dispatchers.Main) {
                        binding.city.text = weatherResponse.name

                        val weatherIcon = weatherResponse.weather[0].icon
                        val weatherIconId = resources.getIdentifier(
                            "weather_$weatherIcon",
                            "drawable",
                            requireContext().packageName
                        )
                        binding.weather.setImageResource(weatherIconId)

                        val currentTemp = weatherResponse.main.temp.toString()
                        binding.currentTemp.text = "$currentTemp\u2103"

                        binding.weatherConditions.text = weatherResponse.weather[0].main
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "City not found!", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Please connect to internet!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private val animationList = listOf(
        R.drawable.leaf_ranger_idle,
        R.drawable.leaf_ranger_death,
        R.drawable.leaf_ranger_skill_0,
        R.drawable.leaf_ranger_skill_1,
        R.drawable.leaf_ranger_skill_2,
        R.drawable.leaf_ranger_skill_3,
        R.drawable.leaf_ranger_skill_4,
        R.drawable.leaf_ranger_skill_5,
        R.drawable.leaf_ranger_skill_6,
        R.drawable.leaf_ranger_skill_7,
        R.drawable.leaf_ranger_skill_8,
        R.drawable.leaf_ranger_skill_9,
        R.drawable.leaf_ranger_skill_10,
        R.drawable.leaf_ranger_skill_11,
        R.drawable.leaf_ranger_skill_12,
        R.drawable.leaf_ranger_skill_13
    )

}