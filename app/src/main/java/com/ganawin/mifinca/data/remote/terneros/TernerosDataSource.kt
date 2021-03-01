package com.ganawin.mifinca.data.remote.terneros

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Ternero
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class TernerosDataSource {

    val firebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun setNewTernero(mutableList: MutableList<String>, colecction: String): String{
        var result = ""
        firebaseFirestore.collection(colecction).document("${UUID.randomUUID()}")
            .set(Ternero(mutableList[0], mutableList[1], mutableList[2], mutableList[3],
                mutableList[4], mutableList[5]))
            .addOnSuccessListener {
                result = "Registro agregado"
            }.addOnFailureListener {
                result = it.toString()
            }.await()
        return result
    }

    suspend fun getListTerneros(collection: String): Resource<List<Ternero>>{
        val ternerosList = mutableListOf<Ternero>()
        val querySnapshot = firebaseFirestore.collection(collection).get().await()
        for (ternero in querySnapshot.documents){
            ternero.toObject(Ternero::class.java)?.let { fbTernero ->
                ternerosList.add(fbTernero)
            }
        }
        return Resource.Success(ternerosList)
    }
}