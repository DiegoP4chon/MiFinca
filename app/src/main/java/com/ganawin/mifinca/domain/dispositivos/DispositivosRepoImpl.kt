package com.ganawin.mifinca.domain.dispositivos

import com.ganawin.mifinca.data.remote.dispositivos.DispositivosDataSource

class DispositivosRepoImpl(private val dataSource: DispositivosDataSource): DispositivosRepo {

    override suspend fun newDevice(tokenRegistro: String, email: String) {
        return dataSource.newDevice(tokenRegistro, email)
    }
}