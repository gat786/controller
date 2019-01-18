package `in`.webxstudio.batmobilecontroller

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.json.JSONObject

class ApiController:AnkoLogger{
    val sensorData = SensorData()
    val port = "8080"

    fun getData(): String {
        val requiredUrl = "$sensorData.baseUrl:$port/getData"

        var result = ""
        val jsonObject = JsonObjectRequest(Request.Method.GET,requiredUrl,null,
            Response.Listener {
                 response -> result = response.toString()
            },Response.ErrorListener {
                error -> info { "error occurred $error" }
            }
            )
        return result
    }

    fun putData(direction: Direction? = null){
        var output = ""
        when(direction){
            Direction.Right -> output = "right"
            Direction.Left -> output = "left"
            Direction.Backward -> output = "backward"
            Direction.Forward -> output = "forward"
        }
        val requestUrl = "${sensorData.baseUrl}:$port/putData"

        val request = StringRequest(Request.Method.GET, "$requestUrl/?direction=$output/",
            Response.Listener {
                response -> info { "response is $response" }
            },Response.ErrorListener {
                info { "error occurred" }
            }
        )
    }
}