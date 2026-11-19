
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

import androidx.lifecycle.viewmodel.compose.viewModel

import cl.duoc.tutorvirtual.data.EsucarRoomRepository
import cl.duoc.tutorvirtual.viewmodel.EvidenciasViewModel
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

// ======================================================
// ADJUNTAR PDF A UN ENTREGABLE
// TUTOR VIRTUAL ESUCAR - PLAN B
//
// Permite:
// 1. Seleccionar una etapa metodologica.
// 2. Seleccionar un entregable existente.
// 3. Elegir un archivo PDF de Android.
// 4. Registrar la evidencia en Room.
//
// El PDF no se copia a SQLite.
// Room conserva su URI y el nombre del documento.
// El ViewModel solicita permiso persistente de lectura.
// ======================================================

@Composable
fun AdjuntarEvidenciaScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit,
    evidenciasViewModel: EvidenciasViewModel = viewModel()
) {

    val context = LocalContext.current
    val colores = LocalColoresEsucar.current

    val repository = remember(context) {
        EsucarRoomRepository(context.applicationContext)
    }

    // ==============================================
    // OBSERVAR ETAPAS DESDE ROOM
    // ==============================================

    val etapasFlow = remember(repository) {
        repository.observarEtapas()
    }

    val etapas by etapasFlow.collectAsState(
        initial = emptyList()
    )

    var etapaSeleccionadaId by rememberSaveable {
        mutableIntStateOf(0)
    }

    var entregableSeleccionadoId by rememberSaveable {
        mutableIntStateOf(0)
    }

    var menuEtapasAbierto by remember {
        mutableStateOf(false)
    }

    var menuEntregablesAbierto by remember {
        mutableStateOf(false)
    }

    var mensajeLocal by remember {
        mutableStateOf(
            "Selecciona una etapa y un entregable."
        )
    }

    var nombrePdf by remember {
        mutableStateOf<String?>(null)
    }

    // ==============================================
    // INICIALIZAR ETAPA
    // ==============================================

    LaunchedEffect(etapas) {

        if (
            etapas.isNotEmpty() &&
            etapas.none { it.id == etapaSeleccionadaId }
        ) {

            etapaSeleccionadaId = etapas.first().id

            entregableSeleccionadoId = 0
        }
    }

    // ==============================================
    // OBSERVAR ENTREGABLES DE LA ETAPA
    // ==============================================

    val entregablesFlow = remember(
        repository,
        etapaSeleccionadaId
    ) {

        repository.observarEntregables(
            etapaSeleccionadaId
        )
    }

    val entregables by entregablesFlow.collectAsState(
        initial = emptyList()
    )

    LaunchedEffect(
        etapaSeleccionadaId,
        entregables
    ) {

        if (
            entregables.none {
                it.id == entregableSeleccionadoId
            }
        ) {

            entregableSeleccionadoId = 0
        }
    }

    // ==============================================
    // ESTADOS DEL VIEWMODEL
    // ==============================================

    val procesando by
    evidenciasViewModel.procesando.collectAsState()

    val mensajeViewModel by
    evidenciasViewModel.mensaje.collectAsState()

    // ==============================================
    // SELECTOR NATIVO DE PDF
    // ==============================================

    val selectorPdf =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            if (uri == null) {

                mensajeLocal =
                    "No seleccionaste ningún documento."

            } else {

                val idEntregable =
                    entregableSeleccionadoId

                if (idEntregable <= 0) {

                    mensajeLocal =
                        "Selecciona primero un entregable."

                } else {

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

                    val nombreArchivo =
                        nombre ?: "Documento académico.pdf"

                    nombrePdf = nombreArchivo

                    mensajeLocal =
                        "Registrando documento en Room..."

                    evidenciasViewModel.registrarPdf(
                        entregableId = idEntregable,
                        nombreArchivo = nombreArchivo,
                        uri = uri,

                        onResultado = { correcto ->

                            mensajeLocal = if (correcto) {

                                "El PDF quedó vinculado " +
                                        "al entregable seleccionado."

                            } else {

                                "No se pudo registrar el PDF. " +
                                        "Revisa el mensaje de error."
                            }
                        }
                    )
                }
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

        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        Text(
            text = "Adjuntar evidencia PDF",
            color = colores.texto,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Tutor Virtual ESUCAR",
            color = colores.textoSecundario,
            fontSize = 14.sp
        )

        // ==========================================
        // FORMULARIO
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(18.dp),

            colors = CardDefaults.cardColors(
                containerColor = colores.tarjeta
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp),

                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Vincular documento a un trabajo",

                    color = colores.texto,

                    fontSize = 18.sp,

                    fontWeight = FontWeight.Bold
                )

                // ==================================
                // ELEGIR ETAPA
                // ==================================

                Text(
                    text = "1. Etapa metodológica",

                    color = colores.texto,

                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = {
                            menuEtapasAbierto = true
                        },

                        enabled = etapas.isNotEmpty(),

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = etapas
                                .firstOrNull {
                                    it.id == etapaSeleccionadaId
                                }
                                ?.titulo
                                ?: "Seleccionar etapa",

                            maxLines = 2
                        )
                    }

                    DropdownMenu(
                        expanded = menuEtapasAbierto,

                        onDismissRequest = {
                            menuEtapasAbierto = false
                        }
                    ) {

                        etapas.forEach { etapa ->

                            DropdownMenuItem(
                                text = {
                                    Text(etapa.titulo)
                                },

                                onClick = {

                                    etapaSeleccionadaId =
                                        etapa.id

                                    entregableSeleccionadoId = 0

                                    nombrePdf = null

                                    mensajeLocal =
                                        "Ahora selecciona un entregable."

                                    menuEtapasAbierto = false

                                    evidenciasViewModel
                                        .limpiarMensaje()
                                }
                            )
                        }
                    }
                }

                // ==================================
                // ELEGIR ENTREGABLE
                // ==================================

                Text(
                    text = "2. Entregable académico",

                    color = colores.texto,

                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = {
                            menuEntregablesAbierto = true
                        },

                        enabled = entregables.isNotEmpty(),

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = entregables
                                .firstOrNull {
                                    it.id == entregableSeleccionadoId
                                }
                                ?.titulo
                                ?: "Seleccionar entregable",

                            maxLines = 2
                        )
                    }

                    DropdownMenu(
                        expanded = menuEntregablesAbierto,

                        onDismissRequest = {
                            menuEntregablesAbierto = false
                        }
                    ) {

                        entregables.forEach { entregable ->

                            DropdownMenuItem(
                                text = {
                                    Text(entregable.titulo)
                                },

                                onClick = {

                                    entregableSeleccionadoId =
                                        entregable.id

                                    nombrePdf = null

                                    mensajeLocal =
                                        "Entregable seleccionado. " +
                                                "Ahora puedes adjuntar un PDF."

                                    menuEntregablesAbierto = false

                                    evidenciasViewModel
                                        .limpiarMensaje()
                                }
                            )
                        }
                    }
                }

                if (entregables.isEmpty()) {

                    Text(
                        text = "No hay entregables registrados " +
                                "en esta etapa. Puedes crear uno " +
                                "desde Mis entregables.",

                        color = colores.textoSecundario,

                        fontSize = 13.sp
                    )
                }

                // ==================================
                // SELECCIONAR PDF
                // ==================================

                Text(
                    text = "3. Documento PDF",

                    color = colores.texto,

                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {

                        nombrePdf = null

                        mensajeLocal =
                            "Abriendo selector de documentos..."

                        evidenciasViewModel.limpiarMensaje()

                        selectorPdf.launch(
                            arrayOf("application/pdf")
                        )
                    },

                    enabled =
                        entregableSeleccionadoId > 0 &&
                                !procesando,

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 54.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = colores.encabezado,
                        contentColor = colores.textoEncabezado
                    )
                ) {

                    Text("Seleccionar PDF y adjuntar")
                }
            }
        }

        // ==========================================
        // RESULTADO
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = colores.tarjeta
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Estado del documento",

                    color = colores.texto,

                    fontWeight = FontWeight.Bold,

                    fontSize = 18.sp
                )

                if (procesando) {

                    CircularProgressIndicator(
                        color = colores.acento
                    )
                }

                if (nombrePdf != null) {

                    Text(
                        text = nombrePdf.orEmpty(),

                        color = colores.texto,

                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = mensajeLocal,

                    color = colores.textoSecundario,

                    fontSize = 14.sp
                )

                if (mensajeViewModel != null) {

                    Text(
                        text = mensajeViewModel.orEmpty(),

                        color = colores.texto,

                        fontSize = 13.sp
                    )
                }
            }
        }

        Text(
            text = "El documento permanece en " +
                    "el almacenamiento de Android. " +
                    "Room guarda su nombre, tipo y " +
                    "referencia para recuperarlo después.",

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
