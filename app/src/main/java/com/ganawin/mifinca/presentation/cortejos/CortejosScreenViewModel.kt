package com.ganawin.mifinca.presentation.cortejos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.domain.cortejos.CortejosRepo
import java.util.HashMap

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

    fun getOneCortejo(collection: String, document: String) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.getOneCortejo(collection, document)))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun updateCortejo(collection: String, document: String, map: HashMap<String, Any>) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.updateCortejo(collection, document, map)))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun deleteCortejo(collection: String, document: String) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.deleteCortejo(collection, document)))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}
@Suppress("UNCHECKED_CAST")
class CortejosScreenViewModelFactory(private val repo: CortejosRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CortejosScreenViewModel(repo) as T
    }
}