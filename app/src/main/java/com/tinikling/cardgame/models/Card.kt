package com.tinikling.cardgame.models

data class Card(
    val id: Int,        // Resource ID of the card's image
    var isFaceUp: Boolean = false,  // Whether the card is currently face up
    var isMatched: Boolean = false  // Whether the card has been matched
)