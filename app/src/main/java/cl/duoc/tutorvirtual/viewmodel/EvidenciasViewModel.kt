
package cl.duoc.tutorvirtual.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import cl.duoc.tutorvirtual.data.EsucarRoomRepository
import cl.duoc.tutorvirtual.data.local.EvidenciaEntity

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ======================================================
// VIEWMODEL DE EVIDENCIAS ACADEMICAS
// TUTOR VIRTUAL ESUCAR - PLAN B
//
// MVVM + ROOM + SQLITE
//
// Funcionalidades:
// 1. Consultar evidencias por entregable.
// 2. Registrar documentos PDF.
// 3. Evitar PDF duplicados por URI.
// 4. Registrar fotografias ya guardadas.
// 5. Eliminar registros de evidencias.
//
// No modifica las tablas existentes.
// ======================================================

class EvidenciasViewModel(
    application: Application
) : AndroidViewModel(application) {

    // ==============================================
    // REPOSITORIO
    // ==============================================

    private val repository =
        EsucarRoomRepository(application)

    private val resolver =
        application.contentResolver

    // ==============================================
    // ESTADOS DE LA INTERFAZ
    // ==============================================

    private val _procesando =
        MutableStateFlow(false)

    val procesando: StateFlow<Boolean> =
        _procesando.asStateFlow()

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    // ==============================================
    // CONSULTAR EVIDENCIAS
    // ==============================================

    fun observarEvidencias(
        entregableId: Int
    ): Flow<List<EvidenciaEntity>> {

        return repository.observarEvidencias(
            entregableId
        )
    }

    // ==============================================
    // REGISTRAR DOCUMENTO PDF
    // ==============================================

    fun registrarPdf(
        entregableId: Int,
        nombreArchivo: String,
        uri: Uri,
        onResultado: (Boolean) -> Unit = {}
    ) {

        // Evitar otra operacion mientras
        // existe un registro en proceso.
        if (_procesando.value) {

            _mensaje.value =
                "Ya existe una operación en curso."

            onResultado(false)

            return
        }

        _procesando.value = true
        _mensaje.value = null

        viewModelScope.launch {

            try {

                require(entregableId > 0) {
                    "Selecciona un entregable válido."
                }

                require(nombreArchivo.isNotBlank()) {
                    "El documento debe tener un nombre."
                }

                // ==================================
                // BUSCAR EVIDENCIAS EXISTENTES
                // ==================================

                val evidenciasExistentes =
                    repository.observarEvidencias(
                        entregableId
                    ).first()

                // La URI identifica el documento
                // seleccionado desde Android.
                val uriNueva = uri.toString()

                val documentoDuplicado =
                    evidenciasExistentes.any { evidencia ->

                        evidencia.tipo == "PDF" &&
                                evidencia.uriArchivo == uriNueva
                    }

                // ==================================
                // EVITAR DUPLICADOS
                // ==================================

                if (documentoDuplicado) {

                    _mensaje.value =
                        "Este PDF ya está adjunto " +
                                "al entregable seleccionado."

                    onResultado(false)

                    return@launch
                }

                // ==================================
                // CONSERVAR ACCESO AL PDF
                // ==================================

                resolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                // ==================================
                // CREAR REGISTRO ROOM
                // ==================================

                val evidencia = EvidenciaEntity(
                    entregableId = entregableId,
                    nombreArchivo = nombreArchivo.trim(),
                    tipo = "PDF",
                    uriArchivo = uriNueva
                )

                repository.guardarEvidencia(
                    evidencia
                )

                _mensaje.value =
                    "Documento PDF registrado correctamente."

                onResultado(true)

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _mensaje.value =
                    "No se pudo registrar el PDF: " +
                            (e.message ?: "Error desconocido.")

                onResultado(false)

            } finally {

                _procesando.value = false
            }
        }
    }

    // ==============================================
    // REGISTRAR FOTOGRAFIA
    // ==============================================

    fun registrarFoto(
        entregableId: Int,
        nombreArchivo: String,
        uriArchivo: String,
        onResultado: (Boolean) -> Unit = {}
    ) {

        if (_procesando.value) {

            _mensaje.value =
                "Ya existe una operación en curso."

            onResultado(false)

            return
        }

        _procesando.value = true
        _mensaje.value = null

        viewModelScope.launch {

            try {

                require(entregableId > 0) {
                    "Selecciona un entregable válido."
                }

                require(nombreArchivo.isNotBlank()) {
                    "La fotografía debe tener un nombre."
                }

                require(uriArchivo.isNotBlank()) {
                    "Primero debes guardar la fotografía."
                }

                val evidencia = EvidenciaEntity(
                    entregableId = entregableId,
                    nombreArchivo = nombreArchivo.trim(),
                    tipo = "FOTO",
                    uriArchivo = uriArchivo
                )

                repository.guardarEvidencia(
                    evidencia
                )

                _mensaje.value =
                    "Fotografía registrada correctamente."

                onResultado(true)

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _mensaje.value =
                    "No se pudo registrar la fotografía: " +
                            (e.message ?: "Error desconocido.")

                onResultado(false)

            } finally {

                _procesando.value = false
            }
        }
    }

    // ==============================================
    // ELIMINAR REGISTRO DE EVIDENCIA
    // ==============================================

    fun eliminarEvidencia(
        evidencia: EvidenciaEntity,
        onResultado: (Boolean) -> Unit = {}
    ) {

        if (_procesando.value) {

            _mensaje.value =
                "Ya existe una operación en curso."

            onResultado(false)

            return
        }

        _procesando.value = true
        _mensaje.value = null

        viewModelScope.launch {

            try {

                repository.eliminarEvidencia(
                    evidencia
                )

                _mensaje.value =
                    "Registro de evidencia eliminado."

                onResultado(true)

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _mensaje.value =
                    "No se pudo eliminar la evidencia: " +
                            (e.message ?: "Error desconocido.")

                onResultado(false)

            } finally {

                _procesando.value = false
            }
        }
    }

    // ==============================================
    // LIMPIAR MENSAJE
    // ==============================================

    fun limpiarMensaje() {

        _mensaje.value = null
    }
}
