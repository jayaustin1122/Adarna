package com.tinikling.cardgame.ui.main

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.tinikling.cardgame.R
import com.tinikling.cardgame.databinding.DialogPlayerInputBinding
import com.tinikling.cardgame.databinding.DialogQuestionBinding
import com.tinikling.cardgame.databinding.FragmentDashBoardBinding
import com.tinikling.cardgame.ui.multiplayer.MultiPlayerFragment
import com.tinikling.cardgame.utils.DialogUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.config.PointerType


class DashBoardFragment : Fragment() {
    private lateinit var binding : FragmentDashBoardBinding
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var loadingDialog: SweetAlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashBoardBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playGif()

        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val areGuidesShown = sharedPreferences.getBoolean("areGuidesShown", false)
        if (!areGuidesShown) {
            showGuide()
        }
        binding.newRunButton.setOnClickListener {
            showDialog()
        }
        binding.sound.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.sound.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.mute))
            } else {

                mediaPlayer?.start()
                binding.sound.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.sound))
            }
        }

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.dashboard)
        mediaPlayer?.isLooping = true // To loop the music
        mediaPlayer?.start()
        binding.exitButton.setOnClickListener {
            loadingDialog = DialogUtils.showLoading(requireActivity())
            loadingDialog.show()
            lifecycleScope.launch {
                delay(2000)
                findNavController().navigate(R.id.leaderBoardsFragment)
                loadingDialog.dismiss()
            }
        }

        binding.continueButton.setOnClickListener {
            showPlayerInputDialog()
        }
        binding.exit.setOnClickListener {
            requireActivity().finishAffinity()
        }

    }
    private fun showPlayerInputDialog() {
        // Inflate the dialog's layout with ViewBinding
        val binding = DialogPlayerInputBinding.inflate(LayoutInflater.from(requireContext()))

        // Create an AlertDialog builder and set the custom view
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root) // Set the custom view
            .create() // Create the dialog instance

        // Set cancel button listener
        binding.cancel.setOnClickListener {
            dialog.dismiss() // Corrected the dismiss method
        }

        // Set start button listener
        binding.start.setOnClickListener {
            val playerNames = binding.playerNamesEditText.text.toString()
            val gameDuration = binding.gameDurationEditText.text.toString()

            if (playerNames.isNotEmpty() && gameDuration.isNotEmpty()) {
                val playerList = playerNames.split(",").map { it.trim() }
                val durationInMinutes = gameDuration.toIntOrNull()

                if (durationInMinutes != null && durationInMinutes >= 5) {
                    // Create a bundle to pass the data
                    val bundle = Bundle().apply {
                        putStringArray("playerNames", playerList.toTypedArray())
                        putInt("gameDuration", durationInMinutes)
                    }

                    // Show loading dialog
                    loadingDialog = DialogUtils.showLoading(requireActivity())
                    loadingDialog.show()

                    // Navigate to the MultiPlayerFragment
                    lifecycleScope.launch {
                        val multiPlayerFragment = MultiPlayerFragment()
                        multiPlayerFragment.arguments = bundle
                        findNavController().navigate(R.id.multiPlayerFragment, bundle)
                        loadingDialog.dismiss() // Dismiss loading dialog after navigation
                    }

                    // Dismiss the input dialog
                    dialog.dismiss()

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter a game duration of 5 seconds or more.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enter both player names and game duration.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Show the dialog
        dialog.show()
    }



    private fun playGif() {
        val torchViews = listOf(binding.torch1, binding.torch2, binding.torch3, binding.torch4)
        torchViews.forEach { torchView ->
            Glide.with(this)
                .asGif()
                .load(R.drawable.torch)
                .into(torchView)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
    private fun showDialog() {
        val defendDialogBinding =
            DialogQuestionBinding.inflate(LayoutInflater.from(requireContext())) // Inflate a custom dialog layout for defending

        val defendDialog = AlertDialog.Builder(requireContext())
            .setView(defendDialogBinding.root)
            .create()

        defendDialogBinding.answerButton1.setOnClickListener {
            loadingDialog = DialogUtils.showLoading(requireActivity())
            loadingDialog.show()
            lifecycleScope.launch {
                delay(2000)
                findNavController().navigate(R.id.homeFragment)
                defendDialog.dismiss()
                loadingDialog.dismiss()
            }

        }
        defendDialogBinding.answerButton2.setOnClickListener {
            loadingDialog = DialogUtils.showLoading(requireActivity())
            loadingDialog.show()
            lifecycleScope.launch {
                delay(2000)
                findNavController().navigate(R.id.averageFragment)
                defendDialog.dismiss()
                loadingDialog.dismiss()
            }
        }
        defendDialogBinding.answerButton3.setOnClickListener {
            loadingDialog = DialogUtils.showLoading(requireActivity())
            loadingDialog.show()
            lifecycleScope.launch {
                delay(2000)
                findNavController().navigate(R.id.averageFragment)
                defendDialog.dismiss()
                loadingDialog.dismiss()
            }
        }

        defendDialog.show()
    }
    private fun showGuide() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        val builder = GuideView.Builder(this@DashBoardFragment.requireContext())
            .setTitle("Single Player")
            .setContentText("This is where you can Play Different levels Easy, Medium, Hard")
            .setGravity(Gravity.center)
            .setDismissType(DismissType.anywhere)
            .setPointerType(PointerType.circle)
            .setTargetView(binding.newRunButton)
            .setGuideListener { view: View ->
                showGuide2()
            }

        val guideView = builder.build()
        guideView.show()
        sharedPreferences.edit().putBoolean("areGuidesShown", true).apply()

    }

    private fun showGuide2() {

        val builder = GuideView.Builder(this@DashBoardFragment.requireContext())
            .setTitle("MultiPlayer")
            .setContentText("This is where you can Play with Your Friends")
            .setGravity(Gravity.center)
            .setDismissType(DismissType.anywhere)
            .setPointerType(PointerType.circle)
            .setTargetView(binding.continueButton)
            .setGuideListener { view: View ->
                showGuide3()
            }

        val guideView = builder.build()
        guideView.show()
    }

    private fun showGuide3() {

        val builder = GuideView.Builder(this@DashBoardFragment.requireContext())
            .setTitle("Leaderboards")
            .setContentText("This is where you can see All players Scores")
            .setGravity(Gravity.center)
            .setDismissType(DismissType.anywhere)
            .setPointerType(PointerType.circle)
            .setTargetView(binding.exitButton)
            .setGuideListener { view: View ->
                showGuide4()
            }

        val guideView = builder.build()
        guideView.show()
    }
    private fun showGuide4() {

        val builder = GuideView.Builder(this@DashBoardFragment.requireContext())
            .setTitle("Music")
            .setContentText("This is where you can On or Off Music")
            .setGravity(Gravity.center)
            .setDismissType(DismissType.anywhere)
            .setPointerType(PointerType.circle)
            .setTargetView(binding.sound)
            .setGuideListener { view: View ->

            }

        val guideView = builder.build()
        guideView.show()
    }

}