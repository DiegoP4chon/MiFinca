package com.ganawin.mifinca.domain.leche

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Leche

interface LecheRepo {
    suspend fun registrarLeche(litros: Int, fecha: String, collection: String): String
    suspend fun getListLeche(collection: String): List<Leche>
}