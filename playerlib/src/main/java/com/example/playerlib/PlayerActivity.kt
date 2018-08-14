package com.example.playerlib

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

class PlayerActivity : AppCompatActivity() {
    private lateinit var player: SimpleExoPlayer
    private lateinit var playerView: PlayerView

    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady: Boolean = false
    private var isPlayerInit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.video_view)
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

            playerView.player = player
            player.playWhenReady = playWhenReady
            player.seekTo(currentWindow, playbackPosition)

            isPlayerInit = true
        }

        val mediaSource: MediaSource = buildMediaSource(Uri.parse(getString(R.string.media_url_mp4)))
        player.prepare(mediaSource, true, false)
    }

    private fun releasePlayer() {
        if (!isPlayerInit) return

        playbackPosition = player.currentPosition
        currentWindow = player.currentWindowIndex
        playWhenReady = player.playWhenReady
        player.release()

        isPlayerInit = false
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory("exoplayer")).createMediaSource(uri)
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        val flags: IntArray = intArrayOf(
                View.SYSTEM_UI_FLAG_LOW_PROFILE,
                View.SYSTEM_UI_FLAG_FULLSCREEN,
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE,
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY,
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION,
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        var visibility = 0
        for (i in flags)
            visibility = visibility or i

        playerView.systemUiVisibility = visibility
    }
}
