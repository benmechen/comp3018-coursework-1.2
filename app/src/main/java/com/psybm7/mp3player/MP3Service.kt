package com.psybm7.mp3player

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import java.lang.Error

/**
 * Polling interval for current progress (ms)
 */
const val PROGRESS_INTERVAL: Long = 500

/**
 * Service to play an MP3 file in the background.
 * Can be bound to receive updates on the current player state and progress.
 * Registers a notification showing the current playing track.
 */
class MP3Service : Service() {
    /**
     * Notification config values
     */
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "mp3player"
        private const val CHANNEL_NAME = "MP3Player"
    }

    /**
     * Allow Activities to bind to this service for updates
     */
    inner class MP3Binder: Binder() {
        val service: MP3Service
            get() = this@MP3Service
    }

    /**
     * Publicly accessible player instance to handle
     * playing MP3s
     */
    val player: MP3Player = MP3Player()

    /**
     * Current track progress out of 100, updated every
     * [PROGRESS_INTERVAL] milliseconds
     */
    val progress: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>(0.0)
    }

    /**
     * Current selected media.
     * Use to get track information (name, uri)
     */
    val selectedMedia: MutableLiveData<MP3> by lazy {
        MutableLiveData<MP3>()
    }

    /**
     * Handler for the progress update task that runs on a set interval
     */
    private lateinit var progressHandler: Handler

    /**
     * Calculate the current progress and wait [PROGRESS_INTERVAL] milliseconds
     * before calculating again
     */
    private val setProgressTask = object : Runnable {
        override fun run() {
            progress.value = updateProgress()
            progressHandler.postDelayed(this, PROGRESS_INTERVAL)
        }
    }

    /**
     * Binder instance
     */
    private val binder: MP3Binder = MP3Binder()

    /**
     * Return the binder instance to allow Activities to bind to this service
     */
    override fun onBind(intent: Intent): IBinder {
        return this.binder
    }

    /**
     * Start the progress handler loop
     */
    override fun onCreate() {
        super.onCreate()
        this.progressHandler = Handler(Looper.getMainLooper())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    /**
     * Stop the player and release resources
     */
    override fun onDestroy() {
        this.player.stop()
        super.onDestroy()
    }

    /**
     * Publicly callable function to play a track or resume playback.
     * This function must be provided a track to play on it's first call.
     * 1. Update selected track if applicable
     * 2. Build a notification channel for this service
     * 3. Start the service in the foreground
     * 4. Load and play the track
     * 5. Start progress updates
     */
    fun play(media: MP3? = null) {
        if (media != null) {
            this.selectedMedia.value = media
        }

        val selectedMedia = this.selectedMedia.value ?: throw Error("No media given")

        this.buildChannel()
        val notification = this.buildNotification(selectedMedia)

        startForeground(NOTIFICATION_ID, notification)

        if (this.player.state == MP3Player.MP3PlayerState.STOPPED || this.player.state == MP3Player.MP3PlayerState.ERROR) {
            this.player.load(selectedMedia.uri)
        }
        this.progressHandler.post(this.setProgressTask)
        this.player.play()
    }

    /**
     * Pause the track and stop progress updates whilst paused
     */
    fun pause() {
        this.progressHandler.removeCallbacks(this.setProgressTask)
        this.player.pause()
    }

    /**
     * Stop the track and progress updates, and stop this service
     * as it is no longer needed
     */
    fun stop() {
        this.progressHandler.removeCallbacks(this.setProgressTask)
        this.player.stop()
        stopForeground(true)
    }

    /**
     * Use the duration and current position of the playing track
     * to calculate the progress as a percentage
     */
    private fun updateProgress(): Double {
        if (this.player.state != MP3Player.MP3PlayerState.PLAYING || this.player.duration == 0) return 0.0

        val percent = this.player.progress.toDouble() / this.player.duration.toDouble()
        return percent * 100
    }

    /**
     * Build a notification to show the current media title
     * as a foreground notification
     */
    private fun buildNotification(media: MP3): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(media.name)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .build()
    }

    /**
     * Build a notification channel for the foreground notification
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildChannel(): NotificationChannel {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return channel
    }
}