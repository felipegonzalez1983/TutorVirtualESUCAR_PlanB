
package cl.duoc.tutorvirtual.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Almacenamiento local exclusivo de entregables
private val Context.entregablesDataStore:
        DataStore<Preferences> by preferencesDataStore(
    name = "entregables_esucar"
)

class EntregablesPreferences(
    private val context: Context
) {

    // Leer el estado guardado de una etapa
    fun obtenerEstado(etapaId: Int): Flow<String> {

        val clave = stringPreferencesKey(
            "estado_etapa_$etapaId"
        )

        return context.entregablesDataStore.data.map {
                preferencias ->
            preferencias[clave] ?: "Por iniciar"
        }
    }

    // Guardar el nuevo estado de una etapa
    suspend fun guardarEstado(
        etapaId: Int,
        nuevoEstado: String
    ) {
        val estadosPermitidos = listOf(
            "Por iniciar",
            "En desarrollo",
            "Enviado",
            "Aprobado"
        )

        require(nuevoEstado in estadosPermitidos) {
            "Estado no válido"
        }

        val clave = stringPreferencesKey(
            "estado_etapa_$etapaId"
        )

        context.entregablesDataStore.edit {
                preferencias ->
            preferencias[clave] = nuevoEstado
        }
    }
}
