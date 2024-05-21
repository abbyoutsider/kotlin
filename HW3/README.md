## Weather App

**About the Project**

This project is an Android application that provides weather information to the user. It uses the OpenWeatherMap API to fetch weather data for a specific location. The location can be either the user’s current location (obtained via the device’s location services) or a city entered by the user in a search bar.

**How it Works**

* **Location Services:** When the app starts, it tries to get the device’s last known location using the FusedLocationProviderClient. If the app has the necessary location permissions, it fetches the weather data for the current location.
* **User Input:** The app also provides a search bar where the user can enter a city name. When the user clicks the search button, the app fetches the weather data for the entered city.
* **Fetching Weather Data:** The app constructs a URL with the city name and the OpenWeatherMap API key, then sends a GET request to this URL to fetch the weather data. The response from the API is a JSON object containing various weather details.
* **Updating the UI:** The app parses the JSON response and updates the UI with the fetched weather data. It displays various weather details like temperature, pressure, humidity, sunrise and sunset times, and more.
* **Error Handling:** The app handles various error scenarios, such as the city not being found, the device not having an internet connection, or the app not having location permissions. It informs the user about these errors via toast messages.

**Demo**
https://drive.google.com/file/d/17cLqGVHZQKXaO7lPFDBt4wSyDf0mlR_5/view?usp=drive_link

**Development Time**

While I don't have an exact record of the hours spent, I dedicated approximately four days to complete this project.


**Resources**
* **OpenWeather:** OpenWeather: https://openweathermap.org/appid
* **General Debugging:** Gemini ([https://www.wired.com/story/how-to-use-google-gemini-ai-bard-chatbot/](https://www.wired.com/story/how-to-use-google-gemini-ai-bard-chatbot/))
* **Understanding Kotlin Drawable:** [https://developer.android.com/reference/kotlin/android/graphics/drawable/Drawable](https://developer.android.com/reference/kotlin/android/graphics/drawable/Drawable)
* **Icon Design:**
    * Material Design Icons: [https://m3.material.io/styles/icons](https://m3.material.io/styles/icons)
    * Google Fonts Icons: [https://fonts.google.com/icons](https://fonts.google.com/icons)