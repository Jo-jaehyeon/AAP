package com.example.aap.Schedule

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aap.R
import com.example.aap.databinding.ChecklistBinding
import com.example.aap.databinding.FragmentScheduleBinding
import com.example.aap.databinding.WalklistBinding

class ScheduleFragment : Fragment() {
    var binding: FragmentScheduleBinding? = null
    var myyear = 0
    var mymonth = 0
    var mydayOfMonth = 0
    lateinit var dbHelper: DBHelper

    //임의의값
    var petId : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun getAllRecord(_year: Int, _month: Int, _day: Int, _binding: FragmentScheduleBinding, _petId : Int) {
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
                    }
                    .show()
            }
        }

        dbHelper.walkListClickListener = object : DBHelper.onWalkListClickListener{
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
                        Log.i(
                            "check",
                            ccontent + "," + myyear.toString() + mymonth.toString() + mydayOfMonth.toString()
                        )
                        dbHelper.insertCheck(ccontent, myyear, mymonth, mydayOfMonth, petId)
                        getAllRecord(myyear, mymonth, mydayOfMonth, binding!!, petId)
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
                mymonth = month+1
                mydayOfMonth = dayOfMonth
                checkText.text = "$myyear 년 $mymonth 월 $mydayOfMonth 일의 체크리스트"
                walkText.text = "$myyear 년 $mymonth 월 $mydayOfMonth 일의 산책"
                getAllRecord(myyear, mymonth, mydayOfMonth, binding!!, petId)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}