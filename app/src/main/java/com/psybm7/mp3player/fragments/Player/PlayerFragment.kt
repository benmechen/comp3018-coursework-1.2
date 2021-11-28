package com.psybm7.mp3player.fragments.Player

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import com.psybm7.mp3player.databinding.FragmentPlayerBinding


/**
 * Fragment to contain the Player controls
 * Communication is via the [PlayerViewModel]
 * Displays the current track name and progress, as well as media controls
 */
class PlayerFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentPlayerBinding

    /**
     * Player view model, for interaction with this Fragment's
     * parent Activity
     */
    private val viewModel: PlayerViewModel by activityViewModels()


    /**
     * Set up databinding
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PlayerFragment.
         */
        @JvmStatic
        fun newInstance() =
            PlayerFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    /**
     * Listen for updates in the view model,
     * and update the UI accordingly.
     * Register this class for callbacks to the Play/Pause button
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.viewModel.state.observe(this, { state -> this.setState(state) })
        this.viewModel.name.observe(this, { name ->
            binding.name = name
        })

        // Android progress bars only support integers, which gives it a very low resolution.
        // To make the updates smoother, the progress bar scale has been multiplied by 100.
        // This allows us to convert Double values to Integers with a higher resolution.
        this.viewModel.progress.observe(this, { progress ->
            binding.progress = (progress * 100).toInt()
        })

        this.binding.btPlayerAction.setOnClickListener(this)
    }

//    SECTION: UI Handlers
    /**
     * On Play/Pause button click
     */
    override fun onClick(view: View) {
        Log.d("PlayerFragment", "Action")
        this.viewModel.action()
    }

//    SECTION: State updates
    /**
     * Update the UI when the Player state changes
     */
    private fun setState(state: PlayerViewModel.State) {
        when (state) {
            PlayerViewModel.State.DEFAULT -> this.onMediaStop()
            PlayerViewModel.State.PAUSED -> this.onMediaPause()
            PlayerViewModel.State.PLAYING -> this.onMediaPlay()
        }
    }

    /**
     * Set the button icon to a Pause icon
     * Show the Player if hidden
     */
    private fun onMediaPlay() {
        this.binding.btPlayerAction.foreground = AppCompatResources.getDrawable(context!!, android.R.drawable.ic_media_pause)
        this.show()
    }

    /**
     * Set the button icon to a Play icon
     * Show the Player if hidden
     */
    private fun onMediaPause() {
        this.binding.btPlayerAction.foreground = AppCompatResources.getDrawable(context!!, android.R.drawable.ic_media_play)
        this.show()
    }

    /**
     * Hide the Player as nothing is playing
     */
    private fun onMediaStop() {
        this.hide()
    }

    private fun hide() {
        this.binding.root.visibility = View.INVISIBLE
    }

    private fun show() {
        this.binding.root.visibility = View.VISIBLE
    }
}