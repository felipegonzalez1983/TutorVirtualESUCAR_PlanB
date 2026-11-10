
package cl.duoc.tutorvirtual.data

import android.content.Context

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import cl.duoc.tutorvirtual.model.ActualizacionAcademica

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import org.json.JSONArray
import org.json.JSONObject

// Almacenamiento exclusivo de novedades académicas
private val Context.actualizacionesDataStore
        by preferencesDataStore(name = "actualizaciones_esucar")

class ActualizacionesPreferences(
    private val context: Context
) {

    // Clave para guardar las novedades
    private val claveActualizaciones =
        stringPreferencesKey("actualizaciones_guardadas")

    // Clave para registrar la última sincronización
    private val claveUltimaSincronizacion =
        longPreferencesKey("ultima_sincronizacion")

    // 1. Recuperar las novedades almacenadas
    fun obtenerActualizaciones():
            Flow<List<ActualizacionAcademica>> {

        return context.actualizacionesDataStore.data.map {
                preferencias ->

            val contenido = preferencias[claveActualizaciones]

            if (contenido.isNullOrBlank()) {
                emptyList()
            } else {
                convertirDesdeJson(contenido)
            }
        }
    }

    // 2. Consultar la última sincronización
    fun obtenerUltimaSincronizacion(): Flow<Long?> {

        return context.actualizacionesDataStore.data.map {
                preferencias ->

            preferencias[claveUltimaSincronizacion]
        }
    }

    // 3. Guardar novedades descargadas correctamente
    suspend fun guardarActualizaciones(
        actualizaciones: List<ActualizacionAcademica>
    ) {

        // Evitar reemplazar contenido por una lista vacía
        if (actualizaciones.isEmpty()) {
            return
        }

        val contenidoJson =
            convertirAJson(actualizaciones)

        context.actualizacionesDataStore.edit {
                preferencias ->

            preferencias[claveActualizaciones] =
                contenidoJson

            preferencias[claveUltimaSincronizacion] =
                System.currentTimeMillis()
        }
    }

    // 4. Convertir novedades a texto JSON
    private fun convertirAJson(
        actualizaciones: List<ActualizacionAcademica>
    ): String {

        val arreglo = JSONArray()

        actualizaciones.forEach { actualizacion ->

            val objeto = JSONObject().apply {

                put("id", actualizacion.id)

                put("titulo", actualizacion.titulo)

                put(
                    "descripcion",
                    actualizacion.descripcion
                )

                put(
                    "fechaPublicacion",
                    actualizacion.fechaPublicacion
                )

                put("fuente", actualizacion.fuente)
            }

            arreglo.put(objeto)
        }

        return arreglo.toString()
    }

    // 5. Recuperar novedades desde JSON
    private fun convertirDesdeJson(
        contenido: String
    ): List<ActualizacionAcademica> {

        return try {

            val arreglo = JSONArray(contenido)

            val resultado =
                mutableListOf<ActualizacionAcademica>()

            for (i in 0 until arreglo.length()) {

                val objeto = arreglo.getJSONObject(i)

                resultado.add(
                    ActualizacionAcademica(
                        id = objeto.getString("id"),
                        titulo = objeto.getString("titulo"),
                        descripcion = objeto.getString(
                            "descripcion"
                        ),
                        fechaPublicacion =
                            objeto.getString(
                                "fechaPublicacion"
                            ),
                        fuente = objeto.getString("fuente")
                    )
                )
            }

            resultado

        } catch (e: Exception) {

            // Si los datos están dañados, no cerrar la app
            emptyList()
        }
    }
}
