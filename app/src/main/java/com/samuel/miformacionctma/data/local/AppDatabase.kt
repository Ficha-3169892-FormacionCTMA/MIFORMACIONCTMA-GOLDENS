package com.samuel.miformacionctma.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Agregar las nuevas columnas a la tabla evidencias
                db.execSQL("ALTER TABLE evidencias ADD COLUMN evidenciaUri TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE evidencias ADD COLUMN mimeType TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE evidencias ADD COLUMN tamanoBytes INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE evidencias ADD COLUMN estadoSincronizacion TEXT NOT NULL DEFAULT 'LOCAL'")
                
                // Nota: La relación de ForeignKey se define en la entidad para Room, 
                // pero Room no soporta ALTER TABLE para agregar ForeignKeys directamente en SQLite sin recrear la tabla.
                // Si se requiere integridad referencial estricta a nivel de SQLite post-migración, 
                // se suele usar una tabla temporal, pero para este incremento simplificamos manteniendo el esquema de Room actualizado.
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "miformacion_database"
                )
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
