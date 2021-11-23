package com.psybm7.mp3player

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    enum class State {
        DEFAULT,
        PLAYING,
        PAUSED
    }

    val state: MutableLiveData<State> by lazy {
        MutableLiveData<State>(State.DEFAULT)
    }

    val selectedMedia: MutableLiveData<MP3> by lazy {
        MutableLiveData<MP3>()
    }

    private val player: MP3Player = MP3Player()

    fun getState(): State? {
        return this.state.value
    }

    fun getSelectedMedia(): MP3? {
        return this.selectedMedia.value
    }

    fun play(media: MP3) {
        Log.d("MainViewModel", "Play")

        this.selectedMedia.value = media

        val selectedMedia = this.getSelectedMedia() ?: throw Error("No media found")
        this.player.load(selectedMedia.uri)
        this.player.play()

        if (this.player.state == MP3Player.MP3PlayerState.ERROR) this.stop()
        else this.state.value = State.PLAYING
    }

    fun pause() {
        Log.d("MainViewModel", "Pause")

        this.player.pause()
        this.state.value = State.PAUSED
    }

    fun stop() {
        Log.d("MainViewModel", "Stop")

        this.selectedMedia.value = null
        this.player.stop()
        this.state.value = State.DEFAULT
    }

    fun getProgress(): Int {
        if (this.player.state != MP3Player.MP3PlayerState.PLAYING || this.player.duration == 0) return 0

        val percent = this.player.progress.toDouble() / this.player.duration.toDouble()
        return (percent * 100).toInt()
    }
}