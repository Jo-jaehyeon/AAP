package com.example.aap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.aap.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val textarr = arrayListOf<String>("펫 정보", "일정 관리", "물품 관리", "검색", "게시판", "보호소")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.viewPager.adapter = MyFragStateAdapter(this)
        TabLayoutMediator(binding.tablayout, binding.viewPager) {
            tab, position ->
            tab.text = textarr[position]
        }.attach()
    }

}