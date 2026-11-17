package cl.duoc.tutorvirtual.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "etapas")
data class EtapaEntity(

    @PrimaryKey
    val id: Int,

    val titulo: String,

    val descripcion: String,

    val formatoRequerido: String,

    val fechaLimite: String,

    val estado: String = "Por iniciar"
)
