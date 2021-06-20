package com.example.aap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.aap.databinding.ActivityLoadingBinding
import java.util.*
import kotlin.concurrent.schedule

class LoadingActivity : AppCompatActivity() {
    //private val SPLASH_TIME_OUT: Long = 3000
    lateinit var binding : ActivityLoadingBinding

//    lateinit var handler: Handler
//    lateinit var runnable: Runnable
    lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun skipactivity(){
        val intent = Intent(this, SignIOActivity::class.java)
        startActivity(intent)
    }

    private fun init(){
        //val handler = Handler(Looper.getMainLooper())

        timer = Timer()
        timer.schedule(5000){
            skipactivity()
        }
        binding.skipbtn.setOnClickListener {
            timer.cancel()
            skipactivity()
        }

//        handler = Handler()
//        runnable= Runnable {
//            val intent = Intent(this, SignInActivity::class.java)
//            startActivity(intent)
//        }
//
//        handler.postDelayed({
//            val intent = Intent(this, SignIOActivity::class.java)
//            startActivity(intent)
//            finish()
//        },5000)
//        /*이 프로젝트에서는 실행이 안됨.*/
//        binding.skipbtn.setOnClickListener {
//            handler.removeCallbacks(runnable)
//        }

    }



}