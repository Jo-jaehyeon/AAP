package com.example.aap.Community

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aap.R
import com.example.aap.databinding.ActivityMainBinding
import com.example.aap.databinding.FragmentBoardBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BoardFragment: Fragment() {
    lateinit var binding: FragmentBoardBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: BoardAdapter
    lateinit var boardRdb: DatabaseReference
    var wTitle =""
    var wBoardNum =0
    var wContent =""
    var findQuery = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardBinding.inflate(layoutInflater)
        boardRdb = FirebaseDatabase.getInstance().getReference("Boards/contents")
        val i = activity?.intent;
        wTitle = i?.getStringExtra("string").toString();
        wBoardNum = i?.getLongExtra("int",0).toString().toInt();
        wContent = i?.getStringExtra("content").toString()
        var board1 = boardRdb.child("board" + 2)
        board1.child("boardNum").setValue(wBoardNum)
        board1.child("boardTitle").setValue(wTitle)
        board1.child("boardContent").setValue(wContent)
        init()
        return binding.root
    }
    private fun init(){
        val ct = requireContext()
        layoutManager = LinearLayoutManager(ct,LinearLayoutManager.VERTICAL,false)
        boardRdb = FirebaseDatabase.getInstance().getReference("Boards/contents")
        val query = boardRdb.limitToLast(50)
        val option = FirebaseRecyclerOptions.Builder<Board>()
            .setQuery(query,Board::class.java)
            .build()

        adapter = BoardAdapter(option)
        adapter.itemClickListener = object :BoardAdapter.OnitemClickListener{
            override fun OnItemClick(view: View, position: Int) {
                binding.apply {
                    //게시글 뷰어 인텐트 이동
                    //initAdapter()
                    val ct = requireContext()
                    val i= Intent(ct,BoardViewerActivity::class.java)
                    i.putExtra("int", wBoardNum)
                    i.putExtra("string", wTitle)
                    i.putExtra("content", wContent)
                    startActivity(i)
                }
            }
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.apply{
            textView.setOnClickListener {

            }
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            writeBtn.setOnClickListener {
                //게시글 작성 액티비티 이동
//                var board1 = boardRdb.child("board2")
//                board1.child("boardNum").setValue(1)
//                board1.child("boardTitle").setValue("78907890")
//                board1.child("boardContent").setValue("12341234")
//                board1.child("boardUid").setValue("shs")
                val intent = Intent(it.context,BoardWriteActivity::class.java)
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
                        val ct = requireContext()
                        val intent = Intent(ct,BoardViewerActivity::class.java)
                        intent.putExtra("int", wBoardNum)
                        intent.putExtra("string", wTitle)
                        intent.putExtra("content", wContent)
                        startActivity(intent)
                    }
                }

            }
            binding.recyclerView.adapter = adapter
            adapter.startListening()
        }
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


}