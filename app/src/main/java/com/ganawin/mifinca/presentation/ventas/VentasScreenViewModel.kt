package com.ganawin.mifinca.presentation.ventas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.domain.terneros.TerneroRepo
import com.ganawin.mifinca.domain.ventas.VentasRepo
import com.ganawin.mifinca.presentation.ternero.TerneroScreenViewModel
import java.util.HashMap

class VentasScreenViewModel(private val repo: VentasRepo): ViewModel() {

    fun setNewVenta(mutableList: MutableList<String>, colecction: String) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.setNewVenta(mutableList, colecction)))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

   fun getListVenta(collection: String) = liveData {
       emit(Resource.Loading())
       try {
           emit(Resource.Success(repo.getListVentas(collection)))
       } catch (e: Exception) {
           emit(Resource.Failure(e))
       }
   }
    fun getOneVenta(collection: String, document: String) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.getOneVenta(collection, document)))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun updateVenta(collection: String, document: String, map: HashMap<String, Any>) = liveData {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.updateVenta(collection, document, map)))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}
class VentasScreenViewModelFactory(private val repo: VentasRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VentasScreenViewModel(repo) as T
    }
}