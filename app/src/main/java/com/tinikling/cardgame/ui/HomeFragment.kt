package com.tinikling.cardgame.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.tinikling.cardgame.R
import com.tinikling.cardgame.adapter.CardAdapter
import com.tinikling.cardgame.databinding.FragmentHomeBinding
import com.tinikling.cardgame.models.Card


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var adapter: CardAdapter
    private var cards: MutableList<Card> = mutableListOf()
    private var firstCardIndex: Int? = null // Store the index of the first flipped card
    private var points: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // 4 cards per row

        setupGame()
        Glide.with(this)
            .asGif()
            .load(R.drawable.bg)
            .into(binding.bg)
        // Initialize adapter and set it to RecyclerView
        adapter = CardAdapter(cards) { position -> onCardClicked(position) }
        binding.recyclerView.adapter = adapter
    }
    private fun setupGame() {
        // Example card images (you can use real images here)
        val images = listOf(
            R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_foreground
        ).shuffled()

        // Add card objects to the list (2 cards for each image)
        for (i in images.indices) {
            cards.add(Card(id = images[i], isFaceUp = false, isMatched = false))
        }
    }

    private fun onCardClicked(position: Int) {
        val clickedCard = cards[position]

        if (!clickedCard.isFaceUp && !clickedCard.isMatched) {
            // Flip the card
            clickedCard.isFaceUp = true
            adapter.notifyItemChanged(position)

            if (firstCardIndex == null) {
                // This is the first card flipped
                firstCardIndex = position
            } else {
                // This is the second card flipped
                checkForMatch(firstCardIndex!!, position)
                firstCardIndex = null
            }
        }
    }

    private fun checkForMatch(firstCardIndex: Int, secondCardIndex: Int) {
        val firstCard = cards[firstCardIndex]
        val secondCard = cards[secondCardIndex]

        if (firstCard.id == secondCard.id) {
            // Cards match
            firstCard.isMatched = true
            secondCard.isMatched = true
            points += 1

            // Show toast for the match and points
            Toast.makeText(requireContext(), "Match found! Points: $points", Toast.LENGTH_SHORT).show()
        } else {
            // Cards do not match, flip them back after a delay
            Handler(Looper.getMainLooper()).postDelayed({
                firstCard.isFaceUp = false
                secondCard.isFaceUp = false
                adapter.notifyItemChanged(firstCardIndex)
                adapter.notifyItemChanged(secondCardIndex)
            }, 1000)
        }
    }
}