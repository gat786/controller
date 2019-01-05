package `in`.webxstudio.batmobilecontroller

import android.app.Activity
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

class MainActivity : AppCompatActivity(), AnkoLogger
{
    val videoUrl = "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8"

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
    }

    fun initializePlayer(){
        player = ExoPlayerFactory.newSimpleInstance(baseContext)
        video_view.player = player
        //player.prepare(createDataSource(videoUrl))
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

    fun pauseListener(){
        val listener  = object: Player.EventListener{

        }
    }
}
