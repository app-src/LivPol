package io.github.ashishthehulk.livpol

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class FetchEpicData : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var captchaImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fetch_epic_data)

        // Initialize views
        progressBar = findViewById(R.id.progressBar)
        captchaImageView = findViewById(R.id.captchaImageView)

        val epicNumberField:EditText = findViewById(R.id.epicNumText)
        val solvedCaptchaField:EditText = findViewById(R.id.solvedCaptchaText)
        val searchButton:Button = findViewById(R.id.searchButton)

        var captchaId = ""

        // Show loading indicator
        progressBar.visibility = ProgressBar.VISIBLE

        // Fetch captcha data asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            val captchaData = getCaptchaData()

            // Update UI on the main thread
            withContext(Dispatchers.Main) {
                if (captchaData != null) {
                    // Load captcha image
                    val imageBytes = android.util.Base64.decode(captchaData["captcha"], android.util.Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    captchaImageView.setImageBitmap(bitmap)
                    captchaImageView.visibility = ImageView.VISIBLE

                    captchaId = captchaData["id"].toString()
                } else {
                    Log.e("MainActivity", "Failed to get captcha data")
                }

                // Hide loading indicator
                progressBar.visibility = ProgressBar.GONE
            }
        }

        // Set OnClickListener to the button
        searchButton.setOnClickListener {
            // Get text from text fields
            val epicNumber = epicNumberField.text.toString()
            val solvedCaptcha = solvedCaptchaField.text.toString()

            // Check if any text field is empty
            if (epicNumber.isEmpty() || solvedCaptcha.isEmpty()) {
                // Prompt user to fill both fields
                Toast.makeText(this, "Please fill both fields", Toast.LENGTH_SHORT).show()
            } else {
                // Print values in Logcat
                Log.d("MainActivity", "Text 1: $epicNumber, Text 2: $solvedCaptcha")
                val intent = Intent(this, DisplayData::class.java).apply {
                    putExtra("epicno", epicNumber)
                    putExtra("captchaData", solvedCaptcha)
                    putExtra("captchaId", captchaId)
                }
                startActivity(intent)

            }
        }


    }

    private fun getCaptchaData(): Map<String, String>? {
        var captchaData: Map<String, String>? = null

        val url = URL("https://gateway-voters.eci.gov.in/api/v1/captcha-service/generateCaptcha")
        val connection = url.openConnection() as HttpURLConnection

        // Set request properties
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/json, text/plain, */*")
        connection.setRequestProperty("Origin", "https://electoralsearch.eci.gov.in")
        connection.setRequestProperty("appName", "ELECTORAL_SEARCH")

        // Set connection and read timeouts
        connection.connectTimeout = 10000 // 10 seconds
        connection.readTimeout = 10000 // 10 seconds

        try {
            // Get response code
            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()

                // Parse JSON response
                val jsonResponse = JSONObject(response)

                if (jsonResponse.getString("status") == "Success" && jsonResponse.getInt("statusCode") == 200) {
                    captchaData = mapOf(
                        "captcha" to jsonResponse.getString("captcha"),
                        "id" to jsonResponse.getString("id")
                    )
                } else {
                    Log.e("Captcha", "Invalid response")
                }
            } else {
                Log.e("Captcha", "Failed to get data. Status code: $responseCode")
            }
        } catch (e: Exception) {
            Log.e("Captcha", "An error occurred: ${e.message}")
        } finally {
            connection.disconnect()
        }

        return captchaData
    }

}