package com.psybm7.mp3player.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.psybm7.mp3player.MP3
import com.psybm7.mp3player.MainViewModel
import com.psybm7.mp3player.databinding.FragmentPlayerBinding

const val PROGRESS_INTERVAL: Long = 500

/**
 * A simple [Fragment] subclass.
 * Use the [PlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayerFragment : Fragment() {
    private lateinit var binding: FragmentPlayerBinding

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var progressHandler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)

        this.progressHandler = Handler(Looper.getMainLooper())

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PlayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            PlayerFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.viewModel.state.observe(this, { state -> this.setState(state) })

        viewModel.selectedMedia.observe(this, { media ->
            if (media == null) this.onMediaStop()
            else {
                binding.name = media.name
            }
        })
    }

    private val setProgressTask = object : Runnable {
        override fun run() {
            setProgress()
            progressHandler.postDelayed(this, PROGRESS_INTERVAL)
        }
    }

    private fun setProgress() {
        this.binding.progress = this.viewModel.getProgress()
    }

    private fun setState(state: MainViewModel.State) {
        when (state) {
            MainViewModel.State.DEFAULT -> this.onMediaStop()
            MainViewModel.State.PAUSED -> this.onMediaPause()
            MainViewModel.State.PLAYING -> this.onMediaPlay()
        }
    }

    private fun onMediaPlay() {
        this.binding.btPlayerAction.foreground = AppCompatResources.getDrawable(context!!, android.R.drawable.ic_media_pause)
        this.show()
        this.progressHandler.post(this.setProgressTask)
    }

    private fun onMediaPause() {
        this.binding.btPlayerAction.foreground = AppCompatResources.getDrawable(context!!, android.R.drawable.ic_media_play)
        this.show()
        this.progressHandler.removeCallbacks(this.setProgressTask)
    }

    private fun onMediaStop() {
        this.hide()
        this.progressHandler.removeCallbacks(this.setProgressTask)
    }

    private fun hide() {
        this.binding.root.visibility = View.INVISIBLE
    }

    private fun show() {
        this.binding.root.visibility = View.VISIBLE
    }
}