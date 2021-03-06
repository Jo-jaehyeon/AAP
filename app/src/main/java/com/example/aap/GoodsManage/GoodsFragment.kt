package com.example.aap.GoodsManage

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.aap.Alarm.GoodsAlarmReceiver
import com.example.aap.PetInfo.PetIdName
import com.example.aap.PetInfo.PetInfoDBHelper
import com.example.aap.R
import com.example.aap.databinding.FragmentGoodsBinding
import com.example.aap.databinding.GoodslistBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.ArrayList

class GoodsFragment : Fragment() {
    var binding: FragmentGoodsBinding? = null
    lateinit var dbHelper: GoodsDBHelper
    lateinit var petDBHelper: PetInfoDBHelper
    var petId: Int = 0

    lateinit var calendar: Calendar
    lateinit var myintent: Intent
    lateinit var alarmManager: AlarmManager
    var pendingIntent: PendingIntent? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGoodsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpinner()
        init()
        activity?.registerReceiver(receiver, IntentFilter("com.example.MYALARM"))
        drawGraph()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(receiver)
        binding = null
    }

    fun init() {
        dbHelper = GoodsDBHelper(requireActivity())
        dbHelper.goodsClickListener = object : GoodsDBHelper.onGoodsClickListener {
            override fun onGoodsClick(_gid: Int, _gname: String, _gcount: Int) {
                val goodslistBinding = GoodslistBinding.inflate(layoutInflater)
                goodslistBinding.gnameEdit.setText(_gname)
                goodslistBinding.gcountEdit.setText(_gcount.toString())

                val goodslistBuilder = AlertDialog.Builder(requireActivity())
                goodslistBuilder.setView(goodslistBinding.root)
                        .setPositiveButton("??????") { _, _ ->
                            var gname = goodslistBinding.gnameEdit.text.toString()
                            var gcount = goodslistBinding.gcountEdit.text.toString().toInt()
                            dbHelper.updateGoods(_gid, gname, gcount)
                            dbHelper.getAllRecord(binding!!, petId)
                            drawGraph()
                            Toast.makeText(requireActivity(), "?????? ?????? ??????", Toast.LENGTH_SHORT).show()

                        }
                        .setNeutralButton("??????") { _, _ ->
                            Toast.makeText(requireActivity(), "??????", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("??????") { _, _ ->
                            dbHelper.deleteGoods(_gid)
                            dbHelper.getAllRecord(binding!!, petId)

                            if ((dbHelper.getGoodsNum(petId)) == 0) {
                                deleteAlarm()
                            }

                            drawGraph()
                            Toast.makeText(requireActivity(), "?????? ?????? ??????", Toast.LENGTH_SHORT).show()
                        }
                        .show()
            }
        }

        dbHelper.getAllRecord(binding!!, petId)

        binding!!.addGoods.setOnClickListener {
            val goodslistBinding = GoodslistBinding.inflate(layoutInflater)
            val goodslistBuilder = AlertDialog.Builder(requireActivity())
            goodslistBuilder.setView(goodslistBinding.root)
                    .setPositiveButton("??????") { _, _ ->
                        var gname = goodslistBinding.gnameEdit.text.toString()
                        var gcount = 0
                        try {
                            gcount = goodslistBinding.gcountEdit.text.toString().toInt()
                        } catch (e: NumberFormatException) {
                            Toast.makeText(requireActivity(), "?????? ????????? ?????? ????????? ????????????. ??????????????????.", Toast.LENGTH_SHORT).show()
                        }
                        dbHelper.insertGoods(gname, gcount, petId)
                        dbHelper.getAllRecord(binding!!, petId)

                        val _count = dbHelper.getGoodsNum(petId)
                        if (_count == 1) {
                            setAlarm()
                        }

                        drawGraph()
                        Toast.makeText(requireActivity(), "?????? ?????? ??????. ?????? ????????? ???????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()

                    }
                    .setNegativeButton("??????") { _, _ ->
                        Toast.makeText(requireActivity(), "??????", Toast.LENGTH_SHORT).show()
                    }
                    .show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun setAlarm() {
        calendar = Calendar.getInstance()

        //var _hour = calendar.get(Calendar.HOUR_OF_DAY)
        //calendar.set(Calendar.HOUR_OF_DAY, _hour)
        //calendar.set(Calendar.MINUTE, 45)


        //???????????? ??????
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 20)
        calendar.set(Calendar.MILLISECOND, 0)

        //Receiver ??????
        myintent = Intent(requireActivity(), GoodsAlarmReceiver::class.java)
        //myintent.putExtra("number", binding)
        pendingIntent = PendingIntent.getBroadcast(
                requireActivity(),
                10,
                myintent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )


        //?????? ??????
        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun deleteAlarm() {
        if (pendingIntent != null) {
            Toast.makeText(requireActivity(), "????????? ????????? ????????????. ?????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()

            alarmManager.cancel(pendingIntent)
            pendingIntent = null
        }
    }

    var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val mode = intent!!.getStringExtra("mode")
            if (mode != null) {
                when (mode) {
                    "update" -> {
                        dbHelper.dailyUpdateGoods(petId, binding!!)
                        dbHelper.getAllRecord(binding!!, petId)
                        drawGraph()
                    }
                }
            }
        }
    }

    val labelList = ArrayList<String>()
    val valList = ArrayList<Int>()

    fun drawGraph() {
        labelList.clear()
        valList.clear()
        dbHelper.getGraphContents(petId, labelList, valList)

        val entries = ArrayList<BarEntry>()
        for (i in 0 until valList.size) {
            entries.add(BarEntry(i.toFloat(), valList.get(i).toFloat()))
        }

        val barDataSet: BarDataSet = BarDataSet(entries, "???????????? ??????")
        barDataSet.color = ColorTemplate.rgb("#ff7b22")
        barDataSet.valueTextSize = 12f

        val barData = BarData(barDataSet)
        barData.barWidth = 0.35f


        binding!!.barChart.apply {
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (valList.size > value.toInt()) {
                        return labelList[value.toInt()]
                    } else {
                        return "??????"
                    }

                }
            }

            data = barData

            //Chart??? ???????????? ???????????????
            animateXY(0, 800)

            //??????, Pinch ????????????
            setScaleEnabled(false)
            setPinchZoom(false)

            //Chart ?????? description ?????? ??????
            description = null

            //Legend??? ????????? ????????? ???????????????
            //????????? ????????? ????????? ??????
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT

            //????????? ???, ?????? ??????/???????????? ???????????????.
            //?????? ?????? ?????? 0?????? ???????????? ?????? ?????? ???????????????.
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f

            //xAxis, yAxis ?????? ???????????? ?????? ????????? ???????????????
            //?????? ????????? Grid ?????? ??????
            xAxis.setDrawGridLines(false)

            //??????????????? ?????? ?????? ????????? ???????????? ???????????????
            //?????? ?????????/???????????? ?????? ??????
            axisRight.setDrawLabels(false)

            //x??? ????????? ?????? ??????
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            //x??? ????????? ?????? ??????
            xAxis.labelCount = entries.size

            invalidate()
        }

    }

    fun initSpinner() {
        petDBHelper = PetInfoDBHelper(requireActivity())

        //????????????????????? ??????????????? ???????????? ????????????(????????? ??????)??? string?????? ??????(????????? ??????)??? ???????????? ????????? ??????
        val adapter = ArrayAdapter<String>(
                requireActivity(),
                //android.R.layout.simple_spinner_dropdown_item,
                R.layout.spinner_item,
                java.util.ArrayList<String>()
        )

        val petList: java.util.ArrayList<PetIdName> = petDBHelper.getAllPetIDName()
        for (i in 0 until petList.size) {
            adapter.add(petList[i].name)
            Log.i("pet name" , petList[i].name)
        }



        binding!!.apply {
            goodsSpinner.adapter = adapter

            if(!goodsSpinner.adapter.isEmpty) {
                goodsSpinner.setSelection(0)
            }

            goodsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {
                    petId = petList[position].id
                    dbHelper.getAllRecord(binding!!, petId)
                    drawGraph()
                }

            }


        }
    }


}