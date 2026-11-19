package cl.duoc.tutorvirtual.ui.screens

import android.graphics.Bitmap

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

// ======================================================
// EVIDENCIA CON CAMARA
// TUTOR VIRTUAL ESUCAR - PLAN B
//
// Permite abrir la camara del dispositivo
// y visualizar una fotografia de prueba.
//
// Esta primera version:
// - No guarda archivos permanentemente.
// - No modifica Room ni SQLite.
// - No asocia fotos a entregables.
// - No necesita permiso CAMERA porque delega
//   la captura a una aplicacion de camara.
// ======================================================

@Composable
fun EvidenciaCamaraScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit
) {

    val colores = LocalColoresEsucar.current

    // Fotografia temporal en memoria.
    // No se guarda en la base de datos.
    var fotografia by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var mensaje by remember {
        mutableStateOf(
            "Presiona el botón para tomar " +
                    "una fotografía de prueba."
        )
    }

    // ==============================================
    // LANZADOR DE LA CAMARA NATIVA
    // ==============================================

    val camaraLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            if (bitmap != null) {

                fotografia = bitmap

                mensaje =
                    "Fotografía capturada correctamente."

            } else {

                mensaje =
                    "No se capturó ninguna fotografía."
            }
        }

    // ==============================================
    // INTERFAZ DE LA PANTALLA
    // ==============================================

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // ==========================================
        // ENCABEZADO
        // ==========================================

        Text(
            text = "Evidencia fotográfica",

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

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Registro de evidencias",

                    color = colores.texto,

                    fontSize = 19.sp,

                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Utiliza la cámara de tu " +
                            "dispositivo para capturar " +
                            "una evidencia académica.",

                    color = colores.textoSecundario,

                    fontSize = 14.sp,

                    lineHeight = 21.sp
                )

                Text(
                    text = "Esta es una prueba de " +
                            "integración con un recurso " +
                            "nativo de Android.",

                    color = colores.textoSecundario,

                    fontSize = 13.sp
                )
            }
        }

        // ==========================================
        // BOTON ABRIR CAMARA
        // ==========================================

        Button(
            onClick = {

                mensaje = "Abriendo cámara..."

                camaraLauncher.launch(null)
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
                text = if (fotografia == null) {
                    "Abrir cámara"
                } else {
                    "Tomar otra fotografía"
                },

                fontSize = 16.sp,

                fontWeight = FontWeight.Bold
            )
        }

        // ==========================================
        // RESULTADO DE CAPTURA
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
                    .padding(18.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Vista previa",

                    color = colores.texto,

                    fontSize = 18.sp,

                    fontWeight = FontWeight.Bold
                )

                if (fotografia != null) {

                    Image(
                        bitmap = fotografia!!.asImageBitmap(),

                        contentDescription =
                            "Fotografía académica capturada",

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),

                        contentScale = ContentScale.Fit
                    )

                } else {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                colores.fondo,
                                RoundedCornerShape(12.dp)
                            ),

                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "La fotografía aparecerá aquí",

                            color = colores.textoSecundario,

                            textAlign = TextAlign.Center,

                            modifier = Modifier.padding(16.dp)
                        )
                    }
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
        // AVISO SOBRE ALMACENAMIENTO
        // ==========================================

        Text(
            text = "La fotografía es temporal y " +
                    "no se guarda todavía. " +
                    "Posteriormente podremos asociarla " +
                    "a un entregable académico.",

            color = colores.textoSecundario,

            style = MaterialTheme.typography.bodySmall,

            textAlign = TextAlign.Center
        )

        // ==========================================
        // VOLVER
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
