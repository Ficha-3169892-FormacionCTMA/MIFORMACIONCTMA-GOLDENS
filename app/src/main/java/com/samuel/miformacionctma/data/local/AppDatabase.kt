package com.samuel.miformacionctma.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.samuel.miformacionctma.data.local.dao.*
import com.samuel.miformacionctma.data.local.entities.*

@Database(
    entities = [
        UserEntity::class,
        ActividadEntity::class,
        BitacoraEntity::class,
        EvidenciaEntity::class,
        AsistenciaEntity::class,
        NovedadEntity::class,
        CertificadoEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun actividadDao(): ActividadDao
    abstract fun bitacoraDao(): BitacoraDao
    abstract fun evidenciaDao(): EvidenciaDao
    abstract fun asistenciaDao(): AsistenciaDao
    abstract fun novedadDao(): NovedadDao
    abstract fun certificadoDao(): CertificadoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "miformacion_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
