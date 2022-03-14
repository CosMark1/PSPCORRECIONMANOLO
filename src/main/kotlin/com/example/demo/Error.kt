package com.example.demo

import com.google.gson.Gson

class Error(var codigo : Int,var motivo :String) {
    override fun toString(): String {
        val gson= Gson()
        return gson.toJson(this)
    }
}