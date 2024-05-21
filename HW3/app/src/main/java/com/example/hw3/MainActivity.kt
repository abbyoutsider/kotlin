package com.example.hw3

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.weatherapp.model.Weather.WeatherResponse
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale
import android.Manifest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    // API key for OpenWeatherMap
    private val apikey= ""//Replace with your own apikey

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // This method is called when the activity is starting
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display
        setContentView(R.layout.activity_main) // Set the activity layout

        //Get the last location on device
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastKnownLocation()

        // Get the search button and search bar views
        val searchButton = findViewById<Button>(R.id.search_button)
        val searchBar = findViewById<EditText>(R.id.search_bar)

        // Set an onClick listener for the search button
        searchButton.setOnClickListener {
            // Get the city name from the search bar
            val cityName = searchBar.text.toString()
            if (cityName.isNotEmpty()) {
                // If the city name is not empty, fetch the weather data
                val weatherDataUrl="https://api.openweathermap.org/data/2.5/weather?q=$cityName&units=metric&appid=$apikey"
                fetchWeatherData(weatherDataUrl)
                dismissKeyboard() // Add this line to dismiss the keyboard
            }else{
                // If the city name is empty, show a toast message
                runOnUiThread {
                    Toast.makeText(this, "City is Empty!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //Fetch the weather data based on last location
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You need to ask the user to enable the permissions
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    val cityName = addresses?.get(0)?.locality
                    Log.d("Location","cityname: $cityName")
                    val weatherDataUrl="https://api.openweathermap.org/data/2.5/weather?q=$cityName&units=metric&appid=$apikey"
                    fetchWeatherData(weatherDataUrl)
                    dismissKeyboard()
                }
            }
        }
    }

    // This method fetches the weather data from OpenWeatherMap
    private fun fetchWeatherData(urlString: String) {
        Thread {
            try {
                // Create a URL and open a connection
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection

                // Check the response code
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    // If the response code is OK, read the response
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val content = reader.readLine()
                    reader.close()

                    // Log the response
                    Log.d("fetchWeatherData", "resJson:$content")

                    // Parse the response JSON and convert it to a WeatherResponse object
                    val jsonElement = JsonParser.parseString(content)
                    val weatherResponse: WeatherResponse = Gson().fromJson(jsonElement, WeatherResponse::class.java)

                    // Update the UI on the main thread
                    runOnUiThread {
                        // Update your views here
                        val cityView = findViewById<TextView>(R.id.city)
                        cityView.text = weatherResponse.name
                        val weatherIcon = weatherResponse.weather[0].icon
                        Log.d("fetchWeatherData", "weatherIcon: $weatherIcon")
                        val weatherIconId = resources.getIdentifier("weather_$weatherIcon", "drawable", packageName)
                        Log.d("fetchWeatherData", "weatherIconId: $weatherIconId")
                        val weatherImageView = findViewById<ImageView>(R.id.weather)
                        weatherImageView.setImageResource(weatherIconId)
                        val lastUpdateView = findViewById<TextView>(R.id.lastUpdateTime)
                        lastUpdateView.text = "Last Updated: "+convertUnixTimestampToExactDateAndTime(weatherResponse.dt)
                        val currentTempView = findViewById<TextView>(R.id.current_temp)
                        val currentTemp = weatherResponse.main.temp.toString()
                        currentTempView.text = "$currentTemp\u2103"
                        val weatherView = findViewById<TextView>(R.id.weather_conditions)
                        weatherView.text = weatherResponse.weather[0].main
                        val minTempView = findViewById<TextView>(R.id.min_temp)
                        minTempView.text = weatherResponse.main.temp_min.toString()+"℃"
                        val maxTempView = findViewById<TextView>(R.id.max_temp)
                        maxTempView.text = weatherResponse.main.temp_max.toString()+"℃"
                        val pressureView = findViewById<TextView>(R.id.pressure)
                        pressureView.text = weatherResponse.main.pressure.toString()
                        val humidityView = findViewById<TextView>(R.id.humidity)
                        humidityView.text = weatherResponse.main.humidity.toString()
                        val sunriseView = findViewById<TextView>(R.id.sunrise_time)
                        sunriseView.text =  convertUnixTimestampToAmPm(weatherResponse.sys.sunrise)
                        val sunsetView = findViewById<TextView>(R.id.sunset_time)
                        sunsetView.text =  convertUnixTimestampToAmPm(weatherResponse.sys.sunset)
                        val windView = findViewById<TextView>(R.id.wind_speed)
                        windView.text = weatherResponse.wind.speed.toString()
                    }
                } else {
                    // If the response code is not OK, log an error and show a toast message
                    Log.e("fetchWeatherData", "Connection failed with response code: ${connection.responseCode}")
                    runOnUiThread {
                        Toast.makeText(this, "City not found!", Toast.LENGTH_LONG).show()
                    }
                }
                // Disconnect the connection
                connection.disconnect()
            } catch (e: Exception) {
                // Log any exceptions and show a toast message
                Log.e("fetchWeatherData", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Please connect to internet!", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }


    // This method dismisses the keyboard
    private fun dismissKeyboard() {
        // Get the input method manager and hide the keyboard
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    // This method converts a Unix timestamp to a string in the format "hh:mm a"
    private fun convertUnixTimestampToAmPm(timestamp: Long): String {
        // Convert the timestamp to a Date object and format it
        val date = Date(timestamp * 1000L)
        val sdf = SimpleDateFormat("hh:mm a")
        sdf.timeZone = TimeZone.getDefault() // Use local time zone
        return sdf.format(date)
    }

    // This method converts a Unix timestamp to a string in the format "yyyy-MM-dd HH:mm a"
    private fun convertUnixTimestampToExactDateAndTime(timestamp: Long): String {
        // Convert the timestamp to a Date object and format it
        val date = Date(timestamp * 1000L)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm a")
        sdf.timeZone = TimeZone.getDefault() // Use local time zone
        return sdf.format(date)
    }

    private fun getCityName(lat: Double, long: Double): String? {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(lat, long, 1)
        return addresses?.get(0)?.locality
    }
}





