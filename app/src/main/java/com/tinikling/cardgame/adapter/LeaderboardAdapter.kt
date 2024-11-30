package com.tinikling.cardgame.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tinikling.cardgame.R
import com.tinikling.cardgame.databinding.ItemLeaderboardBinding
import com.tinikling.cardgame.models.LeaderboardEntry

class LeaderboardAdapter(private val leaderboardEntries: List<LeaderboardEntry>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    // ViewHolder class with ViewBinding
    class LeaderboardViewHolder(val binding: ItemLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        // Inflate the item layout using ViewBinding
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val entry = leaderboardEntries[position]
        holder.binding.apply {
            rankTextView.text = (position + 1).toString()  // Rank starts from 1
            nameTextView.text = entry.name
            pointsTextView.text = entry.points
            timeUsedTextView.text = "${entry.timeRemaining}s"

            // Set background color based on rank
            when (position) {
                0 -> {  // First place, Gold
                    card.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.gold))
                }
                1 -> {  // Second place, Silver
                    card.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.silver))
                }
                2 -> {  // Third place, Bronze
                    card.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.bronze))
                }
                else -> {  // Default background for other ranks
                    card.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.default_card_bg))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return leaderboardEntries.size
    }
}
