package com.example.aap.Community

import java.sql.Timestamp

data class Board(var boardNum:Int?, var boardTitle:String,var boardContent:String, var uid:String, val timestamp:Long?) {
    constructor():this(null,"noinfo","noinfo","noinfo",null)
}