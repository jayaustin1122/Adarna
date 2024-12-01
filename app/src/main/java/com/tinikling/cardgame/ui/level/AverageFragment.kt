package com.tinikling.cardgame.ui.level

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.google.firebase.firestore.FirebaseFirestore
import com.tinikling.cardgame.R
import com.tinikling.cardgame.adapter.CardAdapter
import com.tinikling.cardgame.databinding.DialogEnterNameBinding
import com.tinikling.cardgame.databinding.FragmentAverageBinding
import com.tinikling.cardgame.models.Card


class AverageFragment : Fragment() {
    private lateinit var binding : FragmentAverageBinding
    private lateinit var adapter: CardAdapter
    private var cards: MutableList<Card> = mutableListOf()
    private var isCardClickable: Boolean = true
    private var firstCardIndex: Int? = null // Store the index of the first flipped card
    private var points: Int = 0
    private var hints: Int = 0
    private var timeUsed: String = ""
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAverageBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.points.text = "Points: $points"
        binding.recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 3) // 4 cards per row
        startTimer(2)
        setupGame()

        binding.hints.setOnClickListener {
            if (points % 2 == 0 && points != 0) {
                showHintForMatchingCards() // Show hint if points are divisible by 2, 4, or 6
                hints++
            } else {
                Toast.makeText(
                    requireContext(),
                    "You can use hints after matching 2 sets of cards.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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

    private fun startTimer(durationInMinutes: Int) {
        // Convert minutes to milliseconds
        val durationInMillis = durationInMinutes * 60000L

        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutesRemaining = secondsRemaining / 60
                val secondsRemainingInMinute = secondsRemaining % 60

                // Show time in format: MM:SS
                binding.timerText.text = String.format("%02d:%02d", minutesRemaining, secondsRemainingInMinute)
                timeUsed = "$minutesRemaining:$secondsRemainingInMinute"
            }

            override fun onFinish() {
                if (isAdded) {
                    Toast.makeText(
                        requireActivity(), "Time’s up! Please Try Again.", Toast.LENGTH_LONG
                    ).show()
                }
                findNavController().navigateUp()
                onGameFinished(timeUsed)
            }
        }
        countDownTimer.start()
    }


    private fun updateRecyclerView() {
        if (cards.isEmpty()) {
            onGameFinished(timeUsed)
        } else {
            adapter.notifyDataSetChanged()
        }
    }

    private fun onCardClicked(position: Int) {
        if (!isCardClickable) return
        val clickedCard = cards[position]

        if (!clickedCard.isFaceUp && !clickedCard.isMatched) {

            clickedCard.isFaceUp = true
            adapter.notifyItemChanged(position)

            isCardClickable = false

            if (firstCardIndex == null) {

                firstCardIndex = position
                isCardClickable = true
            } else {

                checkForMatch(firstCardIndex!!, position)
                firstCardIndex = null
            }
        }
    }
    private fun showMatchMessage() {
        val matchMessage = binding.matchMessage

        val messages = listOf("Mahusay!", "Magaling!", "Ang galing mo!", "Excellent!", "Great job!", "Tama yan!")

        val selectedMessage = when {
            points >= 10 -> "Astig ka!"
            points % 2 == 0 -> messages[0]
            points % 3 == 0 -> messages[1]
            else -> messages.random()
        }


        matchMessage.text = selectedMessage
        matchMessage.visibility = View.VISIBLE

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_in)
        matchMessage.startAnimation(animation)

        Handler(Looper.getMainLooper()).postDelayed({
            matchMessage.visibility = View.GONE
        }, 1500)
    }

    private fun showHintForMatchingCards() {

        for (i in cards.indices) {
            val card1 = cards[i]
            if (!card1.isMatched) {
                for (j in i + 1 until cards.size) {
                    val card2 = cards[j]
                    if (card1.pair == card2.pair && !card2.isMatched) {

                        shakeCardAtIndex(i)
                        shakeCardAtIndex(j)
                        return
                    }
                }
            }
        }
    }

    private fun shakeCardAtIndex(index: Int) {
        val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(index)
        viewHolder?.itemView?.let { cardView ->
            val shakeAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
            cardView.startAnimation(shakeAnimation)
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
            showMatchMessage()
            Toast.makeText(requireContext(), "Match found! Points: $points", Toast.LENGTH_SHORT).show()
            updateRecyclerView()
            Handler(Looper.getMainLooper()).postDelayed({
                cards[firstCardIndex].isFaceUp = false
                cards[secondCardIndex].isFaceUp = false
                if (firstCardIndex > secondCardIndex) {
                    cards.removeAt(firstCardIndex)
                    cards.removeAt(secondCardIndex)
                } else {
                    cards.removeAt(secondCardIndex)
                    cards.removeAt(firstCardIndex)
                }
                adapter.notifyItemRangeRemoved(firstCardIndex.coerceAtMost(secondCardIndex), 2)
                closeAllCardsAndReshuffle()

                isCardClickable = true
            }, 1000)
        } else {
            updateRecyclerView()

            Handler(Looper.getMainLooper()).postDelayed({
                firstCard.isFaceUp = false
                secondCard.isFaceUp = false
                adapter.notifyItemChanged(firstCardIndex)
                adapter.notifyItemChanged(secondCardIndex)
                isCardClickable = true
            }, 1000)
        }
    }
    private fun closeAllCardsAndReshuffle() {
        updateRecyclerView()
        // Close all remaining unmatched cards
        for (i in cards.indices) {
            val card = cards[i]
            if (!card.isMatched && card.isFaceUp) {
                card.isFaceUp = false
                adapter.notifyItemChanged(i)
            }
        }
        cards.shuffle()
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("MissingInflatedId")
    private fun onGameFinished(timeUsed: String) {
        if (!isAdded || activity == null) {
            // If the fragment is not attached, don't proceed with showing dialog or navigation
            return
        }

        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val binding = DialogEnterNameBinding.inflate(inflater)
        builder.setView(binding.root)

        // Set the text for the views using View Binding
        binding.pointsTextView.text = "Points: $points"
        binding.hintUsedTextView.text = "Hints Used: $hints"
        binding.timerText.text = "Time Remaining: $timeUsed"

        val dialog = builder.create()

        // Handle submit button click
        binding.buttonSubmit.setOnClickListener {
            val playerName = binding.editTextPlayerName.text.toString()

            if (playerName.isBlank()) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Please enter your name to continue.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Save leaderboard if name is valid
                saveLeaderboard(playerName, timeUsed)
                if (isAdded) {
                    findNavController().navigateUp()
                }
                dialog.dismiss()
            }
        }

        if (isAdded && !dialog.isShowing) {
            dialog.show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }

    override fun onStop() {
        super.onStop()
        countDownTimer?.cancel()
    }
    private fun saveLeaderboard(userName: String, timeUsed: String) {
        val timeStamp = System.currentTimeMillis()
        val leaderboardData = hashMapOf(
            "name" to userName,
            "points" to points.toString(),
            "hintsUsed" to hints.toString(),
            "timeRemaining" to timeUsed.toString(),
            "level" to "average"
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("leaderBoards")
            .document(timeStamp.toString())
            .set(leaderboardData)
            .addOnSuccessListener {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Your score has been Uploaded!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error saving score: $e", Toast.LENGTH_SHORT).show()
                }
            }
    }


}