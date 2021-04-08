package com.ganawin.mifinca.presentation.leche

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.domain.leche.LecheRepo
import java.util.HashMap

class LecheScreenViewModel(private val repo: LecheRepo): ViewModel() {

    fun registrarLeche(litros: Int, fecha: String, collection: String) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.registrarLeche(litros, fecha, collection)))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun getListLeche(collection: String) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.getListLeche(collection)))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun getListLecheFilter(collection: String, idInicio: Int, idFin:Int) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.getListLecheFilter(collection, idInicio, idFin)))
        } catch (e : Exception) {
            emit(Resource.Failure(e))
        }
    }

    fun updateRegistroLeche(collection: String, document: String, map: HashMap<String, Any>) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.updateRegistroLeche(collection, document, map)))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}

@Suppress("UNCHECKED_CAST")
class LecheScreenViewModelFactory(private val repo: LecheRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LecheScreenViewModel(repo) as T
    }
}