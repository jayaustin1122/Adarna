package com.tinikling.cardgame.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.tinikling.cardgame.databinding.ItemRoomBinding

data class Room(val gameId: String, val timestamp: Long?, val playerNames: List<String>,    val isOpen: Boolean) {

}

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

            // Make the item non-clickable and show a toast if isOpen is false
            itemView.setOnClickListener {
                if (room.isOpen) {
                    onItemClick(room.gameId, room.playerNames.firstOrNull())
                } else {
                    Toast.makeText(itemView.context, "Match is still ongoing", Toast.LENGTH_SHORT).show()
                }
            }

            // Disable clicking if the room is not open
            itemView.isClickable = room.isOpen
            itemView.alpha = if (room.isOpen) 1f else 0.5f  // Optional: dim the room UI if not open
        }
    }
}

