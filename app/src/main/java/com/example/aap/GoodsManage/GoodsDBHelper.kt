package com.example.aap.GoodsManage

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import com.example.aap.databinding.FragmentGoodsBinding
import java.util.ArrayList

class GoodsDBHelper(val cont: Context) : SQLiteOpenHelper(cont, DB_NAME, null, DB_VERSION) {

    companion object {
        val DB_NAME = "goods_db.db"
        val DB_VERSION = 1

        // goods 테이블 정보
        val GOODS_TABLE_NAME = "goods"
        val GID = "gid"
        val GNAME = "gname"
        val GCOUNT = "gcount"
        val PETID = "petid"

    }

    var goodsClickListener: onGoodsClickListener? = null

    interface onGoodsClickListener {
        fun onGoodsClick(_gid: Int, _gname: String, _gcount: Int)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        var create_table = "create table if not exists $GOODS_TABLE_NAME(" +
                "$GID integer primary key autoincrement, " +
                "$GNAME text, " +
                "$GCOUNT integer, " +
                "$PETID integer not null);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        var drop_table = "drop table if exists $GOODS_TABLE_NAME ;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun deleteGoods(_gid: Int) {
        val db = writableDatabase
        db.delete(GOODS_TABLE_NAME, "$GID=?", arrayOf(_gid.toString()))
        db.close()
    }


    fun updateGoods(_gid: Int, _gname: String, _gcount: Int) {
        val db = writableDatabase

        val values: ContentValues = ContentValues()
        values.put(GNAME, _gname)
        values.put(GCOUNT, _gcount)

        db.update(GOODS_TABLE_NAME, values, "$GID=?", arrayOf(_gid.toString()))
        db.close()
    }

    fun insertGoods(_gname: String, _gcount: Int, _petId: Int): Boolean {
        val db = writableDatabase

        val values: ContentValues = ContentValues()
        values.put(GNAME, _gname)
        values.put(GCOUNT, _gcount)
        values.put(PETID, _petId)

        val flag = db.insert(GOODS_TABLE_NAME, null, values) > 0

        db.close()
        return flag
    }

    fun getAllRecord(_binding: FragmentGoodsBinding, _petId: Int) {
        _binding!!.goodsTableLayout.removeAllViewsInLayout()

        var strsql = "select * from $GOODS_TABLE_NAME where $PETID = '$_petId';"
        val db = readableDatabase
        var cursor = db.rawQuery(strsql, null)
        if (cursor.count != 0) {
            showGoodsRecord(cursor, _binding)
        }
        cursor.close()
        db.close()
    }

    fun showGoodsRecord(cursor: Cursor, _binding: FragmentGoodsBinding) {
        cursor.moveToFirst()

        // 레코드 추가하기 - 데이터베이스의 레코드를 읽어서 추가시켜주기
        val rowParam = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        val viewParam0 = TableRow.LayoutParams(0, 100, 2f)
        val viewParam1 = TableRow.LayoutParams(0, 100, 7f)
        val viewParam2 = TableRow.LayoutParams(0, 100, 7f)

        var index = 1


        do {
            val _gid: Int = cursor.getString(0).toInt()
            val _gname: String = cursor.getString(1)
            val _gcount: Int = cursor.getString(2).toInt()

            val row = TableRow(cont)
            row.layoutParams = rowParam

            val indexTextView = TextView(cont)
            indexTextView.tag = 0
            indexTextView.layoutParams = viewParam0
            indexTextView.text = index.toString()
            index++
            indexTextView.textSize = 13.0f
            indexTextView.gravity = Gravity.CENTER
            row.addView(indexTextView)

            for (i in 1 until 3) {
                val textView = TextView(cont)
                textView.tag = i //i에 해당하는 테그값을 줌
                if (i == 1) {
                    textView.layoutParams = viewParam1
                } else if (i == 2) {
                    textView.layoutParams = viewParam2
                }
                textView.text = cursor.getString(i)
                textView.textSize = 13.0f
                textView.gravity = Gravity.CENTER

                textView.setOnLongClickListener {
                    goodsClickListener!!.onGoodsClick(_gid, _gname, _gcount)
                    true
                }
                row.addView(textView)
            }

            row.setOnLongClickListener {
                goodsClickListener!!.onGoodsClick(_gid, _gname, _gcount)
                true
            }
            _binding!!.goodsTableLayout.addView(row)

        } while (cursor.moveToNext()) //val recordCount = cursor.count
    }

    fun getGoodsNum(_petId: Int): Int {
        var strsql = "select * from $GOODS_TABLE_NAME where $PETID = '$_petId';"
        val db = readableDatabase
        var cursor = db.rawQuery(strsql, null)

        val number = cursor.count

        cursor.close()
        db.close()

        return number
    }

    fun dailyUpdateGoods(_petId: Int, _binding: FragmentGoodsBinding) {

        _binding!!.goodsTableLayout.removeAllViewsInLayout()

        val db = writableDatabase
        var strsql = "select * from $GOODS_TABLE_NAME where $PETID = '$_petId';"
        var cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()

        do {
            val _gid: Int = cursor.getString(0).toInt()
            var _gcount: Int = cursor.getString(2).toInt()

            val values: ContentValues = ContentValues()
            if(_gcount > 0){
                _gcount--
            }
            values.put(GCOUNT, _gcount)

            db.update(GOODS_TABLE_NAME, values, "$GID=?", arrayOf(_gid.toString()))

        } while (cursor.moveToNext()) //val recordCount = cursor.count

        cursor.moveToFirst()
        if (cursor.count != 0) {

            showGoodsRecord(cursor, _binding)
        }

        cursor.close()
        db.close()
    }

    fun getGraphContents(_petId: Int, _labelList : ArrayList<String>, _valList : ArrayList<Int>){
        var strsql = "select * from $GOODS_TABLE_NAME where $PETID = '$_petId';"
        val db = readableDatabase
        var cursor = db.rawQuery(strsql, null)
        if (cursor.count != 0) {
            cursor.moveToFirst()
            do {
                val _gname: String = cursor.getString(1)
                val _gcount: Int = cursor.getString(2).toInt()
                _labelList.add(_gname)
                _valList.add(_gcount)
            } while (cursor.moveToNext()) //val recordCount = cursor.count
        }
        cursor.close()
        db.close()
    }

}