
package cl.duoc.tutorvirtual.model

// Representa una novedad o actualización académica
data class ActualizacionAcademica(

    // Identificador único de la actualización
    val id: String,

    // Título que verá el estudiante
    val titulo: String,

    // Información de la actualización
    val descripcion: String,

    // Fecha de publicación en formato AAAA-MM-DD
    val fechaPublicacion: String,

    // Origen de la información
    val fuente: String

)
