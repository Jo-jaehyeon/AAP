package com.example.aap.Community

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64.encodeToString
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aap.MainActivity
import com.example.aap.databinding.ActivityGalleryWriteBinding
import com.example.aap.databinding.FragmentGalleryBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import org.jetbrains.anko.startActivityForResult
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest


class GalleryWriteActivity : AppCompatActivity() {
    lateinit var binding : ActivityGalleryWriteBinding
    lateinit var binding2 : FragmentGalleryBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: GalleryAdapter
    lateinit var galleryRdb: DatabaseReference
    lateinit var galleryCountRdb : DatabaseReference
    var viewProfile : View? = null
    var pickImageFromAlbum = 0
    var fbStorage : FirebaseStorage? = null
    var uriPhoto : Uri? = null
    var str = ""
    var bitmap:Bitmap?= null
    var findQuery = false
    val GALLEERY_CODE = 10
    var count = 0
    var bitmapString = ""
    var path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryWriteBinding.inflate(layoutInflater)
        binding2  = FragmentGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        galleryCountRdb = FirebaseDatabase.getInstance().getReference("GalleriesCount").child("count")
        galleryCountRdb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var data = snapshot.value.toString()
                str = data

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    //.setMovementMethod(new ScrollingMovementMethod()); 스크롤 추가
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            uriPhoto= data?.data
            try{
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uriPhoto)
//                binding.galleryInputimage.setImageBitmap(bitmap)
                binding.galleryInputimage.setImageURI(uriPhoto)
                imageUpload(binding.root)
            }catch (e: Exception){
                Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun imageUpload(view :View){
//        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imgFileName = "IMAGE_"+(str.toInt()+1)+".jpg"
        var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

        storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
            Toast.makeText(view.context,"Image Uploaded",Toast.LENGTH_SHORT).show()
        }
    }

    private fun init(){
        fbStorage = FirebaseStorage.getInstance()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        galleryRdb = FirebaseDatabase.getInstance().getReference("Galleries/contents")
        val query = galleryRdb.orderByKey()
        val option = FirebaseRecyclerOptions.Builder<Gallery>()
            .setQuery(query, Gallery::class.java)
            .build()

        adapter = GalleryAdapter(option)

        binding.apply {
            CoroutineScope(Dispatchers.IO).async {
                galleryInputimage.setOnClickListener {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    resultLauncher.launch(intent)
//                var photoPickerIntent = Intent(Intent.ACTION_PICK)
//                photoPickerIntent.type = "image/*"
//                startActivityForResult(photoPickerIntent, pickImageFromAlbum)
                }
            }

            galleryWrite.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
//                Handler().postDelayed({
                    binding2.galleryRecyclerView.layoutManager = layoutManager
                    binding2.galleryRecyclerView.adapter = adapter
                CoroutineScope(Dispatchers.IO).async {
                    val timestamp = System.currentTimeMillis()
                    count = str.toInt() + 1
                    val content = Gallery(
                            count, galleryTitleEdit.text.toString(),
                            galleryContentEdit.text.toString(), "gs://allaboutpet-1c6f3.appspot.com/images/IMAGE_"+count+".jpg", "noinfo", timestamp
                    )
                    galleryRdb.child("gallery" + count).setValue(content)
                    galleryCountRdb.setValue(count)
                }.await()

                    initAdapter()
                    val intent = Intent(it.context, MainActivity::class.java)
                    intent.putExtra("int", count)
                    intent.putExtra("string", galleryTitleEdit.text.toString())
                    intent.putExtra("content", galleryContentEdit.text.toString())
                    intent.putExtra("image", "gs://allaboutpet-1c6f3.appspot.com/images/IMAGE_"+count+".jpg")
                    startActivity(intent)
                }
//                },3000)

                }
                galleryBack.setOnClickListener {
                    val intent = Intent(it.context, MainActivity::class.java)
                    startActivity(intent)
                }

        }
    }
//    fun BitMapToString(bitmap: Bitmap?): String {
//        val baos = ByteArrayOutputStream()
//        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
//        val b = baos.toByteArray()
//        return android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT)
//    }




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






}