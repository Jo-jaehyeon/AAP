package com.example.aap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.aap.Community.BoardFragment
import com.example.aap.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val textarr = arrayListOf<String>("펫 정보", "일정 관리", "물품 관리", "검색", "게시판", "보호소")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        val i = this.intent
        val flag = i.getStringExtra("flag")
        if(flag=="1000"){
            binding.viewPager.setCurrentItem(4)
        }
    }

    private fun init() {
        binding.viewPager.adapter = MyFragStateAdapter(this)
        TabLayoutMediator(binding.tablayout, binding.viewPager) {
            tab, position ->
            tab.text = textarr[position]
        }.attach()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==1000){
            binding.viewPager.setCurrentItem(4)
        }
    }

}