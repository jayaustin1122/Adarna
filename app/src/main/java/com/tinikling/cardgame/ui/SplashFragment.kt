package com.tinikling.cardgame.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tinikling.cardgame.R
import com.tinikling.cardgame.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {
    private lateinit var binding : FragmentSplashBinding
    private var progressStatus = 0
    private lateinit var handler: Handler
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        simulateProgress()
        handler.postDelayed({
            findNavController().navigate(R.id.dashBoardFragment)
        }, 5000)
    }
    private fun simulateProgress() {
        handler.post(object : Runnable {
            override fun run() {
                if (progressStatus < 100) {
                    progressStatus += 2 // Increase progress gradually
                    binding.progressBar.progress = progressStatus
                    handler.postDelayed(this, 100) // Update every 100ms
                }
            }
        })
    }
}