package com.tinikling.cardgame.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinikling.cardgame.databinding.ItemRoomBinding

data class Room(val gameId: String, val timestamp: Long?, val playerNames: List<String>)

class RoomsAdapter(private val onItemClick: (String, String?) -> Unit) : RecyclerView.Adapter<RoomsAdapter.RoomViewHolder>() {

    private var roomsList = listOf<Room>()

    fun submitList(rooms: List<Room>) {
        roomsList = rooms
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomsList[position]
        holder.bind(room)
    }

    override fun getItemCount(): Int = roomsList.size

    inner class RoomViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: Room) {
            binding.timestampTextView.text = room.timestamp?.toString() ?: "Room ID: ${room.gameId}"
            binding.playerNamesTextView.text = room.playerNames.joinToString(", ")

            itemView.setOnClickListener {
                onItemClick(room.gameId, room.playerNames.firstOrNull())
            }
        }
    }
}
