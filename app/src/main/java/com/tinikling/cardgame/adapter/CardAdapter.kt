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

    private var isClickable: Boolean = true // Default to true, meaning clickable

    inner class CardViewHolder(private val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card, position: Int) {
            // Show the image or description based on whether the card is face up
            if (card.isFaceUp || card.isMatched) {
                if (card.id != null) {
                    binding.cardImage.setImageResource(card.id)
                    binding.title.text = card.name
                    binding.title.visibility = View.VISIBLE
                    binding.cardDescription.visibility = View.GONE
                } else {
                    binding.title.visibility = View.GONE
                    binding.cardImage.setImageResource(R.drawable.bg)
                    binding.cardDescription.visibility = View.VISIBLE
                    binding.cardDescription.text = card.description
                }
            } else {
                binding.cardImage.setImageResource(R.drawable.cardback)
                binding.cardDescription.visibility = View.GONE
                binding.title.visibility = View.GONE
            }

            // Set click listener for card flip animation
            if (isClickable) {
                binding.cardView.setOnClickListener {
                    if (!card.isFaceUp && !card.isMatched) {
                        flipCard(binding.cardImage, card) {
                            // After flip, notify the adapter about the click event
                            cardClickListener(position)  // Use position here
                        }
                    }
                }
            } else {
                // Disable click event if it's not the player's turn
                binding.cardView.setOnClickListener(null)
            }
        }
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position], position)  // Pass position explicitly to bind method
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }


    override fun getItemCount(): Int = cards.size

    // Function to enable/disable clicks based on the player's turn
    fun setClickable(isClickable: Boolean) {
        this.isClickable = isClickable
        notifyDataSetChanged() // Notify the adapter that the clickable state has changed
    }
}
