package com.example.aap

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.aap.databinding.ActivitySignUpBinding
import com.google.firebase.database.*

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding

    lateinit var rdb: DatabaseReference

    var flag = -1
    var flag2 = -1
    val DELAYMILLI = 1700.toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }


    private fun checkID(idk: String) {
        rdb = FirebaseDatabase.getInstance().getReference("Accounts").child(idk)
        rdb.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.value.toString() //{id=<id>, pwd=<pwd>}

                if(data == "null"){
                    flag = 0    //중복되는 아이디 없음
                    flag2 = 1 //중복확인해줌
                }else{
                    flag = 1
                }
            }
        })

    }



    private fun init() {
        val handler = Handler()

        //아이디 중복확인
        binding.idconfirm.setOnClickListener {
            val suid = binding.SUidinput.text.toString()
            rdb = FirebaseDatabase.getInstance().getReference("Accounts").child(suid)

            checkID(suid)

            handler.postDelayed({
                when (flag) {
                    0 -> Toast.makeText(this, "아이디 사용이 가능합니다.", Toast.LENGTH_SHORT).show()
                    1 -> {
                        Toast.makeText(this, "이미 사용중인 아이디가 있습니다.", Toast.LENGTH_SHORT).show()
                        flag = -1
                    }
                }

            }, DELAYMILLI)
        }

        binding.SUSUbtn.setOnClickListener {
            //성공하면
            if (flag2 == 1) {
                //데이터베이스에 삽입
                val item = Account(binding.SUidinput.text.toString(), binding.SUpwdinput.text.toString())
                rdb.setValue(item)

                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()

            } else {
                //중복확인 하라고 띄워주기
                Toast.makeText(this, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

    }



}