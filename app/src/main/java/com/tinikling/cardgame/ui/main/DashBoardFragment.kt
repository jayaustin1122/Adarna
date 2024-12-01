package com.tinikling.cardgame.ui.main

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        binding.newRunButton.setOnClickListener {
            showDialog()
        }
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.maps)
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
    }
    private fun showPlayerInputDialog() {
        // Inflate the dialog's layout with ViewBinding
        val binding = DialogPlayerInputBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Enter Player Names and Game Duration")
            .setView(binding.root) // Set the root of the binding as the view for the dialog
            .setPositiveButton("Start Game") { dialogInterface, _ ->
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
                        loadingDialog = DialogUtils.showLoading(requireActivity())
                        loadingDialog.show()
                        lifecycleScope.launch {
                            val multiPlayerFragment = MultiPlayerFragment()
                            multiPlayerFragment.arguments = bundle
                            findNavController().navigate(R.id.multiPlayerFragment, bundle)
                            loadingDialog.dismiss()
                        }

                    } else {
                        Toast.makeText(requireContext(), "Please enter a game duration of 5 sec or more.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter both names and game duration.", Toast.LENGTH_SHORT).show()
                }

                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
            .create()

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
}