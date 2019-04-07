package `in`.webxstudio.batmobilecontroller

import android.content.Context
import android.util.Log
import android.widget.TextView
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

val TAG = "MqttController"

class MqttController{
    lateinit var mqttClient: MqttAndroidClient

    val temperature = "temp"
    val humidity = "humidity"
    val obstacle = "obstacle"

    val directions = "directions"

    fun createController(serverUrl:String,context: Context): MqttAndroidClient {
        Log.d(TAG,"ServerUrl is $serverUrl")
        mqttClient = MqttAndroidClient(context,"tcp://$serverUrl:1883",MqttClient.generateClientId())
        val options = MqttConnectOptions()
        options.isCleanSession = true
        Log.d(TAG,"Connecting to server $serverUrl")
        mqttClient.connect(options)
        Log.d(TAG,"Connected")
        return mqttClient
    }

    fun sendDirection(direction:String,client: MqttAndroidClient){
        client.publish("directions",direction.toByteArray(),1,true)
        Log.d(TAG,"Message Published")
    }

    fun getData(client: MqttAndroidClient,
                tempTextview:TextView,
                humidityTextView: TextView,
                obstacleTextView:TextView){

        client.subscribe(temperature,1)
        client.subscribe(humidity,1)
        client.subscribe(obstacle,1)

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