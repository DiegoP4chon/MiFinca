package com.ganawin.mifinca.domain.ventas

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Venta
import java.util.HashMap

interface VentasRepo {
    suspend fun setNewVenta(mutableList: MutableList<String>, colecction: String): String
    suspend fun getListVentas(collection: String): List<Venta>
    suspend fun getOneVenta(collection: String, document: String): List<Venta>
    suspend fun updateVenta(collection: String, document: String, map: HashMap<String, Any>): Resource<String>
}