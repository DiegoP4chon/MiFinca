package com.ganawin.mifinca.domain.terneros

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Ternero
import com.ganawin.mifinca.data.remote.terneros.TernerosDataSource

class TerneroRepoImpl(private val dataSource: TernerosDataSource): TerneroRepo {
    override suspend fun setNewTernero(
        mutableList: MutableList<String>,
        collection: String
    ): String {
        return dataSource.setNewTernero(mutableList, collection)
    }

    override suspend fun getListTerneros(collection: String): Resource<List<Ternero>> {
        return dataSource.getListTerneros(collection)
    }

}
