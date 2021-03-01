package com.ganawin.mifinca.data.remote.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class LoginDataSource {

    suspend fun signIn(email: String, password: String): FirebaseUser?{
        val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        return authResult.user
    }

    suspend fun createUser(email: String, password: String){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
    }

}