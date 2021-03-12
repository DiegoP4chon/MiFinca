package com.ganawin.mifinca.core

class GenerateId {

    fun generateID(fecha: String): Int{
        val fechaArray = fecha.split("/")
        val fechaReversed = fechaArray.reversed()
        var fechaString = ""
        for (nums in fechaReversed){
            fechaString += if(nums.length < 2){
                "0$nums"
            } else {
                nums
            }
        }

        return fechaString.toInt()
    }
}