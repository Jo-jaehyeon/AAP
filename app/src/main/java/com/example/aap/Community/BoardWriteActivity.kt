package com.example.aap.Community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aap.MainActivity
import com.example.aap.R
import com.example.aap.databinding.ActivityBoardWriteBinding
import com.example.aap.databinding.ActivityMainBinding
import com.example.aap.databinding.FragmentBoardBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BoardWriteActivity : AppCompatActivity() {
    lateinit var binding : ActivityBoardWriteBinding
    lateinit var binding2 : FragmentBoardBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: BoardAdapter
    lateinit var boardRdb: DatabaseReference
    var findQuery = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardWriteBinding.inflate(layoutInflater)
        binding2  = FragmentBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    //.setMovementMethod(new ScrollingMovementMethod()); 스크롤 추가

    private fun init(){
        layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        boardRdb = FirebaseDatabase.getInstance().getReference("Boards/contents")
        val query = boardRdb.orderByKey()
        val query2 = boardRdb.limitToLast(1)
        //val boardnum = boardRdb.orderByChild("boardNum").limitToLast(1)
        //val query = rdb.orderByKey()
        val option = FirebaseRecyclerOptions.Builder<Board>()
            .setQuery(query, Board::class.java)
            .build()
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Get Post object and use the values to update the UI
//                val post = dataSnapshot.getValue()
//                // ...
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//        }
//        boardRdb.addValueEventListener(postListener)
        adapter = BoardAdapter(option)

        binding.apply {
            writeEnroll.setOnClickListener {
                binding2.recyclerView.layoutManager = layoutManager
                binding2.recyclerView.adapter = adapter
                val num = 10
                //var boardcount = boardRdb.limitToLast(1).limitToFirst(2).get()
                val content = Board(num,titleEdit.text.toString(),
                    contentEdit.text.toString(),"noinfo")
                boardRdb.child("board" +num).setValue(content)
                initAdapter()
                val intent = Intent(it.context,MainActivity::class.java)
                intent.putExtra("int", num)
                intent.putExtra("string", titleEdit.text.toString())
                intent.putExtra("content", contentEdit.text.toString())
                startActivity(intent)
            }
            writeBack.setOnClickListener {
                val intent = Intent(it.context, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun initAdapter() {
        if (findQuery) {
            findQuery = false
            if (adapter != null)
                adapter.startListening()
            val query = boardRdb.limitToLast(50)
            //val query = rdb.orderByKey()
            val option = FirebaseRecyclerOptions.Builder<Board>()
                .setQuery(query, Board::class.java)
                .build()
            adapter = BoardAdapter(option)
            adapter.itemClickListener = object : BoardAdapter.OnitemClickListener{
                override fun OnItemClick(view: View, position: Int) {
                    binding.apply{
                        //게시글 뷰어 인텐트 이동
                        val intent = Intent(parent,BoardViewerActivity::class.java)
                        startActivity(intent)
                    }
                }

            }

            binding2.recyclerView.adapter = adapter
            adapter.startListening()
        }
    }

}