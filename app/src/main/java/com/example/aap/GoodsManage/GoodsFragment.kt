package com.example.aap.GoodsManage

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.aap.R
import com.example.aap.databinding.FragmentGoodsBinding
import com.example.aap.databinding.GoodslistBinding
import java.lang.NumberFormatException

class GoodsFragment : Fragment() {
    var binding: FragmentGoodsBinding? = null
    lateinit var dbHelper: GoodsDBHelper
    var petId : Int = 0

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
                        }
                        .setNeutralButton("취소") { _, _ ->
                        }
                        .setNegativeButton("삭제") { _, _ ->
                            dbHelper.deleteGoods(_gid)
                            dbHelper.getAllRecord(binding!!, petId)
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
                            Toast.makeText(requireActivity(), "숫자형식이 아닙니다! 수정해주세요.", Toast.LENGTH_SHORT).show()
                        }
                        dbHelper.insertGoods(gname, gcount, petId)
                        dbHelper.getAllRecord(binding!!, petId)
                    }
                    .setNegativeButton("취소") { _, _ ->
                    }
                    .show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}