package cl.duoc.tutorvirtual.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntregableDao {

    // Observar todos los entregables
    @Query(
        "SELECT * FROM entregables " +
                "ORDER BY id ASC"
    )
    fun obtenerTodos(): Flow<List<EntregableEntity>>

    // Observar entregables de una etapa
    @Query(
        "SELECT * FROM entregables " +
                "WHERE etapaId = :etapaId " +
                "ORDER BY id ASC"
    )
    fun obtenerPorEtapa(
        etapaId: Int
    ): Flow<List<EntregableEntity>>

    // Buscar un entregable por ID
    @Query(
        "SELECT * FROM entregables " +
                "WHERE id = :id LIMIT 1"
    )
    suspend fun obtenerPorId(
        id: Int
    ): EntregableEntity?

    // Insertar un entregable
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(
        entregable: EntregableEntity
    ): Long

    // Actualizar un entregable
    @Update
    suspend fun actualizar(
        entregable: EntregableEntity
    )

    // Cambiar el estado de un entregable
    @Query(
        "UPDATE entregables SET estado = :nuevoEstado " +
                "WHERE id = :entregableId"
    )
    suspend fun actualizarEstado(
        entregableId: Int,
        nuevoEstado: String
    )

    // Eliminar un entregable
    @Delete
    suspend fun eliminar(
        entregable: EntregableEntity
    )

    // Contar entregables de una etapa
    @Query(
        "SELECT COUNT(*) FROM entregables " +
                "WHERE etapaId = :etapaId"
    )
    suspend fun contarPorEtapa(
        etapaId: Int
    ): Int
}
