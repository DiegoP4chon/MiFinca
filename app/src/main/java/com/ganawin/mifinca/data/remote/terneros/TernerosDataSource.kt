package com.ganawin.mifinca.data.remote.terneros

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Ternero
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashMap

class TernerosDataSource {

    val firebaseFirestore = FirebaseFirestore.getInstance()
    private var result = ""

    suspend fun setNewTernero(mutableList: MutableList<String>, colecction: String): String{
        val nameDocument = "${UUID.randomUUID()}"
        firebaseFirestore.collection(colecction).document(nameDocument)
            .set(Ternero(mutableList[0], mutableList[1], mutableList[2], mutableList[3],
                mutableList[4], mutableList[5], nameDocument))
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

    suspend fun deleteTernero(collection: String, document: String): Resource<String> {
        firebaseFirestore.collection(collection).document(document)
                .delete()
                .addOnSuccessListener {
                    result = "Registro eliminado"
                }
                .addOnFailureListener {
                    result = it.toString()
                }.await()
        return Resource.Success(result)
    }

    suspend fun updateTernero(collection: String, document: String, map: HashMap<String, Any>): Resource<String>{
        firebaseFirestore.collection(collection).document(document).update(map)
                .addOnSuccessListener {
                    result = "Registro modificado"
                }
                .addOnFailureListener {
                    result = it.toString()
                }.await()
        return  Resource.Success(result)
    }
}