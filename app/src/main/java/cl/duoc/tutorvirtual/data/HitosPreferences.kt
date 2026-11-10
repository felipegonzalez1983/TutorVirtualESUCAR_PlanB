package cl.duoc.tutorvirtual.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.text.ParseException
import java.util.Locale

// Almacenamiento local de los hitos académicos
private val Context.hitosDataStore by preferencesDataStore(
    name = "hitos_esucar"
)

class HitosPreferences(
    private val context: Context
) {

    // 1. Leer si un hito está completado
    fun obtenerCompletado(hitoId: Int): Flow<Boolean> {
        val clave = booleanPreferencesKey(
            "hito_completado_$hitoId"
        )

        return context.hitosDataStore.data.map { preferencias ->
            preferencias[clave] ?: false
        }
    }

    // 2. Guardar si un hito está completado
    suspend fun guardarCompletado(
        hitoId: Int,
        completado: Boolean
    ) {
        val clave = booleanPreferencesKey(
            "hito_completado_$hitoId"
        )

        context.hitosDataStore.edit { preferencias ->
            preferencias[clave] = completado
        }
    }

    // 3. Leer fecha de entrega
    fun obtenerFecha(hitoId: Int): Flow<String?> {
        val clave = stringPreferencesKey(
            "fecha_hito_$hitoId"
        )

        return context.hitosDataStore.data.map { preferencias ->
            preferencias[clave]
        }
    }

    // 4. Guardar fecha de entrega
    suspend fun guardarFecha(
        hitoId: Int,
        fecha: String
    ) {
        val formato = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.ROOT
        ).apply {
            isLenient = false
        }

        try {
            val fechaInterpretada = formato.parse(fecha)

            if (
                fechaInterpretada == null ||
                formato.format(fechaInterpretada) != fecha
            ) {
                throw IllegalArgumentException(
                    "La fecha no es válida"
                )
            }
        } catch (e: ParseException) {
            throw IllegalArgumentException(
                "La fecha no es válida",
                e
            )
        }

        val clave = stringPreferencesKey(
            "fecha_hito_$hitoId"
        )

        context.hitosDataStore.edit { preferencias ->
            preferencias[clave] = fecha
        }
    }

    // 5. Eliminar fecha de entrega
    suspend fun eliminarFecha(hitoId: Int) {
        val clave = stringPreferencesKey(
            "fecha_hito_$hitoId"
        )

        context.hitosDataStore.edit { preferencias ->
            preferencias.remove(clave)
        }
    }

    // 6. Consultar si los recordatorios están activados
    fun obtenerRecordatoriosActivos(
        hitoId: Int
    ): Flow<Boolean> {

        val clave = booleanPreferencesKey(
            "recordatorios_hito_$hitoId"
        )

        return context.hitosDataStore.data.map { preferencias ->
            preferencias[clave] ?: false
        }
    }

    // 7. Activar o desactivar recordatorios
    suspend fun guardarRecordatoriosActivos(
        hitoId: Int,
        activos: Boolean
    ) {
        val clave = booleanPreferencesKey(
            "recordatorios_hito_$hitoId"
        )

        context.hitosDataStore.edit { preferencias ->
            preferencias[clave] = activos
        }
    }
}
