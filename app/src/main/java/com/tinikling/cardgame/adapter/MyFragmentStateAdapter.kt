package com.tinikling.cardgame.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tinikling.cardgame.ui.leaderboards.EasyFragment
import com.tinikling.cardgame.ui.leaderboards.HardFragmentLeaderBoards
import com.tinikling.cardgame.ui.leaderboards.MediumFragment

class MyFragmentStateAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EasyFragment()
            1 -> MediumFragment()
            2 -> HardFragmentLeaderBoards()
            else -> EasyFragment()
        }
    }
}