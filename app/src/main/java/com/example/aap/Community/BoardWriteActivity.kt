package com.example.aap.Community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aap.MainActivity
import com.example.aap.R
import com.example.aap.databinding.ActivityBoardWriteBinding
import com.example.aap.databinding.ActivityMainBinding
import com.example.aap.databinding.FragmentBoardBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import kotlinx.coroutines.delay
import org.jetbrains.anko.support.v4.viewPager

class BoardWriteActivity : AppCompatActivity() {
    lateinit var binding : ActivityBoardWriteBinding
    lateinit var binding2 : FragmentBoardBinding
    lateinit var binding3 : MainActivity
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: BoardAdapter
    lateinit var boardRdb: DatabaseReference
    lateinit var boardRdbCount: DatabaseReference
    var str = ""
    var findQuery = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardWriteBinding.inflate(layoutInflater)
        binding2  = FragmentBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        boardRdbCount = FirebaseDatabase.getInstance().getReference("BoardsCount").child("count")
        boardRdbCount.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var data = snapshot.value.toString()
                str = data
                Log.i("BOARDCOUNT",data)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        init()
    }
    private fun init(){
        layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        boardRdb = FirebaseDatabase.getInstance().getReference("Boards/contents")
        val query = boardRdb.orderByKey()
        val option = FirebaseRecyclerOptions.Builder<Board>()
            .setQuery(query, Board::class.java)
            .build()
        adapter = BoardAdapter(option)
        binding.apply {

            writeEnroll.setOnClickListener {
                Handler().postDelayed({
                    binding2.recyclerView.layoutManager = layoutManager
                    binding2.recyclerView.adapter = adapter
                    var count2 = str.toInt()+1
                    val timestamp = System.currentTimeMillis()
                    val content = Board(count2,titleEdit.text.toString(),
                            contentEdit.text.toString(),"noinfo",timestamp)
                    boardRdb.child("board" +count2).setValue(content)
                    str = count2.toString()
                    boardRdbCount.setValue(count2)
                    initAdapter()
                    val intent = Intent(it.context,MainActivity::class.java)
                    intent.putExtra("int", count2)
                    intent.putExtra("string", titleEdit.text.toString())
                    intent.putExtra("content", contentEdit.text.toString())
                    intent.putExtra("timestamp",timestamp)
                    intent.putExtra("flag","1000")
                    startActivity(intent)
                },1000.toLong())

            }
            writeBack.setOnClickListener {
                val intent = Intent(it.context, MainActivity::class.java)
                intent.putExtra("flag","1000")
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