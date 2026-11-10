package cl.duoc.tutorvirtual.model

data class Etapa(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val formatoRequerido: String,
    val fechaLimite: String,
    val estado: String = "Por iniciar"
)
