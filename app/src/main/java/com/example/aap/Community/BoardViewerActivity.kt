package com.example.aap.Community

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.aap.R
import com.example.aap.databinding.ActivityBoardViewerBinding

class BoardViewerActivity : AppCompatActivity() {
    lateinit var binding: ActivityBoardViewerBinding
    var bTitle =""
    var bBoardNum =0
    var bContent =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val i = intent;
        bTitle = i.getStringExtra("string").toString();
        bBoardNum = i.getLongExtra("int",0).toString().toInt();
        bContent = i.getStringExtra("content").toString()
        init()
    }

    private fun init(){
        binding.apply {
            textTitle.setText(bTitle)
            textContent.setText(bContent)
        }
    }
}