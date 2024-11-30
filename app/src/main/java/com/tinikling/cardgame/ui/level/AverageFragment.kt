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
import com.tinikling.cardgame.databinding.FragmentAverageBinding
import com.tinikling.cardgame.models.Card


class AverageFragment : Fragment() {
    private lateinit var binding : FragmentAverageBinding
    private lateinit var adapter: CardAdapter
    private var cards: MutableList<Card> = mutableListOf()
    private var firstCardIndex: Int? = null // Store the index of the first flipped card
    private var points: Int = 0
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAverageBinding.inflate(layoutInflater)
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

            //scene
//            Card("", id = null, description = "Ang pagpapagaling na awit ng Ibong Adarna para kay Haring Fernando.", 13),
//            Card("Mga Ibon Kumakanta", id = R.drawable.birdsinging, description = "", 13),

            Card("", id = null, description = "Ano ang papel ni Don Pedro sa kwento ng Ibong Adarna?", 16),
            Card("Kontrabida", id = R.drawable.pedro, description = "Ano ang papel ni Don Pedro sa kwento ng Ibong Adarna?", 16),


//            Card("", id = null, description = "Ano ang pangunahing misyon ni Don Juan sa kwento ng Ibong Adarna?", 17),
//            Card("Hulihin ang Ibong Adarna", id = R.drawable.ico, description = "Ano ang pangunahing misyon ni Don Juan sa kwento ng Ibong Adarna?", 17),

            Card("", id = null, description = "Ano ang ginagawa ng Ibong Adarna kapag kumakanta ito?", 18),
            Card("Pinapatulog", id = R.drawable.sleep, description = "Ano ang ginagawa ng Ibong Adarna kapag kumakanta ito?", 18),

//
//            Card("", id = null, description = "Bakit mahalaga ang papel ni Don Diego sa kwento ng Ibong Adarna?", 19),
//            Card("Siya ang nagpakita ng malasakit sa kanyang ama", id = R.drawable.diego, description = "Bakit mahalaga ang papel ni Don Diego sa kwento ng Ibong Adarna?", 19),


//            Card("", id = null, description = "Paano tinraydor ni Don Pedro si Don Juan matapos mahuli ang Ibong Adarna?", 20),
//            Card("Sinaktan niya si Don Juan at iniwan sa balon", id = R.drawable.balon, description = "Paano tinraydor ni Don Pedro si Don Juan matapos mahuli ang Ibong Adarna?", 20),

//            Card("", id = null, description = "Ano ang nagiging papel ng Ermitanyo sa buhay ni Don Juan?", 21),
//            Card("Nagbigay ng payo at mga mahiwagang gamit", id = R.drawable.advice, description = "Ano ang nagiging papel ng Ermitanyo sa buhay ni Don Juan?", 21),

            Card("", id = null, description = "Sa kabuuan ng kwento, paano nakatulong ang iba't ibang pantulong na tauhan (tulad ng mga ermitanyo at hayop) sa moral at espiritwal na paglaki ni Don Juan?", 22),
            Card("Tinuruan siya ng kababaang-loob at pagtitiwala sa Diyos", id = R.drawable.believe, description = "Sa kabuuan ng kwento, paano nakatulong ang iba't ibang pantulong na tauhan (tulad ng mga ermitanyo at hayop) sa moral at espiritwal na paglaki ni Don Juan?", 22),
            Card("", id = null, description = "Ano ang kinakanta ng Ibong Adarna upang makapagpagaling ng may sakit?", 28),
            Card("Pitong Awit", id = R.drawable.birdsinging, description = "Ano ang kinakanta ng Ibong Adarna upang makapagpagaling ng may sakit?", 28),


            Card("", id = null, description = "Ano ang nangyayari sa mga taong nahuhuli ng awit ng Ibong Adarna?", 29),
            Card("Nagiging bato", id = R.drawable.stone, description = "Ano ang nangyayari sa mga taong nahuhuli ng awit ng Ibong Adarna?", 29),


//            Card("", id = null, description = "Ano ang pangunahing layunin ng kwento ng Ibong Adarna?", 31),
//            Card("Ituro ang halaga ng pagtitiis at kabutihan", id = R.drawable.bg, description = "Ano ang pangunahing layunin ng kwento ng Ibong Adarna?", 31),

            Card("", id = null, description = "Saan nagmula ang Ibong Adarna ayon sa kwento?", 32),
            Card("Puno ng Kabutihan", id = R.drawable.bg, description = "Saan nagmula ang Ibong Adarna ayon sa kwento?", 32),




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