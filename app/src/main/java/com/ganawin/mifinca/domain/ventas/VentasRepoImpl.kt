package com.ganawin.mifinca.domain.ventas

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Venta
import com.ganawin.mifinca.data.remote.ventas.VentasDataSource
import java.util.HashMap

class VentasRepoImpl(private val dataSource: VentasDataSource): VentasRepo {
    override suspend fun setNewVenta(mutableList: MutableList<String>, colecction: String): String {
        return dataSource.setNewVenta(mutableList, colecction)
    }

    override suspend fun getListVentas(collection: String): List<Venta> {
        return dataSource.getListVentas(collection)
    }

    override suspend fun getOneVenta(collection: String, document: String): List<Venta> {
        return dataSource.getOneVenta(collection, document)
    }

    override suspend fun updateVenta(collection: String, document: String, map: HashMap<String, Any>): Resource<String> {
        return dataSource.updateVenta(collection, document, map)
    }
}