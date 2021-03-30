package com.ganawin.mifinca.presentation.cortejos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.domain.cortejos.CortejosRepo

class CortejosScreenViewModel(private val repo: CortejosRepo): ViewModel() {

    fun setNewCortejo(mutableList: MutableList<String>, colecction: String) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.setNewCortejo(mutableList, colecction)))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    fun getListCortejos(collection: String) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.getListCortejo(collection)))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }
}
class CortejosScreenViewModelFactory(private val repo: CortejosRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CortejosScreenViewModel(repo) as T
    }
}