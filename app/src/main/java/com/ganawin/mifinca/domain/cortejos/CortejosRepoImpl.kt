package com.ganawin.mifinca.domain.cortejos

import com.ganawin.mifinca.data.model.Cortejo
import com.ganawin.mifinca.data.remote.cortejos.CortejosDataSource

class CortejosRepoImpl(private val dataSource: CortejosDataSource): CortejosRepo {
    override suspend fun setNewCortejo(
        mutableList: MutableList<String>,
        colecction: String
    ): String {
        return dataSource.setNewCortejo(mutableList, colecction)
    }

    override suspend fun getListCortejo(collection: String): List<Cortejo> {
        return dataSource.getListCortejo(collection)
    }
}