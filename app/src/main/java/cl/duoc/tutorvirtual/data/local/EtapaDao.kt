package cl.duoc.tutorvirtual.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EtapaDao {

    // Observar todas las etapas
    @Query("SELECT * FROM etapas ORDER BY id ASC")
    fun obtenerTodasLasEtapas(): Flow<List<EtapaEntity>>

    // Buscar una etapa por ID
    @Query("SELECT * FROM etapas WHERE id = :id LIMIT 1")
    suspend fun obtenerEtapaPorId(
        id: Int
    ): EtapaEntity?

    // Insertar una etapa o actualizarla si existe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEtapa(
        etapa: EtapaEntity
    )

    // Insertar varias etapas
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarEtapas(
        etapas: List<EtapaEntity>
    )

    // Actualizar una etapa existente
    @Update
    suspend fun actualizarEtapa(
        etapa: EtapaEntity
    )

    // Actualizar solamente el estado
    @Query(
        "UPDATE etapas SET estado = :nuevoEstado " +
                "WHERE id = :etapaId"
    )
    suspend fun actualizarEstado(
        etapaId: Int,
        nuevoEstado: String
    )

    // Contar las etapas registradas
    @Query("SELECT COUNT(*) FROM etapas")
    suspend fun contarEtapas(): Int
}
