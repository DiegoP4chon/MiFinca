package com.ganawin.mifinca.data.remote.ventas

import com.ganawin.mifinca.core.GenerateId
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Venta
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.*

class VentasDataSource {

    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private var result = ""

    suspend fun setNewVenta(mutableList: MutableList<String>, colecction: String): String{
        val nameDocument = "${UUID.randomUUID()}"
        val id = GenerateId().generateID(mutableList[0])
        firebaseFirestore.collection(colecction).document(nameDocument)
            .set(
                Venta(nameDocument, id, mutableList[1], mutableList[0], mutableList[2], mutableList[3].toLong(),
                    mutableList[4], mutableList[5])
            )
            .addOnSuccessListener {
                result = "Registro agregado"
            }.addOnFailureListener {
                result = it.toString()
            }.await()
        return result
    }

    suspend fun getListVentas(collection: String): List<Venta> {
        val ventaList = mutableListOf<Venta>()
        val querySnapshot = firebaseFirestore.collection(collection)
            .orderBy("id", Query.Direction.DESCENDING).get().await()
        for (venta in querySnapshot.documents){
            venta.toObject(Venta::class.java)?.let { fbVenta ->
                ventaList.add(fbVenta)
            }
        }
        return ventaList
    }

    suspend fun getOneVenta(collection: String, document: String): List<Venta> {
        val ventaList = mutableListOf<Venta>()
        val querySnapshot = firebaseFirestore.collection(collection)
                .whereEqualTo("document", document).get().await()
        for (venta in querySnapshot.documents){
            venta.toObject(Venta::class.java)?.let { fbVenta ->
                ventaList.add(fbVenta)
            }
        }
        return ventaList
    }

    suspend fun updateVenta(collection: String, document: String, map: HashMap<String, Any>): Resource<String>{
        firebaseFirestore.collection(collection).document(document).update(map)
                .addOnSuccessListener {
                    result = "Registro modificado"
                }
                .addOnFailureListener {
                    result = it.toString()
                }.await()
        return Resource.Success(result)
    }

    suspend fun deleteVenta(collection: String, document: String): Resource<String> {
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