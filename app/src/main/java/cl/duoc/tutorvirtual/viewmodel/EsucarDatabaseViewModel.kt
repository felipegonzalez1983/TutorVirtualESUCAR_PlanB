package cl.duoc.tutorvirtual.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tutorvirtual.data.EsucarRoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EsucarDatabaseViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        EsucarRoomRepository(
            application.applicationContext
        )

    private val _baseDatosLista =
        MutableStateFlow(false)

    val baseDatosLista: StateFlow<Boolean> =
        _baseDatosLista.asStateFlow()

    private val _mensajeError =
        MutableStateFlow<String?>(null)

    val mensajeError: StateFlow<String?> =
        _mensajeError.asStateFlow()

    init {
        inicializarBaseDatos()
    }

    private fun inicializarBaseDatos() {

        viewModelScope.launch {

            try {

                // Guardar las cinco etapas en SQLite
                repository.inicializarEtapas()

                // Confirmar que existen registros
                val cantidad =
                    repository.contarEtapas()

                _baseDatosLista.value =
                    cantidad >= 5

                _mensajeError.value = null

            } catch (e: Exception) {

                _baseDatosLista.value = false

                _mensajeError.value =
                    e.message ?: "Error de base de datos"
            }
        }
    }
}
