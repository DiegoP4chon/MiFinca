package com.ganawin.mifinca.data.model

data class Venta(
    val document:String = "",
    val id: Int = 0,
    val descripVenta: String = "",
    val fecha_venta: String = "",
    val comprador: String = "",
    val valorVenta: Long = 0L,
    val idPhoto: String = "",
    val url_Photo: String = ""
)
