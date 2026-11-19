package cl.duoc.tutorvirtual.data

import android.content.Context

import cl.duoc.tutorvirtual.data.local.EntregableEntity
import cl.duoc.tutorvirtual.data.local.EsucarDatabase
import cl.duoc.tutorvirtual.data.local.EtapaEntity
import cl.duoc.tutorvirtual.data.local.EvidenciaEntity

import kotlinx.coroutines.flow.Flow

// ======================================================
// REPOSITORIO ROOM - TUTOR VIRTUAL ESUCAR
//
// Gestiona:
// 1. Etapas metodologicas
// 2. Entregables academicos
// 3. Evidencias: fotos y documentos PDF
//
// Conserva las funciones anteriores.
// ======================================================

class EsucarRoomRepository(context: Context) {

    // ==============================================
    // CONEXION CON SQLITE MEDIANTE ROOM
    // ==============================================

    private val database =
        EsucarDatabase.obtenerInstancia(
            context.applicationContext
        )

    private val etapaDao =
        database.etapaDao()

    private val entregableDao =
        database.entregableDao()

    // NUEVO: DAO DE EVIDENCIAS
    private val evidenciaDao =
        database.evidenciaDao()

    // ==============================================
    // ETAPAS METODOLOGICAS
    // ==============================================

    // Cargar las cinco etapas sin duplicarlas
    suspend fun inicializarEtapas() {

        val etapasIniciales =
            EtapasRepository.obtenerEtapas().map { etapa ->

                EtapaEntity(
                    id = etapa.id,
                    titulo = etapa.titulo,
                    descripcion = etapa.descripcion,
                    formatoRequerido = etapa.formatoRequerido,
                    fechaLimite = etapa.fechaLimite,
                    estado = etapa.estado
                )
            }

        // IGNORE conserva los registros existentes
        etapaDao.insertarEtapas(etapasIniciales)
    }

    // Consultar etapas en tiempo real
    fun observarEtapas(): Flow<List<EtapaEntity>> {

        return etapaDao.obtenerTodasLasEtapas()
    }

    // Consultar etapa por ID
    suspend fun obtenerEtapaPorId(
        id: Int
    ): EtapaEntity? {

        return etapaDao.obtenerEtapaPorId(id)
    }

    // Actualizar estado de una etapa
    suspend fun actualizarEstadoEtapa(
        etapaId: Int,
        nuevoEstado: String
    ) {

        etapaDao.actualizarEstado(
            etapaId,
            nuevoEstado
        )
    }

    // Contar etapas almacenadas
    suspend fun contarEtapas(): Int {

        return etapaDao.contarEtapas()
    }

    // ==============================================
    // ENTREGABLES ACADEMICOS
    // ==============================================

    // Consultar entregables por etapa
    fun observarEntregables(
        etapaId: Int
    ): Flow<List<EntregableEntity>> {

        return entregableDao.obtenerPorEtapa(etapaId)
    }

    // Guardar nuevo entregable
    suspend fun guardarEntregable(
        entregable: EntregableEntity
    ): Long {

        return entregableDao.insertar(entregable)
    }

    // Actualizar entregable existente
    suspend fun actualizarEntregable(
        entregable: EntregableEntity
    ) {

        entregableDao.actualizar(entregable)
    }

    // Cambiar estado de un entregable
    suspend fun actualizarEstadoEntregable(
        entregableId: Int,
        nuevoEstado: String
    ) {

        entregableDao.actualizarEstado(
            entregableId,
            nuevoEstado
        )
    }

    // Eliminar entregable
    suspend fun eliminarEntregable(
        entregable: EntregableEntity
    ) {

        // Por la clave foranea ON DELETE CASCADE,
        // Room tambien eliminara los registros
        // de evidencias vinculados al entregable.
        //
        // Los archivos fisicos requieren
        // una limpieza independiente.

        entregableDao.eliminar(entregable)
    }

    // ==============================================
    // EVIDENCIAS ACADEMICAS
    // ==============================================

    // ----------------------------------------------
    // CONSULTAR EVIDENCIAS DE UN ENTREGABLE
    // ----------------------------------------------

    fun observarEvidencias(
        entregableId: Int
    ): Flow<List<EvidenciaEntity>> {

        return evidenciaDao.obtenerPorEntregable(
            entregableId
        )
    }

    // ----------------------------------------------
    // CONSULTAR EVIDENCIA POR ID
    // ----------------------------------------------

    suspend fun obtenerEvidenciaPorId(
        evidenciaId: Int
    ): EvidenciaEntity? {

        return evidenciaDao.obtenerPorId(
            evidenciaId
        )
    }

    // ----------------------------------------------
    // GUARDAR REFERENCIA DE UNA EVIDENCIA
    // ----------------------------------------------

    suspend fun guardarEvidencia(
        evidencia: EvidenciaEntity
    ): Long {

        require(evidencia.entregableId > 0) {
            "La evidencia necesita un entregable válido."
        }

        require(evidencia.nombreArchivo.isNotBlank()) {
            "El nombre del archivo es obligatorio."
        }

        require(
            evidencia.tipo == "PDF" ||
                    evidencia.tipo == "FOTO"
        ) {
            "El tipo de evidencia debe ser PDF o FOTO."
        }

        require(evidencia.uriArchivo.isNotBlank()) {
            "La referencia del archivo es obligatoria."
        }

        return evidenciaDao.insertar(
            evidencia
        )
    }

    // ----------------------------------------------
    // CONTAR EVIDENCIAS POR ENTREGABLE
    // ----------------------------------------------

    suspend fun contarEvidencias(
        entregableId: Int
    ): Int {

        return evidenciaDao.contarPorEntregable(
            entregableId
        )
    }

    // ----------------------------------------------
    // ELIMINAR REFERENCIA DE EVIDENCIA
    // ----------------------------------------------

    suspend fun eliminarEvidencia(
        evidencia: EvidenciaEntity
    ) {

        // Elimina el registro de Room.
        //
        // Si es una fotografia almacenada
        // en un archivo privado de la app,
        // su archivo fisico debera eliminarse
        // por separado.

        evidenciaDao.eliminar(
            evidencia
        )
    }
}
