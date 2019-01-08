package `in`.webxstudio.batmobilecontroller

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class ApiController:AnkoLogger{
    val baseUrl = "http://192.168.0.1"

    fun getData(): SensorData? {
        val requiredUrl = "$baseUrl/get"
        var data = SensorData()
        val jsonObject = JsonObjectRequest(Request.Method.GET,requiredUrl,null,
            Response.Listener {
                    response ->  info { "response is $response" }
                data = Gson().fromJson<SensorData>(response.toString(),SensorData::class.java)
            },Response.ErrorListener {
                error -> info { "error occurred $error" }
            }
            )
        return data
    }

    fun putData(){
        val requestUrl = "$baseUrl/post"

        val stringResponse = StringRequest(Request.Method.POST,requestUrl,
            Response.Listener {
                    response -> info{ "response is $response" }
            }, Response.ErrorListener {
                error -> info { "error is $error" }
            }
        )
    }
}