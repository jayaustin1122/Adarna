package com.tinikling.cardgame.ui.level

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.tinikling.cardgame.R
import com.tinikling.cardgame.adapter.CardAdapter
import com.tinikling.cardgame.databinding.DialogEnterNameBinding
import com.tinikling.cardgame.databinding.FragmentHomeBinding
import com.tinikling.cardgame.models.Card


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CardAdapter
    private var cards: MutableList<Card> = mutableListOf()
    private var isCardClickable: Boolean = true
    private var firstCardIndex: Int? = null // Store the index of the first flipped card
    private var points: Int = 0
    private var hints: Int = 0
    private var timeUsed: String = ""
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.points.text = "Points: $points"
        binding.recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 3) // 4 cards per row
        startTimer(3)
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
                playMatchSoundGameOver()
            }
        }
        countDownTimer.start()
    }


    private fun updateRecyclerView() {
        if (cards.isEmpty()) {
            onGameFinished(timeUsed)
            playMatchSoundWinner()
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
            playMatchSound()
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
        binding.timerText.text = "Time Ramaining: $timeUsed"

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
        // Prepare the leaderboard data
        val leaderboardData = hashMapOf(
            "name" to userName,
            "points" to points.toString(),
            "hintsUsed" to hints.toString(),
            "timeRemaining" to timeUsed,
            "level" to "easy"
        )

        // Log the data that will be uploaded
        Log.d("Leaderboard", "Saving data: $leaderboardData")

        // Get Firestore instance
        val db = FirebaseFirestore.getInstance()

        // Save the data with a document ID based on the timestamp
        db.collection("leaderBoards")
            .document(timeStamp.toString())
            .set(leaderboardData)
            .addOnSuccessListener {
                Log.d("Leaderboard", "Data successfully uploaded with timestamp: $timeStamp")

                if (isAdded) {
                    Toast.makeText(requireContext(), "Your score has been uploaded!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Leaderboard", "Error saving score: ${e.message}", e)

                if (isAdded) {
                    Toast.makeText(requireContext(), "Error saving score: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun playMatchSoundWinner() {
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.winner)
        mediaPlayer.start()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }, 2000)
    }
    private fun playMatchSound() {
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.effectmatch)
        mediaPlayer.start()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }, 2000)
    }
    private fun playMatchSoundGameOver() {
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.timeout)
        mediaPlayer.start()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }, 2000)
    }
}