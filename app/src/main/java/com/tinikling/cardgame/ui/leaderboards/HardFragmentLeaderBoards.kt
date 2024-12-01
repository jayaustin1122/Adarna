package com.tinikling.cardgame.ui.leaderboards

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.tinikling.cardgame.R
import com.tinikling.cardgame.adapter.LeaderboardAdapter
import com.tinikling.cardgame.databinding.FragmentEasyBinding
import com.tinikling.cardgame.databinding.FragmentHardLeaderBoardsBinding


class HardFragmentLeaderBoards : Fragment() {
    private lateinit var binding: FragmentHardLeaderBoardsBinding
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private val leaderboardEntries = mutableListOf<Map<String, Any>>()
    private val timeoutHandler = Handler(Looper.getMainLooper())
    private var isDataFetched = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHardLeaderBoardsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        showLoadingDialog()
        fetchLeaderboardData()
        timeoutHandler.postDelayed({
            if (!isDataFetched) {
                showNoInternetDialog()
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
        Log.d("Leaderboard", "Fetching leaderboard data without filter...")

        db.collection("leaderBoards")
            // Comment out the level filter to check if it retrieves data
            .whereEqualTo("level", "hard")
            .get()
            .addOnSuccessListener { result ->
                Log.d("Leaderboard", "Successfully fetched ${result.size()} documents.")
                leaderboardEntries.clear()

                for (document in result) {
                    // Extract data manually from the document
                    val points = document.getString("points")?: ""
                    val timeRemaining = document.getString("timeRemaining") ?: ""
                    val name = document.getString("name") ?: ""
                    val level = document.getString("level") ?: ""

                    Log.d("Leaderboard", "Name: $name, Points: $points, Time Remaining: $timeRemaining, level $level")

                    // Create a map or any structure to store this data
                    val entry = mapOf(
                        "points" to points,
                        "timeRemaining" to timeRemaining,
                        "name" to name
                    )

                    leaderboardEntries.add(entry)
                }

                // Sorting manually based on points and timeRemaining
                leaderboardEntries.sortWith(
                    compareByDescending<Map<String, Any>> { it["points"] as String }
                        .thenBy { parseTime(it["timeRemaining"] as String) }
                )

                Log.d("Leaderboard", "Data sorted successfully.")
                leaderboardAdapter.notifyDataSetChanged()
                dismissLoadingDialog() // Dismiss loading dialog
                isDataFetched = true // Mark data as fetched
                Log.d("Leaderboard", "Data fetch and update complete.")
            }
            .addOnFailureListener { exception ->
                dismissLoadingDialog() // Dismiss loading dialog on failure
                Log.e("Leaderboard", "Error fetching data: ${exception.message}")
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