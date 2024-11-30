package com.tinikling.cardgame.ui.level

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tinikling.cardgame.R
import com.tinikling.cardgame.adapter.CardAdapter
import com.tinikling.cardgame.databinding.FragmentHomeBinding
import com.tinikling.cardgame.models.Card


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CardAdapter
    private var cards: MutableList<Card> = mutableListOf()
    private var isCardClickable: Boolean = true
    private var firstCardIndex: Int? = null // Store the index of the first flipped card
    private var points: Int = 0
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
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
            Card("", id = null, description = "Sino ang tagapagligtas ni Don Juan matapos siyang pagtaksilan ng kanyang mga kapatid?", 35),
            Card("Ermitanyo", id = R.drawable.ermitanyo, description = "", 35),
//            Card("", id = null, description = "Sino ang hari ng Berbanya sa simula ng kwento?", 15),
//            Card("Haring Fernando", id = R.drawable.fernando, description = "Sino ang hari ng Berbanya sa simula ng kwento?", 15),

            Card("", id = null, description = "Ang hayop na nakatulong kay Don Juan sa pagkuha ng Ibong Adarna", 14),
            Card("Agila", id = R.drawable.agila, description = "Ang hayop na nakatulong kay Don Juan sa pagkuha ng Ibong Adarna", 14),

            Card("", id = null, description = "Pangunahing tauhan sa Ibong Adarna", 13),
            Card("Don Juan", id = R.drawable.juan, description = "Pangunahing tauhan sa Ibong Adarna", 13),

            Card("", id = null, description = "Awit na nagpapagaling kay Haring Fernando", 2),
            Card("Ibon Adarna", id = R.drawable.singibon, description = "Awit na nagpapagaling kay Haring Fernando", 2),

//
//            Card("", id = null, description = " Ang panganay na prinsipe na inggit sa kanyang kapatid, at nagpaplano laban kay Don Juan", 4),
//            Card("Don Pedro", id = R.drawable.pedro, description = "", 4),
//
//            Card("", id = null, description = "Ang pangalawang prinsipe na tahimik ngunit tumutulong kay Don Pedro sa kanyang mga pakana", 5),
//            Card("Don Diego", id = R.drawable.diego, description = "", 5),
//
//            Card("", id = null, description = "Ama ng tatlong prinsipe na nagkasakit at nangangailangan ng paghilom ng Ibong Adarna.  ", 7),
//            Card("Hari", id = R.drawable.hari, description = "", 7),


            Card("", id = null, description = "Ina ng tatlong prinsipe at asawa ni Haring Fernando", 8),
            Card("Reyna", id = R.drawable.reyna, description = "", 8),

            Card("", id = null, description = "Isang hari na nagbibigay ng mga pagsubok kay Don Juan", 9),
            Card("Salermo", id = R.drawable.salermo, description = "", 9),


//            Card("", id = null, description = "Ang magandang prinsesa na tumutulong kay Don Juan sa mga pagsubok at naging kanyang asawa.  ", 10),
//            Card("Maria", id = R.drawable.maria, description = "", 10),



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
                    requireActivity(), "Time’s up! Please Try Again.", Toast.LENGTH_LONG
                ).show()
                findNavController().navigateUp()
            }
        }
        countDownTimer.start()
    }


    private fun onCardClicked(position: Int) {
        if (!isCardClickable) return  // Prevent clicks while another animation or check is ongoing

        val clickedCard = cards[position]

        if (!clickedCard.isFaceUp && !clickedCard.isMatched) {
            // Flip the card
            clickedCard.isFaceUp = true
            adapter.notifyItemChanged(position)

            isCardClickable = false  // Disable further clicks until this card is processed

            if (firstCardIndex == null) {
                // This is the first card flipped
                firstCardIndex = position
                isCardClickable = true  // Re-enable clicks since only one card is flipped
            } else {
                // This is the second card flipped
                checkForMatch(firstCardIndex!!, position)
                firstCardIndex = null
            }
        }
    }
    private fun showMatchMessage() {
        val matchMessage = binding.matchMessage // Assuming you use View Binding
        matchMessage.text = if (points % 2 == 0) "Mahusay!" else "Magaling!" // Alternate between the two messages
        matchMessage.visibility = View.VISIBLE

        // Load and start the animation
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_in) // or R.anim.fade_in
        matchMessage.startAnimation(animation)

        // Hide the message after animation
        Handler(Looper.getMainLooper()).postDelayed({
            matchMessage.visibility = View.GONE
        }, 1500) // Hide message after 1.5 seconds
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
            showMatchMessage()
            Toast.makeText(requireContext(), "Match found! Points: $points", Toast.LENGTH_SHORT).show()

            // Delay to visually confirm the match before removing cards
            Handler(Looper.getMainLooper()).postDelayed({
                // Remove the matched cards from the list after delay
                cards[firstCardIndex].isFaceUp = false
                cards[secondCardIndex].isFaceUp = false

                // Ensure correct order of removal by removing higher index first
                if (firstCardIndex > secondCardIndex) {
                    cards.removeAt(firstCardIndex)
                    cards.removeAt(secondCardIndex)
                } else {
                    cards.removeAt(secondCardIndex)
                    cards.removeAt(firstCardIndex)
                }

                // Notify adapter of the card removal
                adapter.notifyItemRangeRemoved(firstCardIndex.coerceAtMost(secondCardIndex), 2)

                // Flip all remaining cards face down and reshuffle
                closeAllCardsAndReshuffle()

                isCardClickable = true // Re-enable clicks after cards are processed
            }, 1000)  // 1-second delay before removing cards
        } else {
            // Cards do not match, flip them back after a delay
            Handler(Looper.getMainLooper()).postDelayed({
                firstCard.isFaceUp = false
                secondCard.isFaceUp = false
                adapter.notifyItemChanged(firstCardIndex)
                adapter.notifyItemChanged(secondCardIndex)
                isCardClickable = true  // Re-enable clicks after flipping back
            }, 1000)  // 1-second delay before flipping them back
        }
    }
    private fun closeAllCardsAndReshuffle() {
        // Close all remaining unmatched cards
        for (i in cards.indices) {
            val card = cards[i]
            if (!card.isMatched && card.isFaceUp) {
                card.isFaceUp = false
                adapter.notifyItemChanged(i)
            }
        }

        // Reshuffle the cards
        cards.shuffle()

        // Notify the adapter that the entire dataset has changed
        adapter.notifyDataSetChanged()
    }


}