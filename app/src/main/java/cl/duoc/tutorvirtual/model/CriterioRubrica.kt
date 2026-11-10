package cl.duoc.tutorvirtual.model

data class CriterioRubrica(
    val id: Int,
    val etapaId: Int,
    val titulo: String,
    val descripcion: String,
    val puntajeMaximo: Int
)
