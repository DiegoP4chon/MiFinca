package com.ganawin.mifinca.domain.leche

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Leche
import com.ganawin.mifinca.data.remote.leche.LecheDataSource
import java.util.HashMap

class LecheRepoImpl(private val dataSource: LecheDataSource): LecheRepo {

    override suspend fun registrarLeche(litros: Int, fecha: String, collection: String): String {
        return dataSource.registrarLeche(litros, fecha, collection)
    }

    override suspend fun getListLeche(collection: String): List<Leche> {
        return  dataSource.getListLeche(collection)
    }

    override suspend fun getListLecheFilter(
        collection: String,
        idInicio: Int,
        idFin: Int
    ): List<Leche> {
        return  dataSource.getListLecheFilter(collection, idInicio, idFin)
    }

    override suspend fun updateRegistroLeche(collection: String, document: String,
                                             map: HashMap<String, Any>): Resource<String> {
        return  dataSource.updateRegistroLeche(collection, document, map)
    }
}