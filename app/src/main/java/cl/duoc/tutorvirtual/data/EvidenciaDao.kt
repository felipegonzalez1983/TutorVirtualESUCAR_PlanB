package cl.duoc.tutorvirtual.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import kotlinx.coroutines.flow.Flow

// ======================================================
// DAO DE EVIDENCIAS ACADEMICAS
// TUTOR VIRTUAL ESUCAR - PLAN B
//
// Operaciones disponibles:
//
// 1. Guardar una evidencia.
// 2. Consultar evidencias por entregable.
// 3. Consultar una evidencia por su ID.
// 4. Contar evidencias por entregable.
// 5. Eliminar una evidencia.
//
// No modifica los entregables existentes.
// ======================================================

@Dao
interface EvidenciaDao {

    // ==============================================
    // CONSULTAR EVIDENCIAS DE UN ENTREGABLE
    // ==============================================

    @Query(
        """
        SELECT * FROM evidencias
        WHERE entregableId = :entregableId
        ORDER BY fechaRegistro DESC, id DESC
        """
    )
    fun obtenerPorEntregable(
        entregableId: Int
    ): Flow<List<EvidenciaEntity>>

    // ==============================================
    // CONSULTAR UNA EVIDENCIA POR SU ID
    // ==============================================

    @Query(
        """
        SELECT * FROM evidencias
        WHERE id = :evidenciaId
        LIMIT 1
        """
    )
    suspend fun obtenerPorId(
        evidenciaId: Int
    ): EvidenciaEntity?

    // ==============================================
    // GUARDAR EVIDENCIA
    // ==============================================

    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertar(
        evidencia: EvidenciaEntity
    ): Long

    // ==============================================
    // CONTAR EVIDENCIAS DE UN ENTREGABLE
    // ==============================================

    @Query(
        """
        SELECT COUNT(*) FROM evidencias
        WHERE entregableId = :entregableId
        """
    )
    suspend fun contarPorEntregable(
        entregableId: Int
    ): Int

    // ==============================================
    // ELIMINAR UNA EVIDENCIA
    // ==============================================

    @Delete
    suspend fun eliminar(
        evidencia: EvidenciaEntity
    )
}
