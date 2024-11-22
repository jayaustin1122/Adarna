package com.tinikling.cardgame.adapter

import android.view.LayoutInflater
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
            // If the card is face up or matched, show its front; otherwise, show the back
            if (card.isFaceUp || card.isMatched) {
                binding.cardImage.setImageResource(card.id) // Show the card's front
            } else {
                binding.cardImage.setImageResource(R.drawable.cardback) // Show the card's back
            }

            // Set click listener with card flip animation
            binding.cardView.setOnClickListener {
                if (!card.isFaceUp && !card.isMatched) {
                    flipCard(binding.cardImage, card) {
                        // After the flip, notify the adapter about the click event
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
