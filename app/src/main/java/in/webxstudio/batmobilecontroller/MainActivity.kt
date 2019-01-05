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
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainActivity : AppCompatActivity(), AnkoLogger
{
    val videoUrl = "http://192.168.137.240:8160/"

    private lateinit var player:SimpleExoPlayer
    val mHandler = Handler()
    var mRunnable = object : Runnable{
        override fun run() {
            info { "pressed" }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializePlayer()

        up_button.setOnTouchListener { v, event ->
            onTouchEvent(event,Direction.Forward,v)
        }

        down_button.setOnTouchListener { v, event ->
            onTouchEvent(event,Direction.Backward,v)
        }

        left_button.setOnTouchListener { v, event ->
            onTouchEvent(event)
        }

    }
    fun onTouchEvent(
        event: MotionEvent?,
        direction: Direction? = null,
        view: View): Boolean {
        if (direction==Direction.Forward){
            info { "move ahead" }
            view.elevation = Integer.parseInt("4").toFloat()
        }
        else if (direction == Direction.Backward){
            info { "move backwards" }
        }
        else if (direction == Direction.Right){
            info { "increasing angle towards right" }
        }
        else if (direction == Direction.Left){
            info { "increasing angle towards left" }
        }
        return true
    }

    fun initializePlayer(){
        player = ExoPlayerFactory.newSimpleInstance(baseContext)
        video_view.player = player
        player.prepare(getHlsMediaSource(videoUrl))
    }

    fun createDataSource(videoUrl:String): DashMediaSource? {
        val dataSourceFactory = DefaultDataSourceFactory(baseContext, Util.getUserAgent(
            baseContext,"BatMobile"
        ))


        val dashMediaSource = DashMediaSource.Factory(
            DefaultDashChunkSource.Factory(dataSourceFactory),
            dataSourceFactory
        )

        val bandwidthMeter = DefaultBandwidthMeter()
        val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))

        return dashMediaSource.createMediaSource(Uri.parse(videoUrl))
    }

    fun getHlsMediaSource(hls: String): MediaSource {
        val bandwidthMeter = DefaultBandwidthMeter()
        val applicationName = Util.getUserAgent(baseContext, "BatMobile")
        val dataSourceFactory = DefaultDataSourceFactory(baseContext, applicationName, bandwidthMeter)
        val factory = HlsMediaSource.Factory(dataSourceFactory)
        return factory.createMediaSource(Uri.parse(hls))
    }

    fun pauseListener(){
        val listener  = object: Player.EventListener{

        }
    }
}
enum class Direction{
    Forward,Backward,Right,Left
}