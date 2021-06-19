package com.example.aap.Schedule

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import com.example.aap.Alarm.ScheduleAlarmReceiver
import com.example.aap.PetInfo.PetIdName
import com.example.aap.PetInfo.PetInfoDBHelper
import com.example.aap.R
import com.example.aap.databinding.ChecklistBinding
import com.example.aap.databinding.FragmentScheduleBinding
import com.example.aap.databinding.WalklistBinding
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class ScheduleFragment : Fragment() {
    var binding: FragmentScheduleBinding? = null
    var myyear = 0
    var mymonth = 0
    var mydayOfMonth = 0
    private val OPEN_GALLERY = 1
    lateinit var dbHelper: DBHelper
    lateinit var petDBHelper : PetInfoDBHelper

    //임의의값
    var petId: Int = 0

    lateinit var calendar: Calendar
    lateinit var myintent: Intent
    lateinit var alarmManager: AlarmManager
    var pintentArray: HashMap<Int, PendingIntent> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpinner()
        initAlarm()
        init()

    }

    fun getAllRecord(
        _year: Int,
        _month: Int,
        _day: Int,
        _binding: FragmentScheduleBinding,
        _petId: Int
    ) {
        dbHelper.getAllRecord(_year, _month, _day, _binding, _petId)
    }

    fun init() {
        dbHelper = DBHelper(requireActivity())
        dbHelper.checkListClickListener = object : DBHelper.onCheckListClickListener {
            override fun onCheckListClick(_cid: Int, _ccontent: String) {
                val checklistBinding = ChecklistBinding.inflate(layoutInflater)
                checklistBinding.contentEdit.setText(_ccontent)

                val checklistBuilder = AlertDialog.Builder(requireActivity())
                checklistBuilder.setView(checklistBinding.root)
                    .setPositiveButton("수정") { _, _ ->
                        var ccontent = checklistBinding.contentEdit.text.toString()
                        dbHelper.updateCheck(_cid, ccontent)
                        getAllRecord(myyear, mymonth, mydayOfMonth, binding!!, petId)
                    }
                    .setNeutralButton("취소") { _, _ ->
                    }
                    .setNegativeButton("삭제") { _, _ ->
                        dbHelper.deleteCheck(_cid)
                        getAllRecord(myyear, mymonth, mydayOfMonth, binding!!, petId)

                        val _code: List<Int> = dbHelper.getCheckNum(myyear, mymonth, mydayOfMonth, petId)
                        if (_code[0] == -1) {
                            deleteAlarm(_code[1])

                        }
                    }
                    .show()
            }
        }

        dbHelper.walkListClickListener = object : DBHelper.onWalkListClickListener {
            override fun onWalkListClick(_wid: Int, _wtime: String, _wspecial: String) {
                val walklistBinding = WalklistBinding.inflate(layoutInflater)
                walklistBinding.walkTimeEdit.setText(_wtime)
                walklistBinding.specialEdit.setText(_wspecial)

                val walklistBuilder = AlertDialog.Builder(requireActivity())
                walklistBuilder.setView(walklistBinding.root)
                    .setPositiveButton("수정") { _, _ ->
                        var walkTime = walklistBinding.walkTimeEdit.text.toString()
                        var special = walklistBinding.specialEdit.text.toString()
                        dbHelper.updateWalk(_wid, walkTime, special)
                        getAllRecord(myyear, mymonth, mydayOfMonth, binding!!, petId)
                    }
                    .setNegativeButton("삭제") { _, _ ->
                        dbHelper.deleteWalk(_wid)
                        getAllRecord(myyear, mymonth, mydayOfMonth, binding!!, petId)
                    }
                    .setNeutralButton("취소") { _, _ ->
                    }
                    .show()
            }

        }

        binding!!.apply {
            addCheck.setOnClickListener {
                val checklistBinding = ChecklistBinding.inflate(layoutInflater)
                val checklistBuilder = AlertDialog.Builder(requireActivity())
                checklistBuilder.setView(checklistBinding.root)
                    .setPositiveButton("추가") { _, _ ->
                        var ccontent = checklistBinding.contentEdit.text.toString()

                        dbHelper.insertCheck(ccontent, myyear, mymonth, mydayOfMonth, petId)
                        getAllRecord(myyear, mymonth, mydayOfMonth, binding!!, petId)

                        val _code: List<Int> = dbHelper.getCheckNum(myyear, mymonth, mydayOfMonth, petId)
                        if (_code[0] != -1) {
                            setAlarm(_code[1], myyear, mymonth - 1, mydayOfMonth, _code[0])

                        }

                    }
                    .setNegativeButton("취소") { _, _ ->
                    }
                    .show()
            }

            addWalk.setOnClickListener {
                val walklistBinding = WalklistBinding.inflate(layoutInflater)
                val walklistBuilder = AlertDialog.Builder(requireActivity())
                walklistBuilder.setView(walklistBinding.root)
                    .setPositiveButton("추가") { _, _ ->
                        var walkTime = walklistBinding.walkTimeEdit.text.toString()
                        var special = walklistBinding.specialEdit.text.toString()

                        dbHelper.insertWalk(walkTime, special, myyear, mymonth, mydayOfMonth, petId)
                        getAllRecord(myyear, mymonth, mydayOfMonth, binding!!, petId)
                    }
                    .setNegativeButton("취소") { _, _ ->
                    }
                    .show()
            }

            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                myyear = year
                mymonth = month + 1
                mydayOfMonth = dayOfMonth
                checkText.text = "$myyear 년 $mymonth 월 $mydayOfMonth 일의 체크리스트"
                walkText.text = "$myyear 년 $mymonth 월 $mydayOfMonth 일의 산책"
                getAllRecord(myyear, mymonth, mydayOfMonth, binding!!, petId)
            }

            imgBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                startActivityForResult(intent, OPEN_GALLERY)
            }

            myyear = calendar.get(Calendar.YEAR)
            mymonth = calendar.get(Calendar.MONTH) + 1
            mydayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            checkText.text = "$myyear 년 $mymonth 월 $mydayOfMonth 일의 체크리스트"
            walkText.text = "$myyear 년 $mymonth 월 $mydayOfMonth 일의 산책"
            getAllRecord(myyear, mymonth, mydayOfMonth, binding!!, petId)


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
        {
            var currentImage: Uri? = data?.data

            try
            {
                val bitmap:Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, currentImage)
                var StoragePath = context?.cacheDir.toString() + "/Pet/Image/$petId"

                var Folder = File(StoragePath)
                if(!Folder.exists())        //폴더 없으면 생성
                    Folder.mkdirs()

                val date = "$myyear" + "_$mymonth" + "_$mydayOfMonth"
                var fileName = "$date.png"
                val ImagePath = "$StoragePath/$fileName"

                var file = File(StoragePath, fileName)


                val fos = FileOutputStream(file)
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.close()

                if(petDBHelper.insertPetAlbum(ImagePath, date, petId))
                    Toast.makeText(activity, "저장이 성공하였습니다", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(activity, "저장이 실패하였습니다", Toast.LENGTH_SHORT).show()

            }catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
        else
        {
            Log.d("Camera", "someting wrong")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun initAlarm() {
        //알람 설정
        calendar = Calendar.getInstance()
        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //myintent = Intent(requireActivity(), AlarmReceiver::class.java)
    }

    fun setAlarm(_code: Int, _year: Int, _month: Int, _date: Int, _count: Int) {
        //calendar = Calendar.getInstance()

        //var _hour = calendar.get(Calendar.HOUR_OF_DAY)
        //calendar.set(Calendar.HOUR_OF_DAY, _hour)
        //calendar.set(Calendar.MINUTE, 45)

        //알람시간 설정
        calendar.set(Calendar.YEAR, _year)
        calendar.set(Calendar.MONTH, _month)
        calendar.set(Calendar.DATE, _date)

        calendar.set(Calendar.HOUR_OF_DAY, 7)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        //Receiver 설정
        myintent = Intent(requireActivity(), ScheduleAlarmReceiver::class.java)
        myintent.putExtra("number", _count.toString())
        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity(),
            _code,
            myintent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        //알람 설정
        //alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        Toast.makeText(requireActivity(), "해당일의 스케줄 알림이 등록되었습니다.", Toast.LENGTH_SHORT).show()

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        if (pintentArray.containsKey(_code)) {
            pintentArray.remove(_code)

        }
        pintentArray.put(_code, pendingIntent)

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)

    }

    fun deleteAlarm(_code: Int) {
        val pendingIntent: PendingIntent? = pintentArray.get(_code)
        if(pendingIntent != null){
            Toast.makeText(requireActivity(), "해당일의 스케줄 알림이 모두 취소되었습니다.", Toast.LENGTH_SHORT).show()

            alarmManager.cancel(pendingIntent)
        }
    }

    fun initSpinner() {
        petDBHelper = PetInfoDBHelper(requireActivity())

        //안드로이드에서 기본적으로 제공하는 레이아웃(두번째 인자)에 string배열 값을(세번째 인자)를 매핑하는 어뎁터 생성
        val adapter = ArrayAdapter<String>(
                requireActivity(),
                R.layout.spinner_item,
                java.util.ArrayList<String>()
        )

        val petList: java.util.ArrayList<PetIdName> = petDBHelper.getAllPetIDName()
        for (i in 0 until petList.size) {
            adapter.add(petList[i].name)
            Log.i("pet name" , petList[i].name)
        }



        binding!!.apply {
            scheduleSpinner.adapter = adapter

            if(!scheduleSpinner.adapter.isEmpty) {
                scheduleSpinner.setSelection(0)
            }

            scheduleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {
                    petId = petList[position].id
                    getAllRecord(myyear, mymonth, mydayOfMonth, binding!!, petId)
                }

            }


        }
    }
}