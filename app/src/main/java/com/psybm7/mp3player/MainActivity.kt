package com.psybm7.mp3player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.psybm7.mp3player.databinding.ActivityMainBinding
import com.psybm7.mp3player.fragments.Player.PlayerViewModel

/**
 * Primary activity to hold the list of local media
 * Coordinates between the Service and ViewModel
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    /**
     * View model to allow interaction between this Activity
     * and the [PlayerFragment]
     */
    private val playerViewModel: PlayerViewModel by viewModels()

    /**
     * Bound service for Client-Service like communication
     */
    private var service: MP3Service? = null

    /**
     * Has the activity been bound to the service?
     */
    private var bound: Boolean = false

    /**
     * Connection to get the service instance
     * and handle disconnecting
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as MP3Service.MP3Binder
            service = binder.service
            bound = true
            onBound()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    /**
     * 1. Set up DataBinding
     * 2. Query for local media
     * 3. Set up an adapter to link the list rows with the Activity
     * 4. Listen for updates from PlayerFragment (ie. when the user presses Play/Pause)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 2
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Audio.Media.IS_MUSIC + "!= 0",
            null, null)

        // 3
        if (cursor != null) {
            val adapter = MediaAdapter(cursor) { media ->
                val state = this.onMediaItemClick(media)
                this.playerViewModel.state.value = state
                return@MediaAdapter state
            }
            binding.rvMediaList.adapter = adapter
        }

        binding.rvMediaList.layoutManager = LinearLayoutManager(this)

        // 4
        this.playerViewModel.state.observe(this, { state -> this.onPlayerAction(state) })
    }

    /**
     * Bind the service
     */
    override fun onStart() {
        super.onStart()
        this.bindService()
    }

    /**
     * Unbind the service
     */
    override fun onStop() {
        super.onStop()
        this.unbindService()
        this.bound = false
    }

    /**
     * Start the MP3Service as a Foreground Service
     * Bind to it to receive updates
     */
    private fun bindService() {
        val intent = Intent(this, MP3Service::class.java)
        startForegroundService(intent)
        bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * Unbind from service,
     * but keep it running in the background so music keeps playing
     */
    private fun unbindService() {
        Intent(this, MP3Service::class.java).also {
            unbindService(serviceConnection)
        }
    }

    /**
     * When bound to the service,
     * register observers on it's states
     */
    private fun onBound() {
        this.service?.selectedMedia?.observe(this, { media ->
            Log.d("MainActivity", media.name)
            this.playerViewModel.name.value = media.name
        })
        this.service?.progress?.observe(this, { progress ->
            this.playerViewModel.progress.value = progress
        })
    }

    /**
     * Handler Player actions (Play, Pause)
     */
    private fun onPlayerAction(state: PlayerViewModel.State) {
        when (state) {
            PlayerViewModel.State.PLAYING -> this.service?.play()
            PlayerViewModel.State.PAUSED -> this.service?.pause()
            PlayerViewModel.State.DEFAULT -> this.service?.stop()
        }
    }

    /**
     * Select a track, and play or stop it
     * depending on the current state
     */
    private fun onMediaItemClick(media: MP3): PlayerViewModel.State {
        if (this.service?.player?.state == MP3Player.MP3PlayerState.PLAYING) {
            this.service?.stop()
            return PlayerViewModel.State.DEFAULT
        }

        this.service?.play(media)
        return PlayerViewModel.State.PLAYING
    }

}