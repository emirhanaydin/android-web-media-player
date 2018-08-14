package com.example.playerlib

import android.annotation.SuppressLint
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.io.IOException
import java.lang.Exception

class PlayerActivity : AppCompatActivity() {
    private val TAG = PlayerActivity::class.qualifiedName
    private val BANDWITH_METER: DefaultBandwidthMeter = DefaultBandwidthMeter()

    private lateinit var player: SimpleExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var componentListener: ComponentListener

    private var positionMs: Long = 0
    private var windowIndex = 0
    private var playWhenReady = false
    private var isPlayerInit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.video_view)
        componentListener = ComponentListener()
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23)
            initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT <= 23 || isPlayerInit)
            initializePlayer()

    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23)
            releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23)
            releasePlayer()
    }

    private fun initializePlayer() {
        if (!isPlayerInit) {
            player = ExoPlayerFactory.newSimpleInstance(
                    DefaultRenderersFactory(this),
                    DefaultTrackSelector(),
                    DefaultLoadControl())

            player.addListener(componentListener)
            player.addAnalyticsListener(componentListener)
            player.playWhenReady = playWhenReady
            player.seekTo(windowIndex, positionMs)

            playerView.player = player

            isPlayerInit = true
        }

        val mediaSource: MediaSource = buildMediaSource(Uri.parse(getString(R.string.media_url_dash)))
        player.prepare(mediaSource, true, false)
    }

    private fun releasePlayer() {
        if (!isPlayerInit) return

        positionMs = player.currentPosition
        windowIndex = player.currentWindowIndex
        playWhenReady = player.playWhenReady

        player.removeListener(componentListener)
        player.removeAnalyticsListener(componentListener)
        player.release()

        isPlayerInit = false
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val manifestDataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory("ua")
        val defaultHttpDataSourceFactory = DefaultHttpDataSourceFactory("ua", BANDWITH_METER)
        val dashChunkSourceFactory: DefaultDashChunkSource.Factory = DefaultDashChunkSource.Factory(defaultHttpDataSourceFactory)
        val dashMediaSourceFactory: DashMediaSource.Factory = DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)

        return dashMediaSourceFactory.createMediaSource(uri)
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    inner class ComponentListener : Player.DefaultEventListener(), AnalyticsListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val stateString: String = when (playbackState) {
                Player.STATE_IDLE -> "ExoPlayer.STATE_IDLE           -"
                Player.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                Player.STATE_READY -> "ExoPlayer.STATE_READY         -"
                Player.STATE_ENDED -> "ExoPlayer.STATE_ENDED         -"
                else -> "UNKNOWN_STATE                               -"
            }

            Log.d(TAG, "Changed state to $stateString playWhenReady $playWhenReady")
        }

        override fun onPlayerStateChanged(eventTime: AnalyticsListener.EventTime?, playWhenReady: Boolean, playbackState: Int) {
            //Do nothing.
        }

        override fun onPlaybackParametersChanged(eventTime: AnalyticsListener.EventTime?, playbackParameters: PlaybackParameters?) {
            //Do nothing.
        }

        override fun onSeekProcessed(eventTime: AnalyticsListener.EventTime?) {
            //Do nothing.
        }

        override fun onTracksChanged(eventTime: AnalyticsListener.EventTime?, trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            //Do nothing.
        }

        override fun onPlayerError(eventTime: AnalyticsListener.EventTime?, error: ExoPlaybackException?) {
            //Do nothing.
        }

        override fun onLoadingChanged(eventTime: AnalyticsListener.EventTime?, isLoading: Boolean) {
            //Do nothing.
        }

        override fun onPositionDiscontinuity(eventTime: AnalyticsListener.EventTime?, reason: Int) {
            //Do nothing.
        }

        override fun onRepeatModeChanged(eventTime: AnalyticsListener.EventTime?, repeatMode: Int) {
            //Do nothing.
        }

        override fun onTimelineChanged(eventTime: AnalyticsListener.EventTime?, reason: Int) {
            //Do nothing.
        }

        override fun onSeekStarted(eventTime: AnalyticsListener.EventTime?) {
            //Do nothing.
        }

        override fun onDownstreamFormatChanged(eventTime: AnalyticsListener.EventTime?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
            //Do nothing.
        }

        override fun onDrmKeysLoaded(eventTime: AnalyticsListener.EventTime?) {
            //Do nothing.
        }

        override fun onMediaPeriodCreated(eventTime: AnalyticsListener.EventTime?) {
            //Do nothing.
        }

        override fun onRenderedFirstFrame(eventTime: AnalyticsListener.EventTime?, surface: Surface?) {
            //Do nothing.
        }

        override fun onReadingStarted(eventTime: AnalyticsListener.EventTime?) {
            //Do nothing.
        }

        override fun onBandwidthEstimate(eventTime: AnalyticsListener.EventTime?, totalLoadTimeMs: Int, totalBytesLoaded: Long, bitrateEstimate: Long) {
            //Do nothing.
        }

        override fun onNetworkTypeChanged(eventTime: AnalyticsListener.EventTime?, networkInfo: NetworkInfo?) {
            //Do nothing.
        }

        override fun onViewportSizeChange(eventTime: AnalyticsListener.EventTime?, width: Int, height: Int) {
            //Do nothing.
        }

        override fun onDrmKeysRestored(eventTime: AnalyticsListener.EventTime?) {
            //Do nothing.
        }

        override fun onDecoderDisabled(eventTime: AnalyticsListener.EventTime?, trackType: Int, decoderCounters: DecoderCounters?) {
            //Do nothing.
        }

        override fun onShuffleModeChanged(eventTime: AnalyticsListener.EventTime?, shuffleModeEnabled: Boolean) {
            //Do nothing.
        }

        override fun onDecoderInputFormatChanged(eventTime: AnalyticsListener.EventTime?, trackType: Int, format: Format?) {
            //Do nothing.
        }

        override fun onAudioSessionId(eventTime: AnalyticsListener.EventTime?, audioSessionId: Int) {
            //Do nothing.
        }

        override fun onDrmSessionManagerError(eventTime: AnalyticsListener.EventTime?, error: Exception?) {
            //Do nothing.
        }

        override fun onLoadStarted(eventTime: AnalyticsListener.EventTime?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
            //Do nothing.
        }

        override fun onUpstreamDiscarded(eventTime: AnalyticsListener.EventTime?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
            //Do nothing.
        }

        override fun onLoadCanceled(eventTime: AnalyticsListener.EventTime?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
            //Do nothing.
        }

        override fun onMediaPeriodReleased(eventTime: AnalyticsListener.EventTime?) {
            //Do nothing.
        }

        override fun onDecoderInitialized(eventTime: AnalyticsListener.EventTime?, trackType: Int, decoderName: String?, initializationDurationMs: Long) {
            //Do nothing.
        }

        override fun onDroppedVideoFrames(eventTime: AnalyticsListener.EventTime?, droppedFrames: Int, elapsedMs: Long) {
            //Do nothing.
        }

        override fun onDecoderEnabled(eventTime: AnalyticsListener.EventTime?, trackType: Int, decoderCounters: DecoderCounters?) {
            //Do nothing.
        }

        override fun onVideoSizeChanged(eventTime: AnalyticsListener.EventTime?, width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
            //Do nothing.
        }

        override fun onAudioUnderrun(eventTime: AnalyticsListener.EventTime?, bufferSize: Int, bufferSizeMs: Long, elapsedSinceLastFeedMs: Long) {
            //Do nothing.
        }

        override fun onLoadCompleted(eventTime: AnalyticsListener.EventTime?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
            //Do nothing.
        }

        override fun onDrmKeysRemoved(eventTime: AnalyticsListener.EventTime?) {
            //Do nothing.
        }

        override fun onLoadError(eventTime: AnalyticsListener.EventTime?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?, error: IOException?, wasCanceled: Boolean) {
            //Do nothing.
        }

        override fun onMetadata(eventTime: AnalyticsListener.EventTime?, metadata: Metadata?) {
            //Do nothing.
        }
    }
}
