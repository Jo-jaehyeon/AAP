package com.example.aap

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.aap.GoodsManage.GoodsFragment
import com.example.aap.PetInfo.PetInfoFragment
import com.example.aap.Schedule.ScheduleFragment

class MyFragStateAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0-> PetInfoFragment()
            1-> ScheduleFragment()
            2-> GoodsFragment()
            3-> FindFragment()
            4-> CommunityFragment()
            else-> PetInfoFragment()
        }
    }


}