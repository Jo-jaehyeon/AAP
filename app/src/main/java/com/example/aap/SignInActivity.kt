package com.example.aap

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aap.databinding.ActivitySignInBinding
import com.google.firebase.database.*

class SignInActivity : AppCompatActivity() {
    lateinit var binding :ActivitySignInBinding

    lateinit var rdb: DatabaseReference


    var flag = -1
    val DELAYMILLI = 1700.toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun checkLogin(idk: String, pwdk: String) {
        rdb = FirebaseDatabase.getInstance().getReference("Accounts").child(idk)
        rdb.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.value.toString() //{id=<id>, pwd=<pwd>}
                val dataSplit = data.split("=", ", ", "}", "{")
                /*Log.i("CHECK", dataSplit[0])  //{
                Log.i("CHECK1", dataSplit[1])   //id tag
                Log.i("CHECK2", dataSplit[2])   //id
                Log.i("CHECK3", dataSplit[3])   //pwd tag
                Log.i("CHECK4", dataSplit[4])   //pwd
                Log.i("CHECK", dataSplit[5])    //}
                */
                //1.아이디 없는 경우 1'. 아이디 있는데 2. pwd맞는 경우 2'.pwd다른 경우
                if (data == "null"){
                    //아이디는 없는 경우
                    flag = 0
                    //Log.i("Data", data) //"null" String값
                }
                else {
                    flag = if (dataSplit[4] == pwdk) 1 else 2
                }
            }
        })

    }


    private fun init(){
        val handler = Handler()

        binding.button.setOnClickListener {
            val idkey = binding.siidinput.text.toString()
            val pwdkey = binding.sipwdinput.text.toString() //적힌게 없으면 null

            rdb = FirebaseDatabase.getInstance().getReference("Accounts").child(idkey)

            checkLogin(idkey, pwdkey)

            handler.postDelayed({
                when (flag) {
                    1 -> {
                        flag = -1
                        //Toast.makeText(this, "로그인 됨", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    0 -> {
                        flag = -1
                        Toast.makeText(this, "아이디 없음", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        flag = -1
                        Toast.makeText(this, "비밀번호가 잘못되었음", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        Toast.makeText(this, "time delayed", Toast.LENGTH_SHORT).show()
                    }
                }

            }, DELAYMILLI)


        }

    }


}