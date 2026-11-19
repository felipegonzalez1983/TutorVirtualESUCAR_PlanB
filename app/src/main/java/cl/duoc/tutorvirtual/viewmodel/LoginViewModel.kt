package cl.duoc.tutorvirtual.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tutorvirtual.data.EstudianteDemo
import cl.duoc.tutorvirtual.data.EstudiantesDemoRepository
import cl.duoc.tutorvirtual.data.SesionPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ==================================================
// LOGIN ESTUDIANTIL - TUTOR VIRTUAL ESUCAR
// AUTENTICACION DE DEMOSTRACION + DATASTORE
// ==================================================

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val sesionPreferences =
        SesionPreferences(application)

    // ESTUDIANTE ACTUAL
    private val _estudianteActual =
        MutableStateFlow<EstudianteDemo?>(null)

    val estudianteActual: StateFlow<EstudianteDemo?> =
        _estudianteActual.asStateFlow()

    // MENSAJE DE ERROR
    private val _mensajeError =
        MutableStateFlow<String?>(null)

    val mensajeError: StateFlow<String?> =
        _mensajeError.asStateFlow()

    // INDICA SI DATASTORE ESTA CARGANDO
    private val _restaurandoSesion =
        MutableStateFlow(true)

    val restaurandoSesion: StateFlow<Boolean> =
        _restaurandoSesion.asStateFlow()

    // EVITA QUE UNA LECTURA ANTIGUA
    // REABRA UNA SESION CERRADA
    private var versionSesion = 0

    init {
        recuperarSesion()
    }

    // ==================================================
    // RECUPERAR SESION AL ABRIR LA APLICACION
    // ==================================================

    private fun recuperarSesion() {

        val versionInicial = versionSesion

        viewModelScope.launch {

            try {

                val identificadorGuardado =
                    sesionPreferences.estudianteId.first()

                if (versionInicial == versionSesion) {

                    val estudiante =
                        identificadorGuardado?.let { id ->

                            EstudiantesDemoRepository
                                .obtenerPorIdentificador(id)
                        }

                    _estudianteActual.value = estudiante
                }

            } catch (e: Exception) {

                _mensajeError.value =
                    "No se pudo recuperar la sesión guardada."

            } finally {

                _restaurandoSesion.value = false
            }
        }
    }

    // ==================================================
    // INICIAR SESION
    // ==================================================

    fun iniciarSesion(
        identificador: String,
        contrasena: String
    ): Boolean {

        _mensajeError.value = null

        if (identificador.isBlank()) {

            _mensajeError.value =
                "Ingresa tu identificador de estudiante."

            return false
        }

        if (contrasena.isBlank()) {

            _mensajeError.value =
                "Ingresa tu contraseña."

            return false
        }

        val estudiante =
            EstudiantesDemoRepository.autenticar(
                identificador = identificador,
                clave = contrasena
            )

        if (estudiante == null) {

            _estudianteActual.value = null

            _mensajeError.value =
                "Identificador o contraseña incorrectos."

            return false
        }

        versionSesion++

        _estudianteActual.value = estudiante

        _restaurandoSesion.value = false

        // GUARDAR IDENTIFICADOR, NO CONTRASEÑA
        viewModelScope.launch {

            try {

                sesionPreferences.guardarSesion(
                    estudiante.identificador
                )

            } catch (e: Exception) {

                _mensajeError.value =
                    "Ingresaste, pero no se pudo guardar " +
                            "la sesión para el próximo inicio."
            }
        }

        return true
    }

    // ==================================================
    // CERRAR SESION
    // ==================================================

    fun cerrarSesion() {

        versionSesion++

        _estudianteActual.value = null

        _mensajeError.value = null

        viewModelScope.launch {

            try {

                sesionPreferences.cerrarSesion()

            } catch (e: Exception) {

                _mensajeError.value =
                    "No se pudo borrar la sesión guardada."
            }
        }
    }

    // ==================================================
    // CONSULTAR SESION ACTIVA
    // ==================================================

    fun tieneSesionActiva(): Boolean {

        return _estudianteActual.value != null
    }

    // ==================================================
    // LIMPIAR ERRORES
    // ==================================================

    fun limpiarError() {

        _mensajeError.value = null
    }
}
