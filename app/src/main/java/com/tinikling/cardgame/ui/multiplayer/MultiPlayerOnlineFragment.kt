package com.tinikling.cardgame.ui.multiplayer

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tinikling.cardgame.R
import com.tinikling.cardgame.adapter.CardAdapter
import com.tinikling.cardgame.databinding.DialogendgameBinding
import com.tinikling.cardgame.databinding.FragmentMultiPlayerOnlineBinding
import com.tinikling.cardgame.models.Card
import com.tinikling.cardgame.utils.DialogUtils

class MultiPlayerOnlineFragment : Fragment() {
    private lateinit var binding: FragmentMultiPlayerOnlineBinding
    private lateinit var database: DatabaseReference
    private var points: Int = 0
    private lateinit var auth: FirebaseAuth
    private lateinit var playerNames: Array<String>
    private var currentPlayerIndex = 0
    private var gameDuration = 60
    private var gameId: String? = null
    private var nameUser: String? = null
    private var timerRunning = true
    private var playerPoints = mutableMapOf<String, Int>()
    private lateinit var adapter: CardAdapter
    private var cards: MutableList<Card> = mutableListOf()
    private var isCardClickable: Boolean = true
    private var firstCardIndex: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMultiPlayerOnlineBinding.inflate(layoutInflater)
        // Initialize Firebase instances


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        playerNames = arguments?.getStringArray("playerNames") ?: arrayOf()
        gameId = arguments?.getString("gameId")
        nameUser = arguments?.getString("playerName")
        Log.d("send", "from multiplayer $nameUser")

        settingUpGame()
        startGameTimer()
        for (player in playerNames) {
            playerPoints[player] = 0
        }
        startGame()
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        setupGame()
        adapter = CardAdapter(cards) { position ->
            onCardClicked(position)
            select()
        }
        binding.recyclerView.adapter = adapter

    }

    private fun settingUpGame() {
        val progressDialog = DialogUtils.showLoading(activity)
        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()  // Dismiss the dialog after 10 seconds
        }, 10000) // 10,000 milliseconds = 10 seconds
    }

    private fun setupGame() {
        val cardData = listOf(
            Card("", id = null, description = "Saan nagmula ang Ibong Adarna ayon sa kwento?", 32),
            Card("Puno ng Kabutihan", id = R.drawable.bg, description = "Saan nagmula ang Ibong Adarna ayon sa kwento?", 32),
            Card("", id = null, description = "Ang pagtataksil ng magkapatid (si Don Pedro at Don Diego na itinali si Don Juan sa puno.", 11),
            Card("Tali", id = R.drawable.tali, description = "", 11),
            Card("", id = null, description = "Ano ang ginagamit ni Don Juan upang hindi siya makatulog habang hinihintay ang Ibong Adarna?", 33),
            Card("Sibat", id = R.drawable.sibat, description = "", 33),
            Card("", id = null, description = "Sino ang tagapagligtas ni Don Juan matapos siyang pagtaksilan ng kanyang mga kapatid?", 35),
            Card("Ermitanyo", id = R.drawable.ermitanyo, description = "", 35),
            Card("", id = null, description = "Sino ang hari ng Berbanya sa simula ng kwento?", 15),
            Card("Haring Fernando", id = R.drawable.fernando, description = "Sino ang hari ng Berbanya sa simula ng kwento?", 15),


            Card("", id = null, description = "Ano ang nangyayari sa mga taong nahuhuli ng awit ng Ibong Adarna?", 29),
            Card("Nagiging bato", id = R.drawable.stone, description = "Ano ang nangyayari sa mga taong nahuhuli ng awit ng Ibong Adarna?", 29),


            ).shuffled()

        cards.addAll(cardData)
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
    private fun checkForMatch(firstCardIndex: Int, secondCardIndex: Int) {
        val firstCard = cards[firstCardIndex]
        val secondCard = cards[secondCardIndex]

        if (firstCard.pair == secondCard.pair) {
            // Cards match
            firstCard.isMatched = true
            secondCard.isMatched = true
            points += 1
            playerPoints[playerNames[currentPlayerIndex]] = playerPoints.getOrDefault(playerNames[currentPlayerIndex], 0) + 1
            playMatchSound()
            Toast.makeText(requireContext(), "Match found! Points: $points", Toast.LENGTH_SHORT).show()
            updateRecyclerView()
            addNewCardPair()
            adapter.notifyDataSetChanged()
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
    private fun addNewCardPair() {
        adapter.notifyDataSetChanged()

        val newCardPair = mutableListOf(
            Card("", id = null, description = "Ilang beses nagpalit ng kulay ang Ibong Adarna habang kumakanta?", 34),
            Card("Pito", id = R.drawable.pito, description = "", 34),

            Card("", id = null, description = "Sumisimbolo ng kapahamakan ng isang tao", 36),
            Card("Singsing", id = R.drawable.singsing, description = "", 36),



            Card("", id = null, description = "Ano ang sumisimbolo sa katuparan ng pangarap o tagumpay?.  ", 30),
            Card("Ibon", id = R.drawable.singibon, description = "Ano ang sumisimbolo sa katuparan ng pangarap o tagumpay?", 30),

            Card("", id = null, description = "Nang matagpuan ni Don Juan ang mahiwagang balon at tumalon dito.  ", 12),
            Card("Balon", id = R.drawable.balon, description = "", 12),

            Card("", id = null, description = "Nang mahuli ni Don Juan ang Ibong Adarna", 3),
            Card("Lambat", id = R.drawable.lambat, description = "Nang mahuli ni Don Juan ang Ibong Adarna", 3),

            Card("", id = null, description = "Saan nagmula ang kwento ng Ibong Adarna?", 23),
            Card("Europa", id = R.drawable.europa, description = "Saan nagmula ang kwento ng Ibong Adarna?", 23),

            Card("", id = null, description = "Ilan ang magkakapatid na prinsipe sa kwento ng Ibong Adarna?", 24),
            Card("3", id = R.drawable.three, description = "Ilan ang magkakapatid na prinsipe sa kwento ng Ibong Adarna?", 24),

            Card("", id = null, description = "Ano ang pangalan ng pinakamagiting na prinsipe sa kwento ng Ibong Adarna?", 25),
            Card("Don Juan", id = R.drawable.three, description = "Ano ang pangalan ng pinakamagiting na prinsipe sa kwento ng Ibong Adarna?", 25),

            Card("", id = null, description = "Sa anong lugar naninirahan ang Ibong Adarna?", 26),
            Card("Bundok Tabor", id = R.drawable.tabor, description = "Sa anong lugar naninirahan ang Ibong Adarna?", 26),

            Card("", id = null, description = "Ano ang dahilan ng pagpapadala ni Haring Fernando sa kanyang mga anak upang hanapin ang Ibong Adarna?", 27),
            Card("karamdaman siya na hindi gumagaling", id = R.drawable.fernando, description = "Ano ang dahilan ng pagpapadala ni Haring Fernando sa kanyang mga anak upang hanapin ang Ibong Adarna?", 27),


            Card("", id = null, description = "Ang pagpapagaling na awit ng Ibong Adarna para kay Haring Fernando.", 13),
            Card("Mga Ibon Kumakanta", id = R.drawable.birdsinging, description = "", 13),

            Card("", id = null, description = "Ano ang papel ni Don Pedro sa kwento ng Ibong Adarna?", 16),
            Card("Kontrabida", id = R.drawable.pedro, description = "Ano ang papel ni Don Pedro sa kwento ng Ibong Adarna?", 16),


            Card("", id = null, description = "Ano ang pangunahing misyon ni Don Juan sa kwento ng Ibong Adarna?", 17),
            Card("Hulihin ang Ibong Adarna", id = R.drawable.ico, description = "Ano ang pangunahing misyon ni Don Juan sa kwento ng Ibong Adarna?", 17),

            Card("", id = null, description = "Ano ang ginagawa ng Ibong Adarna kapag kumakanta ito?", 18),
            Card("Pinapatulog", id = R.drawable.sleep, description = "Ano ang ginagawa ng Ibong Adarna kapag kumakanta ito?", 18),


            Card("", id = null, description = "Bakit mahalaga ang papel ni Don Diego sa kwento ng Ibong Adarna?", 19),
            Card("Siya ang nagpakita ng malasakit sa kanyang ama", id = R.drawable.diego, description = "Bakit mahalaga ang papel ni Don Diego sa kwento ng Ibong Adarna?", 19),


            Card("", id = null, description = "Paano tinraydor ni Don Pedro si Don Juan matapos mahuli ang Ibong Adarna?", 20),
            Card("Sinaktan niya si Don Juan at iniwan sa balon", id = R.drawable.balon, description = "Paano tinraydor ni Don Pedro si Don Juan matapos mahuli ang Ibong Adarna?", 20),

            Card("", id = null, description = "Ano ang nagiging papel ng Ermitanyo sa buhay ni Don Juan?", 21),
            Card("Nagbigay ng payo at mga mahiwagang gamit", id = R.drawable.advice, description = "Ano ang nagiging papel ng Ermitanyo sa buhay ni Don Juan?", 21),

            Card("", id = null, description = "Sa kabuuan ng kwento, paano nakatulong ang iba't ibang pantulong na tauhan (tulad ng mga ermitanyo at hayop) sa moral at espiritwal na paglaki ni Don Juan?", 22),
            Card("Tinuruan siya ng kababaang-loob at pagtitiwala sa Diyos", id = R.drawable.believe, description = "Sa kabuuan ng kwento, paano nakatulong ang iba't ibang pantulong na tauhan (tulad ng mga ermitanyo at hayop) sa moral at espiritwal na paglaki ni Don Juan?", 22),
            Card("", id = null, description = "Ano ang kinakanta ng Ibong Adarna upang makapagpagaling ng may sakit?", 28),
            Card("Pitong Awit", id = R.drawable.birdsinging, description = "Ano ang kinakanta ng Ibong Adarna upang makapagpagaling ng may sakit?", 28),

            Card("", id = null, description = "Ang hayop na nakatulong kay Don Juan sa pagkuha ng Ibong Adarna", 14),
            Card("Agila", id = R.drawable.agila, description = "Ang hayop na nakatulong kay Don Juan sa pagkuha ng Ibong Adarna", 14),

            Card("", id = null, description = "Pangunahing tauhan sa Ibong Adarna", 13),
            Card("Don Juan", id = R.drawable.juan, description = "Pangunahing tauhan sa Ibong Adarna", 13),

            Card("", id = null, description = "Awit na nagpapagaling kay Haring Fernando", 2),
            Card("Ibon Adarna", id = R.drawable.singibon, description = "Awit na nagpapagaling kay Haring Fernando", 2),


            Card("", id = null, description = " Ang panganay na prinsipe na inggit sa kanyang kapatid, at nagpaplano laban kay Don Juan", 4),
            Card("Don Pedro", id = R.drawable.pedro, description = "", 4),

            Card("", id = null, description = "Ang pangalawang prinsipe na tahimik ngunit tumutulong kay Don Pedro sa kanyang mga pakana", 5),
            Card("Don Diego", id = R.drawable.diego, description = "", 5),

            Card("", id = null, description = "Ama ng tatlong prinsipe na nagkasakit at nangangailangan ng paghilom ng Ibong Adarna.  ", 7),
            Card("Hari", id = R.drawable.hari, description = "", 7),


            Card("", id = null, description = "Ina ng tatlong prinsipe at asawa ni Haring Fernando", 8),
            Card("Reyna", id = R.drawable.reyna, description = "", 8),

            Card("", id = null, description = "Isang hari na nagbibigay ng mga pagsubok kay Don Juan", 9),
            Card("Salermo", id = R.drawable.salermo, description = "", 9),


            Card("", id = null, description = "Ang magandang prinsesa na tumutulong kay Don Juan sa mga pagsubok at naging kanyang asawa.  ", 10),
            Card("Maria", id = R.drawable.maria, description = "", 10),

            )

        // Check for already existing matching pairs in the existing cards
        val matchingCards = mutableListOf<Card>()
        val indicesToRemove = mutableListOf<Int>()

        for (i in newCardPair.indices) {
            for (j in i + 1 until newCardPair.size) {
                if (newCardPair[i].pair == newCardPair[j].pair) {
                    matchingCards.add(newCardPair[i])
                    matchingCards.add(newCardPair[j])

                    indicesToRemove.add(i)
                    indicesToRemove.add(j)
                    break
                }
            }
            if (matchingCards.size >= 2) break
        }

        // Check if the matching cards already exist in the game
        val duplicateFound = cards.any { card -> matchingCards.contains(card) }
        if (duplicateFound) {
            Log.d("MultiPlayerFragment", "Duplicate cards not added")
        } else {
            if (matchingCards.size == 2) {
                cards.addAll(matchingCards)
                val startIndex = cards.size - matchingCards.size
                adapter.notifyItemRangeInserted(startIndex, matchingCards.size)

                indicesToRemove.sortedDescending().forEach { index ->
                    newCardPair.removeAt(index)
                }
            } else {
                Log.d("MultiPlayerFragment", "No matching pair found")
            }
        }
    }
    private fun updateRecyclerView() {
        if (cards.isEmpty()) {
            endGame()
        } else {
            adapter.notifyDataSetChanged()
        }
    }
    private fun endGame() {
        findNavController().navigate(R.id.dashBoardFragment)
        val highestScore = playerPoints.values.maxOrNull() ?: 0
        val winners = playerPoints.filter { it.value == highestScore }.keys

        var scoresMessage = "Game Over!\n"
        playerPoints.forEach { (player, score) ->
            scoresMessage += "$player: $score points\n"
        }

        scoresMessage += if (winners.size > 1) {
            "\nWinners: ${winners.joinToString(", ")} with $highestScore points!"
        } else {
            "\nWinner: ${winners.first()} with $highestScore points!"
        }

        val binding = DialogendgameBinding.inflate(LayoutInflater.from(requireContext()))
        binding.winners.text = scoresMessage

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        binding.okay.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun startGameTimer() {
        val countDownTimer = object : CountDownTimer(gameDuration.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update timer display every second
                val secondsLeft = millisUntilFinished / 1000
                val minutes = secondsLeft / 60
                val seconds = secondsLeft % 60
                binding.timerText.text = "${minutes}:${String.format("%02d", seconds)}"

                if (secondsLeft % 10 == 0L) {
                    updateTurnInFirebase()
                }

            }

            override fun onFinish() {
                Toast.makeText(context, "Game Over!", Toast.LENGTH_SHORT).show()
                deleteGameRoom()
                endGame()
                shakeRecyclerView()

            }
        }
        countDownTimer.start()

        // Start listening to changes in player turns
        listenForPlayerTurnUpdates()
    }

    private fun deleteGameRoom() {
        if (gameId != null) {
            // Delete the current game room from Firebase
            database.child("rooms").child(gameId!!)
                .removeValue()
                .addOnSuccessListener {
                    Log.d("MultiPlayerOnline", "Game room deleted successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("MultiPlayerOnline", "Failed to delete game room: ${exception.message}")
                }
        }
    }


    private fun startGame() {
        if (gameId != null) {
            // Automatically set the first player's turn
            database.child("rooms").child(gameId!!).child("turns")
                .setValue(playerNames!![0])

            // Initialize the currentPlayerIndex to start with the first player
            currentPlayerIndex = 0
        }
    }

    private fun updateTurnInFirebase() {
        if (gameId != null) {
            // Set the current player's email as the turn in Firebase
            database.child("rooms").child(gameId!!).child("turns")
                .setValue(playerNames!![currentPlayerIndex])

            // Move to the next player in a round-robin fashion
            currentPlayerIndex = (currentPlayerIndex + 1) % playerNames!!.size
        }
    }


    private fun listenForPlayerTurnUpdates() {
        if (gameId != null) {
            // Listen to changes in the "turns" node in Firebase
            database.child("rooms").child(gameId!!).child("turns")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentTurnEmail = snapshot.getValue(String::class.java)
                        if (currentTurnEmail != null) {
                            // Enable or disable the play turn button based on the current turn
                            enablePlayTurnButton(currentTurnEmail)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("MultiPlayerOnline", "Failed to read current turn: ${error.message}")
                    }
                })
        }
    }

    private fun enablePlayTurnButton(currentTurnEmail: String) {

        if (nameUser == currentTurnEmail) {
            // It's the current user's turn
            binding.currentPlayerText.text = "Your Turn"
            binding.recyclerView.isEnabled = true
            binding.recyclerView.isClickable = true
            adapter.setClickable(true) // Enable card clicks for the current player
        } else {
            // Not the current user's turn
            binding.recyclerView.isEnabled = false
            settingUpGame()
            binding.recyclerView.isClickable = false
            binding.currentPlayerText.text = "It's ${currentTurnEmail}'s Turn"
            adapter.setClickable(false) // Disable card clicks for other players
        }
    }


    private fun shakeRecyclerView() {
        val recyclerView = binding.recyclerView

        val shakeAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
        recyclerView.startAnimation(shakeAnimation)
    }
    private fun select() {
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.select)
        mediaPlayer.start()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }, 2000)
    }
    private fun playMatchSound() {
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.match)
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
