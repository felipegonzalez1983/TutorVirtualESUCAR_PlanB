
package cl.duoc.tutorvirtual.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import cl.duoc.tutorvirtual.data.EsucarRoomRepository
import cl.duoc.tutorvirtual.data.local.EvidenciaEntity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ======================================================
// VIEWMODEL DE EVIDENCIAS ACADEMICAS
// TUTOR VIRTUAL ESUCAR - PLAN B
//
// Arquitectura MVVM + Room + SQLite
//
// Permite:
// 1. Consultar evidencias por entregable.
// 2. Guardar referencias de archivos PDF.
// 3. Registrar fotografias ya guardadas.
// 4. Eliminar registros de evidencias.
//
// IMPORTANTE:
// Las fotografias deben guardarse como archivos
// permanentes antes de llamar a registrarFoto.
//
// Los PDF se seleccionan mediante OpenDocument.
// ======================================================

class EvidenciasViewModel(
    application: Application
) : AndroidViewModel(application) {

    // ==============================================
    // REPOSITORIO ROOM
    // ==============================================

    private val repository =
        EsucarRoomRepository(application)

    private val resolver =
        application.contentResolver

    // ==============================================
    // ESTADO DE OPERACIONES
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
    // REGISTRAR PDF
    // ==============================================

    fun registrarPdf(
        entregableId: Int,
        nombreArchivo: String,
        uri: Uri,
        onResultado: (Boolean) -> Unit = {}
    ) {

        viewModelScope.launch {

            _procesando.value = true
            _mensaje.value = null

            try {

                require(entregableId > 0) {
                    "Selecciona un entregable válido."
                }

                require(nombreArchivo.isNotBlank()) {
                    "El documento debe tener un nombre."
                }

                // Conservar acceso al documento
                // seleccionado mediante OpenDocument.
                resolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                val evidencia = EvidenciaEntity(
                    entregableId = entregableId,
                    nombreArchivo = nombreArchivo.trim(),
                    tipo = "PDF",
                    uriArchivo = uri.toString()
                )

                repository.guardarEvidencia(
                    evidencia
                )

                _mensaje.value =
                    "Documento PDF registrado correctamente."

                onResultado(true)

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
    // REGISTRAR FOTOGRAFIA YA GUARDADA
    // ==============================================

    fun registrarFoto(
        entregableId: Int,
        nombreArchivo: String,
        uriArchivo: String,
        onResultado: (Boolean) -> Unit = {}
    ) {

        viewModelScope.launch {

            _procesando.value = true
            _mensaje.value = null

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

        viewModelScope.launch {

            _procesando.value = true
            _mensaje.value = null

            try {

                repository.eliminarEvidencia(
                    evidencia
                )

                _mensaje.value =
                    "Registro de evidencia eliminado."

                onResultado(true)

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
