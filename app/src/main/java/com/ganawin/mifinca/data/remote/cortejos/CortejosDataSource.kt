package com.ganawin.mifinca.data.remote.cortejos

import com.ganawin.mifinca.core.GenerateId
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Cortejo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.*

class CortejosDataSource {

    val firebaseFirestore = FirebaseFirestore.getInstance()
    private var result = ""

    suspend fun setNewCortejo(mutableList: MutableList<String>, colecction: String): String {
        val nameDocument = "${UUID.randomUUID()}"
        val id = GenerateId().generateID(mutableList[0])
        firebaseFirestore.collection(colecction).document(nameDocument)
                .set(
                        Cortejo(nameDocument, id, mutableList[0], mutableList[1], mutableList[2],
                                mutableList[3], mutableList[4], mutableList[5], mutableList[6],
                                mutableList[7], mutableList[8])
                )
                .addOnSuccessListener {
                    result = "Registro agregado"
                }.addOnFailureListener {
                    result = it.toString()
                }.await()
        return result
    }

    suspend fun getListCortejo(collection: String): List<Cortejo> {
        val cortejosList = mutableListOf<Cortejo>()
        val querySnapshot = firebaseFirestore.collection(collection)
                .orderBy("id", Query.Direction.DESCENDING).get().await()
        for (cortejo in querySnapshot.documents) {
            cortejo.toObject(Cortejo::class.java)?.let { fbCortejo ->
                cortejosList.add(fbCortejo)
            }
        }
        return cortejosList
    }

    suspend fun getOneCortejo(collection: String, document: String): List<Cortejo> {
        val listCortejo = mutableListOf<Cortejo>()
        val querySnapshot = firebaseFirestore.collection(collection)
                .whereEqualTo("document", document).get().await()
        for (cortejo in querySnapshot.documents) {
            cortejo.toObject(Cortejo::class.java)?.let { fbCortejo ->
                listCortejo.add(fbCortejo)
            }
        }
        return listCortejo
    }

    suspend fun updateCortejo(collection: String, document: String, map: HashMap<String, Any>): Resource<String> {
        firebaseFirestore.collection(collection).document(document).update(map)
                .addOnSuccessListener {
                    result = "Registro modificado"
                }
                .addOnFailureListener {
                    result = it.toString()
                }.await()
        return Resource.Success(result)
    }

    suspend fun deleteCortejo(collection: String, document: String): Resource<String> {
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
}