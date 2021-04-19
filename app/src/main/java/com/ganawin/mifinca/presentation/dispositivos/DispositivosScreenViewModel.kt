package com.ganawin.mifinca.presentation.dispositivos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.domain.dispositivos.DispositivosRepo
import java.lang.Exception

class DispositivosScreenViewModel(private val repo: DispositivosRepo) : ViewModel() {

    fun newDevice(tokenRegistro: String, email: String) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.newDevice(tokenRegistro, email)))
        }catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }
}

@Suppress("UNCHECKED_CAST")
class DispositivosScreenViewModelFactory(private val repo: DispositivosRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DispositivosScreenViewModel(repo) as T
    }
}