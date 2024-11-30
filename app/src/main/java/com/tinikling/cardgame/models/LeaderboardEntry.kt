package com.tinikling.cardgame.models

data class LeaderboardEntry(
    val name: String = "",
    val points: String = "0",
    val hintsUsed: String = "0",
    val timeRemaining: String = "0:0"
)
