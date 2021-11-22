package com.psybm7.mp3player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.psybm7.mp3player.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;

    private val viewModel: MainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Audio.Media.IS_MUSIC + "!= 0",
            null, null)

        Log.d("comp3018", cursor.toString())

        if (cursor != null) {
            val adapter = MediaAdapter(cursor) { media -> this.onMediaItemClick(media) }
            binding.rvMediaList.adapter = adapter
        }

        binding.rvMediaList.layoutManager = LinearLayoutManager(this)

//        viewModel.mediaList.observe(this, Observer<List<MP3>> { mediaList ->
//        })
    }

    private fun onMediaItemClick(media: MP3) {
        Log.d("comp3018", media.uri)
        this.viewModel.play(media)
    }

}