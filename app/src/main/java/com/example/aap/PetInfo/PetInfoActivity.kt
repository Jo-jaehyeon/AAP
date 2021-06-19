package com.example.aap.PetInfo


import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.aap.MainActivity
import com.example.aap.R
import com.example.aap.databinding.ContentScrollingBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class PetInfoActivity : AppCompatActivity() {
    var gender = 0
    var isNeutral = 0
    lateinit var bitmap:Bitmap
    var ImagePath = ""
    lateinit var petname: String
    lateinit var mydb: PetInfoDBHelper
    private val OPEN_GALLERY = 1
    lateinit var binding: ContentScrollingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petname = intent.getStringExtra("name").toString()
        binding = ContentScrollingBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_pet_info)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = petname

        initData()
        initBtn()
        init()

    }

    override fun onDestroy() {
        super.onDestroy()
        mydb.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menu = menuInflater.inflate(R.menu.menu_pet_info, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.remove ->
            {
                val dlgBuilder = AlertDialog.Builder(this)
                dlgBuilder.setTitle(petname)
                    .setMessage(petname + "을(를) 삭제하시겠습니까?")
                    .setPositiveButton("삭제"){
                            _, _ ->
                        val pid = mydb.getPID(petname)

                        //보유 질병 삭제
                        val dislist = mydb.getPetDis(pid)
                        for(i in dislist)
                            mydb.removePetDis(petname, i)

                        //접종 이력 삭제
                        val inolist = mydb.getPetIno(pid)
                        for(i in inolist)
                            mydb.removePetIno(petname, i)

                        //펫 정보 삭제
                        mydb.removePetInfo(petname)

                        //메인 화면으로
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                    .setNegativeButton("취소"){
                            _, _ ->
                    }
                    .show()

            }
        }
        return true
    }

    fun initData()
    {
        mydb = PetInfoDBHelper(this)
        mydb.getAllRecord(petname, binding)
        val pid = mydb.getPID(petname)
        val pet = mydb.getPetInfo(pid)

        ImagePath = pet.image
        findViewById<EditText>(R.id.PetName2).setText(pet.name)
        findViewById<EditText>(R.id.PetAge2).setText(pet.age.toString())
        findViewById<EditText>(R.id.BirthdayView2).setText(pet.birth)
        gender = pet.gender
        isNeutral = pet.neutral

        val ToolbarImage = findViewById<ImageView>(R.id.ToolbarImage)
        if(ImagePath == "") {
            Log.d("image", "")
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.loadimg)
            ToolbarImage?.setImageBitmap(bitmap)
        }
        else
        {
            val file = File(ImagePath)
            if(file.exists())
            {
                Log.d("image", ImagePath)
                bitmap = BitmapFactory.decodeFile(ImagePath)
                ToolbarImage.setImageBitmap(bitmap)
            }
            else {
                Log.d("image", "null")
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.loadimg)
                ToolbarImage?.setImageBitmap(bitmap)
            }
        }

    }

    fun initBtn()
    {
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener{
            openGallery()
        }
        findViewById<ImageButton>(R.id.Malebtn2).setOnClickListener {
            if (gender == 0) {
                gender = 1
                findViewById<ImageButton>(R.id.Malebtn2).setImageResource(R.drawable.male_selected)
                findViewById<ImageButton>(R.id.Femalebtn2).setImageResource(R.drawable.female_normal)
            }
        }
        findViewById<ImageButton>(R.id.Femalebtn2).setOnClickListener {
            if (gender == 1) {
                gender = 0
                findViewById<ImageButton>(R.id.Malebtn2).setImageResource(R.drawable.male_normal)
                findViewById<ImageButton>(R.id.Femalebtn2).setImageResource(R.drawable.female_selected)
            }
        }
        findViewById<ImageButton>(R.id.Neutralbtn2).setOnClickListener {
            if (isNeutral == 0) {
                isNeutral = 1
                findViewById<ImageButton>(R.id.Neutralbtn2).setImageResource(R.drawable.neutral_selected)
                findViewById<ImageButton>(R.id.NonNeutralbtn2).setImageResource(R.drawable.nonneutral_normal)
            }
        }
        findViewById<ImageButton>(R.id.NonNeutralbtn2).setOnClickListener {
            if (isNeutral == 1) {
                isNeutral = 0
                findViewById<ImageButton>(R.id.Neutralbtn2).setImageResource(R.drawable.neutral_normal)
                findViewById<ImageButton>(R.id.NonNeutralbtn2).setImageResource(R.drawable.nonneutral_selected)
            }
        }
        findViewById<Button>(R.id.Plusbtn1).setOnClickListener{
            val inputtext = EditText(this)

            val dlgBuilder = AlertDialog.Builder(this)
            dlgBuilder.setTitle("질병이력")
                .setView(inputtext)
                .setPositiveButton("추가"){
                        _, _ ->
                    if(inputtext.text.isNotEmpty())
                    {
                        Toast.makeText(this, inputtext.text.toString(), Toast.LENGTH_SHORT).show()
                        val flag = mydb.insertPetDisease(inputtext.text.toString(), mydb.getPID(petname))
                        if(flag)
                            mydb.getAllRecord(petname, binding)
                        else
                            Log.d("insert", "fail")
                    }
                    else
                        Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("취소"){
                        _, _ ->
                }
                .show()
        }
        findViewById<Button>(R.id.Plusbtn2).setOnClickListener{
            val inputtext = EditText(this)

            val dlgBuilder = AlertDialog.Builder(this)
            dlgBuilder.setTitle("접종이력")
                .setView(inputtext)
                .setPositiveButton("추가"){
                        _, _ ->
                    if(inputtext.text.isNotEmpty())
                    {
                        Toast.makeText(this, inputtext.text.toString(), Toast.LENGTH_SHORT).show()
                        val flag = mydb.insertPetInoculation(inputtext.text.toString(), mydb.getPID(petname))
                        if(flag)
                            mydb.getAllRecord(petname, binding)
                        else
                            Log.d("insert", "fail")
                    }
                    else
                        Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("취소"){
                        _, _ ->
                }
                .show()
        }
        findViewById<Button>(R.id.Rewritebtn).setOnClickListener {
            saveImage()         //이미지 저장

            //수정된 정보 반영
            val newname = findViewById<EditText>(R.id.PetName2).text.toString()
            val newage = Integer.parseInt(findViewById<EditText>(R.id.PetAge2).text.toString())
            val newbirth = findViewById<EditText>(R.id.BirthdayView2).text.toString()
            val temp = PetInfoDBHelper.PetInfo(newname, ImagePath, newage, gender, isNeutral, newbirth)

            mydb.updatePetInfo(temp)

            //보유 질병 수정

            //접종 이력 수정

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        findViewById<Button>(R.id.AlbumBtn).setOnClickListener {
            val intent = Intent(this, PetAlbumActivity::class.java)
            intent.putExtra("name", petname)
            startActivity(intent)
        }
    }

    fun init() {

        if (gender == 1) {
            findViewById<ImageButton>(R.id.Malebtn2).setImageResource(R.drawable.male_selected)
            findViewById<ImageButton>(R.id.Femalebtn2).setImageResource(R.drawable.female_normal)
        } else {
            findViewById<ImageButton>(R.id.Malebtn2).setImageResource(R.drawable.male_normal)
            findViewById<ImageButton>(R.id.Femalebtn2).setImageResource(R.drawable.female_selected)
        }
        if (isNeutral == 1) {
            findViewById<ImageButton>(R.id.Neutralbtn2).setImageResource(R.drawable.neutral_selected)
            findViewById<ImageButton>(R.id.NonNeutralbtn2).setImageResource(R.drawable.nonneutral_normal)
        } else {
            findViewById<ImageButton>(R.id.Neutralbtn2).setImageResource(R.drawable.neutral_normal)
            findViewById<ImageButton>(R.id.NonNeutralbtn2).setImageResource(R.drawable.nonneutral_selected)
        }

    }

    private fun openGallery()
    {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, OPEN_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
        {
            var currentImage: Uri? = data?.data

            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImage)
                val ToolbarImage = findViewById<ImageView>(R.id.ToolbarImage)
                ToolbarImage.setImageBitmap(bitmap)
            }catch (e:Exception)
            {
                e.printStackTrace()
            }
        }
        else
        {
            Log.d("Camera", "someting wrong")
        }
    }

    fun saveImage()
    {
        var StoragePath = cacheDir.toString() + "/Pet/Image"

        var Folder = File(StoragePath)
        if(!Folder.exists())        //폴더 없으면 생성
            Folder.mkdirs()

        var fileName = "$petname.png"
        ImagePath = StoragePath+"/"+fileName

        var file = File(StoragePath, fileName)


        val fos = FileOutputStream(file)
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()

    }



}