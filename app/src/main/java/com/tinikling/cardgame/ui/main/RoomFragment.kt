package com.tinikling.cardgame.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tinikling.cardgame.R
import com.tinikling.cardgame.adapter.PlayersAdapter
import com.tinikling.cardgame.databinding.FragmentRoomBinding

class RoomFragment : Fragment() {

    private lateinit var binding: FragmentRoomBinding
    private lateinit var database: DatabaseReference
    private lateinit var playersAdapter: PlayersAdapter // RecyclerView adapter for showing players
    private var gameId: String = "" // Store game ID
    private var playersList: MutableList<String> = mutableListOf()
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomBinding.inflate(inflater, container, false)

        // Initialize Firebase Realtime Database reference
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        // Set up RecyclerView
        playersAdapter = PlayersAdapter(playersList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playersAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playerName = arguments?.getString("playerName1")

        // Automatically create a room with a unique game ID (timestamp)
        createRoom(playerName)

        // Add listener to "Magsimula" button
        binding.start.setOnClickListener {
            startGame(playerName)
        }

        // Listen for changes in the player list
        listenForPlayers()
        // Listen for changes to the "start" boolean to detect when the game starts
        listenForGameStart(playerName)
    }

    // Creates a new room with a unique game ID (timestamp) and empty player list
    private fun createRoom(playerName: String?) {
        gameId = System.currentTimeMillis().toString() // Use current timestamp as gameId

        // Create the initial room data with the first player
        val gameRoom = mapOf(
            "gameId" to gameId,
            "playerNames" to listOf(playerName),
            "start" to false,
            "isOpen" to true,
            "turn" to ""
        )

        // Upload the room data to Firebase RTDB
        database.child("rooms").child(gameId).setValue(gameRoom)
            .addOnSuccessListener {
                Toast.makeText(context, "Room created successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to create room", Toast.LENGTH_SHORT).show()
            }
    }


    // Listens for changes in the player list and updates the RecyclerView
    private fun listenForPlayers() {
        database.child("rooms").child(gameId).child("playerNames")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    playersList.clear()
                    for (playerSnapshot in snapshot.children) {
                        val playerName = playerSnapshot.getValue(String::class.java)
                        playerName?.let { playersList.add(it) }
                    }
                    playersAdapter.notifyDataSetChanged() // Update the RecyclerView
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error loading players", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Listens for the "start" boolean, and navigates to the multiplayer fragment when true
    private fun listenForGameStart(playerName: String?) {
        database.child("rooms").child(gameId).child("start")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val start = snapshot.getValue(Boolean::class.java) ?: false
                    if (start) {
                        // Fetch playerNames list from the database before navigating
                        database.child("rooms").child(gameId).child("playerNames")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(playerSnapshot: DataSnapshot) {
                                    val playerList = mutableListOf<String>()
                                    for (player in playerSnapshot.children) {
                                        val playerName = player.getValue(String::class.java)
                                        playerName?.let { playerList.add(it) }
                                    }

                                    // Pass the playerNames and gameDuration as a bundle
                                    val bundle = Bundle().apply {
                                        putStringArray("playerNames", playerList.toTypedArray())
                                        putInt("gameDuration", 10)
                                        putString("gameId", gameId)
                                        putString("playerName", playerName)
                                    }
                                    Log.d("send", "ss$playerName")
                                    // Navigate to the MultiPlayerOnlineFragment
                                    findNavController().navigate(R.id.multiPlayerOnlineFragment, bundle)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, "Error fetching player names", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error checking start status", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun startGame(playerName: String?) {
        // Retrieve the player names from the database
        database.child("rooms").child(gameId).child("playerNames")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val playerList = mutableListOf<String>()
                    for (player in snapshot.children) {
                        val playerName = player.getValue(String::class.java)
                        playerName?.let { playerList.add(it) }
                    }

                    // Check if the number of players is sufficient to start the game
                    if (playerList.size < 2) {
                        // Not enough players to start the game
                        Toast.makeText(context, "At least 2 players are required to start the game", Toast.LENGTH_SHORT).show()
                    } else {
                        // Proceed to start the game if there are 2 or more players
                        database.child("rooms").child(gameId).child("start").setValue(true)
                        database.child("rooms").child(gameId).child("isOpen").setValue(false)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Game started!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to start game", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error retrieving player list", Toast.LENGTH_SHORT).show()
                }
            })
    }

}
