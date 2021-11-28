package com.psybm7.mp3player.fragments.Player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel to facilitate communication between
 * the [PlayerFragment] and parent Activities ([MainActivity])
 */
class PlayerViewModel: ViewModel() {
    /**
     * Player states
     */
    enum class State {
        DEFAULT,
        PLAYING,
        PAUSED
    }

    /**
     * Current player state
     * Update to change the Player UI's behaviour:
     * - DEFAULT: Hide the player
     * - PLAYING: Show the player, set the icon to "Pause"
     * - PAUSED: Show the player, set the icon to "Play"
     */
    val state: MutableLiveData<State> by lazy {
        MutableLiveData<State>(State.DEFAULT)
    }

    /**
     * Current track name
     */
    val name: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    /**
     * Current track progress (out of 100)
     */
    val progress: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    /**
     * If currently playing then pause, and vice versa
     */
    fun action() {
        when (this.state.value) {
            State.PAUSED, State.DEFAULT -> this.state.value = State.PLAYING
            State.PLAYING -> this.state.value = State.PAUSED
        }
    }
}