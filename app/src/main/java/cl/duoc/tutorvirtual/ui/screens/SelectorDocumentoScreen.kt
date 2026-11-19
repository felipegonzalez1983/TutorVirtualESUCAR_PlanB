package cl.duoc.tutorvirtual.ui.screens

import android.net.Uri
import android.provider.OpenableColumns

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

// ======================================================
// SELECTOR DE DOCUMENTOS PDF
// TUTOR VIRTUAL ESUCAR - PLAN B
//
// Utiliza el selector de documentos de Android.
//
// Permite:
// - Seleccionar un archivo PDF.
// - Consultar su nombre.
// - Mostrar el resultado en pantalla.
//
// No modifica Room.
// No guarda archivos en SQLite.
// No asocia documentos a entregables todavía.
// ======================================================

@Composable
fun SelectorDocumentoScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit
) {

    val context = LocalContext.current
    val colores = LocalColoresEsucar.current

    var uriSeleccionada by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var nombreDocumento by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var mensaje by rememberSaveable {
        mutableStateOf(
            "Selecciona un documento PDF " +
                    "desde tu dispositivo."
        )
    }

    // ==============================================
    // SELECTOR NATIVO DE DOCUMENTOS
    // ==============================================

    val selectorPDF =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            if (uri != null) {

                uriSeleccionada = uri.toString()

                val nombre = try {

                    context.contentResolver.query(
                        uri,
                        arrayOf(
                            OpenableColumns.DISPLAY_NAME
                        ),
                        null,
                        null,
                        null
                    )?.use { cursor ->

                        val columna =
                            cursor.getColumnIndex(
                                OpenableColumns.DISPLAY_NAME
                            )

                        if (
                            columna >= 0 &&
                            cursor.moveToFirst()
                        ) {

                            cursor.getString(columna)

                        } else {
                            null
                        }
                    }

                } catch (e: Exception) {
                    null
                }

                nombreDocumento =
                    nombre ?: "Documento PDF seleccionado"

                mensaje =
                    "Documento seleccionado correctamente."

            } else {

                mensaje =
                    "No se seleccionó ningún documento."
            }
        }

    // ==============================================
    // INTERFAZ PRINCIPAL
    // ==============================================

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Documentos académicos",

            color = colores.texto,

            fontSize = 26.sp,

            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Tutor Virtual ESUCAR",

            color = colores.textoSecundario,

            fontSize = 15.sp
        )

        // ==========================================
        // TARJETA INFORMATIVA
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(20.dp),

            colors = CardDefaults.cardColors(
                containerColor = colores.tarjeta
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Seleccionar evidencia",

                    color = colores.texto,

                    fontSize = 19.sp,

                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Puedes seleccionar un " +
                            "documento PDF que corresponda " +
                            "a un informe, pauta o trabajo " +
                            "académico.",

                    color = colores.textoSecundario,

                    fontSize = 14.sp,

                    lineHeight = 21.sp
                )

                Text(
                    text = "El archivo se selecciona " +
                            "mediante el sistema de " +
                            "documentos de Android.",

                    color = colores.textoSecundario,

                    fontSize = 13.sp
                )
            }
        }

        // ==========================================
        // BOTON SELECCIONAR PDF
        // ==========================================

        Button(
            onClick = {

                selectorPDF.launch(
                    arrayOf("application/pdf")
                )
            },

            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),

            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = colores.encabezado,
                contentColor = colores.textoEncabezado
            )
        ) {

            Text(
                text = "Seleccionar documento PDF",

                fontSize = 16.sp,

                fontWeight = FontWeight.Bold
            )
        }

        // ==========================================
        // RESULTADO DE LA SELECCION
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(20.dp),

            colors = CardDefaults.cardColors(
                containerColor = colores.tarjeta
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "Documento seleccionado",

                    color = colores.texto,

                    fontSize = 18.sp,

                    fontWeight = FontWeight.Bold
                )

                if (uriSeleccionada != null) {

                    Text(
                        text = nombreDocumento.orEmpty(),

                        color = colores.texto,

                        fontSize = 16.sp,

                        fontWeight = FontWeight.Bold,

                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Archivo PDF detectado",

                        color = colores.textoSecundario,

                        fontSize = 13.sp
                    )

                } else {

                    Text(
                        text = "Todavía no has " +
                                "seleccionado un archivo.",

                        color = colores.textoSecundario,

                        fontSize = 14.sp,

                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = mensaje,

                    color = colores.texto,

                    fontSize = 14.sp,

                    textAlign = TextAlign.Center
                )
            }
        }

        // ==========================================
        // INFORMACION SOBRE ALMACENAMIENTO
        // ==========================================

        Text(
            text = "Esta pantalla permite probar " +
                    "la selección nativa de PDF. " +
                    "El documento todavía no se " +
                    "guarda como evidencia de " +
                    "un entregable académico.",

            color = colores.textoSecundario,

            style = MaterialTheme.typography.bodySmall,

            textAlign = TextAlign.Center
        )

        // ==========================================
        // BOTON VOLVER
        // ==========================================

        OutlinedButton(
            onClick = onVolver,

            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Volver",

                color = colores.texto
            )
        }
    }
}
