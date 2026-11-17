package cl.duoc.tutorvirtual.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "entregables",

    foreignKeys = [
        ForeignKey(
            entity = EtapaEntity::class,
            parentColumns = ["id"],
            childColumns = ["etapaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["etapaId"])
    ]
)
data class EntregableEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "etapaId")
    val etapaId: Int,

    val titulo: String,

    val descripcion: String,

    val fechaEntrega: String,

    val estado: String = "Por iniciar",

    val observaciones: String = ""
)
