package cl.duoc.tutorvirtual.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        EtapaEntity::class,
        EntregableEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EsucarDatabase : RoomDatabase() {

    abstract fun etapaDao(): EtapaDao

    abstract fun entregableDao(): EntregableDao

    companion object {

        @Volatile
        private var INSTANCE: EsucarDatabase? = null

        fun obtenerInstancia(
            context: Context
        ): EsucarDatabase {

            return INSTANCE ?: synchronized(this) {

                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    EsucarDatabase::class.java,
                    "esucar_database"
                ).build()

                INSTANCE = instancia

                instancia
            }
        }
    }
}
