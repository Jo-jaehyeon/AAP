package com.example.aap.PetInfo

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.aap.MainActivity
import com.example.aap.R
import com.example.aap.databinding.ActivityPetPlusBinding
import com.example.aap.databinding.ContentScrollingBinding

class PetPlusActivity : AppCompatActivity() {
    lateinit var binding: ActivityPetPlusBinding

    lateinit var mydb: PetInfoDBHelper
    var disList = mutableListOf<String>()
    var inoList = mutableListOf<String>()

    var gender = 1
    var isNeutral = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetPlusBinding.inflate(layoutInflater)

        setContentView(binding.root)
        init()
        initBtn()
    }

    fun init()
    {
        mydb = PetInfoDBHelper(this)

        binding.apply {
            Malebtn.setOnClickListener{
                if(gender == 0)
                {
                    gender = 1
                    Malebtn.setImageResource(R.drawable.male_selected)
                    Femalebtn.setImageResource(R.drawable.female_normal)
                }
            }
            Femalebtn.setOnClickListener{
                if(gender == 1)
                {
                    gender = 0
                    Malebtn.setImageResource(R.drawable.male_normal)
                    Femalebtn.setImageResource(R.drawable.female_selected)
                }
            }
            Neutralbtn.setOnClickListener{
                if(isNeutral == 0)
                {
                    isNeutral = 1
                    Neutralbtn.setImageResource(R.drawable.neutral_selected)
                    NonNeutralbtn.setImageResource(R.drawable.nonneutral_normal)
                }
            }
            NonNeutralbtn.setOnClickListener{
                if(isNeutral == 1)
                {
                    isNeutral = 0
                    Neutralbtn.setImageResource(R.drawable.neutral_normal)
                    NonNeutralbtn.setImageResource(R.drawable.nonneutral_selected)
                }
            }
        }

    }

    fun initBtn()
    {

        binding.apply {
            Plusbtn1?.setOnClickListener {
                val inputtext = EditText(this@PetPlusActivity)

                val dlgBuilder = AlertDialog.Builder(this@PetPlusActivity)
                dlgBuilder.setTitle("보유질병")
                    .setView(inputtext)
                    .setPositiveButton("추가"){
                            _, _ ->
                        disList.add(inputtext.text.toString())
                        showDisRecord(binding)
                    }
                    .setNegativeButton("취소"){
                            _, _ ->
                    }
                    .show()
            }
            Plusbtn2?.setOnClickListener {
                val inputtext = EditText(this@PetPlusActivity)

                val dlgBuilder = AlertDialog.Builder(this@PetPlusActivity)
                dlgBuilder.setTitle("접종이력")
                    .setView(inputtext)
                    .setPositiveButton("추가"){
                            _, _ ->
                        inoList.add(inputtext.text.toString())
                        showInoRecord(binding)
                    }
                    .setNegativeButton("취소"){
                            _, _ ->
                    }
                    .show()
            }
            Plusbtn?.setOnClickListener{
                if(PetName.text.isEmpty())
                    Toast.makeText(this@PetPlusActivity, "펫 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                else if(PetAge.text.isEmpty())
                    Toast.makeText(this@PetPlusActivity, "펫 나이를 입력해주세요", Toast.LENGTH_SHORT).show()
                else if(BirthdayView.text.isEmpty())
                    Toast.makeText(this@PetPlusActivity, "펫 생일을 입력해주세요", Toast.LENGTH_SHORT).show()
                else
                {
                    val info = PetInfoDBHelper.PetInfo(PetName.text.toString(), "" ,Integer.parseInt(PetAge.text.toString()), gender, isNeutral, BirthdayView.text.toString())
                    mydb.insertPetBasic(info)

                    val pid = mydb.getPID(PetName.text.toString())
                    for(i in disList)
                        mydb.insertPetDisease(i, pid)

                    for(j in inoList)
                        mydb.insertPetInoculation(j, pid)

                    val intent = Intent(this@PetPlusActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }

        }

    }

    fun showDisRecord(_binding: ActivityPetPlusBinding) {
        _binding!!.disTableLayout.removeAllViewsInLayout()

        val rowParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        val viewParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100)

        if(disList.isEmpty()) {
            Log.d("dislist", "empty")
            return
        }

        for(i in disList)
        {
            val row = TableRow(this)
            row.layoutParams = rowParam
            row.setOnClickListener {
                //클릭하면 삭제가능하게끔
                val textView = row.getChildAt(0) as TextView
                val text = textView.text.toString()

                val dlgBuilder = AlertDialog.Builder(this@PetPlusActivity)
                dlgBuilder.setTitle("보유질병 삭제")
                    .setMessage(text + "을(를) 삭제하시겠습니까?")
                    .setPositiveButton("삭제"){
                            _, _ ->
                        disList.remove(text)
                        showDisRecord(_binding)
                    }
                    .setNegativeButton("취소"){
                            _, _ ->
                    }
                    .show()
            }
            val textView = TextView(this)
            textView.layoutParams = viewParam
            textView.text = i
            textView.textSize = 18.0f
            textView.gravity = Gravity.CENTER
            row.addView(textView)

            _binding.disTableLayout.addView(row)
        }

    }

    fun showInoRecord(_binding: ActivityPetPlusBinding) {
        _binding!!.inoTableLayout.removeAllViewsInLayout()

        val rowParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        val viewParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100)

        if(inoList.isEmpty()) {
            Log.d("inolist", "empty")
            return
        }

        for(i in inoList)
        {
            val row = TableRow(this)
            row.layoutParams = rowParam
            row.setOnClickListener {
                //클릭하면 삭제가능하게끔
                val textView = row.getChildAt(0) as TextView
                val text = textView.text.toString()
                val dlgBuilder = AlertDialog.Builder(this@PetPlusActivity)
                dlgBuilder.setTitle("접종이력 삭제")
                    .setMessage(text + "을(를)삭제하시겠습니까?")
                    .setPositiveButton("삭제"){
                            _, _ ->
                        inoList.remove(text)
                        showInoRecord(_binding)
                    }
                    .setNegativeButton("취소"){
                            _, _ ->
                    }
                    .show()
            }
            val textView = TextView(this)
            textView.layoutParams = viewParam
            textView.text = i
            textView.textSize = 18.0f
            textView.gravity = Gravity.CENTER
            row.addView(textView)

            _binding.inoTableLayout.addView(row)
        }

    }
}