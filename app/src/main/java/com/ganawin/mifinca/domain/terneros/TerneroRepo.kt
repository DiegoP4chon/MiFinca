package com.ganawin.mifinca.domain.terneros

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Ternero

interface TerneroRepo {
    suspend fun setNewTernero(mutableList: MutableList<String>, collection: String): String
    suspend fun getListTerneros(collection: String): Resource<List<Ternero>>
}