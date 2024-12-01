package com.tinikling.cardgame.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.tinikling.cardgame.R
import com.tinikling.cardgame.adapter.LeaderboardAdapter
import com.tinikling.cardgame.adapter.MyFragmentStateAdapter
import com.tinikling.cardgame.databinding.FragmentLeaderBoardsBinding
import com.tinikling.cardgame.models.LeaderboardEntry

class LeaderBoardsFragment : Fragment() {

    private lateinit var binding: FragmentLeaderBoardsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaderBoardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout = binding.tabLayout
        val viewPager2 =binding.pager
        val myAdapter = MyFragmentStateAdapter(requireActivity())
        viewPager2.adapter = myAdapter

        TabLayoutMediator(tabLayout, viewPager2) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> {
                    tab.text = "Easy"
                }
                1 -> {
                    tab.text = "Medium"
                }
                2 -> {
                    tab.text = "Hard"
                }
            }
        }.attach()

    }


}
