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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tinikling.cardgame.R
import com.tinikling.cardgame.databinding.DialogQuestionBinding
import com.tinikling.cardgame.databinding.FragmentDashBoardBinding


class DashBoardFragment : Fragment() {
    private lateinit var binding : FragmentDashBoardBinding
    private var mediaPlayer: MediaPlayer? = null
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
            .setCancelable(false)
            .create()

        defendDialogBinding.answerButton1.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
            defendDialog.dismiss()
        }
        defendDialogBinding.answerButton2.setOnClickListener {
            findNavController().navigate(R.id.averageFragment)
            defendDialog.dismiss()
        }
        defendDialogBinding.answerButton3.setOnClickListener {
            findNavController().navigate(R.id.hardFragment)
            defendDialog.dismiss()
        }

        defendDialog.show()
    }
}