package com.ganawin.mifinca.core

import com.google.firebase.auth.FirebaseAuth

class CurrentUser {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var userUID: String = ""

    fun userUid(): String {
        firebaseAuth.currentUser?.let {
            userUID = firebaseAuth.currentUser!!.uid
        }
        return userUID
    }
}