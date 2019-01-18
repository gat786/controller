package `in`.webxstudio.batmobilecontroller

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import yjkim.mjpegviewer.MjpegCallback
import yjkim.mjpegviewer.MjpegView

class MainActivity : AppCompatActivity(), AnkoLogger
{
    val sensorData = SensorData()
    val port = "12345"
    val videoUrl = "${sensorData.baseUrl}:$port/?action=stream"
    var theta = 0

    private lateinit var player:SimpleExoPlayer
    val mHandler = Handler()
    val apiController = ApiController()

    var mRunnable = object : Runnable{
        override fun run() {
            val data = apiController.getData()
            info { "recieved $data" }
        }
    }
    val delay : Long = 5000
    fun requestData(){
        mHandler.postDelayed(mRunnable,delay)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestData()

        mHandler.postDelayed(mRunnable,1000)

        up_button.setOnClickListener {
            apiController.putData(direction = Direction.Forward)
        }

        down_button.setOnClickListener {
            apiController.putData(direction = Direction.Backward)
        }

        right_button.setOnClickListener {
            apiController.putData(direction = Direction.Right)
        }

        left_button.setOnClickListener {
            apiController.putData(direction = Direction.Left)
        }

        val mjpegView:MjpegView = video_view

        mjpegView.Start(videoUrl)
    }
}
enum class Direction{
    Forward,Backward,Right,Left
}