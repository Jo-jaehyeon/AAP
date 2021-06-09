package com.example.aap.Community

import android.graphics.Bitmap

data class Gallery(var galleryNum:Int?, var galleryTitle:String,var galleryContent:String, var galleryImage:String?, var uid:String) {
    constructor():this(null,"noinfo","noinfo",null,"noinfo")
}