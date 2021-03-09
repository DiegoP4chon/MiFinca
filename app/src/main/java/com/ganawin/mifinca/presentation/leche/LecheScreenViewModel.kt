package com.ganawin.mifinca.presentation.leche

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Leche
import com.ganawin.mifinca.domain.leche.LecheRepo

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
}

class LecheScreenViewModelFactory(private val repo: LecheRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LecheScreenViewModel(repo) as T
    }
}