package com.ganawin.mifinca.core

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class CalculateEdad {

    @SuppressLint("SimpleDateFormat")
    fun calcularEdad(fecha: String): String{

        val fechaNacDate: Date? = SimpleDateFormat("dd/MM/yyyy").parse(fecha)
        val fechaActual = Date(System.currentTimeMillis())
        val diferenciaFechas = fechaActual.time - fechaNacDate!!.time
        val segundos = diferenciaFechas/1000
        val minutos = segundos/60
        val horas = minutos/60
        val dias = horas/24
        val anios:Double = dias/365.0
        val meses = (anios % 1) * 12
        return "Edad: ${anios.toInt()} años y ${meses.toInt()} meses"

    }

}