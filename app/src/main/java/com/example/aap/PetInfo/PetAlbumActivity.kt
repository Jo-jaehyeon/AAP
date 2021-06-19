package com.example.aap.PetInfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import android.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import com.example.aap.databinding.ActivityPetAlbumBinding
import com.example.aap.databinding.ActivityPetPlusBinding

class PetAlbumActivity : AppCompatActivity() {
    lateinit var binding: ActivityPetAlbumBinding
    lateinit var mydb : PetInfoDBHelper
    var data:ArrayList<AlbumData> = ArrayList()
    var petname = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetAlbumBinding.inflate(layoutInflater)
        initData()
        initRecyclerView()
        setContentView(binding.root)
    }

    private fun initRecyclerView()
    {
        binding.apply {
            albumRecycler.layoutManager = GridLayoutManager(this@PetAlbumActivity, 2)
            albumRecycler.adapter = AlbumAdapter(data)
        }

    }
    private fun initData()
    {
        mydb = PetInfoDBHelper(this)
        petname = intent.getStringExtra("name").toString()
        data = mydb.getPetAlbum(mydb.getPID(petname))

    }
}