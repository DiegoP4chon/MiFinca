package com.ganawin.mifinca.data.remote.leche

import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Leche
import com.ganawin.mifinca.data.model.Ternero
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class LecheDataSource {

    val firebaseFirestore = FirebaseFirestore.getInstance()
    var result: String = ""

    suspend fun registrarLeche(litros: Int, fecha: String, collection: String): String{
        val nameDocument = "${UUID.randomUUID()}"
        firebaseFirestore.collection(collection).document(nameDocument)
                .set(Leche(fecha, litros))
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
        val querySnapshot = firebaseFirestore.collection(collection).get().await()
        for (entregaLeche in querySnapshot.documents){
            entregaLeche.toObject(Leche::class.java)?.let { fbLeche ->
                lecheList.add(fbLeche)
            }
        }
        return lecheList
    }
}