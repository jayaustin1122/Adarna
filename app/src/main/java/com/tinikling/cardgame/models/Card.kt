package com.tinikling.cardgame.models

data class Card(
    val id: Int?,                // Image resource ID can be null
    val description: String?,     // Description can also be null
    val pair: Int?,     // Description can also be null
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)
