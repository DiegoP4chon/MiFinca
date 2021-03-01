package com.ganawin.mifinca.domain.auth

import com.google.firebase.auth.FirebaseUser

interface LoginRepo {
    suspend fun signIn(email: String, password: String): FirebaseUser?
    suspend fun createUser(email: String, password: String)
}