package cl.duoc.tutorvirtual.data

import android.content.Context
import cl.duoc.tutorvirtual.data.local.EntregableEntity
import cl.duoc.tutorvirtual.data.local.EsucarDatabase
import cl.duoc.tutorvirtual.data.local.EtapaEntity
import kotlinx.coroutines.flow.Flow

class EsucarRoomRepository(context: Context) {

    // Conexion con SQLite mediante Room
    private val database =
        EsucarDatabase.obtenerInstancia(
            context.applicationContext
        )

    private val etapaDao = database.etapaDao()

    private val entregableDao =
        database.entregableDao()

    // Cargar las cinco etapas metodologicas
    // sin duplicar registros existentes
    suspend fun inicializarEtapas() {

        val etapasIniciales =
            EtapasRepository.obtenerEtapas().map { etapa ->

                EtapaEntity(
                    id = etapa.id,
                    titulo = etapa.titulo,
                    descripcion = etapa.descripcion,
                    formatoRequerido =
                        etapa.formatoRequerido,
                    fechaLimite = etapa.fechaLimite,
                    estado = etapa.estado
                )
            }

        // IGNORE conserva registros que ya existen
        etapaDao.insertarEtapas(etapasIniciales)
    }

    // Consultar etapas en tiempo real
    fun observarEtapas(): Flow<List<EtapaEntity>> {
        return etapaDao.obtenerTodasLasEtapas()
    }

    // Consultar una etapa por ID
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

    // Consultar entregables por etapa
    fun observarEntregables(
        etapaId: Int
    ): Flow<List<EntregableEntity>> {
        return entregableDao.obtenerPorEtapa(etapaId)
    }

    // Guardar un entregable nuevo
    suspend fun guardarEntregable(
        entregable: EntregableEntity
    ): Long {
        return entregableDao.insertar(entregable)
    }

    // Actualizar un entregable existente
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
}
