
package cl.duoc.tutorvirtual.data

import cl.duoc.tutorvirtual.model.Etapa

object EtapasRepository {

    val etapas = listOf(
        Etapa(
            id = 1,
            titulo = "Planteamiento del problema",
            descripcion = "Define el problema de investigación, delimita el fenómeno (qué, quiénes, dónde y cuándo) y formula la pregunta, los objetivos y la justificación.",
            formatoRequerido = "Capítulo con antecedentes, descripción del problema, pregunta de investigación, objetivos general y específicos, justificación y delimitación.",
            fechaLimite = "Por definir"
        ),

        Etapa(
            id = 2,
            titulo = "Marco Teórico",
            descripcion = "Busca y selecciona fuentes confiables para organizar los antecedentes y conceptos que sustentan el problema, citados en norma APA 7.",
            formatoRequerido = "Capítulo con antecedentes investigativos, bases teóricas y conceptos clave, con citas y referencias en APA 7.",
            fechaLimite = "Por definir"
        ),

        Etapa(
            id = 3,
            titulo = "Metodología",
            descripcion = "Define el diseño, el tipo y el enfoque de la investigación, el universo o muestra y las técnicas e instrumentos de recolección.",
            formatoRequerido = "Capítulo con diseño, tipo y enfoque, población y muestra, técnicas e instrumentos y resguardos éticos.",
            fechaLimite = "Por definir"
        ),

        Etapa(
            id = 4,
            titulo = "Recogida de información",
            descripcion = "Realiza el trabajo de campo y registra la información con las técnicas e instrumentos definidos, cuidando el rigor ético.",
            formatoRequerido = "Registro de las técnicas e instrumentos aplicados y de la información recopilada, con los consentimientos en anexos.",
            fechaLimite = "Por definir"
        ),

        Etapa(
            id = 5,
            titulo = "Análisis de resultados",
            descripcion = "Analiza e interpreta los resultados en relación con los objetivos o hipótesis y propone alternativas de solución.",
            formatoRequerido = "Capítulo de análisis de resultados vinculado a cada objetivo, con tablas y figuras numeradas y comentadas.",
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
