package com.example.aap

import android.accounts.Account
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.aap.databinding.ActivitySignUpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding

    lateinit var rdb: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun clearInput(){
        binding.apply{
            SUidinput.text.clear()
            SUpwdinput.text.clear()
        }
    }



    private fun init() {
        var flag1 = 0
        var flag2 = 1

        rdb = FirebaseDatabase.getInstance().getReference("Accounts")

        //아이디 중복확인
        binding.idconfirm.setOnClickListener {
            /*아이디 중복확인. toast message띄워주기 or 상단에 messagebox*/
            //flag1 = 1

            if(flag1 == 0){
                //아이디가 맞지 않으면
                binding.SUwarnmsg.visibility = View.VISIBLE
            }
            else{
                //아이디 중복확인 눌렀는지 확인해주기
                flag2 = 1
            }
        }

        //성공하면
        if(flag2 == 1) {
            binding.SUSUbtn.setOnClickListener {
                //데이터베이스에 삽입
                val item =
                    Account(binding.SUidinput.text.toString(), binding.SUpwdinput.text.toString())
                rdb.child(binding.SUidinput.text.toString()).setValue(item)

                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        else{
            //중복확인 하라고 띄워주기
            binding.SUwarnmsg.visibility = View.VISIBLE
        }

    }



}