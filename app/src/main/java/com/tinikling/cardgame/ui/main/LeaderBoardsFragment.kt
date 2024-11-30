package com.tinikling.cardgame.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.tinikling.cardgame.adapter.LeaderboardAdapter
import com.tinikling.cardgame.databinding.FragmentLeaderBoardsBinding
import com.tinikling.cardgame.models.LeaderboardEntry

class LeaderBoardsFragment : Fragment() {

    private lateinit var binding: FragmentLeaderBoardsBinding
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private val leaderboardEntries = mutableListOf<LeaderboardEntry>()
    private val timeoutHandler = Handler(Looper.getMainLooper())
    private var isDataFetched = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaderBoardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        showLoadingDialog() // Show loading dialog
        fetchLeaderboardData()

        // Set a timeout for 5 seconds to check if data has been fetched
        timeoutHandler.postDelayed({
            if (!isDataFetched) {
                showNoInternetDialog() // Show the "connect to internet" dialog
            }
        }, 5000)
    }

    private fun setupRecyclerView() {
        leaderboardAdapter = LeaderboardAdapter(leaderboardEntries)
        binding.leaderboardRecyclerView.adapter = leaderboardAdapter
        binding.leaderboardRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun fetchLeaderboardData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("leaderBoards")
            .get()
            .addOnSuccessListener { result ->
                leaderboardEntries.clear()
                for (document in result) {
                    val entry = document.toObject(LeaderboardEntry::class.java)
                    leaderboardEntries.add(entry)
                }
                leaderboardEntries.sortWith(compareByDescending<LeaderboardEntry> { it.points.toInt() }
                    .thenBy { parseTime(it.timeRemaining) })
                leaderboardAdapter.notifyDataSetChanged()
                dismissLoadingDialog() // Dismiss loading dialog
                isDataFetched = true // Mark data as fetched
            }
            .addOnFailureListener { exception ->
                dismissLoadingDialog() // Dismiss loading dialog on failure
                Toast.makeText(requireContext(), "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun parseTime(time: String): Int {
        val parts = time.split(":")
        return if (parts.size == 2) {
            try {
                val minutes = parts[0].toInt()
                val seconds = parts[1].toInt()
                minutes * 60 + seconds
            } catch (e: NumberFormatException) {
                0
            }
        } else {
            0
        }
    }

    // Function to show a loading dialog
    private fun showLoadingDialog() {
        binding.progressBar.visibility = View.VISIBLE // Show ProgressBar
    }

    // Function to dismiss the loading dialog
    private fun dismissLoadingDialog() {
        binding.progressBar.visibility = View.GONE // Hide ProgressBar
    }

    // Function to show a "No internet" dialog if data is not fetched in 5 seconds
    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No Internet Connection")
        builder.setMessage("Please connect to the internet to view the leaderboards.")
        builder.setPositiveButton("Retry") { dialog, _ ->
            dialog.dismiss()
            showLoadingDialog() // Show loading dialog again
            fetchLeaderboardData() // Retry fetching data
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}
