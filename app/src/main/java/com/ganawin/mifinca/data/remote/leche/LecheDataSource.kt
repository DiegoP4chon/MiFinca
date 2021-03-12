package com.ganawin.mifinca.data.remote.leche

import android.util.Log
import com.ganawin.mifinca.core.GenerateId
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
                .set(Leche(id, fecha, litros))
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

        /*val queryAsigned = firebaseFirestore.collection(collection)
                .whereGreaterThanOrEqualTo("id", 20190311)
                .whereLessThanOrEqualTo("id", 20201110)
                .orderBy("id", Query.Direction.DESCENDING).get().await()
        val listLecheOrder = mutableListOf<Leche>()
        for (listaEntrge in queryAsigned.documents){
            listaEntrge.toObject(Leche::class.java)?.let {
                listLecheOrder.add(it)
            }
        }
        Log.d("listaLecheOrder", listLecheOrder.toString())*/

        for (entregaLeche in querySnapshot.documents){
            entregaLeche.toObject(Leche::class.java)?.let { fbLeche ->
                lecheList.add(fbLeche)
            }
        }
        return lecheList
    }
}