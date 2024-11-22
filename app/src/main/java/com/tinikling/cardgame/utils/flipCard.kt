package com.tinikling.cardgame.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.widget.ImageView
import com.tinikling.cardgame.R
import com.tinikling.cardgame.models.Card

fun flipCard(view: View, card: Card, onAnimationEnd: () -> Unit) {
    val flipOutAnimator = ObjectAnimator.ofFloat(view, "rotationY", 0f, 90f)
    val flipInAnimator = ObjectAnimator.ofFloat(view, "rotationY", -90f, 0f)

    flipOutAnimator.duration = 200
    flipInAnimator.duration = 200

    flipOutAnimator.addListener(object : android.animation.Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {

        }

        override fun onAnimationEnd(p0: Animator) {
            if (card.isFaceUp) {
                // Show the card front
                (view as? ImageView)?.setImageResource(card.id!!)
            } else {
                // Show the card back
                (view as? ImageView)?.setImageResource(R.drawable.cardback)
            }

            flipInAnimator.start() // Start the second half of the flip
        }

        override fun onAnimationCancel(p0: Animator) {

        }

        override fun onAnimationRepeat(p0: Animator) {

        }
    })

    flipInAnimator.addListener(object : android.animation.Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {

        }

        override fun onAnimationEnd(p0: Animator) {
            onAnimationEnd()
        }

        override fun onAnimationCancel(p0: Animator) {

        }

        override fun onAnimationRepeat(p0: Animator) {

        }
    })

    // Start the flip-out animation (first half)
    flipOutAnimator.start()
}
