package com.ganawin.mifinca.data.remote.leche

import android.util.Log
import com.ganawin.mifinca.core.GenerateId
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Leche
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.*

class LecheDataSource {

    val firebaseFirestore = FirebaseFirestore.getInstance()
    var result: String = ""

    suspend fun registrarLeche(litros: Int, fecha: String, collection: String): String{
        val nameDocument = "${UUID.randomUUID()}"
        val id = GenerateId().generateID(fecha)
        firebaseFirestore.collection(collection).document(nameDocument)
                .set(Leche(nameDocument, id, fecha, litros))
                .addOnSuccessListener {
                    result = "Entrega registrada"
                }
                .addOnFailureListener {
                    result = it.toString()
                }
                .await()
        return result
    }

    suspend fun getListLeche(collection: String): List<Leche>{
        val lecheList = mutableListOf<Leche>()
        val querySnapshot = firebaseFirestore.collection(collection)
                .orderBy("id", Query.Direction.DESCENDING).get().await()

        for (entregaLeche in querySnapshot.documents){
            entregaLeche.toObject(Leche::class.java)?.let { fbLeche ->
                lecheList.add(fbLeche)
            }
        }
        return lecheList
    }

    suspend fun getListLecheFilter(collection: String, idInicio: Int, idFin:Int): List<Leche>{
        val queryAsigned = firebaseFirestore.collection(collection)
                .whereGreaterThanOrEqualTo("id", idInicio)
                .whereLessThanOrEqualTo("id", idFin)
                .orderBy("id", Query.Direction.DESCENDING).get().await()
        val listLecheFilter = mutableListOf<Leche>()
        for (listaEntrge in queryAsigned.documents){
            listaEntrge.toObject(Leche::class.java)?.let {
                listLecheFilter.add(it)
            }
        }
        Log.d("listaLecheOrder", listLecheFilter.toString())
        return listLecheFilter
    }

    suspend fun updateRegistroLeche(collection: String, document: String, map: HashMap<String, Any>): Resource<String>{
        firebaseFirestore.collection(collection).document(document).update(map)
                .addOnSuccessListener {
                    result = "Entrega modificada"
                }
                .addOnFailureListener {
                    result = it.toString()
                }.await()
        return Resource.Success(result)
    }
}