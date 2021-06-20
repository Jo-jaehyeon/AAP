package com.example.aap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.aap.databinding.ActivityLoadingBinding

class LoadingActivity : AppCompatActivity() {
    //private val SPLASH_TIME_OUT: Long = 3000
    lateinit var binding : ActivityLoadingBinding

    lateinit var handler: Handler
    lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler()
        runnable= Runnable {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        /*이 프로젝트에서는 실행이 안됨.*/
        binding.skipbtn.setOnClickListener {
            handler.removeCallbacks(runnable)
        }
        init()
    }


    private fun init(){
        handler.postDelayed({
            val intent = Intent(this, SignIOActivity::class.java)
            startActivity(intent)
            finish()
        },5000)

    }



}