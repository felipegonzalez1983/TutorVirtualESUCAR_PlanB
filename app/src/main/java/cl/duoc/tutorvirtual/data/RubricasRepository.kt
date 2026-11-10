package cl.duoc.tutorvirtual.data

import cl.duoc.tutorvirtual.model.CriterioRubrica

object RubricasRepository {

    // Criterios de ejemplo para el prototipo.
    // No representan una rúbrica oficial de ESUCAR.
    // puntajeMaximo = 0 indica puntaje no definido.

    private val criterios = listOf(

        // ETAPA 1: PLANTEAMIENTO DEL PROBLEMA

        CriterioRubrica(
            id = 1,
            etapaId = 1,
            titulo = "Claridad del problema",
            descripcion = "El problema de investigación está " +
                    "definido de manera clara y comprensible.",
            puntajeMaximo = 0
        ),

        CriterioRubrica(
            id = 2,
            etapaId = 1,
            titulo = "Justificación",
            descripcion = "Se explica la importancia " +
                    "y relevancia del problema estudiado.",
            puntajeMaximo = 0
        ),

        CriterioRubrica(
            id = 3,
            etapaId = 1,
            titulo = "Objetivos",
            descripcion = "Los objetivos son claros " +
                    "y coherentes con el problema planteado.",
            puntajeMaximo = 0
        ),

        // ETAPA 2: MARCO TEÓRICO

        CriterioRubrica(
            id = 4,
            etapaId = 2,
            titulo = "Fundamentos teóricos",
            descripcion = "Se presentan conceptos y teorías " +
                    "relacionados con la investigación.",
            puntajeMaximo = 0
        ),

        CriterioRubrica(
            id = 5,
            etapaId = 2,
            titulo = "Fuentes bibliográficas",
            descripcion = "Se utilizan referencias pertinentes " +
                    "para respaldar la investigación.",
            puntajeMaximo = 0
        ),

        CriterioRubrica(
            id = 6,
            etapaId = 2,
            titulo = "Organización del contenido",
            descripcion = "La información se presenta " +
                    "de forma ordenada y coherente.",
            puntajeMaximo = 0
        ),

        // ETAPA 3: METODOLOGÍA

        CriterioRubrica(
            id = 7,
            etapaId = 3,
            titulo = "Diseño metodológico",
            descripcion = "Se describe claramente " +
                    "el enfoque de la investigación.",
            puntajeMaximo = 0
        ),

        CriterioRubrica(
            id = 8,
            etapaId = 3,
            titulo = "Procedimientos",
            descripcion = "Se explican los pasos utilizados " +
                    "para desarrollar la investigación.",
            puntajeMaximo = 0
        ),

        CriterioRubrica(
            id = 9,
            etapaId = 3,
            titulo = "Coherencia metodológica",
            descripcion = "Los métodos seleccionados " +
                    "son apropiados para los objetivos.",
            puntajeMaximo = 0
        ),

        // ETAPA 4: RECOGIDA DE INFORMACIÓN

        CriterioRubrica(
            id = 10,
            etapaId = 4,
            titulo = "Instrumentos de recopilación",
            descripcion = "Se describen los instrumentos " +
                    "utilizados para obtener información.",
            puntajeMaximo = 0
        ),

        CriterioRubrica(
            id = 11,
            etapaId = 4,
            titulo = "Registro de información",
            descripcion = "Los datos recopilados " +
                    "se registran de forma organizada.",
            puntajeMaximo = 0
        ),

        CriterioRubrica(
            id = 12,
            etapaId = 4,
            titulo = "Calidad de los datos",
            descripcion = "La información obtenida " +
                    "es pertinente para la investigación.",
            puntajeMaximo = 0
        ),

        // ETAPA 5: ANÁLISIS DE RESULTADOS

        CriterioRubrica(
            id = 13,
            etapaId = 5,
            titulo = "Presentación de resultados",
            descripcion = "Los resultados se presentan " +
                    "de manera clara y ordenada.",
            puntajeMaximo = 0
        ),

        CriterioRubrica(
            id = 14,
            etapaId = 5,
            titulo = "Interpretación",
            descripcion = "Se explican los resultados " +
                    "y su relación con los objetivos.",
            puntajeMaximo = 0
        ),

        CriterioRubrica(
            id = 15,
            etapaId = 5,
            titulo = "Conclusiones",
            descripcion = "Las conclusiones se fundamentan " +
                    "en los resultados obtenidos.",
            puntajeMaximo = 0
        )
    )

    // Obtener todos los criterios
    fun obtenerTodos(): List<CriterioRubrica> {
        return criterios
    }

    // Obtener criterios de una etapa específica
    fun obtenerPorEtapa(
        etapaId: Int
    ): List<CriterioRubrica> {
        return criterios.filter {
            it.etapaId == etapaId
        }
    }
}
