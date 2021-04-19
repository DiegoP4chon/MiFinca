package com.ganawin.mifinca.domain.dispositivos

interface DispositivosRepo {

    suspend fun newDevice(tokenRegistro: String, email: String)
}