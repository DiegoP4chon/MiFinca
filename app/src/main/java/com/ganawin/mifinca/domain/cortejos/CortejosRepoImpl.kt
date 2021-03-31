package com.ganawin.mifinca.domain.cortejos

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Cortejo
import com.ganawin.mifinca.data.remote.cortejos.CortejosDataSource
import java.util.HashMap

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

    override suspend fun getOneCortejo(collection: String, document: String): List<Cortejo> {
        return dataSource.getOneCortejo(collection, document)
    }

    override suspend fun updateCortejo(collection: String, document: String, map: HashMap<String, Any>): Resource<String> {
        return dataSource.updateCortejo(collection, document, map)
    }

    override suspend fun deleteCortejo(collection: String, document: String): Resource<String> {
        return dataSource.deleteCortejo(collection, document)
    }
}