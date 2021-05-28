package com.example.aap

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.aap.databinding.ActivitySignIOBinding

class SignIOActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignIOBinding

    val SIGN_IN_REQUEST=100
    val SIGN_UP_REQUEST=101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignIOBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        binding.IOSibtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.IOSubtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivityForResult(intent, SIGN_UP_REQUEST)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            SIGN_UP_REQUEST ->{
                if(resultCode== Activity.RESULT_OK){
                    Toast.makeText(this, "회원가입이 완료되었습니다.\n로그인 후 사용해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


}