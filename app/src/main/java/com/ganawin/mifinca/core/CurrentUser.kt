package com.ganawin.mifinca.core

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class CurrentUser {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var userUID: String = ""
    private var userEmail: String = ""

    fun userUid(): String {
        firebaseAuth.currentUser?.let {
            userUID = firebaseAuth.currentUser!!.uid
        }
        return userUID
    }

    fun userEmail(): String {
        firebaseAuth.currentUser?.let {
            userEmail = firebaseAuth.currentUser!!.email!!
            Log.d("verCorreo", userEmail)
        }

        return  userEmail
    }
}