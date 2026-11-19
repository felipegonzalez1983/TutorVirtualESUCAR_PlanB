package cl.duoc.tutorvirtual.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import cl.duoc.tutorvirtual.data.EsucarRoomRepository
import cl.duoc.tutorvirtual.data.local.EvidenciaEntity

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ======================================================
// TUTOR VIRTUAL ESUCAR - PLAN B
// EVIDENCIAS VIEWMODEL
//
// FUNCIONALIDADES:
// - Consultar evidencias de un entregable
// - Registrar documentos PDF
// - Evitar PDF duplicados
// - Registrar fotografias permanentes
// - Eliminar referencias PDF de Room
// - Eliminar fotografias fisicas y su registro
// - Gestionar mensajes y estado de procesamiento
// ======================================================

class EvidenciasViewModel(
    application: Application
) : AndroidViewModel(application) {

    // ==============================================
    // REPOSITORIO Y ALMACENAMIENTO
    // ==============================================

    private val repository =
        EsucarRoomRepository(
            application.applicationContext
        )

    private val resolver =
        application.contentResolver

    // ==============================================
    // ESTADOS
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

                val uriNueva = uri.toString()

                // Consultar evidencias existentes.
                val evidenciasExistentes =
                    repository.observarEvidencias(
                        entregableId
                    ).first()

                // Evitar guardar nuevamente el mismo PDF.
                val documentoDuplicado =
                    evidenciasExistentes.any { evidencia ->

                        evidencia.tipo == "PDF" &&
                                evidencia.uriArchivo == uriNueva
                    }

                if (documentoDuplicado) {

                    _mensaje.value =
                        "Este PDF ya está adjunto " +
                                "al entregable seleccionado."

                    onResultado(false)
                    return@launch
                }

                // Mantener permiso de lectura del PDF.
                resolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                // Registrar referencia en Room.
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
    // ELIMINAR EVIDENCIA
    //
    // PDF:
    // - Eliminar solamente el registro de Room.
    // - Conservar el archivo original.
    //
    // FOTO:
    // - Eliminar el archivo de MediaStore.
    // - Eliminar su registro de Room.
    //
    // Si Android no autoriza eliminar la foto,
    // se conserva el registro para evitar que
    // desaparezca de la app sin haberse borrado.
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

                require(evidencia.id > 0) {
                    "La evidencia no tiene un ID válido."
                }

                when (evidencia.tipo) {

                    // ==================================
                    // ELIMINACION DE FOTOGRAFIA
                    // ==================================

                    "FOTO" -> {

                        val uri = Uri.parse(
                            evidencia.uriArchivo
                        )

                        // Solo admitir referencias a
                        // contenido multimedia de Android.
                        require(
                            uri.scheme == "content" &&
                                    uri.authority == "media"
                        ) {
                            "La fotografía no tiene una " +
                                    "referencia MediaStore válida."
                        }

                        // Eliminar la fotografia fisica.
                        // Se hace en un hilo de I/O.
                        val filasEliminadas =
                            withContext(Dispatchers.IO) {

                                resolver.delete(
                                    uri,
                                    null,
                                    null
                                )
                            }

                        // Una fila indica archivo borrado.
                        // Cero puede significar que la imagen
                        // ya no existe en MediaStore.
                        if (filasEliminadas < 0) {

                            error(
                                "No fue posible eliminar la fotografía."
                            )
                        }

                        // Eliminar el registro de Room.
                        repository.eliminarEvidencia(
                            evidencia
                        )

                        _mensaje.value =
                            if (filasEliminadas > 0) {

                                "Fotografía y registro " +
                                        "eliminados correctamente."

                            } else {

                                "Registro eliminado. " +
                                        "La fotografía ya no " +
                                        "estaba disponible."
                            }
                    }

                    // ==================================
                    // ELIMINACION DE PDF
                    // ==================================

                    "PDF" -> {

                        repository.eliminarEvidencia(
                            evidencia
                        )

                        _mensaje.value =
                            "PDF desvinculado correctamente. " +
                                    "El archivo original se conserva."
                    }

                    // ==================================
                    // TIPO DESCONOCIDO
                    // ==================================

                    else -> {

                        error(
                            "Tipo de evidencia no reconocido."
                        )
                    }
                }

                onResultado(true)

            } catch (e: CancellationException) {

                throw e

            } catch (e: SecurityException) {

                _mensaje.value =
                    "Android no autorizó eliminar " +
                            "la fotografía. El registro se conserva."

                onResultado(false)

            } catch (e: Exception) {

                _mensaje.value =
                    "No se pudo completar la eliminación: " +
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
