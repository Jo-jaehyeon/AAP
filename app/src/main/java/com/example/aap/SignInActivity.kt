package com.example.aap

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aap.databinding.ActivitySignInBinding
import com.google.firebase.database.DatabaseReference

class SignInActivity : AppCompatActivity() {
    lateinit var binding :ActivitySignInBinding

    lateinit var layoutManager: LinearLayoutManager
    //lateinit var rdb: DatabaseReference
    //lateinit var adapter: AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }


    private fun init(){
        var flag = 1


        binding.button.setOnClickListener {
            /*로그인*/
            //flag = 1

            /*if()로그인에 실패하면 toast message띄워주기 or 상단에 messagebox, clearinputtext()*/
            if(flag == 0){
                binding.SIwarnmsg.visibility = VISIBLE
            }

            //성공하면
            else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }


}