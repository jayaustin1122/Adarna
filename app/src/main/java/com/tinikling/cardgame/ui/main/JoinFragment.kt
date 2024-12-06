package com.tinikling.cardgame.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.database.*
import com.tinikling.cardgame.R
import com.tinikling.cardgame.adapter.Room
import com.tinikling.cardgame.adapter.RoomsAdapter
import com.tinikling.cardgame.databinding.FragmentJoinBinding
import com.tinikling.cardgame.utils.DialogUtils

class JoinFragment : Fragment() {

    private lateinit var binding: FragmentJoinBinding
    private lateinit var database: DatabaseReference
    private lateinit var roomsAdapter: RoomsAdapter
    private var playerNameKo : String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinBinding.inflate(layoutInflater)
        database = FirebaseDatabase.getInstance().reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerNameKo = arguments?.getString("playerName1").toString()
        Log.d("send", "ss$playerNameKo")

        roomsAdapter = RoomsAdapter { gameId, playerName ->
            showJoinDialog(gameId, playerName,playerNameKo)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = roomsAdapter
        }

        fetchRooms()
    }

    private fun fetchRooms() {
        database.child("rooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomsList = mutableListOf<Room>()
                for (roomSnapshot in snapshot.children) {
                    val gameId = roomSnapshot.key
                    val timestamp = roomSnapshot.child("timestamp").getValue(Long::class.java)
                    val playerNamesSnapshot = roomSnapshot.child("playerNames")
                    val isOpen = roomSnapshot.child("isOpen").getValue(Boolean::class.java) ?: true // Default to true if not set

                    // Continue processing rooms even if isOpen is false
                    val playerNames = mutableListOf<String>()
                    for (playerSnapshot in playerNamesSnapshot.children) {
                        val playerName = playerSnapshot.getValue(String::class.java)
                        playerName?.let { playerNames.add(it) }
                    }

                    gameId?.let {
                        roomsList.add(Room(gameId, timestamp, playerNames, isOpen))
                    }
                }

                // If roomsList is empty, show "No room created, please wait."
                if (roomsList.isEmpty()) {
                    binding.recyclerView.visibility = View.GONE
                    binding.noRoomsText.visibility = View.VISIBLE
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.noRoomsText.visibility = View.GONE
                    roomsAdapter.submitList(roomsList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error fetching rooms", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun showJoinDialog(gameId: String, playerName: String?, playerNameKo: String?) {
        val dialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
        dialog.titleText = "Sumali sa Laro"
        dialog.contentText = "Sumali?"
        dialog.setConfirmButton("Oo") { sweetAlertDialog ->
            sweetAlertDialog.dismissWithAnimation()
            showLoadingDialog(gameId, playerName, playerNameKo)
        }
        dialog.setCancelButton("Hindi") { sweetAlertDialog ->
            sweetAlertDialog.dismissWithAnimation()
        }
        dialog.show()
    }

    private fun showLoadingDialog(gameId: String, playerName: String?, playerNameKo: String?) {
        val loadingDialog = DialogUtils.showLoading(requireActivity())

        listenForGameStart(gameId, playerName, loadingDialog)
        uploadMyName(gameId,playerNameKo)
    }

    private fun uploadMyName(gameId: String, playerNameKo: String?) {
        if (playerNameKo.isNullOrEmpty()) {
            // Handle case where playerName is empty
            Toast.makeText(context, "Kailangan ng Pangalan na Manlalaro", Toast.LENGTH_SHORT).show()
            return
        }

        // Reference to the playerNames list in the specific game room
        val playerNamesRef = database.child("rooms").child(gameId).child("playerNames")

        // Retrieve the existing player names from Firebase
        playerNamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentPlayerNames = mutableListOf<String>()

                // Check if there are any existing player names
                if (snapshot.exists()) {
                    for (player in snapshot.children) {
                        val existingPlayerName = player.getValue(String::class.java)
                        existingPlayerName?.let { currentPlayerNames.add(it) }
                    }
                }
                currentPlayerNames.add(playerNameKo)
                playerNamesRef.setValue(currentPlayerNames)
                    .addOnSuccessListener {
                        // Successfully added the player name
                        Toast.makeText(context, "Sumali!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                        Toast.makeText(context, "Error adding player: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
                Toast.makeText(context, "Error fetching player names: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun listenForGameStart(gameId: String, playerName: String?, loadingDialog: SweetAlertDialog) {
        database.child("rooms").child(gameId).child("start")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val start = snapshot.getValue(Boolean::class.java) ?: false
                    if (start) {
                        // Game has started, fetch player names and proceed
                        database.child("rooms").child(gameId).child("playerNames")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(playerSnapshot: DataSnapshot) {
                                    val playerList = mutableListOf<String>()
                                    for (player in playerSnapshot.children) {
                                        val playerName = player.getValue(String::class.java)
                                        playerName?.let { playerList.add(it) }
                                    }

                                    loadingDialog.dismissWithAnimation()
                                    // Navigate to game screen with player names
                                    val bundle = Bundle().apply {
                                        putStringArray("playerNames", playerList.toTypedArray())
                                        putString("gameId", gameId)
                                        putString("playerName", playerNameKo)
                                    }
                                    for (key in bundle.keySet()) {
                                        val value = bundle.get(key)
                                        Log.d("BundleData", "Key: $key, Value: $value")
                                    }
                                    findNavController().navigate(R.id.multiPlayerOnlineFragment, bundle)

                                }

                                override fun onCancelled(error: DatabaseError) {
                                    loadingDialog.dismissWithAnimation()
                                    Toast.makeText(context, "Error fetching player names", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    loadingDialog.dismissWithAnimation()
                    Toast.makeText(context, "Error checking game start", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
