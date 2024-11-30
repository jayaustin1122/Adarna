package com.tinikling.cardgame.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinikling.cardgame.databinding.ItemCardBinding
import com.tinikling.cardgame.models.Card
import com.tinikling.cardgame.R
import com.tinikling.cardgame.utils.flipCard
class CardAdapter(
    private val cards: List<Card>,
    private val cardClickListener: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(private val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card) {
            // Show the image or description based on whether the card is face up
            if (card.isFaceUp || card.isMatched) {
                // If image is available, show it; otherwise, show the description
                if (card.id != null) {
                    binding.cardImage.setImageResource(card.id) // Show the image if available
                    binding.title.text = card.name
                    binding.title.visibility = View.VISIBLE
                    binding.cardDescription.visibility = View.GONE
                } else {
                    binding.title.visibility = View.GONE
                    binding.cardImage.setImageResource(R.drawable.bg) // Set a default card back image
                    binding.cardDescription.visibility = View.VISIBLE
                    binding.cardDescription.text = card.description // Show the description if image is null
                }
            } else {
                binding.cardImage.setImageResource(R.drawable.cardback) // Show the card back when it's not face up
                binding.cardDescription.visibility = View.GONE // Hide the description when the card is not flipped
                binding.title.visibility = View.GONE
            }

            // Set click listener for card flip animation
            binding.cardView.setOnClickListener {
                if (!card.isFaceUp && !card.isMatched) {
                    flipCard(binding.cardImage, card) {
                        // After flip, notify the adapter about the click event
                        cardClickListener(adapterPosition)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size
}
