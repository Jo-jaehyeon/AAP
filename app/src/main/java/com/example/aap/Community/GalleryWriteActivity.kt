package com.example.aap.Community

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.encodeToString
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aap.MainActivity
import com.example.aap.databinding.ActivityGalleryWriteBinding
import com.example.aap.databinding.FragmentGalleryBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.util.*


class GalleryWriteActivity : AppCompatActivity() {
    lateinit var binding : ActivityGalleryWriteBinding
    lateinit var binding2 : FragmentGalleryBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: GalleryAdapter
    lateinit var galleryRdb: DatabaseReference
    var bitmap:Bitmap?= null
    val GalleryStorage =0
    var findQuery = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryWriteBinding.inflate(layoutInflater)
        binding2  = FragmentGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    //.setMovementMethod(new ScrollingMovementMethod()); 스크롤 추가
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            var dataUri= data?.data
            try{
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, dataUri)
                binding.galleryInputimage.setImageBitmap(bitmap)
            }catch (e: Exception){
                Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun init(){
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        galleryRdb = FirebaseDatabase.getInstance().getReference("Galleries/contents")
        val query = galleryRdb.orderByKey()
        val query2 = galleryRdb.limitToLast(1)
        //val boardnum = boardRdb.orderByChild("boardNum").limitToLast(1)
        //val query = rdb.orderByKey()
        val option = FirebaseRecyclerOptions.Builder<Gallery>()
            .setQuery(query, Gallery::class.java)
            .build()

        adapter = GalleryAdapter(option)

        binding.apply {
            galleryInputimage.setOnClickListener {
                val intent =  Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                resultLauncher.launch(intent)

            }
            galleryWrite.setOnClickListener {
                binding2.galleryRecyclerView.layoutManager = layoutManager
                binding2.galleryRecyclerView.adapter = adapter
                val num = 10
                val bitmapString = BitmapToString(bitmap)
                //var boardcount = boardRdb.limitToLast(1).limitToFirst(2).get()
                val content = Gallery(
                    num, galleryTitleEdit.text.toString(),
                    galleryContentEdit.text.toString(), bitmapString, "noinfo"
                )
                galleryRdb.child("gallery" + num).setValue(content)
                initAdapter()
                val intent = Intent(it.context, MainActivity::class.java)
                intent.putExtra("int", num)
                intent.putExtra("string", galleryTitleEdit.text.toString())
                intent.putExtra("content", galleryContentEdit.text.toString())
                intent.putExtra("image", bitmapString)
                startActivity(intent)
            }
            galleryBack.setOnClickListener {
                val intent = Intent(it.context, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }




    fun openSomeActivityForResult() {
        val intent = Intent(this, MainActivity::class.java)
        resultLauncher.launch(intent)
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
            adapter.itemClickListener = object : GalleryAdapter.OnitemClickListener{
                override fun OnItemClick(view: View, position: Int) {
                    binding.apply{
                        //게시글 뷰어 인텐트 이동
                        val intent = Intent(parent, BoardViewerActivity::class.java)
                        startActivity(intent)
                    }
                }

            }

            binding2.galleryRecyclerView.adapter = adapter
            adapter.startListening()
        }
    }

    fun BitmapToString(bitmap: Bitmap?): String? {
        val baos = ByteArrayOutputStream() //바이트 배열을 차례대로 읽어 들이기위한 ByteArrayOutputStream클래스 선언
        bitmap?.compress(Bitmap.CompressFormat.PNG, 70, baos) //bitmap을 압축 (숫자 70은 70%로 압축한다는 뜻)
        val bytes: ByteArray = baos.toByteArray() //해당 bitmap을 byte배열로 바꿔준다.
        return Base64.getEncoder().encodeToString(bytes) //String을 retrurn
    }




}