package cl.duoc.tutorvirtual.data

import cl.duoc.tutorvirtual.model.Hito

object HitosRepository {

    private val hitos = listOf(
        Hito(
            id = 1,
            titulo = "Planteamiento del problema",
            descripcion = "Presentación del problema, justificación y objetivos.",
            fechaLimite = "Por definir"
        ),
        Hito(
            id = 2,
            titulo = "Marco Teórico",
            descripcion = "Entrega de antecedentes y fundamentos teóricos.",
            fechaLimite = "Por definir"
        ),
        Hito(
            id = 3,
            titulo = "Metodología",
            descripcion = "Presentación del diseño y procedimientos de investigación.",
            fechaLimite = "Por definir"
        ),
        Hito(
            id = 4,
            titulo = "Recogida de información",
            descripcion = "Registro y organización de la información recopilada.",
            fechaLimite = "Por definir"
        ),
        Hito(
            id = 5,
            titulo = "Análisis de resultados",
            descripcion = "Presentación e interpretación de los resultados.",
            fechaLimite = "Por definir"
        )
    )

    fun obtenerHitos(): List<Hito> {
        return hitos
    }

    fun obtenerHitoPorId(id: Int): Hito? {
        return hitos.find { it.id == id }
    }
}
