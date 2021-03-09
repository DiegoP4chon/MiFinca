package com.ganawin.mifinca.domain.leche

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Leche
import com.ganawin.mifinca.data.remote.leche.LecheDataSource

class LecheRepoImpl(private val dataSource: LecheDataSource): LecheRepo {

    override suspend fun registrarLeche(litros: Int, fecha: String, collection: String): String {
        return dataSource.registrarLeche(litros, fecha, collection)
    }

    override suspend fun getListLeche(collection: String): List<Leche> {
        return  dataSource.getListLeche(collection)
    }
}