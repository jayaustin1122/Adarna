package com.tinikling.cardgame.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
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
    private lateinit var countDownTimer: CountDownTimer
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
        binding.points.text = points.toString()
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // 4 cards per row
        startTimer()
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
        // Example card data with nullable images and descriptions
        val cardData = listOf(
            Card(id = R.drawable.juan, description = "Ang prinsipe na nakahuli ng Ibong Adarna",1),
            Card(id = null, description = "Ang prinsipe na nakahuli ng Ibong Adarna",1),

            Card(id = null, description = "Awit na nagpapagaling kay Haring Fernando",2),
            Card(id = R.drawable.singibon, description = "Awit na nagpapagaling kay Haring Fernando",2),

            Card(id = null, description = "Nang mahuli ni Don Juan ang Ibong Adarna",3),
            Card(id = R.drawable.lambat, description = "Nang mahuli ni Don Juan ang Ibong Adarna",3)

            // Add more cards as needed
        ).shuffled()

        cards.addAll(cardData)
    }
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(60000, 1000) { // 1 minute
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.timerText.text = "${secondsRemaining}s"
            }

            override fun onFinish() {
                Toast.makeText(
                    requireContext(), "Time’s up! Please Try Again.", Toast.LENGTH_LONG
                ).show()
                findNavController().navigateUp()
            }
        }
        countDownTimer.start()
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

        if (firstCard.pair == secondCard.pair) {
            // Cards match
            firstCard.isMatched = true
            secondCard.isMatched = true
            points += 1
            binding.points.text = "Points $points"

            // Show toast for the match and points
            Toast.makeText(requireContext(), "Match found! Points: $points", Toast.LENGTH_SHORT).show()

            // Delay the removal of cards to allow for visual confirmation of the match
            Handler(Looper.getMainLooper()).postDelayed({
                // Remove the matched cards from the list
                if (firstCardIndex > secondCardIndex) {
                    cards.removeAt(firstCardIndex)
                    cards.removeAt(secondCardIndex)
                } else {
                    cards.removeAt(secondCardIndex)
                    cards.removeAt(firstCardIndex)
                }

                // Notify the adapter that items have been removed
                adapter.notifyItemRangeRemoved(firstCardIndex.coerceAtMost(secondCardIndex), 2)

                // Optionally, if you want to reset all card flips after a match, you can:
                adapter.notifyDataSetChanged()  // Refresh the entire RecyclerView if necessary
            }, 1000) // 2-second delay before removing cards

        } else {
            // Cards do not match, flip them back after a delay
            Handler(Looper.getMainLooper()).postDelayed({
                firstCard.isFaceUp = false
                secondCard.isFaceUp = false
                adapter.notifyItemChanged(firstCardIndex)
                adapter.notifyItemChanged(secondCardIndex)
            }, 1000) // 1-second delay before flipping them back
        }
    }



}