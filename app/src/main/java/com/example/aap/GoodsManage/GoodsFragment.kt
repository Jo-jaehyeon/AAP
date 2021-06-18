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
import android.widget.Toast
import com.example.aap.Alarm.GoodsAlarmReceiver
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
    var petId : Int = 0

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
        init()
        activity?.registerReceiver(receiver, IntentFilter("com.example.MYALARM"))

        drawGraph()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(receiver)
        binding = null
    }

    fun init(){
        dbHelper = GoodsDBHelper(requireActivity())
        dbHelper.goodsClickListener = object : GoodsDBHelper.onGoodsClickListener {
            override fun onGoodsClick(_gid: Int, _gname: String, _gcount: Int) {
                val goodslistBinding = GoodslistBinding.inflate(layoutInflater)
                goodslistBinding.gnameEdit.setText(_gname)
                goodslistBinding.gcountEdit.setText(_gcount.toString())

                val goodslistBuilder = AlertDialog.Builder(requireActivity())
                goodslistBuilder.setView(goodslistBinding.root)
                    .setPositiveButton("수정") { _, _ ->
                        var gname = goodslistBinding.gnameEdit.text.toString()
                        var gcount = goodslistBinding.gcountEdit.text.toString().toInt()
                        dbHelper.updateGoods(_gid, gname, gcount)
                        dbHelper.getAllRecord(binding!!, petId)
                        drawGraph()
                        Toast.makeText(requireActivity(), "물품 수정 완료", Toast.LENGTH_SHORT).show()

                    }
                    .setNeutralButton("취소") { _, _ ->
                        Toast.makeText(requireActivity(), "취소", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("삭제") { _, _ ->
                        dbHelper.deleteGoods(_gid)
                        dbHelper.getAllRecord(binding!!, petId)

                        if((dbHelper.getGoodsNum(petId)) == 0){
                            deleteAlarm()
                        }

                        drawGraph()
                        Toast.makeText(requireActivity(), "물품 삭제 완료", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        }

        dbHelper.getAllRecord(binding!!, petId)

        binding!!.addGoods.setOnClickListener {
            val goodslistBinding = GoodslistBinding.inflate(layoutInflater)
            val goodslistBuilder = AlertDialog.Builder(requireActivity())
            goodslistBuilder.setView(goodslistBinding.root)
                .setPositiveButton("추가") { _, _ ->
                    var gname = goodslistBinding.gnameEdit.text.toString()
                    var gcount = 0
                    try {
                        gcount = goodslistBinding.gcountEdit.text.toString().toInt()
                    }
                    catch (e : NumberFormatException){
                        Toast.makeText(requireActivity(), "물품 수량이 숫자 형식이 아닙니다. 수정해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    dbHelper.insertGoods(gname, gcount, petId)
                    dbHelper.getAllRecord(binding!!, petId)

                    val _count = dbHelper.getGoodsNum(petId)
                    if( _count == 1){
                        setAlarm()
                    }

                    drawGraph()
                    Toast.makeText(requireActivity(), "물품 추가 완료. 매일 자정에 자동으로 물품 수량을 관리합니다.", Toast.LENGTH_SHORT).show()

                }
                .setNegativeButton("취소") { _, _ ->
                    Toast.makeText(requireActivity(), "취소", Toast.LENGTH_SHORT).show()
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


        //알람시간 설정
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        //Receiver 설정
        myintent = Intent(requireActivity(), GoodsAlarmReceiver::class.java)
        //myintent.putExtra("number", binding)
        pendingIntent = PendingIntent.getBroadcast(
            requireActivity(),
            10,
            myintent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        //알람 설정
        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun deleteAlarm() {
        if(pendingIntent != null){
            Toast.makeText(requireActivity(), "관리할 물품이 없습니다. 자동 물품 수량 관리를 종료합니다.", Toast.LENGTH_SHORT).show()

            alarmManager.cancel(pendingIntent)
            pendingIntent = null
        }
    }

    var receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val mode = intent!!.getStringExtra("mode")
            if(mode != null){
                when(mode){
                    "update"->{
                        dbHelper.dailyUpdateGoods(petId,binding!!)
                        dbHelper.getAllRecord(binding!!, petId)
                        drawGraph()
                    }
                }
            }
        }
    }

    val labelList = ArrayList<String>()
    val valList = ArrayList<Int>()

    fun drawGraph(){
        labelList.clear()
        valList.clear()
        dbHelper.getGraphContents(petId, labelList, valList)

        val entries = ArrayList<BarEntry>()
        for(i in 0 until valList.size){
            entries.add(BarEntry(i.toFloat(), valList.get(i).toFloat()))
        }

        val barDataSet : BarDataSet = BarDataSet(entries, "물품수량 현황")
        barDataSet.color = ColorTemplate.rgb("#ff7b22")
        barDataSet.valueTextSize = 12f

        val barData = BarData(barDataSet)
        barData.barWidth = 0.35f


        binding!!.barChart.apply {
            xAxis.valueFormatter = object : ValueFormatter(){
                override fun getFormattedValue(value: Float): String {
                    if(valList.size > value.toInt()){
                        return labelList[value.toInt()]
                    }
                    else{
                        return "기타"
                    }

                }
            }

            data = barData

            //Chart가 그려질때 애니메이션
            animateXY(0,800)

            //터치, Pinch 상호작용
            setScaleEnabled(false)
            setPinchZoom(false)

            //Chart 밑에 description 표시 유무
            description=null

            //Legend는 차트의 범례를 의미합니다
            //범례가 표시될 위치를 설정
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT

            //차트의 좌, 우측 최소/최대값을 설정합니다.
            //차트 제일 밑이 0부터 시작하고 싶은 경우 설정합니다.
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f

            //xAxis, yAxis 둘다 존재하여 따로 설정이 가능합니다
            //차트 내부에 Grid 표시 유무
            xAxis.setDrawGridLines(false)

            //기본적으로 차트 우측 축에도 데이터가 표시됩니다
            //이를 활성화/비활성화 하기 위함
            axisRight.setDrawLabels(false)

            //x축 데이터 표시 위치
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            //x축 데이터 갯수 설정
            xAxis.labelCount = entries.size

            invalidate()
        }




    }

}