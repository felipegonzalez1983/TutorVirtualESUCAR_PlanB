package cl.duoc.tutorvirtual.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.tutorvirtual.data.EntregablesPreferences
import cl.duoc.tutorvirtual.data.EtapasRepository
import cl.duoc.tutorvirtual.model.Etapa
import kotlinx.coroutines.flow.Flow
import java.text.Normalizer
import java.util.Locale

class EtapasViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Acceso a los estados guardados localmente
    private val entregablesPreferences =
        EntregablesPreferences(
            application.applicationContext
        )

    // Catálogo de etapas metodológicas
    val etapas: List<Etapa> =
        EtapasRepository.obtenerEtapas()
            .sortedBy { it.id }

    // Estado del buscador
    var busqueda by mutableStateOf("")
        private set

    // Mantiene los flujos de estado por etapa
    private val flujosEstado =
        mutableMapOf<Int, Flow<String>>()

    // Actualizar texto de búsqueda
    fun actualizarBusqueda(texto: String) {
        busqueda = texto
    }

    // Buscar ignorando mayúsculas y tildes
    private fun normalizarTexto(
        texto: String
    ): String {

        return Normalizer.normalize(
            texto.lowercase(Locale.ROOT),
            Normalizer.Form.NFD
        ).replace(
            Regex("\\p{M}+"),
            ""
        )
    }

    // Filtrar las etapas según la búsqueda
    val etapasFiltradas: List<Etapa>
        get() {

            val textoBuscado =
                normalizarTexto(
                    busqueda.trim()
                )

            return etapas.filter { etapa ->

                val titulo =
                    normalizarTexto(etapa.titulo)

                val descripcion =
                    normalizarTexto(etapa.descripcion)

                textoBuscado.isEmpty() ||
                        titulo.contains(textoBuscado) ||
                        descripcion.contains(textoBuscado)
            }
        }

    // Obtener detalle de una etapa
    fun obtenerEtapaPorId(
        id: Int
    ): Etapa? {

        return EtapasRepository
            .obtenerEtapaPorId(id)
    }

    // Proporcionar el estado persistente
    // de una etapa a la interfaz
    fun observarEstadoEtapa(
        etapaId: Int
    ): Flow<String> {

        return flujosEstado.getOrPut(etapaId) {
            entregablesPreferences
                .obtenerEstado(etapaId)
        }
    }
}
