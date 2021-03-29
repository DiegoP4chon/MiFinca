package com.ganawin.mifinca.domain.leche

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Leche
import java.util.HashMap

interface LecheRepo {
    suspend fun registrarLeche(litros: Int, fecha: String, collection: String): String
    suspend fun getListLeche(collection: String): List<Leche>
    suspend fun getListLecheFilter(collection: String, idInicio: Int, idFin:Int): List<Leche>
    suspend fun updateRegistroLeche(collection: String, document: String, map: HashMap<String, Any>): Resource<String>
}