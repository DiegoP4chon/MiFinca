package com.ganawin.mifinca.data.remote.dispositivos

import com.ganawin.mifinca.data.model.DispositivosRegistrados
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DispositivosDataSource {

    private val firebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun newDevice(tokenRegistro: String, email: String){
        firebaseFirestore.collection("devices").document(email)
                .set(
                        DispositivosRegistrados(email, tokenRegistro)
                )
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }.await()
    }
}