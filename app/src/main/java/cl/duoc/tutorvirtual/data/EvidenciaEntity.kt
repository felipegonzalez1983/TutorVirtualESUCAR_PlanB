package cl.duoc.tutorvirtual.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ======================================================
// EVIDENCIAS ACADEMICAS - TUTOR VIRTUAL ESUCAR
//
// Una evidencia pertenece a un entregable.
//
// Tipos previstos:
// - PDF
// - FOTO
//
// La base de datos almacenara la referencia al
// archivo, no el contenido binario completo.
//
// IMPORTANTE:
// Esta entidad se incorporara a Room mediante
// una migracion posterior de version 1 a 2.
// ======================================================

@Entity(
    tableName = "evidencias",

    foreignKeys = [
        ForeignKey(
            entity = EntregableEntity::class,
            parentColumns = ["id"],
            childColumns = ["entregableId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["entregableId"])
    ]
)

data class EvidenciaEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Entregable al que pertenece la evidencia
    @ColumnInfo(name = "entregableId")
    val entregableId: Int,

    // Nombre visible del archivo
    val nombreArchivo: String,

    // Valores previstos: PDF o FOTO
    val tipo: String,

    // Referencia al archivo en Android.
    // Para un PDF puede ser una URI content://
    // con permiso persistente de lectura.
    // Para una foto se usara un archivo privado.
    val uriArchivo: String,

    // Fecha de registro en milisegundos
    val fechaRegistro: Long = System.currentTimeMillis()
)
