package com.example.aap

data class Account(var ID: String, var PWD: String) {
    constructor(): this("noinfo", "noinfo")
}