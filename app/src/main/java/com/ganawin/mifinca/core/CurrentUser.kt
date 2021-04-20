package com.ganawin.mifinca.core

import com.google.firebase.auth.FirebaseAuth

class CurrentUser {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var userUID: String = ""
    private var userEmailPhone: String = ""

    fun userUid(): String {
        firebaseAuth.currentUser?.let {
            userUID = firebaseAuth.currentUser!!.uid
        }
        return userUID
    }

    fun userEmail(): String {
        firebaseAuth.currentUser?.let {
            userEmailPhone = firebaseAuth.currentUser!!.phoneNumber!!
        }

        return  userEmailPhone
    }
}