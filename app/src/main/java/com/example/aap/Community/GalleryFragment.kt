package com.example.aap.Community

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri.decode
import android.os.BaseBundle

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil.decode
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aap.R
import com.example.aap.databinding.FragmentBoardBinding
import com.example.aap.databinding.FragmentGalleryBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.common.util.Base64Utils.decode
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineStart
import java.lang.Byte.decode
import java.lang.Integer.decode
import java.lang.Long.decode
import java.lang.Short.decode
import java.net.URLDecoder.decode
import java.util.*

class GalleryFragment: Fragment() {
    lateinit var binding: FragmentGalleryBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: GalleryAdapter
    lateinit var galleryRdb: DatabaseReference
    var wTitle = ""
    var wBoardNum = 0
    var wContent = ""
    var wimage = ""
    var gimage : Bitmap?=null
    var findQuery = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryBinding.inflate(layoutInflater)
        galleryRdb = FirebaseDatabase.getInstance().getReference("Galleries/contents")
        val i = activity?.intent;
        wTitle = i?.getStringExtra("string").toString();
        wBoardNum = i?.getLongExtra("int", 0).toString().toInt();
        wContent = i?.getStringExtra("content").toString()
        wimage = i?.getStringExtra("image").toString()
        val imageBytes = android.util.Base64.decode(wimage, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        init()
        return binding.root
    }

    private fun init() {
        val ct = requireContext()
        layoutManager = LinearLayoutManager(ct, LinearLayoutManager.VERTICAL, false)
        galleryRdb = FirebaseDatabase.getInstance().getReference("Galleries/contents")
        val query = galleryRdb.orderByChild("timestamp")
        val option = FirebaseRecyclerOptions.Builder<Gallery>()
            .setQuery(query, Gallery::class.java)
            .build()

        adapter = GalleryAdapter(option)
        adapter.itemClickListener = object : GalleryAdapter.OnitemClickListener {
            override fun OnItemClick(view: View, position: Int) {
                binding.apply {
                    //게시글 뷰어 인텐트 이동
                    //initAdapter()
                    val ct = requireContext()
                    val i = Intent(ct, GalleryViewerActivity::class.java)
                    i.putExtra("int", wBoardNum)
                    i.putExtra("string", wTitle)
                    i.putExtra("content", wContent)
                    startActivity(i)
                }
            }
        }
        binding.galleryRecyclerView.layoutManager = layoutManager
        binding.galleryRecyclerView.adapter = adapter
        binding.apply {
            textView.setOnClickListener {

            }
            galleryRecyclerView.layoutManager = layoutManager
            galleryRecyclerView.adapter = adapter
            writeBtn.setOnClickListener {
                val intent = Intent(it.context, GalleryWriteActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun initAdapter() {
        if (findQuery) {
            findQuery = false
            if (adapter != null)
                adapter.startListening()
            val query = galleryRdb.limitToLast(50)
            //val query = rdb.orderByKey()
            val option = FirebaseRecyclerOptions.Builder<Gallery>()
                .setQuery(query, Gallery::class.java)
                .build()
            adapter = GalleryAdapter(option)
            adapter.itemClickListener = object : GalleryAdapter.OnitemClickListener {
                override fun OnItemClick(view: View, position: Int) {
                    binding.apply {
                        //게시글 뷰어 인텐트 이동
                        val ct = requireContext()
                        val intent = Intent(ct, GalleryViewerActivity::class.java)
                        intent.putExtra("int", wBoardNum)
                        intent.putExtra("string", wTitle)
                        intent.putExtra("content", wContent)
                        startActivity(intent)
                    }
                }

            }
            binding.galleryRecyclerView.adapter = adapter
            adapter.startListening()
        }
    }
    fun StringToBitmap(encodedString: String?): Bitmap? {
        return try {

            val encodeByte: ByteArray = Base64.getDecoder().decode(encodedString) // String 화 된 이미지를  base64방식으로 인코딩하여 byte배열을 만듬
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size) //만들어진 bitmap을 return
        } catch (e: Exception) {
            e.message
            null
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
