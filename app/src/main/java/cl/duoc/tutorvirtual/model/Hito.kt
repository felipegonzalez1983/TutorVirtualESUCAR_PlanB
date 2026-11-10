package cl.duoc.tutorvirtual.model

data class Hito(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val fechaLimite: String,
    val completado: Boolean = false
)
