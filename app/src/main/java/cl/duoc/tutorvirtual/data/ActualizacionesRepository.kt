package cl.duoc.tutorvirtual.data

import android.content.Context

import cl.duoc.tutorvirtual.model.ActualizacionAcademica

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

import org.json.JSONArray

import java.net.HttpURLConnection
import java.net.URL

// Resultado de intentar actualizar las novedades
data class ResultadoSincronizacion(
    val actualizaciones: List<ActualizacionAcademica>,
    val desdeInternet: Boolean,
    val ultimaSincronizacion: Long?,
    val mensaje: String
)

class ActualizacionesRepository(
    context: Context
) {

    private val preferences = ActualizacionesPreferences(
        context.applicationContext
    )

    // Obtener las novedades guardadas sin usar internet
    suspend fun obtenerGuardadas():
            List<ActualizacionAcademica> {

        return preferences
            .obtenerActualizaciones()
            .first()
    }

    // Descargar y guardar novedades desde una URL HTTPS
    suspend fun sincronizar(
        urlContenido: String
    ): ResultadoSincronizacion = withContext(Dispatchers.IO) {

        try {

            require(urlContenido.startsWith("https://")) {
                "La dirección debe utilizar HTTPS"
            }

            val conexion = URL(
                urlContenido
            ).openConnection() as HttpURLConnection

            conexion.requestMethod = "GET"
            conexion.connectTimeout = 10000
            conexion.readTimeout = 10000

            conexion.setRequestProperty(
                "Accept",
                "application/json"
            )

            val contenido = try {

                if (conexion.responseCode !in 200..299) {
                    throw Exception(
                        "Error HTTP ${conexion.responseCode}"
                    )
                }

                conexion.inputStream
                    .bufferedReader(Charsets.UTF_8)
                    .use { lector ->
                        lector.readText()
                    }

            } finally {
                conexion.disconnect()
            }

            // Convertir el contenido JSON a novedades
            val novedades = convertirJson(contenido)

            if (novedades.isEmpty()) {
                throw Exception(
                    "No se encontraron novedades válidas"
                )
            }

            // Guardar la nueva información en DataStore
            preferences.guardarActualizaciones(
                novedades
            )

            val ultimaActualizacion = preferences
                .obtenerUltimaSincronizacion()
                .first()

            ResultadoSincronizacion(
                actualizaciones = novedades,
                desdeInternet = true,
                ultimaSincronizacion = ultimaActualizacion,
                mensaje = "Contenido actualizado correctamente"
            )

        } catch (e: Exception) {

            // Si falla la conexión, utilizar copia local
            val guardadas = preferences
                .obtenerActualizaciones()
                .first()

            val ultimaActualizacion = preferences
                .obtenerUltimaSincronizacion()
                .first()

            ResultadoSincronizacion(
                actualizaciones = guardadas,
                desdeInternet = false,
                ultimaSincronizacion = ultimaActualizacion,
                mensaje = if (guardadas.isEmpty()) {
                    "No se pudo actualizar y aún no hay contenido guardado"
                } else {
                    "Sin conexión o error de actualización. Mostrando contenido guardado"
                }
            )
        }
    }

    // Convertir JSON descargado a objetos Kotlin
    private fun convertirJson(
        contenido: String
    ): List<ActualizacionAcademica> {

        val arreglo = JSONArray(contenido)

        val resultado =
            mutableListOf<ActualizacionAcademica>()

        for (i in 0 until arreglo.length()) {

            val objeto = arreglo.getJSONObject(i)

            val novedad = ActualizacionAcademica(
                id = objeto.getString("id"),
                titulo = objeto.getString("titulo"),
                descripcion = objeto.getString("descripcion"),
                fechaPublicacion = objeto.getString(
                    "fechaPublicacion"
                ),
                fuente = objeto.getString("fuente")
            )

            require(
                novedad.id.isNotBlank() &&
                        novedad.titulo.isNotBlank() &&
                        novedad.descripcion.isNotBlank() &&
                        novedad.fechaPublicacion.isNotBlank() &&
                        novedad.fuente.isNotBlank()
            ) {
                "Una novedad contiene campos vacíos"
            }

            resultado.add(novedad)
        }

        return resultado
    }
}
