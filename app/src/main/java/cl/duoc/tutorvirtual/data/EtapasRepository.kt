
package cl.duoc.tutorvirtual.data

import cl.duoc.tutorvirtual.model.Etapa

object EtapasRepository {

    val etapas = listOf(
        Etapa(
            id = 1,
            titulo = "Planteamiento del problema",
            descripcion = "Define el problema de investigación, su justificación y los objetivos del trabajo.",
            formatoRequerido = "Documento con planteamiento del problema, justificación y objetivos.",
            fechaLimite = "Por definir"
        ),

        Etapa(
            id = 2,
            titulo = "Marco Teórico",
            descripcion = "Investiga los antecedentes y fundamentos teóricos relacionados con el problema.",
            formatoRequerido = "Documento con antecedentes, conceptos y referencias bibliográficas.",
            fechaLimite = "Por definir"
        ),

        Etapa(
            id = 3,
            titulo = "Metodología",
            descripcion = "Establece el enfoque, diseño y procedimientos de la investigación.",
            formatoRequerido = "Documento que describe el diseño metodológico.",
            fechaLimite = "Por definir"
        ),

        Etapa(
            id = 4,
            titulo = "Recogida de información",
            descripcion = "Organiza la obtención de información necesaria para la investigación.",
            formatoRequerido = "Registro de técnicas e instrumentos de recopilación de datos.",
            fechaLimite = "Por definir"
        ),

        Etapa(
            id = 5,
            titulo = "Análisis de resultados",
            descripcion = "Analiza e interpreta los datos obtenidos durante la investigación.",
            formatoRequerido = "Informe de resultados e interpretación de hallazgos.",
            fechaLimite = "Por definir"
        )
    )

    fun obtenerEtapas(): List<Etapa> {
        return etapas
    }

    fun obtenerEtapaPorId(id: Int): Etapa? {
        return etapas.find { it.id == id }
    }
}
