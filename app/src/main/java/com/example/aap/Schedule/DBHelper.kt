package com.example.aap.Schedule

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.view.Gravity
import android.widget.CheckBox
import android.widget.TableRow
import android.widget.TextView
import com.example.aap.databinding.FragmentScheduleBinding


class DBHelper(val cont: Context) : SQLiteOpenHelper(cont, DB_NAME, null, DB_VERSION) {

    companion object {
        val DB_NAME = "schedule_db.db"
        val DB_VERSION = 1

        //dates 테이블 정보
        val DATE_TABLE_NAME = "dates"
        val DID = "did"
        val DYEAR = "dyear"
        val DMONTH = "dmonth"
        val DDAY = "dday"
        val PETID = "petid"

        // walks 테이블 정보
        val WALK_TABLE_NAME = "walks"
        val WID = "wid"
        val WTIME = "wtime"
        val WSPECIAL = "wspecial"

        //checks 테이블 정보
        val CHECK_TABLE_NAME = "checks"
        val CID = "cid"
        val CCONTENT = "ccontent"
        val CCHECK = "ccheck"
    }

    var checkListClickListener: onCheckListClickListener? = null
    var walkListClickListener: onWalkListClickListener? = null

    interface onCheckListClickListener {
        fun onCheckListClick(_cid: Int, _ccontent: String)
    }

    interface onWalkListClickListener {
        fun onWalkListClick(_wid: Int, _wtime: String, _wspecial: String)
    }

    override fun onCreate(db: SQLiteDatabase?) {

        var create_table = "create table if not exists $DATE_TABLE_NAME(" +
                "$DID integer primary key autoincrement, " +
                "$DYEAR integer not null, " +
                "$DMONTH integer not null, " +
                "$DDAY integer not null, " +
                "$PETID integer not null);"
        db!!.execSQL(create_table)

        create_table = "create table if not exists $WALK_TABLE_NAME(" +
                "$WID integer primary key autoincrement, " +
                "$WTIME text, " +
                "$WSPECIAL text, " +
                "$DID integer not null," +
                "FOREIGN KEY($DID) REFERENCES $DATE_TABLE_NAME($DID) );"
        db!!.execSQL(create_table)

        create_table = "create table if not exists $CHECK_TABLE_NAME(" +
                "$CID integer primary key autoincrement, " +
                "$CCONTENT text, " +
                "$CCHECK integer not null, " +
                "$DID integer not null," +
                "FOREIGN KEY($DID) REFERENCES $DATE_TABLE_NAME($DID) );"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        var drop_table = "drop table if exists $DATE_TABLE_NAME;"
        db!!.execSQL(drop_table)

        drop_table = "drop table if exists $WALK_TABLE_NAME;"
        db!!.execSQL(drop_table)

        drop_table = "drop table if exists $CHECK_TABLE_NAME;"
        db!!.execSQL(drop_table)

        onCreate(db)
    }

    fun getDid(_year: Int, _month: Int, _day: Int, _petId: Int): Int {
        var strsql =
            "select $DID from $DATE_TABLE_NAME where $DYEAR = '$_year' and  $DMONTH = '$_month' and  $DDAY = '$_day' and $PETID = '$_petId';"
        val db = readableDatabase
        var cursor: Cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()

        if (cursor.count == 0) {
            cursor.close()
            db.close()
            return -1
        }

        val dateId: Int = cursor.getString(0).toInt()
        cursor.close()
        db.close()
        return dateId
    }

    fun getAllRecord(_year: Int, _month: Int, _day: Int, _binding: FragmentScheduleBinding, _petId: Int) {
        _binding!!.walkTableLayout.removeAllViewsInLayout()
        _binding!!.checkTableLayout.removeAllViewsInLayout()

        val dateId: Int = getDid(_year, _month, _day, _petId)
        Log.i("dataId_ver3", dateId.toString())
        if (dateId == -1)
            return

        var strsql = "select * from $WALK_TABLE_NAME where $DID = '$dateId';"
        val db = readableDatabase
        var cursor = db.rawQuery(strsql, null)
        if (cursor.count != 0) {
            showWalkRecord(cursor, _binding)
        }


        strsql = "select * from $CHECK_TABLE_NAME where $DID = '$dateId';"
        cursor = db.rawQuery(strsql, null)
        if (cursor.count != 0) {
            Log.i("cursor count", "checklist 0이 아니었다!!")
            showCheckRecord(cursor, _binding)
        }


        cursor.close()
        db.close()
    }


    fun showWalkRecord(cursor: Cursor, _binding: FragmentScheduleBinding) {
        cursor.moveToFirst()

        // 레코드 추가하기 - 데이터베이스의 레코드를 읽어서 추가시켜주기
        val rowParam = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        val viewParam0 = TableRow.LayoutParams(0, 100, 2f)
        val viewParam1 = TableRow.LayoutParams(0, 100, 5f)
        val viewParam2 = TableRow.LayoutParams(0, 100, 10f)

        var index = 1

        do {
            var _wid: Int = cursor.getString(0).toInt()
            var _wtime: String = cursor.getString(1)
            var _wspecial: String = cursor.getString(2)

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
                    walkListClickListener!!.onWalkListClick(_wid, _wtime, _wspecial)
                    true
                }
                row.addView(textView)
            }

            row.setOnLongClickListener {
                walkListClickListener!!.onWalkListClick(_wid, _wtime, _wspecial)
                true
            }
            _binding!!.walkTableLayout.addView(row)

        } while (cursor.moveToNext()) //val recordCount = cursor.count
    }


    fun showCheckRecord(cursor: Cursor, _binding: FragmentScheduleBinding) {
        cursor.moveToFirst()

        // 레코드 추가하기 - 데이터베이스의 레코드를 읽어서 추가시켜주기
        val rowParam = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        val viewParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100)

        do {
            val row = TableRow(cont)
            row.layoutParams = rowParam

            val checkBox = CheckBox(cont)
            var _cid: Int = cursor.getString(0).toInt()
            var _ccontent: String = cursor.getString(1)
            checkBox.tag = _cid
            checkBox.layoutParams = viewParam
            checkBox.text = _ccontent
            checkBox.textSize = 13.0f
            checkBox.gravity = Gravity.CENTER

            if (cursor.getString(2).toInt() == 0)
                checkBox.isChecked = false
            else
                checkBox.isChecked = true
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                updateCheck(_cid, isChecked)
            }
            checkBox.setOnLongClickListener {
                checkListClickListener!!.onCheckListClick(_cid, _ccontent)
                true
            }

            row.addView(checkBox)
            row.setOnLongClickListener {
                checkListClickListener!!.onCheckListClick(_cid, _ccontent)
                true
            }
            _binding!!.checkTableLayout.addView(row)

        } while (cursor.moveToNext()) //val recordCount = cursor.count
    }

    fun deleteCheck(_cid: Int) {
        val db = writableDatabase
        db.delete(CHECK_TABLE_NAME, "$CID=?", arrayOf(_cid.toString()))
        db.close()
    }

    fun updateCheck(_cid: Int, isChecked: Boolean) {
        val db = writableDatabase

        val values: ContentValues = ContentValues()
        if (isChecked == true) {
            values.put(CCHECK, 1)
        } else {
            values.put(CCHECK, 0)
        }
        db.update(CHECK_TABLE_NAME, values, "$CID=?", arrayOf(_cid.toString()))
        db.close()
    }

    fun updateCheck(_cid: Int, _ccontent: String) {
        val db = writableDatabase

        val values: ContentValues = ContentValues()
        values.put(CCONTENT, _ccontent)

        db.update(CHECK_TABLE_NAME, values, "$CID=?", arrayOf(_cid.toString()))
        db.close()
    }


    fun insertCheck(ccontent: String, year: Int, month: Int, day: Int, _petId: Int): Boolean {
        var dateId: Int = getDid(year, month, day, _petId)
        if (dateId == -1) {
            insertDateTable(year, month, day, _petId)
            dateId = getDid(year, month, day, _petId)
            Log.i("dateId_ver2", dateId.toString())
        }
        val values: ContentValues = ContentValues()
        values.put(CCONTENT, ccontent)
        values.put(CCHECK, 0)
        values.put(DID, dateId)
        val db = writableDatabase
        val flag = db.insert(CHECK_TABLE_NAME, null, values) > 0
        Log.i("flag", flag.toString())
        db.close()
        return flag
    }

    fun deleteWalk(_wid: Int) {
        val db = writableDatabase
        db.delete(WALK_TABLE_NAME, "$WID=?", arrayOf(_wid.toString()))
        db.close()
    }


    fun updateWalk(_wid: Int, _wtime: String, _wspecial: String) {
        val db = writableDatabase

        val values: ContentValues = ContentValues()
        values.put(WTIME, _wtime)
        values.put(WSPECIAL, _wspecial)

        db.update(WALK_TABLE_NAME, values, "$WID=?", arrayOf(_wid.toString()))
        db.close()
    }

    fun insertWalk(walkTime: String, special: String, year: Int, month: Int, day: Int, _petId: Int): Boolean {
        var dateId: Int = getDid(year, month, day, _petId)

        if (dateId == -1) {
            insertDateTable(year, month, day, _petId)
            dateId = getDid(year, month, day, _petId)

        }
        val values: ContentValues = ContentValues()
        values.put(WTIME, walkTime)
        values.put(WSPECIAL, special)
        values.put(DID, dateId)
        val db = writableDatabase
        val flag = db.insert(WALK_TABLE_NAME, null, values) > 0

        db.close()
        return flag
    }

    fun insertDateTable(_year: Int, _month: Int, _day: Int, _petId: Int) {
        val values: ContentValues = ContentValues()
        values.put(DYEAR, _year)
        values.put(DMONTH, _month)
        values.put(DDAY, _day)
        values.put(PETID, _petId)
        val db = writableDatabase
        db.insert(DATE_TABLE_NAME, null, values)
        db.close()
    }

}