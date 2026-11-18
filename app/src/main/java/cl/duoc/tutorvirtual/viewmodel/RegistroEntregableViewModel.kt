
package cl.duoc.tutorvirtual.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import cl.duoc.tutorvirtual.data.EsucarRoomRepository
import cl.duoc.tutorvirtual.data.EtapasRepository
import cl.duoc.tutorvirtual.data.local.EntregableEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class RegistroEntregableViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = EsucarRoomRepository(
        application.applicationContext
    )

    val etapas = EtapasRepository
        .obtenerEtapas()
        .sortedBy { it.id }

    val estadosDisponibles = listOf(
        "Por iniciar",
        "En desarrollo",
        "Enviado",
        "Aprobado"
    )

    // CONSULTA DE ROOM CON DIAGNOSTICO
    fun observarEntregables(
        etapaId: Int
    ): Flow<List<EntregableEntity>> {

        Log.d(
            "ESUCAR_FLOW",
            "Solicitando entregables de etapa $etapaId"
        )

        return repository
            .observarEntregables(etapaId)
            .onEach { entregables ->

                Log.d(
                    "ESUCAR_FLOW",
                    "Etapa $etapaId: " +
                            "${entregables.size} registros recibidos"
                )

                entregables.forEach { entregable ->
                    Log.d(
                        "ESUCAR_FLOW",
                        "ID=${entregable.id}, " +
                                "etapaId=${entregable.etapaId}, " +
                                "titulo=${entregable.titulo}"
                    )
                }
            }
            .catch { error ->

                Log.e(
                    "ESUCAR_FLOW",
                    "Error leyendo etapa $etapaId",
                    error
                )

                emit(emptyList())
            }
    }

    // VALIDAR Y GUARDAR ENTREGABLE
    suspend fun registrarEntregable(
        etapaId: Int,
        titulo: String,
        descripcion: String,
        fechaEntrega: String,
        estado: String = "Por iniciar"
    ): String? {

        val tituloLimpio = titulo.trim()
        val descripcionLimpia = descripcion.trim()
        val fechaLimpia = fechaEntrega.trim()

        if (etapas.none { it.id == etapaId }) {
            return "Selecciona una etapa válida."
        }

        if (tituloLimpio.isBlank()) {
            return "Debes ingresar un título."
        }

        if (descripcionLimpia.isBlank()) {
            return "Debes ingresar una descripción."
        }

        if (!validarFecha(fechaLimpia)) {
            return "La fecha debe tener formato AAAA-MM-DD."
        }

        if (estado !in estadosDisponibles) {
            return "Selecciona un estado válido."
        }

        return try {

            withContext(Dispatchers.IO) {

                repository.inicializarEtapas()

                val entregable = EntregableEntity(
                    etapaId = etapaId,
                    titulo = tituloLimpio,
                    descripcion = descripcionLimpia,
                    fechaEntrega = fechaLimpia,
                    estado = estado,
                    observaciones = ""
                )

                repository.guardarEntregable(entregable)
            }

            Log.i(
                "ESUCAR_DATABASE",
                "Entregable registrado correctamente"
            )

            null

        } catch (e: Exception) {

            Log.e(
                "ESUCAR_DATABASE",
                "Error al registrar entregable",
                e
            )

            "No se pudo guardar el entregable."
        }
    }

    private fun validarFecha(
        fecha: String
    ): Boolean {

        if (!Regex(
                """\d{4}-\d{2}-\d{2}"""
            ).matches(fecha)
        ) {
            return false
        }

        return try {

            val formato = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )

            formato.isLenient = false

            formato.parse(fecha) != null

        } catch (e: Exception) {
            false
        }
    }
}
