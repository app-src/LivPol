package io.github.ashishthehulk.livpol

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DisplayData : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_epic_data)

        val extras = intent.extras
        if (extras != null) {
            val epicno = extras.getString("epicno")
            val captchaData = extras.getString("captchaData")
            val captchaId = extras.getString("captchaId").toString()

            // Call the function with the retrieved values
            CoroutineScope(Dispatchers.IO).launch {
                val data = getVoterInfo(epicno, captchaData, captchaId)


                if (data != null) {
                    // Print the received data in Logcat
                    Log.d("SecondActivity", "Received data: ${data.toString()} ")
                    // Parse the JSON response
//                    val jsonResponse = JSONObject(data)
                    val content = data.getJSONObject("content")

// Set values to TextViews
                    try {

                    withContext(Dispatchers.Main) {
                        findViewById<TextView>(R.id.epicNumber).text =
                            content.getString("epicNumber")
                        findViewById<TextView>(R.id.firstName).text =
                            content.getString("applicantFirstName")
                        findViewById<TextView>(R.id.relationName).text =
                            content.getString("relationName")
                        findViewById<TextView>(R.id.age).text = content.getInt("age").toString()
                        findViewById<TextView>(R.id.gender).text = content.getString("gender")
                        findViewById<TextView>(R.id.birthYear).text =
                            content.optString("birthYear", "")
                        findViewById<TextView>(R.id.partNumber).text =
                            content.getInt("partNumber").toString()
                        findViewById<TextView>(R.id.partName).text = content.getString("partName")
                        findViewById<TextView>(R.id.stateName).text = content.getString("stateName")
                        findViewById<TextView>(R.id.createdDttm).text =
                            content.getString("createdDttm")
                        findViewById<TextView>(R.id.psbuildingName).text =
                            content.getString("psbuildingName")
                    }
                    } catch (e : Exception) {
                        Log.e("getVoterInfo", "An severe error occurred: ${e.message}")
                    }
                } else {
                    Log.e("SecondActivity", "Failed to get voter info")
                }

            }
        }


    }

//    private fun getVoterInfo(epicno: String?, captchaData: String?): Any {
//
//        return "it works"
//
//    }


    private suspend fun getVoterInfo(
        epicno: String?,
        captchaData: String?,
        captchaId: String
    ): JSONObject? {
        val url =
            URL("https://gateway.eci.gov.in/api/v1/elastic/search-by-epic-from-national-display")

        val headers = mapOf(
            "Accept" to "application/json, text/plain, */*",
            "Accept-Language" to "en-GB,en-US;q=0.9,en;q=0.8,hi;q=0.7",
            "Connection" to "keep-alive",
            "Content-Type" to "application/json",
            "DNT" to "1",
            "Origin" to "https://electoralsearch.eci.gov.in",
            "Sec-Fetch-Dest" to "empty",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Site" to "same-site",
            "User-Agent" to "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "applicationName" to "ELECTORAL_SEARCH",
            "sec-ch-ua" to "\"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\"",
            "sec-ch-ua-mobile" to "?0",
            "sec-ch-ua-platform" to "\"Linux\""
        )

        val data = JSONObject().apply {
            put("isPortal", true)
            put("epicNumber", epicno)
            put("captchaId", captchaId)
            put("captchaData", captchaData)
            put("securityKey", "na")
        }

        Log.d("getVoterInfo", data.toString())

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }

                connection.doOutput = true
                connection.outputStream.use { outputStream ->
                    outputStream.write(data.toString().toByteArray())
                }

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    val jsonResponse = response.substring(1, response.length - 1) // Remove first and last char
                    Log.d("getVoterInfo", jsonResponse)
                    JSONObject(jsonResponse)
                } else {
                    Log.e("getVoterInfo", "Failed to get data. Status code: $responseCode")
                    null
                }
            } catch (e: Exception) {
                Log.e("getVoterInfo", "An error occurred: ${e.message}")
                null
            }
        }

    }
}
