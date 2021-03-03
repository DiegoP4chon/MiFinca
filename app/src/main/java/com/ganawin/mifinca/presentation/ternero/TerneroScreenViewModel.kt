package com.ganawin.mifinca.presentation.ternero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.domain.auth.LoginRepo
import com.ganawin.mifinca.domain.terneros.TerneroRepo
import com.ganawin.mifinca.presentation.auth.LoginScreenViewModel
import kotlinx.coroutines.Dispatchers

class TerneroScreenViewModel(private val repo: TerneroRepo): ViewModel() {

    fun setNewTernero(mutableList: MutableList<String>, colecction: String) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.setNewTernero(mutableList, colecction)))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    fun fetchListTerneros(collection: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(repo.getListTerneros(collection))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun deleteItemTernero(collection: String, document: String) = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try{
            emit(repo.deleteTernero(collection, document))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun updateItemTernero(collection: String, document: String, map: HashMap<String, Any>)  = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try{
            emit(repo.updateTernero(collection, document, map))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

}
class TerneroScreenViewModelFactory(private val repo: TerneroRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TerneroScreenViewModel(repo) as T
    }
}