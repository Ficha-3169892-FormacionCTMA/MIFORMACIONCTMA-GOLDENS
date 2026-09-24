package com.samuel.miformacionctma.fakes

import com.samuel.miformacionctma.data.local.entities.BitacoraEntity
import com.samuel.miformacionctma.data.local.entities.CertificadoEntity
import com.samuel.miformacionctma.data.local.entities.EvidenciaEntity
import com.samuel.miformacionctma.data.local.entities.NovedadEntity
import com.samuel.miformacionctma.data.local.entities.UserEntity
import com.samuel.miformacionctma.data.repository.AppRepository
import com.samuel.miformacionctma.model.ActividadFormativa
import com.samuel.miformacionctma.network.NetworkResult
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeAppRepository : AppRepository(mockk(relaxed = true), mockk(relaxed = true)) {
    private val actividadesFlow = MutableStateFlow<List<ActividadFormativa>>(emptyList())
    var shouldReturnError = false

    override fun getActividadesStream(query: String): Flow<List<ActividadFormativa>> {
        return actividadesFlow.map { list ->
            if (query.isEmpty()) list else list.filter { it.titulo.contains(query, ignoreCase = true) }
        }
    }

    override suspend fun refreshActividades(): NetworkResult<Unit> {
        return if (shouldReturnError) {
            NetworkResult.Error(com.samuel.miformacionctma.network.NetworkError.SinConexion)
        } else {
            NetworkResult.Success(Unit)
        }
    }

    fun emitActividades(lista: List<ActividadFormativa>) {
        actividadesFlow.value = lista
    }
    
    // Sobrescribir otros métodos según sea necesario para los tests
}
