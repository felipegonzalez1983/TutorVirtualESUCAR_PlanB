package cl.duoc.tutorvirtual.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Almacenamiento local de la ubicacion del trabajo de campo
private val Context.ubicacionDataStore:
        DataStore<Preferences> by preferencesDataStore(
    name = "ubicacion_esucar"
)

// Ubicacion registrada para una etapa
data class UbicacionTrabajo(
    val latitud: Double,
    val longitud: Double,
    val precisionMetros: Float,
    val fechaRegistro: Long
)

class UbicacionPreferences(
    private val context: Context
) {

    private fun clave(etapaId: Int) =
        stringPreferencesKey("ubicacion_etapa_$etapaId")

    // Leer la ubicacion guardada de una etapa
    fun obtenerUbicacion(etapaId: Int): Flow<UbicacionTrabajo?> {

        return context.ubicacionDataStore.data.map { preferencias ->

            val texto = preferencias[clave(etapaId)]
                ?: return@map null

            val partes = texto.split("|")

            if (partes.size != 4) {
                return@map null
            }

            val latitud = partes[0].toDoubleOrNull()
            val longitud = partes[1].toDoubleOrNull()
            val precision = partes[2].toFloatOrNull()
            val fecha = partes[3].toLongOrNull()

            if (latitud == null || longitud == null ||
                precision == null || fecha == null
            ) {
                return@map null
            }

            UbicacionTrabajo(latitud, longitud, precision, fecha)
        }
    }

    // Guardar la ubicacion de una etapa
    suspend fun guardarUbicacion(
        etapaId: Int,
        ubicacion: UbicacionTrabajo
    ) {

        val texto = listOf(
            ubicacion.latitud,
            ubicacion.longitud,
            ubicacion.precisionMetros,
            ubicacion.fechaRegistro
        ).joinToString("|")

        context.ubicacionDataStore.edit { preferencias ->
            preferencias[clave(etapaId)] = texto
        }
    }

    // Eliminar la ubicacion de una etapa
    suspend fun borrarUbicacion(etapaId: Int) {

        context.ubicacionDataStore.edit { preferencias ->
            preferencias.remove(clave(etapaId))
        }
    }
}
