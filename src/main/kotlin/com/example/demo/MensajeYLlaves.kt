package com.example.demo

import com.google.gson.Gson

class MensajeYLlaves(var mensaje : Mensaje, var clave : String) {
    override fun toString(): String {
        val gson= Gson()
        return gson.toJson(this)
    }
}