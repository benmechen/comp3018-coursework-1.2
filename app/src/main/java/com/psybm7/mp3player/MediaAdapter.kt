package com.psybm7.mp3player

import android.database.Cursor
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

class MediaAdapter (private val cursor: Cursor, private val onClickAction: (media: MP3) -> Unit): RecyclerView.Adapter<MediaAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, private val onClickAction: (media: MP3) -> Unit) : RecyclerView.ViewHolder(view) {
        var state: MediaState = MediaState.DEFAULT
        private val tvName: TextView = view.findViewById(R.id.tvMediaName)
        private val btPlay: Button = view.findViewById(R.id.btMediaPlay)
        private lateinit var media: MP3

        fun setMedia(media: MP3) {
            this.media = media
            this.tvName.text = this.media.name
            this.btPlay.setOnClickListener {
//                holder.state = MediaState.PLAYING
                onClickAction(this.media)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the custom layout
        val mediaView = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)

        return ViewHolder(mediaView, onClickAction)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor.moveToPosition(position)
        val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME))
        val uri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA))

        val media = MP3(name, uri)
        holder.setMedia(media)
    }

    override fun getItemCount(): Int {
        return cursor.count
    }

    fun onClickListener() {

    }
}