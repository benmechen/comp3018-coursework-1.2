package com.psybm7.mp3player

import android.database.Cursor
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psybm7.mp3player.fragments.Player.PlayerViewModel

/**
 * Adapt a Media cursor to a list of tracks
 */
class MediaAdapter (private val cursor: Cursor, private val onClickAction: (media: MP3) -> PlayerViewModel.State): RecyclerView.Adapter<MediaAdapter.ViewHolder>() {
    /**
     * Hold the View's current state
     */
    class ViewHolder(view: View, private val onClickAction: (media: MP3) -> PlayerViewModel.State) : RecyclerView.ViewHolder(view) {
        private var state: PlayerViewModel.State = PlayerViewModel.State.DEFAULT
        private val tvName: TextView = view.findViewById(R.id.tvMediaName)
        private val btPlay: Button = view.findViewById(R.id.btMediaPlay)
        private lateinit var media: MP3

        /**
         * Set this row's content.
         * Register a click listener with the button, and use the returned state
         * to update the button text
         */
        fun setMedia(media: MP3) {
            this.media = media
            this.tvName.text = this.media.name
            this.btPlay.setOnClickListener {
                this.setState(onClickAction(this.media))
            }
        }

        /**
         * Update the row's button text depending on the
         * result from the MainActivity
         */
        private fun setState(state: PlayerViewModel.State) {
            this.state = state
            when (state) {
                PlayerViewModel.State.DEFAULT -> this.btPlay.text = "Play"
                PlayerViewModel.State.PAUSED -> this.btPlay.text = "Play"
                PlayerViewModel.State.PLAYING -> this.btPlay.text ="Stop"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the custom layout
        val mediaView = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)

        return ViewHolder(mediaView, onClickAction)
    }

    /**
     * Get the current track and build an MP3 object from it
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor.moveToPosition(position)
        val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME))
        val uri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA))

        val media = MP3(name, uri)
        holder.setMedia(media)
    }

    /**
     * Get total number of items for the RecyclerView
     */
    override fun getItemCount(): Int {
        return cursor.count
    }
}