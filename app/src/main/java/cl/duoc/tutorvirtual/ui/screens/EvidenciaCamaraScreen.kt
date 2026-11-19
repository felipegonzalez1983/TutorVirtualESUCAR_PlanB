package cl.duoc.tutorvirtual.ui.screens

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel

import cl.duoc.tutorvirtual.data.local.EntregableEntity
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar
import cl.duoc.tutorvirtual.viewmodel.EntregablesViewModel
import cl.duoc.tutorvirtual.viewmodel.RegistroEntregableViewModel
import cl.duoc.tutorvirtual.viewmodel.EvidenciasViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ======================================================
// TUTOR VIRTUAL ESUCAR - PLAN B
// FASE 7.7 - EVIDENCIA FOTOGRAFICA
//
// Permite:
// - Seleccionar una etapa metodologica.
// - Seleccionar un entregable existente.
// - Capturar una fotografia de resolucion completa.
// - Guardar la imagen usando MediaStore.
// - Registrar la URI de la foto en Room.
// - Mostrar una vista previa.
//
// La fotografia se guarda en:
// Pictures/ESUCAR
//
// Requiere Android 10 o superior para esta
// modalidad de almacenamiento sin permisos.
//
// No modifica la estructura de SQLite.
// ======================================================

@Composable
fun EvidenciaCamaraScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit,
    entregablesViewModel: EntregablesViewModel = viewModel(),
    registroViewModel: RegistroEntregableViewModel = viewModel(),
    evidenciasViewModel: EvidenciasViewModel = viewModel()
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ==============================================
    // ESTADOS DE LA PANTALLA
    // ==============================================

    var etapaSeleccionadaId by rememberSaveable {
        mutableStateOf(0)
    }

    var entregableSeleccionadoId by rememberSaveable {
        mutableStateOf(0)
    }

    var uriPendiente by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var nombrePendiente by rememberSaveable {
        mutableStateOf("")
    }

    var entregablePendienteId by rememberSaveable {
        mutableStateOf(0)
    }

    var fotoGuardadaUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var nombreFotoGuardada by rememberSaveable {
        mutableStateOf("")
    }

    var vistaPrevia by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var mensaje by remember {
        mutableStateOf(
            "Selecciona una etapa y un entregable " +
                    "para registrar una evidencia fotográfica."
        )
    }

    var registrando by remember {
        mutableStateOf(false)
    }

    // ==============================================
    // ETAPAS Y ENTREGABLES
    // ==============================================

    val etapas = entregablesViewModel.etapas

    val flujoEntregables = remember(
        registroViewModel,
        etapaSeleccionadaId
    ) {
        registroViewModel.observarEntregables(
            etapaSeleccionadaId
        )
    }

    val entregables by flujoEntregables.collectAsState(
        initial = emptyList<EntregableEntity>()
    )

    val entregableSeleccionado = entregables.firstOrNull {
        it.id == entregableSeleccionadoId
    }

    // ==============================================
    // CARGAR VISTA PREVIA DESDE LA FOTO GUARDADA
    // ==============================================

    LaunchedEffect(fotoGuardadaUri) {

        val textoUri = fotoGuardadaUri

        vistaPrevia = if (textoUri != null) {

            withContext(Dispatchers.IO) {

                cargarVistaPreviaFotografia(
                    context = context,
                    uri = Uri.parse(textoUri)
                )
            }

        } else {
            null
        }
    }

    // ==============================================
    // LANZADOR DE CAMARA NATIVA
    // ==============================================

    val camaraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { capturaCorrecta ->

            val textoUri = uriPendiente
            val nombreArchivo = nombrePendiente
            val idEntregable = entregablePendienteId

            uriPendiente = null
            nombrePendiente = ""
            entregablePendienteId = 0

            if (
                capturaCorrecta &&
                textoUri != null &&
                idEntregable > 0
            ) {

                val uriCapturada = Uri.parse(textoUri)

                mensaje = "Guardando evidencia académica..."

                registrando = true

                evidenciasViewModel.registrarFoto(
                    entregableId = idEntregable,
                    nombreArchivo = nombreArchivo,
                    uriArchivo = textoUri
                ) { correcto ->

                    registrando = false

                    if (correcto) {

                        fotoGuardadaUri = textoUri
                        nombreFotoGuardada = nombreArchivo

                        mensaje =
                            "Fotografía guardada y vinculada " +
                                    "al entregable correctamente."

                    } else {

                        // Si Room no registra la evidencia,
                        // eliminamos la imagen recién creada
                        // para evitar archivos huérfanos.
                        try {
                            context.contentResolver.delete(
                                uriCapturada,
                                null,
                                null
                            )
                        } catch (_: Exception) {
                            // Conservamos el mensaje de error.
                        }

                        mensaje =
                            "No fue posible registrar " +
                                    "la fotografía en Room."
                    }
                }

            } else {

                if (textoUri != null) {

                    try {
                        context.contentResolver.delete(
                            Uri.parse(textoUri),
                            null,
                            null
                        )
                    } catch (_: Exception) {
                        // No interrumpir la interfaz.
                    }
                }

                mensaje =
                    "La captura fue cancelada o " +
                            "no se pudo completar."
            }
        }

    // ==============================================
    // INTERFAZ
    // ==============================================

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.spacedBy(18.dp)
    ) {

        // ==========================================
        // ENCABEZADO
        // ==========================================

        Text(
            text = "Evidencia fotográfica",
            color = colores.texto,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Tutor Virtual ESUCAR",
            color = colores.textoSecundario,
            fontSize = 15.sp
        )

        // ==========================================
        // INTRODUCCION
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(20.dp),

            colors = CardDefaults.cardColors(
                containerColor = colores.tarjeta
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Registro de evidencias",
                    color = colores.texto,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Captura una fotografía con " +
                            "la cámara de tu dispositivo " +
                            "y vincúlala a un entregable " +
                            "académico existente.",

                    color = colores.textoSecundario,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )

                Text(
                    text = "Las imágenes se guardan " +
                            "en la carpeta Imágenes/ESUCAR " +
                            "de la tablet.",

                    color = colores.textoSecundario,
                    fontSize = 12.sp
                )
            }
        }

        // ==========================================
        // SELECCION DE ETAPA
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(18.dp),

            colors = CardDefaults.cardColors(
                containerColor = colores.tarjeta
            ),

            border = if (altoContraste) {
                BorderStroke(1.dp, colores.borde)
            } else {
                null
            }
        ) {

            Column(
                modifier = Modifier.padding(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "1. Seleccionar etapa",
                    color = colores.texto,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                etapas.forEach { etapa ->

                    val seleccionada =
                        etapaSeleccionadaId == etapa.id

                    OutlinedButton(
                        onClick = {

                            etapaSeleccionadaId = etapa.id
                            entregableSeleccionadoId = 0

                            mensaje =
                                "Ahora selecciona el entregable."
                        },

                        modifier = Modifier.fillMaxWidth(),

                        border = BorderStroke(
                            if (seleccionada) 2.dp else 1.dp,
                            if (
                                seleccionada &&
                                !altoContraste
                            ) {
                                colores.encabezado
                            } else {
                                colores.borde
                            }
                        ),

                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = colores.texto
                            )
                    ) {

                        Text(
                            text = if (seleccionada) {
                                "✓ ${etapa.titulo}"
                            } else {
                                etapa.titulo
                            },

                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // ==========================================
        // SELECCION DE ENTREGABLE
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(18.dp),

            colors = CardDefaults.cardColors(
                containerColor = colores.tarjeta
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "2. Seleccionar entregable",
                    color = colores.texto,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                if (etapaSeleccionadaId == 0) {

                    Text(
                        text = "Primero selecciona una etapa.",
                        color = colores.textoSecundario,
                        fontSize = 13.sp
                    )

                } else if (entregables.isEmpty()) {

                    Text(
                        text = "No hay entregables registrados " +
                                "para esta etapa.",

                        color = colores.textoSecundario,
                        fontSize = 13.sp
                    )

                } else {

                    entregables.forEach { entregable ->

                        val seleccionado =
                            entregableSeleccionadoId ==
                                    entregable.id

                        OutlinedButton(
                            onClick = {

                                entregableSeleccionadoId =
                                    entregable.id
                            },

                            modifier = Modifier.fillMaxWidth(),

                            border = BorderStroke(
                                if (seleccionado) {
                                    2.dp
                                } else {
                                    1.dp
                                },

                                if (
                                    seleccionado &&
                                    !altoContraste
                                ) {
                                    colores.encabezado
                                } else {
                                    colores.borde
                                }
                            ),

                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    contentColor = colores.texto
                                )
                        ) {

                            Text(
                                text = if (seleccionado) {
                                    "✓ ${entregable.titulo}"
                                } else {
                                    entregable.titulo
                                },

                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (entregableSeleccionado != null) {

                    HorizontalDivider(
                        color = colores.borde
                    )

                    Text(
                        text = "Seleccionado: " +
                                entregableSeleccionado.titulo,

                        color = colores.texto,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // ==========================================
        // CAPTURA DE FOTOGRAFIA
        // ==========================================

        Button(
            onClick = {

                if (
                    Build.VERSION.SDK_INT <
                    Build.VERSION_CODES.Q
                ) {

                    mensaje =
                        "Esta versión de almacenamiento " +
                                "requiere Android 10 o superior."

                } else if (
                    entregableSeleccionado == null
                ) {

                    mensaje =
                        "Primero selecciona un " +
                                "entregable válido."

                } else {

                    val nombreArchivo =
                        "ESUCAR_${System.currentTimeMillis()}.jpg"

                    try {

                        val valores = ContentValues().apply {

                            put(
                                MediaStore.Images.Media.DISPLAY_NAME,
                                nombreArchivo
                            )

                            put(
                                MediaStore.Images.Media.MIME_TYPE,
                                "image/jpeg"
                            )

                            put(
                                MediaStore.Images.Media.RELATIVE_PATH,
                                "Pictures/ESUCAR"
                            )
                        }

                        val nuevaUri =
                            context.contentResolver.insert(
                                MediaStore.Images.Media
                                    .EXTERNAL_CONTENT_URI,
                                valores
                            )

                        if (nuevaUri == null) {

                            mensaje =
                                "No fue posible preparar " +
                                        "el almacenamiento de la foto."

                        } else {

                            nombrePendiente = nombreArchivo

                            entregablePendienteId =
                                entregableSeleccionado.id

                            uriPendiente = nuevaUri.toString()

                            mensaje = "Abriendo cámara..."

                            camaraLauncher.launch(nuevaUri)
                        }

                    } catch (e: Exception) {

                        mensaje =
                            "No fue posible abrir la cámara: " +
                                    (e.message ?: "Error desconocido.")

                        // Intentar limpiar una URI que
                        // pudiera haber quedado pendiente.
                        uriPendiente?.let { texto ->

                            try {

                                context.contentResolver.delete(
                                    Uri.parse(texto),
                                    null,
                                    null
                                )

                            } catch (_: Exception) {
                            }
                        }

                        uriPendiente = null
                        nombrePendiente = ""
                        entregablePendienteId = 0
                    }
                }
            },

            enabled = !registrando,

            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),

            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = if (altoContraste) {
                    Color.Yellow
                } else {
                    colores.encabezado
                },

                contentColor = if (altoContraste) {
                    Color.Black
                } else {
                    colores.textoEncabezado
                }
            )
        ) {

            Text(
                text = if (registrando) {
                    "Registrando fotografía..."
                } else {
                    "3. Tomar fotografía y guardar"
                },

                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ==========================================
        // VISTA PREVIA
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
                    Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "Última evidencia capturada",
                    color = colores.texto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (vistaPrevia != null) {

                    Image(
                        bitmap = vistaPrevia!!.asImageBitmap(),

                        contentDescription =
                            "Fotografía académica guardada",

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
                            text = "La fotografía aparecerá aquí " +
                                    "después de guardarla.",

                            color = colores.textoSecundario,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                if (nombreFotoGuardada.isNotBlank()) {

                    Text(
                        text = nombreFotoGuardada,

                        color = colores.textoSecundario,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = mensaje,
                    color = colores.texto,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )
            }
        }

        // ==========================================
        // INFORMACION IMPORTANTE
        // ==========================================

        Text(
            text = "Cuando la fotografía se registre " +
                    "correctamente, podrás consultarla " +
                    "en Mis entregables, dentro de la " +
                    "sección Evidencias adjuntas.",

            color = colores.textoSecundario,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        // ==========================================
        // VOLVER
        // ==========================================

        OutlinedButton(
            onClick = onVolver,

            modifier = Modifier.fillMaxWidth(),

            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colores.texto
            ),

            border = BorderStroke(
                1.dp,
                colores.borde
            )
        ) {

            Text("Volver")
        }
    }
}

// ======================================================
// UTILIDAD: CARGAR VISTA PREVIA REDUCIDA
//
// La fotografia original queda en MediaStore.
// Para mostrarla en pantalla usamos una version
// reducida, evitando decodificar toda la imagen
// de resolucion completa.
// ======================================================

private fun cargarVistaPreviaFotografia(
    context: android.content.Context,
    uri: Uri
): Bitmap? {

    return try {

        val resolver = context.contentResolver

        val opcionesTamano =
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

        resolver.openInputStream(uri)?.use { entrada ->

            BitmapFactory.decodeStream(
                entrada,
                null,
                opcionesTamano
            )
        }

        val ancho = opcionesTamano.outWidth
        val alto = opcionesTamano.outHeight

        if (ancho <= 0 || alto <= 0) {
            return null
        }

        var escala = 1

        while (
            ancho / escala > 1024 ||
            alto / escala > 1024
        ) {
            escala *= 2
        }

        val opcionesImagen =
            BitmapFactory.Options().apply {

                inSampleSize = escala
            }

        resolver.openInputStream(uri)?.use { entrada ->

            BitmapFactory.decodeStream(
                entrada,
                null,
                opcionesImagen
            )
        }

    } catch (_: Exception) {

        null
    }
}
