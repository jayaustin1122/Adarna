package com.tinikling.cardgame.ui.level

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
import com.tinikling.cardgame.R
import com.tinikling.cardgame.adapter.CardAdapter
import com.tinikling.cardgame.databinding.FragmentHardBinding
import com.tinikling.cardgame.models.Card


class HardFragment : Fragment() {
    private lateinit var binding : FragmentHardBinding
    private lateinit var adapter: CardAdapter
    private var cards: MutableList<Card> = mutableListOf()
    private var firstCardIndex: Int? = null // Store the index of the first flipped card
    private var points: Int = 0
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHardBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.points.text = points.toString()
        binding.recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 3) // 4 cards per row
        startTimer()
        setupGame()
        // Glide.with(this).asGif().load(R.drawable.bg).into(binding.bg)
        // Initialize adapter and set it to RecyclerView
        adapter = CardAdapter(cards) { position -> onCardClicked(position) }
        binding.recyclerView.adapter = adapter
    }
    private fun setupGame() {
        // Example card data with nullable images and descriptions
        val cardData = listOf(

            //simbolo
            Card("", id = null, description = "Ang pagtataksil ng magkapatid (si Don Pedro at Don Diego na itinali si Don Juan sa puno.", 11),
            Card("Tali", id = R.drawable.tali, description = "", 11),
            Card("", id = null, description = "Ano ang ginagamit ni Don Juan upang hindi siya makatulog habang hinihintay ang Ibong Adarna?", 33),
            Card("Sibat", id = R.drawable.sibat, description = "", 33),

            Card("", id = null, description = "Ilang beses nagpalit ng kulay ang Ibong Adarna habang kumakanta?", 34),
            Card("Pito", id = R.drawable.bg, description = "", 34),

            Card("", id = null, description = "Sumisimbolo ng kapahamakan ng isang tao", 36),
            Card("Singsing", id = R.drawable.singsing, description = "", 36),



            Card("", id = null, description = "Ano ang sumisimbolo sa katuparan ng pangarap o tagumpay?.  ", 30),
            Card("Ibon", id = R.drawable.singibon, description = "Ano ang sumisimbolo sa katuparan ng pangarap o tagumpay?", 30),

            Card("", id = null, description = "Nang matagpuan ni Don Juan ang mahiwagang balon at tumalon dito.  ", 12),
            Card("Balon", id = R.drawable.balon, description = "", 12),

//            Card("", id = null, description = "Nang mahuli ni Don Juan ang Ibong Adarna", 3),
//            Card("Lambat", id = R.drawable.lambat, description = "Nang mahuli ni Don Juan ang Ibong Adarna", 3),
//
//            Card("", id = null, description = "Saan nagmula ang kwento ng Ibong Adarna?", 23),
//            Card("Europa", id = R.drawable.europa, description = "Saan nagmula ang kwento ng Ibong Adarna?", 23),
//
//            Card("", id = null, description = "Ilan ang magkakapatid na prinsipe sa kwento ng Ibong Adarna?", 24),
//            Card("3", id = R.drawable.three, description = "Ilan ang magkakapatid na prinsipe sa kwento ng Ibong Adarna?", 24),
//
//            Card("", id = null, description = "Ano ang pangalan ng pinakamagiting na prinsipe sa kwento ng Ibong Adarna?", 25),
//            Card("Don Juan", id = R.drawable.three, description = "Ano ang pangalan ng pinakamagiting na prinsipe sa kwento ng Ibong Adarna?", 25),
//
//            Card("", id = null, description = "Sa anong lugar naninirahan ang Ibong Adarna?", 26),
//            Card("Bundok Tabor", id = R.drawable.tabor, description = "Sa anong lugar naninirahan ang Ibong Adarna?", 26),
//
//            Card("", id = null, description = "Ano ang dahilan ng pagpapadala ni Haring Fernando sa kanyang mga anak upang hanapin ang Ibong Adarna?", 27),
//            Card("karamdaman siya na hindi gumagaling", id = R.drawable.fernando, description = "Ano ang dahilan ng pagpapadala ni Haring Fernando sa kanyang mga anak upang hanapin ang Ibong Adarna?", 27),
//



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
            Toast.makeText(requireContext(), "Match found! Points: $points", Toast.LENGTH_SHORT)
                .show()

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
                //addNewPair()
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