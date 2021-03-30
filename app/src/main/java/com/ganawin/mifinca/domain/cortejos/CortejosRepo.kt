package com.ganawin.mifinca.domain.cortejos

import com.ganawin.mifinca.data.model.Cortejo

interface CortejosRepo {

    suspend fun setNewCortejo(mutableList: MutableList<String>, colecction: String): String
    suspend fun getListCortejo(collection: String): List<Cortejo>
}