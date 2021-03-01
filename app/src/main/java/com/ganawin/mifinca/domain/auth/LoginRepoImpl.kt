package com.ganawin.mifinca.domain.auth

import com.ganawin.mifinca.data.remote.auth.LoginDataSource
import com.google.firebase.auth.FirebaseUser

class LoginRepoImpl(private val dataSource: LoginDataSource): LoginRepo {

    override suspend fun signIn(email: String, password: String): FirebaseUser? {
        return dataSource.signIn(email, password)
    }

    override suspend fun createUser(email: String, password: String) {
        return dataSource.createUser(email, password)
    }
}