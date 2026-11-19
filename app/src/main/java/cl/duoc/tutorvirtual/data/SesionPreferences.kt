package cl.duoc.tutorvirtual.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ==================================================
// DATASTORE PARA SESION ESTUDIANTIL
// TUTOR VIRTUAL ESUCAR - PROTOTIPO
// ==================================================

// Una sola instancia de DataStore por archivo.
private val Context.sesionDataStore by preferencesDataStore(
    name = "sesion_esucar"
)

class SesionPreferences(
    context: Context
) {

    private val dataStore =
        context.applicationContext.sesionDataStore

    companion object {

        private val SESION_ACTIVA =
            booleanPreferencesKey("sesion_activa")

        private val ESTUDIANTE_ID =
            stringPreferencesKey("estudiante_id")
    }

    // Observar el identificador de la sesión
    // almacenada en el dispositivo.
    val estudianteId: Flow<String?> =
        dataStore.data.map { preferences ->

            val activa =
                preferences[SESION_ACTIVA] ?: false

            if (activa) {
                preferences[ESTUDIANTE_ID]
                    ?.takeIf { it.isNotBlank() }
            } else {
                null
            }
        }

    // Guardar la sesión del estudiante.
    // Nunca se guarda su contraseña.
    suspend fun guardarSesion(
        identificador: String
    ) {

        require(identificador.isNotBlank()) {
            "El identificador no puede estar vacío."
        }

        dataStore.edit { preferences ->

            preferences[ESTUDIANTE_ID] =
                identificador.trim()

            preferences[SESION_ACTIVA] = true
        }
    }

    // Cerrar sesión y borrar los datos
    // de identificación persistidos.
    suspend fun cerrarSesion() {

        dataStore.edit { preferences ->

            preferences.remove(ESTUDIANTE_ID)
            preferences[SESION_ACTIVA] = false
        }
    }
}
