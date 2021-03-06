package com.example.aap.PetInfo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.view.Gravity
import android.widget.CheckBox
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.aap.Schedule.DBHelper
import com.example.aap.databinding.ContentScrollingBinding
import com.example.aap.databinding.FragmentPetInfoBinding
import com.example.aap.databinding.FragmentScheduleBinding
import org.jetbrains.anko.matchParent

class PetInfoDBHelper(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object{
        val DB_NAME = "petinfo.db"
        val DB_VERSION = 5

        val TABLE_NAME1 = "petbasic"
        val PID = "pid"                     //펫 ID
        val PNAME = "pname"                 //펫 이름
        val PIMAGE = "pimage"               //펫 이미지 경로
        val PAGE = "page"                   //펫 나이
        val PGENDER = "pgender"             //펫 성별
        val PNEUTRAL = "pneutral"           //펫 중성화 여부
        val PBIRTH = "pbirth"               //펫 생일


        val TABLE_NAME2 = "petdis"
        val DID = "did"
        val DISEASE = "disease"           //보유질병

        val TABLE_NAME3 = "petino"
        val IID = "iid"
        val INOCULATION = "inoculation"   //접종이력

        val TABLE_NAME4 = "petalbum"
        val AID = "aid"
        val DATE = "date"


    }

    override fun onCreate(db: SQLiteDatabase?)
    {
        val create_table1 = "create table if not exists $TABLE_NAME1("+
                "$PID integer primary key autoincrement, " +
                "$PNAME text, " +
                "$PIMAGE text, " +
                "$PAGE integer default 1, " +
                "$PGENDER integer, " +
                "$PNEUTRAL integer, " +
                "$PBIRTH text);"
        db!!.execSQL(create_table1)

        val create_table2 = "create table if not exists $TABLE_NAME2("+
                "$DID integer primary key autoincrement, " +
                "$PID integer, " +
                "$DISEASE text, " +
                "FOREIGN KEY($PID) REFERENCES $TABLE_NAME1($PID) );"
        db!!.execSQL(create_table2)

        val create_table3 = "create table if not exists $TABLE_NAME3("+
                "$IID integer primary key autoincrement, " +
                "$PID integer, " +
                "$INOCULATION text, " +
                "FOREIGN KEY($PID) REFERENCES $TABLE_NAME1($PID) );"
        db!!.execSQL(create_table3)

        val create_table4 = "create table if not exists $TABLE_NAME4("+
                "$AID integer primary key autoincrement, " +
                "$PID integer, " +
                "$DATE text, " +
                "$PIMAGE text, " +
                "FOREIGN KEY($PID) REFERENCES $TABLE_NAME1($PID) );"
        db!!.execSQL(create_table4)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
    {
        val drop_table1 = "drop table if exists $TABLE_NAME1;"
        db!!.execSQL(drop_table1)

        val drop_table2 = "drop table if exists $TABLE_NAME2;"
        db!!.execSQL(drop_table2)

        val drop_table3 = "drop table if exists $TABLE_NAME3;"
        db!!.execSQL(drop_table3)

        val drop_table4 = "drop table if exists $TABLE_NAME4;"
        db!!.execSQL(drop_table4)

        onCreate(db)
    }

    data class PetInfo(val name:String, val image:String, val age: Int, val gender: Int, val neutral: Int, val birth: String)
    {
    }

    fun getPID(pname: String):Int
    {
        var strsql = "select $PID from $TABLE_NAME1 where $PNAME = '$pname';"
        val db = readableDatabase
        var cursor: Cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()

        if (cursor.count == 0) {
            cursor.close()
            db.close()
            return -1
        }

        val pid: Int = cursor.getString(0).toInt()
        cursor.close()

        return pid
    }

    fun getDID(pid: Int, dis: String):Int
    {
        var strsql = "select $DID from $TABLE_NAME2 where $PID = '$pid' and $DISEASE = '$dis';"
        val db = readableDatabase
        var cursor: Cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()

        if (cursor.count == 0) {
            cursor.close()
            db.close()
            return -1
        }

        val did: Int = cursor.getInt(0)
        cursor.close()

        return did
    }

    fun getIID(pid: Int, ino: String):Int
    {
        var strsql = "select $IID from $TABLE_NAME3 where $PID = '$pid' and $INOCULATION = '$ino';"
        val db = readableDatabase
        var cursor: Cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()

        if (cursor.count == 0) {
            cursor.close()
            db.close()
            return -1
        }

        val iid: Int = cursor.getInt(0)
        cursor.close()

        return iid
    }

    fun getAllPet(): MutableList<PetInfo>
    {
        val strsql = "select * from $TABLE_NAME1;"
        val db = readableDatabase

        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()

        var petlist = mutableListOf<PetInfo>()

        if (cursor.count != 0) {
            do {
                val temp = PetInfo(cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6))
                petlist.add(temp)
            }while (cursor.moveToNext())
        }

        cursor.close()

        return petlist
    }

    fun getPetInfo(pid:Int): PetInfo
    {
        val strsql = "select * from $TABLE_NAME1 where $PID = '$pid';"
        val db = readableDatabase

        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()

        if(cursor.count != 0) {
            val temp = PetInfo(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getString(6)
            )
            cursor.close()
            db.close()

            return temp
        }
        else
        {
            val temp = PetInfo(
                    "",
                    "",
                    0,
                    0,
                    0,
                    ""
            )
            cursor.close()

            return temp
        }
    }

    fun getPetDis(pid:Int): MutableList<String>
    {
        val strsql = "select * from $TABLE_NAME2;"
        val db = readableDatabase

        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()

        var dislist = mutableListOf<String>()

        if (cursor.count != 0) {
            do {
                if(cursor.getInt(2) == pid)
                    dislist.add(cursor.getString(1))
            }while (cursor.moveToNext())
        }

        cursor.close()

        return dislist
    }

    fun getPetIno(pid:Int): MutableList<String>
    {
        val strsql = "select * from $TABLE_NAME3;"
        val db = readableDatabase

        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()

        var inolist = mutableListOf<String>()

        if (cursor.count != 0) {
            do {
                if(cursor.getInt(2) == pid)
                    inolist.add(cursor.getString(1))
            }while (cursor.moveToNext())
        }

        cursor.close()

        return inolist
    }

    fun getPetAlbum(pid:Int): ArrayList<AlbumData> {
        val strsql = "select * from $TABLE_NAME4 order by $DATE;"
        val db = readableDatabase

        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()

        var Albumlist = ArrayList<AlbumData>()

        if (cursor.count != 0) {
            do {
                if(cursor.getInt(1) == pid)
                    Albumlist.add(AlbumData(cursor.getString(2), cursor.getString(3)))

            }while (cursor.moveToNext())
        }

        cursor.close()

        return Albumlist
    }

    fun insertPetBasic(product: PetInfo):Boolean
    {
        val values = ContentValues()
        values.put(PNAME, product.name)
        values.put(PIMAGE, product.image)
        values.put(PAGE, product.age)
        values.put(PGENDER, product.gender)
        values.put(PNEUTRAL, product.neutral)
        values.put(PBIRTH, product.birth)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME1, null, values) > 0

        return flag
    }

    fun insertPetDisease(disease: String, pid: Int):Boolean
    {
        val values = ContentValues()
        values.put(DISEASE, disease)
        values.put(PID, pid)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME2, null, values) > 0

        return flag
    }

    fun insertPetInoculation(inoculation: String, pid:Int):Boolean
    {
        val values = ContentValues()
        values.put(INOCULATION, inoculation)
        values.put(PID, pid)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME3, null, values) > 0

        return flag
    }

    fun insertPetAlbum(path: String, date: String, pid:Int):Boolean
    {
        val values = ContentValues()
        values.put(PID, pid)
        values.put(PIMAGE, path)
        values.put(DATE, date)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME4, null, values) > 0

        return flag
    }

    fun removePetInfo(name: String)
    {
        val pid = getPID(name)
        val db = writableDatabase
        db.delete(TABLE_NAME1, "$PID=?", arrayOf(pid.toString()))

    }

    fun removePetDis(name: String, dis: String)
    {
        val pid = getPID(name)
        val did = getDID(pid, dis)

        val db = writableDatabase
        db.delete(TABLE_NAME2, "$DID=?", arrayOf(did.toString()))

    }

    fun removePetIno(name: String, ino: String)
    {
        val pid = getPID(name)
        val iid = getIID(pid, ino)

        val db = writableDatabase
        db.delete(TABLE_NAME3, "$IID=?", arrayOf(iid.toString()))

    }

    fun removePetAlbum(pid: Int)
    {
        val db = writableDatabase
        db.delete(TABLE_NAME4, "$PID=?", arrayOf(pid.toString()))
    }

    fun updatePetInfo(item: PetInfo)
    {
        val db = writableDatabase
        val pid = getPID(item.name)

        val values: ContentValues = ContentValues()
        values.put(PNAME, item.name)
        values.put(PIMAGE, item.image)
        values.put(PAGE, item.age)
        values.put(PGENDER, item.gender)
        values.put(PNEUTRAL, item.neutral)
        values.put(PBIRTH, item.birth)

        db.update(TABLE_NAME1, values, "$PID=?", arrayOf(pid.toString()))
    }

    fun getAllPetIDName(): ArrayList<PetIdName>
    {
        val strsql = "select * from $TABLE_NAME1;"
        val db = readableDatabase

        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()

        var petlist = ArrayList<PetIdName>()

        if (cursor.count != 0) {
            do {
                val temp = PetIdName(cursor.getInt(0), cursor.getString(1))
                petlist.add(temp)
            }while (cursor.moveToNext())
        }

        cursor.close()

        return petlist
    }

    fun getAllRecord(name: String, table1: TableLayout, table2: TableLayout) {

        table1.removeAllViewsInLayout()
        table2.removeAllViewsInLayout()

        val pid: Int = getPID(name)
        if (pid == -1){

            return
        }


        var strsql1 = "select * from $TABLE_NAME2 where $PID = '$pid';"
        val db = readableDatabase
        var cursor = db.rawQuery(strsql1, null)
        if (cursor.count != 0) {

            showDisRecord(name, cursor, table1, table2)
        }

        var strsql2 = "select * from $TABLE_NAME3 where $PID = '$pid';"
        cursor = db.rawQuery(strsql2, null)
        if (cursor.count != 0) {

            showInoRecord(name, cursor, table2, table2)
        }

        cursor.close()
        db.close()
    }


    fun showDisRecord(name: String, cursor: Cursor, distable: TableLayout, inotable: TableLayout) {

        distable.removeAllViewsInLayout()
        cursor.moveToFirst()

        val rowParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        val viewParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100)

        if(cursor.count==0) {

            return
        }

        do {
            val row = TableRow(context)
            row.layoutParams = rowParam
            row.setOnClickListener {
                //클릭하면 삭제가능하게끔
                val disView = row.getChildAt(0) as TextView
                val dis = disView.text.toString()
                val dlgBuilder = AlertDialog.Builder(context)
                dlgBuilder.setTitle("보유질병 삭제")
                    .setMessage(dis + "을(를)삭제하시겠습니까?")
                    .setPositiveButton("삭제"){
                            _, _ ->
                        removePetDis(name, dis)
                        getAllRecord(name, distable, inotable)
                    }
                    .setNegativeButton("취소"){
                            _, _ ->
                    }
                    .show()
            }

            val textView = TextView(context)
            textView.layoutParams = viewParam
            textView.text = cursor.getString(2)
            textView.textSize = 18.0f
            textView.gravity = Gravity.CENTER
            row.addView(textView)


            distable.addView(row)
        }while(cursor.moveToNext())
    }

    fun showInoRecord(name: String, cursor: Cursor, distable: TableLayout, inotable: TableLayout) {

        inotable.removeAllViewsInLayout()
        cursor.moveToFirst()


        val rowParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        val viewParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100)

        if(cursor.count==0) {
            return
        }

        do {
            val row = TableRow(context)
            row.layoutParams = rowParam
            row.setOnClickListener {
                //클릭하면 삭제가능하게끔
                val inoView = row.getChildAt(0) as TextView
                val ino = inoView.text.toString()
                val dlgBuilder = AlertDialog.Builder(context)
                dlgBuilder.setTitle("접종이력 삭제")
                    .setMessage(ino + "을(를)삭제하시겠습니까?")
                    .setPositiveButton("삭제"){
                            _, _ ->
                        removePetIno(name, ino)
                        getAllRecord(name, distable, inotable)
                    }
                    .setNegativeButton("취소"){
                            _, _ ->
                    }
                    .show()
            }

            val textView = TextView(context)
            textView.layoutParams = viewParam
            textView.text = cursor.getString(2)
            textView.textSize = 18.0f
            textView.gravity = Gravity.CENTER
            row.addView(textView)


            inotable.addView(row)
        }while(cursor.moveToNext())
    }

}