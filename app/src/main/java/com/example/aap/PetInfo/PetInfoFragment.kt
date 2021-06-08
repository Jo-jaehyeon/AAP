package com.example.aap.PetInfo


import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aap.R
import com.example.aap.databinding.FragmentPetInfoBinding
import java.io.File

class PetInfoFragment : Fragment() {
    var binding: FragmentPetInfoBinding?= null
    lateinit var mydb: PetInfoDBHelper
    lateinit var petlist: List<PetInfoDBHelper.PetInfo>
    var petindex = 1
    lateinit var pet: PetInfoDBHelper.PetInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetInfoBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mydb.close()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mydb = PetInfoDBHelper(requireContext())
        petlist = mydb.getAllPet()
        initBtn()
        setting()
    }

    fun setting()
    {
        pet = mydb.getPetInfo(petindex)
        binding?.apply{
            if(pet.name != "")
            {
                Log.d("petName", pet.name)
                NameView.text = pet.name
                AgeView.text = pet.age.toString() + "세"
                if(pet.image == "") {
                    Log.d("Mainimage", "")
                    PetImage?.setImageResource(R.drawable.loadimg)
                }
                else
                {
                    val file = File(pet.image)
                    if(file.exists())
                    {
                        Log.d("Mainimage", pet.image)
                        val bitmap = BitmapFactory.decodeFile(pet.image)
                        PetImage.setImageBitmap(bitmap)
                    }
                    else {
                        Log.d("Mainimage", "null")
                        PetImage?.setImageResource(R.drawable.loadimg)
                    }
                }
            }
        }
    }

    fun initBtn(){
        binding!!.apply {
            PetImage.setOnClickListener{
                if(pet.name != "")
                {
                    val intent = Intent(activity, PetInfoActivity::class.java)
                    intent.putExtra("name", pet.name)
                    startActivity(intent)
                }
            }
            plusbtn.setOnClickListener {
                val intent = Intent(activity, PetPlusActivity::class.java)
                startActivity(intent)
            }
            nextbtn.setOnClickListener {
                if(petlist.isNotEmpty())
                {
                    if (petindex == petlist.size)
                        petindex = 1
                    else
                        petindex += 1
                    setting()
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}
