package com.example.aap.Community

data class Board(var boardNum:Int?, var boardTitle:String,var boardContent:String, var uid:String) {
    constructor():this(null,"noinfo","noinfo","noinfo")
}