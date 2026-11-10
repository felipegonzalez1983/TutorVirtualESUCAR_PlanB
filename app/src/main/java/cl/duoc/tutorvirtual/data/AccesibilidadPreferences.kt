package cl.duoc.tutorvirtual.data

import android.content.Context

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Almacenamiento local de accesibilidad
private val Context.accesibilidadDataStore
        by preferencesDataStore(
            name = "accesibilidad_esucar"
        )

class AccesibilidadPreferences(
    private val context: Context
) {

    // Clave del modo de alto contraste
    private val claveAltoContraste =
        booleanPreferencesKey("alto_contraste")

    // Consultar la preferencia guardada
    fun obtenerAltoContraste(): Flow<Boolean> {

        return context.accesibilidadDataStore.data.map {
                preferencias ->

            preferencias[claveAltoContraste] ?: false
        }
    }

    // Activar o desactivar alto contraste
    suspend fun guardarAltoContraste(
        activado: Boolean
    ) {

        context.accesibilidadDataStore.edit {
                preferencias ->

            preferencias[claveAltoContraste] = activado
        }
    }
}
