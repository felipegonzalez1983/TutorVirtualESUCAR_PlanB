package cl.duoc.tutorvirtual.ui.screens

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import cl.duoc.tutorvirtual.data.EsucarRoomRepository
import cl.duoc.tutorvirtual.data.local.EntregableEntity
import cl.duoc.tutorvirtual.data.local.EvidenciaEntity
import cl.duoc.tutorvirtual.model.Etapa
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar
import cl.duoc.tutorvirtual.viewmodel.EntregablesViewModel
import cl.duoc.tutorvirtual.viewmodel.RegistroEntregableViewModel
import cl.duoc.tutorvirtual.viewmodel.EvidenciasViewModel

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.text.SimpleDateFormat
import java.util.Locale

// ======================================================
// TUTOR VIRTUAL ESUCAR - PLAN B
// ENTREGABLES SCREEN
//
// Funcionalidades:
// - Seguimiento de etapas
// - Consulta de entregables con Room
// - Cambio de estados
// - Edicion de entregables
// - Eliminacion de entregables
// - Visualizacion de evidencias PDF y FOTO
// - Apertura de evidencias con Android
// - Eliminacion individual de evidencias
// - Confirmacion diferenciada segun tipo de archivo
// ======================================================

private val opcionesEstado = listOf(
    "Por iniciar",
    "En desarrollo",
    "Enviado",
    "Aprobado"
)

// ======================================================
// VIEWMODEL DE OPERACIONES SOBRE ENTREGABLES
// ======================================================

class EstadoDocumentoViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = EsucarRoomRepository(
        application.applicationContext
    )

    suspend fun cambiarEstadoDocumento(
        entregableId: Int,
        nuevoEstado: String
    ) {
        require(entregableId > 0) {
            "Identificador inválido"
        }

        require(nuevoEstado in opcionesEstado) {
            "Estado inválido"
        }

        withContext(Dispatchers.IO) {
            repository.actualizarEstadoEntregable(
                entregableId,
                nuevoEstado
            )
        }

        Log.i(
            "ESUCAR_DATABASE",
            "Estado actualizado: documento $entregableId"
        )
    }

    suspend fun editarDocumento(
        entregable: EntregableEntity,
        nuevoTitulo: String,
        nuevaDescripcion: String,
        nuevaFecha: String
    ): String? {

        val titulo = nuevoTitulo.trim()
        val descripcion = nuevaDescripcion.trim()
        val fecha = nuevaFecha.trim()

        if (titulo.isBlank()) {
            return "Debes ingresar un título."
        }

        if (descripcion.isBlank()) {
            return "Debes ingresar una descripción."
        }

        if (!fechaValida(fecha)) {
            return "La fecha debe tener formato AAAA-MM-DD."
        }

        return try {
            withContext(Dispatchers.IO) {
                repository.actualizarEntregable(
                    entregable.copy(
                        titulo = titulo,
                        descripcion = descripcion,
                        fechaEntrega = fecha
                    )
                )
            }

            null

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Log.e(
                "ESUCAR_DATABASE",
                "Error al editar documento",
                e
            )

            "No se pudo actualizar el documento."
        }
    }

    suspend fun eliminarDocumento(
        entregable: EntregableEntity
    ) {
        require(entregable.id > 0) {
            "Identificador inválido"
        }

        withContext(Dispatchers.IO) {
            repository.eliminarEntregable(entregable)
        }

        Log.i(
            "ESUCAR_DATABASE",
            "Documento eliminado: ${entregable.id}"
        )
    }

    private fun fechaValida(
        fecha: String
    ): Boolean {

        if (!Regex("""\d{4}-\d{2}-\d{2}""").matches(fecha)) {
            return false
        }

        return try {
            val formato = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )
            formato.isLenient = false
            formato.parse(fecha) != null
        } catch (e: Exception) {
            false
        }
    }
}

// ======================================================
// PANTALLA PRINCIPAL
// ======================================================

@Composable
fun EntregablesScreen(
    modifier: Modifier = Modifier,
    entregablesViewModel: EntregablesViewModel = viewModel(),
    registroViewModel: RegistroEntregableViewModel = viewModel(),
    estadoDocumentoViewModel: EstadoDocumentoViewModel = viewModel(),
    evidenciasViewModel: EvidenciasViewModel = viewModel()
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 110.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ======================================
            // ENCABEZADO INSTITUCIONAL
            // ======================================

            item {
                Surface(
                    color = colores.encabezado,
                    shape = RoundedCornerShape(24.dp),
                    border = if (altoContraste) {
                        BorderStroke(1.dp, colores.borde)
                    } else {
                        null
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "ESUCAR · SEGUIMIENTO",
                            color = colores.acento,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Mis entregables",
                            color = colores.textoEncabezado,
                            fontSize = 28.sp,
                            lineHeight = 34.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Consulta y administra " +
                                    "tus documentos académicos.",
                            color = colores.textoEncabezado,
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Seguimiento de etapas",
                    color = colores.texto,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // ======================================
            // ETAPAS METODOLOGICAS
            // ======================================

            items(
                items = entregablesViewModel.etapas,
                key = { it.id }
            ) { etapa ->

                TarjetaEtapaEntregables(
                    etapa = etapa,
                    entregablesViewModel = entregablesViewModel,
                    registroViewModel = registroViewModel,
                    estadoDocumentoViewModel = estadoDocumentoViewModel,
                    evidenciasViewModel = evidenciasViewModel,
                    snackbarHostState = snackbarHostState
                )
            }

            // ======================================
            // AVISO
            // ======================================

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colores.tarjeta,
                    shape = RoundedCornerShape(16.dp),
                    border = if (altoContraste) {
                        BorderStroke(1.dp, colores.borde)
                    } else {
                        null
                    }
                ) {
                    Text(
                        text = "Los estados se registran manualmente " +
                                "como seguimiento personal. Seleccionar " +
                                "'Aprobado' no constituye una aprobación " +
                                "oficial de ESUCAR.",
                        modifier = Modifier.padding(16.dp),
                        color = colores.textoSecundario,
                        fontSize = 12.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        // ==========================================
        // CONFIRMACIONES
        // ==========================================

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->

            Snackbar(
                snackbarData = data,
                containerColor = if (altoContraste) {
                    Color.Yellow
                } else {
                    colores.encabezado
                },
                contentColor = if (altoContraste) {
                    Color.Black
                } else {
                    Color.White
                }
            )
        }
    }
}

// ======================================================
// TARJETA DE ETAPA
// ======================================================

@Composable
private fun TarjetaEtapaEntregables(
    etapa: Etapa,
    entregablesViewModel: EntregablesViewModel,
    registroViewModel: RegistroEntregableViewModel,
    estadoDocumentoViewModel: EstadoDocumentoViewModel,
    evidenciasViewModel: EvidenciasViewModel,
    snackbarHostState: SnackbarHostState
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black
    val scope = rememberCoroutineScope()

    val flujoEstado = remember(
        entregablesViewModel,
        etapa.id
    ) {
        entregablesViewModel.observarEstado(etapa.id)
    }

    val estado by flujoEstado.collectAsState(
        initial = etapa.estado
    )

    val flujoDocumentos = remember(
        registroViewModel,
        etapa.id
    ) {
        registroViewModel.observarEntregables(etapa.id)
    }

    val documentos by flujoDocumentos.collectAsState(
        initial = emptyList()
    )

    var mostrarEstadosEtapa by remember {
        mutableStateOf(false)
    }

    var guardandoEtapa by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colores.tarjeta
        ),
        shape = RoundedCornerShape(18.dp),
        border = if (altoContraste) {
            BorderStroke(1.dp, colores.borde)
        } else {
            null
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ======================================
            // TITULO DE ETAPA
            // ======================================

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Surface(
                    color = if (altoContraste) {
                        Color.Yellow
                    } else {
                        Color(0xFFE8EFE9)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(46.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = etapa.id.toString().padStart(2, '0'),
                            color = if (altoContraste) {
                                Color.Black
                            } else {
                                colores.texto
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = etapa.titulo,
                    modifier = Modifier.weight(1f),
                    color = colores.texto,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = etapa.descripcion,
                color = colores.textoSecundario,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            HorizontalDivider(color = colores.borde)

            Text(
                text = "Estado actual de la etapa",
                color = colores.textoSecundario,
                fontSize = 13.sp
            )

            IndicadorEstado(estado = estado)

            Button(
                onClick = {
                    mostrarEstadosEtapa = true
                },
                enabled = !guardandoEtapa,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (altoContraste) {
                        Color.Yellow
                    } else {
                        colores.encabezado
                    },
                    contentColor = if (altoContraste) {
                        Color.Black
                    } else {
                        Color.White
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (guardandoEtapa) {
                        "Guardando..."
                    } else {
                        "Cambiar estado de etapa"
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = colores.borde)

            // ======================================
            // DOCUMENTOS REGISTRADOS
            // ======================================

            Text(
                text = "Documentos registrados",
                color = colores.texto,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${documentos.size} entregable(s)",
                color = colores.textoSecundario,
                fontSize = 12.sp
            )

            if (documentos.isEmpty()) {
                Text(
                    text = "Todavía no hay documentos " +
                            "registrados para esta etapa.",
                    color = colores.textoSecundario,
                    fontSize = 13.sp
                )
            } else {
                documentos.forEach { documento ->
                    key(documento.id) {
                        TarjetaDocumentoRegistrado(
                            entregable = documento,
                            estadoDocumentoViewModel =
                                estadoDocumentoViewModel,
                            evidenciasViewModel =
                                evidenciasViewModel,
                            snackbarHostState =
                                snackbarHostState
                        )
                    }
                }
            }
        }
    }

    // ==============================================
    // DIALOGO: ESTADO DE ETAPA
    // ==============================================

    if (mostrarEstadosEtapa) {
        AlertDialog(
            onDismissRequest = {
                mostrarEstadosEtapa = false
            },
            containerColor = colores.tarjeta,
            titleContentColor = colores.texto,
            textContentColor = colores.texto,
            title = {
                Text("Seleccionar estado de etapa")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = etapa.titulo,
                        color = colores.textoSecundario
                    )

                    opcionesEstado.forEach { opcion ->
                        OutlinedButton(
                            onClick = {
                                mostrarEstadosEtapa = false
                                guardandoEtapa = true

                                scope.launch {
                                    try {
                                        entregablesViewModel.guardarEstado(
                                            etapa.id,
                                            opcion
                                        )

                                        snackbarHostState.showSnackbar(
                                            "Estado de etapa guardado"
                                        )

                                    } catch (e: CancellationException) {
                                        throw e

                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(
                                            "No se pudo guardar el estado"
                                        )

                                    } finally {
                                        guardandoEtapa = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(
                                if (altoContraste) 2.dp else 1.dp,
                                colores.borde
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colores.texto
                            )
                        ) {
                            Text(
                                text = if (opcion == estado) {
                                    "✓ $opcion"
                                } else {
                                    opcion
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarEstadosEtapa = false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        color = colores.texto
                    )
                }
            }
        )
    }
}

// ======================================================
// TARJETA DE DOCUMENTO INDIVIDUAL
// ======================================================

@Composable
private fun TarjetaDocumentoRegistrado(
    entregable: EntregableEntity,
    estadoDocumentoViewModel: EstadoDocumentoViewModel,
    evidenciasViewModel: EvidenciasViewModel,
    snackbarHostState: SnackbarHostState
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black
    val scope = rememberCoroutineScope()

    var mostrarEstados by remember {
        mutableStateOf(false)
    }

    var mostrarEdicion by remember {
        mutableStateOf(false)
    }

    var mostrarEliminar by remember {
        mutableStateOf(false)
    }

    var guardando by remember {
        mutableStateOf(false)
    }

    // ==============================================
    // EVIDENCIAS DEL ENTREGABLE
    // ==============================================

    val flujoEvidencias = remember(
        evidenciasViewModel,
        entregable.id
    ) {
        evidenciasViewModel.observarEvidencias(
            entregable.id
        )
    }

    val evidencias by flujoEvidencias.collectAsState(
        initial = emptyList()
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (altoContraste) {
            Color.Black
        } else {
            colores.fondo
        },
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            if (altoContraste) 2.dp else 1.dp,
            colores.borde
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = entregable.titulo,
                color = colores.texto,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = entregable.descripcion,
                color = colores.textoSecundario,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Text(
                text = "Fecha de entrega: ${entregable.fechaEntrega}",
                color = colores.texto,
                fontSize = 12.sp
            )

            Text(
                text = "Estado del documento",
                color = colores.textoSecundario,
                fontSize = 12.sp
            )

            IndicadorEstado(
                estado = entregable.estado
            )

            HorizontalDivider(color = colores.borde)

            // ======================================
            // EVIDENCIAS ADJUNTAS
            // ======================================

            Text(
                text = "Evidencias adjuntas",
                color = colores.texto,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${evidencias.size} archivo(s) adjunto(s)",
                color = colores.textoSecundario,
                fontSize = 12.sp
            )

            if (evidencias.isEmpty()) {
                Text(
                    text = "Este entregable todavía " +
                            "no tiene evidencias adjuntas.",
                    color = colores.textoSecundario,
                    fontSize = 12.sp
                )
            } else {
                evidencias.forEach { evidencia ->
                    key(evidencia.id) {
                        TarjetaEvidenciaAdjunta(
                            evidencia = evidencia,
                            evidenciasViewModel = evidenciasViewModel,
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            }

            HorizontalDivider(color = colores.borde)

            // ======================================
            // CAMBIAR ESTADO DEL DOCUMENTO
            // ======================================

            OutlinedButton(
                onClick = {
                    mostrarEstados = true
                },
                enabled = !guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colores.texto
                ),
                border = BorderStroke(
                    if (altoContraste) 2.dp else 1.dp,
                    colores.borde
                )
            ) {
                Text(
                    text = "Cambiar estado del documento",
                    fontWeight = FontWeight.SemiBold
                )
            }

            // ======================================
            // EDITAR DOCUMENTO
            // ======================================

            OutlinedButton(
                onClick = {
                    mostrarEdicion = true
                },
                enabled = !guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colores.texto
                ),
                border = BorderStroke(
                    if (altoContraste) 2.dp else 1.dp,
                    colores.borde
                )
            ) {
                Text(
                    text = "Editar documento",
                    fontWeight = FontWeight.SemiBold
                )
            }

            // ======================================
            // ELIMINAR ENTREGABLE
            // ======================================

            OutlinedButton(
                onClick = {
                    mostrarEliminar = true
                },
                enabled = !guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (altoContraste) {
                        Color.Yellow
                    } else {
                        Color(0xFFB42318)
                    }
                ),
                border = BorderStroke(
                    if (altoContraste) 2.dp else 1.dp,
                    if (altoContraste) {
                        Color.Yellow
                    } else {
                        Color(0xFFB42318)
                    }
                )
            ) {
                Text(
                    text = "Eliminar documento",
                    fontWeight = FontWeight.Bold
                )
            }

            if (entregable.observaciones.isNotBlank()) {
                HorizontalDivider(color = colores.borde)

                Text(
                    text = "Observaciones: ${entregable.observaciones}",
                    color = colores.textoSecundario,
                    fontSize = 12.sp
                )
            }
        }
    }

    // ==============================================
    // DIALOGO: ESTADO DEL DOCUMENTO
    // ==============================================

    if (mostrarEstados) {
        AlertDialog(
            onDismissRequest = {
                mostrarEstados = false
            },
            containerColor = colores.tarjeta,
            titleContentColor = colores.texto,
            textContentColor = colores.texto,
            title = {
                Text("Estado del documento")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = entregable.titulo,
                        color = colores.textoSecundario
                    )

                    opcionesEstado.forEach { opcion ->
                        OutlinedButton(
                            onClick = {
                                mostrarEstados = false
                                guardando = true

                                scope.launch {
                                    try {
                                        estadoDocumentoViewModel
                                            .cambiarEstadoDocumento(
                                                entregable.id,
                                                opcion
                                            )

                                        snackbarHostState.showSnackbar(
                                            "Estado del documento actualizado"
                                        )

                                    } catch (e: CancellationException) {
                                        throw e

                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(
                                            "No se pudo actualizar el estado"
                                        )

                                    } finally {
                                        guardando = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(
                                1.dp,
                                colores.borde
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colores.texto
                            )
                        ) {
                            Text(
                                text = if (entregable.estado == opcion) {
                                    "✓ $opcion"
                                } else {
                                    opcion
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarEstados = false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        color = colores.texto
                    )
                }
            }
        )
    }

    // ==============================================
    // DIALOGO: EDITAR ENTREGABLE
    // ==============================================

    if (mostrarEdicion) {
        DialogoEditarDocumento(
            entregable = entregable,
            guardando = guardando,
            onCancelar = {
                mostrarEdicion = false
            },
            onGuardar = { titulo, descripcion, fecha ->

                if (!guardando) {
                    guardando = true

                    scope.launch {
                        try {
                            val error = estadoDocumentoViewModel
                                .editarDocumento(
                                    entregable = entregable,
                                    nuevoTitulo = titulo,
                                    nuevaDescripcion = descripcion,
                                    nuevaFecha = fecha
                                )

                            if (error == null) {
                                mostrarEdicion = false

                                snackbarHostState.showSnackbar(
                                    "Documento actualizado correctamente"
                                )
                            } else {
                                snackbarHostState.showSnackbar(error)
                            }

                        } catch (e: CancellationException) {
                            throw e

                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar(
                                "No se pudo editar el documento"
                            )

                        } finally {
                            guardando = false
                        }
                    }
                }
            }
        )
    }

    // ==============================================
    // DIALOGO: ELIMINAR ENTREGABLE
    // ==============================================

    if (mostrarEliminar) {
        AlertDialog(
            onDismissRequest = {
                if (!guardando) {
                    mostrarEliminar = false
                }
            },
            containerColor = colores.tarjeta,
            titleContentColor = colores.texto,
            textContentColor = colores.texto,
            title = {
                Text("¿Eliminar entregable?")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Estás a punto de eliminar:",
                        color = colores.textoSecundario
                    )

                    Text(
                        text = entregable.titulo,
                        color = colores.texto,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Esta acción eliminará el registro " +
                                "del entregable y sus referencias " +
                                "de evidencias en la base de datos " +
                                "local. No se puede deshacer.",
                        color = colores.textoSecundario,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "Importante: las fotografías " +
                                "físicas asociadas podrían " +
                                "permanecer en la tablet. " +
                                "Para borrarlas completamente, " +
                                "elimínalas individualmente primero.",
                        color = colores.textoSecundario,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !guardando,
                    onClick = {
                        guardando = true

                        scope.launch {
                            try {
                                estadoDocumentoViewModel
                                    .eliminarDocumento(entregable)

                                mostrarEliminar = false

                                snackbarHostState.showSnackbar(
                                    "Documento eliminado correctamente"
                                )

                            } catch (e: CancellationException) {
                                throw e

                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(
                                    "No se pudo eliminar el documento"
                                )

                            } finally {
                                guardando = false
                            }
                        }
                    }
                ) {
                    Text(
                        text = if (guardando) {
                            "Eliminando..."
                        } else {
                            "Sí, eliminar"
                        },
                        color = if (altoContraste) {
                            Color.Yellow
                        } else {
                            Color(0xFFB42318)
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !guardando,
                    onClick = {
                        mostrarEliminar = false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        color = colores.texto
                    )
                }
            }
        )
    }
}

// ======================================================
// TARJETA DE EVIDENCIA ADJUNTA
// PDF O FOTOGRAFIA
// ======================================================

@Composable
private fun TarjetaEvidenciaAdjunta(
    evidencia: EvidenciaEntity,
    evidenciasViewModel: EvidenciasViewModel,
    snackbarHostState: SnackbarHostState
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var mostrarConfirmacion by remember {
        mutableStateOf(false)
    }

    var eliminando by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colores.tarjeta,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            if (altoContraste) 2.dp else 1.dp,
            colores.borde
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ======================================
            // INFORMACION DE LA EVIDENCIA
            // ======================================

            Text(
                text = if (evidencia.tipo == "PDF") {
                    "PDF adjunto"
                } else {
                    "Fotografía adjunta"
                },
                color = colores.textoSecundario,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = evidencia.nombreArchivo,
                color = colores.texto,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            // ======================================
            // ABRIR EVIDENCIA
            // ======================================

            OutlinedButton(
                onClick = {
                    try {
                        val uri = Uri.parse(
                            evidencia.uriArchivo
                        )

                        val tipoMime = if (
                            evidencia.tipo == "PDF"
                        ) {
                            "application/pdf"
                        } else {
                            "image/*"
                        }

                        val intent = Intent(
                            Intent.ACTION_VIEW
                        ).apply {
                            setDataAndType(
                                uri,
                                tipoMime
                            )

                            addFlags(
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        }

                        context.startActivity(intent)

                    } catch (e: Exception) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "No se pudo abrir el archivo. " +
                                        "Comprueba que siga disponible " +
                                        "en el dispositivo."
                            )
                        }
                    }
                },
                enabled = !eliminando,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colores.texto
                ),
                border = BorderStroke(1.dp, colores.borde)
            ) {
                Text(
                    text = if (evidencia.tipo == "PDF") {
                        "Abrir PDF"
                    } else {
                        "Abrir fotografía"
                    }
                )
            }

            // ======================================
            // ELIMINAR EVIDENCIA
            // ======================================

            OutlinedButton(
                onClick = {
                    mostrarConfirmacion = true
                },
                enabled = !eliminando,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (altoContraste) {
                        Color.Yellow
                    } else {
                        Color(0xFFB42318)
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    if (altoContraste) {
                        Color.Yellow
                    } else {
                        Color(0xFFB42318)
                    }
                )
            ) {
                Text(
                    text = if (eliminando) {
                        "Eliminando..."
                    } else {
                        "Eliminar adjunto"
                    }
                )
            }
        }
    }

    // ==============================================
    // CONFIRMACION DE ELIMINACION
    // ==============================================

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = {
                if (!eliminando) {
                    mostrarConfirmacion = false
                }
            },
            containerColor = colores.tarjeta,
            titleContentColor = colores.texto,
            textContentColor = colores.texto,
            title = {
                Text(
                    if (evidencia.tipo == "FOTO") {
                        "¿Eliminar fotografía?"
                    } else {
                        "¿Eliminar evidencia?"
                    }
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = evidencia.nombreArchivo,
                        fontWeight = FontWeight.Bold,
                        color = colores.texto
                    )

                    // ==================================
                    // CORRECCION FINAL
                    // MENSAJE SEGUN TIPO DE EVIDENCIA
                    // ==================================

                    Text(
                        text = if (evidencia.tipo == "FOTO") {
                            "Se eliminará esta fotografía " +
                                    "de ESUCAR y también del " +
                                    "almacenamiento de la tablet. " +
                                    "Esta acción no se puede deshacer."
                        } else {
                            "Se eliminará este PDF del " +
                                    "registro de ESUCAR. " +
                                    "El archivo PDF original " +
                                    "permanecerá en el dispositivo."
                        },
                        color = colores.textoSecundario,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !eliminando,
                    onClick = {
                        eliminando = true

                        evidenciasViewModel.eliminarEvidencia(
                            evidencia = evidencia,
                            onResultado = { correcto ->

                                eliminando = false

                                if (correcto) {
                                    mostrarConfirmacion = false
                                }

                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (correcto) {
                                            if (evidencia.tipo == "FOTO") {
                                                "Fotografía eliminada " +
                                                        "de ESUCAR y la tablet"
                                            } else {
                                                "PDF desvinculado " +
                                                        "correctamente"
                                            }
                                        } else {
                                            "No se pudo eliminar " +
                                                    "la evidencia"
                                        }
                                    )
                                }
                            }
                        )
                    }
                ) {
                    Text(
                        text = if (eliminando) {
                            "Eliminando..."
                        } else {
                            "Sí, eliminar"
                        },
                        color = if (altoContraste) {
                            Color.Yellow
                        } else {
                            Color(0xFFB42318)
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !eliminando,
                    onClick = {
                        mostrarConfirmacion = false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        color = colores.texto
                    )
                }
            }
        )
    }
}

// ======================================================
// FORMULARIO DE EDICION DE ENTREGABLE
// ======================================================

@Composable
private fun DialogoEditarDocumento(
    entregable: EntregableEntity,
    guardando: Boolean,
    onCancelar: () -> Unit,
    onGuardar: (String, String, String) -> Unit
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    var titulo by remember(entregable.id) {
        mutableStateOf(entregable.titulo)
    }

    var descripcion by remember(entregable.id) {
        mutableStateOf(entregable.descripcion)
    }

    var fecha by remember(entregable.id) {
        mutableStateOf(entregable.fechaEntrega)
    }

    AlertDialog(
        onDismissRequest = {
            if (!guardando) {
                onCancelar()
            }
        },
        containerColor = colores.tarjeta,
        titleContentColor = colores.texto,
        textContentColor = colores.texto,
        title = {
            Text("Editar entregable")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Documento #${entregable.id}",
                    color = colores.textoSecundario,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = titulo,
                    onValueChange = {
                        titulo = it
                    },
                    label = {
                        Text("Título")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !guardando,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colores.texto,
                        unfocusedTextColor = colores.texto,
                        focusedContainerColor = colores.tarjeta,
                        unfocusedContainerColor = colores.tarjeta
                    )
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = it
                    },
                    label = {
                        Text("Descripción")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !guardando,
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colores.texto,
                        unfocusedTextColor = colores.texto,
                        focusedContainerColor = colores.tarjeta,
                        unfocusedContainerColor = colores.tarjeta
                    )
                )

                OutlinedTextField(
                    value = fecha,
                    onValueChange = {
                        fecha = it
                    },
                    label = {
                        Text("Fecha de entrega")
                    },
                    placeholder = {
                        Text("AAAA-MM-DD")
                    },
                    supportingText = {
                        Text("Ejemplo: 2026-12-10")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !guardando,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colores.texto,
                        unfocusedTextColor = colores.texto,
                        focusedContainerColor = colores.tarjeta,
                        unfocusedContainerColor = colores.tarjeta
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !guardando,
                onClick = {
                    onGuardar(
                        titulo,
                        descripcion,
                        fecha
                    )
                }
            ) {
                Text(
                    text = if (guardando) {
                        "Guardando..."
                    } else {
                        "Guardar cambios"
                    },
                    color = if (altoContraste) {
                        Color.Yellow
                    } else {
                        colores.texto
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                enabled = !guardando,
                onClick = onCancelar
            ) {
                Text(
                    text = "Cancelar",
                    color = colores.texto
                )
            }
        }
    )
}

// ======================================================
// INDICADOR REUTILIZABLE DE ESTADO
// ======================================================

@Composable
private fun IndicadorEstado(
    estado: String
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    val fondo = if (altoContraste) {
        Color.Yellow
    } else {
        when (estado) {
            "Aprobado" -> Color(0xFFDCFCE7)
            "Enviado" -> Color(0xFFDBEAFE)
            "En desarrollo" -> Color(0xFFFFF4D6)
            else -> Color(0xFFE8EFE9)
        }
    }

    val texto = if (altoContraste) {
        Color.Black
    } else {
        when (estado) {
            "Aprobado" -> Color(0xFF166534)
            "Enviado" -> Color(0xFF1D4ED8)
            "En desarrollo" -> Color(0xFF875A13)
            else -> Color(0xFF174B35)
        }
    }

    Surface(
        color = fondo,
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = estado,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp
            ),
            color = texto,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
