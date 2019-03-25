package `in`.webxstudio.batmobilecontroller

import android.util.Log
import android.widget.TextView
import org.eclipse.paho.client.mqttv3.*

val TAG = "MqttController"

class MqttController{
    lateinit var mqttClient: MqttClient

    val temperature = "temp"
    val humidity = "humidity"
    val obstacle = "obstacle"

    val directions = "directions"

    fun createController(serverUrl:String): MqttClient {
        mqttClient = MqttClient(serverUrl,"rover_client")
        val options = MqttConnectOptions()
        options.isCleanSession = true
        Log.d(TAG,"Connecting to server $serverUrl")
        mqttClient.connect(options)
        Log.d(TAG,"Connected")
        return mqttClient
    }

    fun sendDirection(direction:String,client: MqttClient){
        client.publish("directions",direction.toByteArray(),1,true)
        Log.d(TAG,"Message Published")
    }

    fun getData(client: MqttClient,
                tempTextview:TextView,
                humidityTextView: TextView,
                obstacleTextView:TextView){
        client.subscribe(temperature)
        client.subscribe(humidity)
        client.subscribe(obstacle)
        client.setCallback(object : MqttCallback{
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(TAG,"Message received")
                when(topic){
                    temperature ->{
                        if (message!=null)
                            tempTextview.text = message.payload.toString()
                    }
                    humidity ->{
                        if (message!=null)
                            humidityTextView.text = message.payload.toString()
                    }
                    obstacle->{
                        if (message!=null)
                            obstacleTextView.text = message.payload.toString()
                    }
                }
            }

            override fun connectionLost(cause: Throwable?) {
                Log.e(TAG,"connection lost")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(TAG,"Successfully delivered")
            }
        })
    }
}