package com.samuel.miformacionctma.data.local.dao

import androidx.room.*
import com.samuel.miformacionctma.data.local.entities.CertificadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CertificadoDao {
    @Query("SELECT * FROM certificados WHERE userId = :userId")
    fun getCertificadosByUser(userId: String): Flow<List<CertificadoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificado(certificado: CertificadoEntity)
}
