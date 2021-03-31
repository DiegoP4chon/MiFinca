package com.ganawin.mifinca.domain.cortejos

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Cortejo
import java.util.HashMap

interface CortejosRepo {

    suspend fun setNewCortejo(mutableList: MutableList<String>, colecction: String): String
    suspend fun getListCortejo(collection: String): List<Cortejo>
    suspend fun getOneCortejo(collection: String, document: String): List<Cortejo>
    suspend fun updateCortejo(collection: String, document: String, map: HashMap<String, Any>): Resource<String>
    suspend fun deleteCortejo(collection: String, document: String): Resource<String>
}