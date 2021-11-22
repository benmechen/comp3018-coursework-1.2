package com.psybm7.mp3player

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val selectedMedia: MutableLiveData<MP3> by lazy {
        MutableLiveData<MP3>()
    }

    private val player: MP3Player = MP3Player()

    fun getSelectedMedia(): MP3? {
        return this.selectedMedia.value
    }

    fun play(media: MP3) {
        if (this.selectedMedia.value == null)
            this.selectedMedia.value = media

        val selectedMedia = this.getSelectedMedia() ?: throw Error("No media found")
        this.player.load(selectedMedia.uri)
        Log.d("comp3018", this.player.state.toString())
        this.player.play()
    }

    fun pause() {
        this.player.pause()
    }

    fun stop() {
        this.selectedMedia.value = null
        this.player.stop()
    }
}