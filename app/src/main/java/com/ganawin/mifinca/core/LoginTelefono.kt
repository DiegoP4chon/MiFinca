package com.ganawin.mifinca.core

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.*
import java.util.concurrent.TimeUnit

class LoginTelefono(private val activity: Activity) {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val TAG = "verificarAuthPhone"

    private var storedVerificationId: String = ""

    fun verificarTelefono(phoneNumber: String) {

        val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
        auth.languageCode = Locale.getDefault().language
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "verificacion completada: $credential")
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(TAG, "verificacion fallida: $e")

            if(e is FirebaseAuthInvalidCredentialsException){ }
            else if (e is FirebaseTooManyRequestsException){ }
        }

        override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
            Log.d(TAG, "codigo enviado: $verificationId")
            storedVerificationId = verificationId
        }
    }

    fun verificarCodigo(code: String): PhoneAuthCredential{
        return PhoneAuthProvider.getCredential(storedVerificationId!!, code)
    }

}