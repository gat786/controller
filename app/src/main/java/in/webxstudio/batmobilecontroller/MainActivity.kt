package `in`.webxstudio.batmobilecontroller

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.stealthcopter.networktools.SubnetDevices
import com.stealthcopter.networktools.subnet.Device
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.client.mqttv3.MqttClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.ArrayList

class MainActivity : AppCompatActivity(), AnkoLogger
{
    var scannedMacAddress = ""
    val port = "12345"
    val scannerIntegrator = IntentIntegrator(this)
    val regex = "[0-9a-f]{2}([-:]?)[0-9a-f]{2}(\\\\1[0-9a-f]{2}){4}\$"
    val TAG="MainActivity"
    var ip_address = "192.168.0.102"
    val mqttPort = 1883
    val streamUrl = "http://$ip_address:8080/?action=stream"
    lateinit var mqttClient:MqttClient
    val controller = MqttController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        up_button.setOnClickListener {
            //move ahead
            controller.sendDirection("forward",mqttClient)
        }

        down_button.setOnClickListener {
            //backwards
            controller.sendDirection("backward",mqttClient)
        }

        right_button.setOnClickListener {
            //move right
            controller.sendDirection("right",mqttClient)
        }

        left_button.setOnClickListener {
            //move left
            controller.sendDirection("left",mqttClient)
        }


        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
            scannerIntegrator.setOrientationLocked(true)
            scannerIntegrator.initiateScan()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),101)
        }
    }

    fun setupVideoPlayer(videoUrl:String){
        video_view.Start(videoUrl)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(result!=null){
            if(result.contents ==null)
                Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
            else{
                scannedMacAddress = result.contents
                Log.d(TAG,"Scanned Mac Address $scannedMacAddress")
                findDevices()
            }
        }
    }

    var devicesList = ArrayList<Device>()
    fun findDevices(){
        doAsync {
            SubnetDevices.fromLocalAddress().findDevices(object : SubnetDevices.OnSubnetDeviceFound{
                override fun onFinished(devicesFound: ArrayList<Device>?) {
                    devicesList = devicesFound!!
                    Log.d(TAG,"Search Completed")
                    uiThread {
                        setUpAccordingly()
                    }

                }

                override fun onDeviceFound(device: Device?) {
                    Log.i(TAG,"Device found ${device!!.ip} ${device!!.mac}")
                }
            })
        }
    }

    fun setUpAccordingly(){
        var deviceFound = false
        for(device in devicesList){
            if (device.mac != null){
                if (device.mac.toUpperCase() == scannedMacAddress){
                    deviceFound = true
                    ip_address = device.ip
                }
            }
        }
        if(deviceFound){
            Log.d(TAG,"Rover found successfully at $ip_address")
            Toast.makeText(applicationContext,"Rover Found starting services",Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.INVISIBLE
            Log.d(TAG,"Starting streaming service")
            setupVideoPlayer(streamUrl)
            mqttClient = controller.createController(ip_address)
            controller.getData(mqttClient,temp_text,humidity_text,obstacle_text)
        }
        else{
            Log.d(TAG,"Rover not found")
            Toast.makeText(applicationContext,"Rover not found",Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            IntentIntegrator(this).initiateScan()
        }else{
            Toast.makeText(this,"Camera Permission is necessary please allow to continue using app",Toast.LENGTH_SHORT).show()
        }
    }

    fun createMqttConnection(){

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }
}
enum class Direction{
    Forward,Backward,Right,Left
}