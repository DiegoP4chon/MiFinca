package com.ganawin.mifinca.domain.ventas

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Venta

interface VentasRepo {
    suspend fun setNewVenta(mutableList: MutableList<String>, colecction: String): String
    suspend fun getListVentas(collection: String): List<Venta>
}