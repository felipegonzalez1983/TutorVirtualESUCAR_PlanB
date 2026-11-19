package cl.duoc.tutorvirtual.data.local

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

import androidx.sqlite.db.SupportSQLiteDatabase

// ======================================================
// BASE DE DATOS - TUTOR VIRTUAL ESUCAR
//
// ROOM + SQLITE
//
// VERSION 1:
// - Etapas metodologicas
// - Entregables academicos
//
// VERSION 2:
// - Tabla evidencias
// - DAO de evidencias
//
// Migracion 1 -> 2 sin eliminar datos.
// ======================================================

@Database(
    entities = [
        EtapaEntity::class,
        EntregableEntity::class,
        EvidenciaEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class EsucarDatabase : RoomDatabase() {

    // ==============================================
    // DAO DE ETAPAS
    // ==============================================

    abstract fun etapaDao(): EtapaDao

    // ==============================================
    // DAO DE ENTREGABLES
    // ==============================================

    abstract fun entregableDao(): EntregableDao

    // ==============================================
    // DAO DE EVIDENCIAS
    // ==============================================

    abstract fun evidenciaDao(): EvidenciaDao

    companion object {

        @Volatile
        private var INSTANCE: EsucarDatabase? = null

        // ==========================================
        // MIGRACION VERSION 1 A VERSION 2
        // ==========================================

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {

                override fun migrate(
                    db: SupportSQLiteDatabase
                ) {

                    // Crear nueva tabla.
                    // No modificar etapas ni entregables.

                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `evidencias` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `entregableId` INTEGER NOT NULL,
                            `nombreArchivo` TEXT NOT NULL,
                            `tipo` TEXT NOT NULL,
                            `uriArchivo` TEXT NOT NULL,
                            `fechaRegistro` INTEGER NOT NULL,
                            FOREIGN KEY (`entregableId`)
                                REFERENCES `entregables` (`id`)
                                ON UPDATE NO ACTION
                                ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )

                    // Indice para la clave foranea.

                    db.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                            `index_evidencias_entregableId`
                        ON `evidencias` (`entregableId`)
                        """.trimIndent()
                    )
                }
            }

        // ==========================================
        // INSTANCIA UNICA DE ROOM
        // ==========================================

        fun obtenerInstancia(
            context: Context
        ): EsucarDatabase {

            return INSTANCE ?: synchronized(this) {

                val instancia =
                    Room.databaseBuilder(
                        context.applicationContext,
                        EsucarDatabase::class.java,
                        "esucar_database"
                    )

                        // Mantener los datos existentes.
                        .addMigrations(
                            MIGRATION_1_2
                        )

                        .build()

                INSTANCE = instancia

                instancia
            }
        }
    }
}
