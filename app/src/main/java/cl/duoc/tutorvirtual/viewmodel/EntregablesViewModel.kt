package cl.duoc.tutorvirtual.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tutorvirtual.data.EntregablesPreferences
import cl.duoc.tutorvirtual.data.EsucarRoomRepository
import cl.duoc.tutorvirtual.data.EtapasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class EntregablesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val preferences = EntregablesPreferences(
        application.applicationContext
    )

    private val repository = EsucarRoomRepository(
        application.applicationContext
    )

    private val mutex = Mutex()

    private val flujosEstado =
        mutableMapOf<Int, Flow<String>>()

    val etapas = EtapasRepository.obtenerEtapas()
        .sortedBy { it.id }

    val opcionesEstado = listOf(
        "Por iniciar",
        "En desarrollo",
        "Enviado",
        "Aprobado"
    )

    init {
        // Sincronizar los estados existentes en DataStore
        // con SQLite sin eliminar los avances del estudiante.
        viewModelScope.launch {
            try {
                mutex.withLock {
                    repository.inicializarEtapas()

                    etapas.forEach { etapa ->
                        val estadoGuardado = preferences
                            .obtenerEstado(etapa.id)
                            .first()

                        repository.actualizarEstadoEtapa(
                            etapa.id,
                            estadoGuardado
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(
                    "ESUCAR_DATABASE",
                    "Error al sincronizar estados",
                    e
                )
            }
        }
    }

    // La interfaz observa los estados existentes
    // sin acceder directamente a DataStore.
    fun observarEstado(
        etapaId: Int
    ): Flow<String> {
        return flujosEstado.getOrPut(etapaId) {
            preferences.obtenerEstado(etapaId)
        }
    }

    // Actualizar el estado en Room y DataStore.
    suspend fun guardarEstado(
        etapaId: Int,
        nuevoEstado: String
    ) {
        require(nuevoEstado in opcionesEstado) {
            "Estado académico no válido"
        }

        require(etapas.any { it.id == etapaId }) {
            "Etapa académica no válida"
        }

        mutex.withLock {
            repository.inicializarEtapas()

            // Guardado relacional SQLite
            repository.actualizarEstadoEtapa(
                etapaId,
                nuevoEstado
            )

            // Mantener compatibilidad con las
            // pantallas existentes que leen DataStore
            preferences.guardarEstado(
                etapaId,
                nuevoEstado
            )
        }
    }
}
