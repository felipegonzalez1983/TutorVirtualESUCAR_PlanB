package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cl.duoc.tutorvirtual.data.EntregablesPreferences
import cl.duoc.tutorvirtual.data.EtapasRepository
import cl.duoc.tutorvirtual.model.Etapa
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

import kotlinx.coroutines.launch

private val opcionesEstado = listOf(
    "Por iniciar",
    "En desarrollo",
    "Enviado",
    "Aprobado"
)

@Composable
fun EntregablesScreen(
    modifier: Modifier = Modifier
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    val context = LocalContext.current

    val preferences = remember(context) {
        EntregablesPreferences(
            context.applicationContext
        )
    }

    val etapas = remember {
        EtapasRepository.obtenerEtapas()
            .sortedBy { it.id }
    }

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
                bottom = 90.dp
            ),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            // ENCABEZADO
            item {

                Surface(
                    color = colores.encabezado,

                    shape = RoundedCornerShape(24.dp),

                    border = if (altoContraste) {
                        BorderStroke(
                            1.dp,
                            colores.borde
                        )
                    } else {
                        null
                    }
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
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
                            text = "Consulta y actualiza el " +
                                    "estado de cada etapa de " +
                                    "tu Trabajo de Título.",

                            color = colores.textoEncabezado,

                            fontSize = 14.sp,

                            lineHeight = 21.sp
                        )
                    }
                }
            }

            // TÍTULO DEL LISTADO
            item {

                Text(
                    text = "Seguimiento de etapas",

                    color = colores.texto,

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Bold
                )
            }

            // CINCO ETAPAS ACADÉMICAS
            items(
                items = etapas,
                key = { it.id }
            ) { etapa ->

                TarjetaEntregableAccesible(
                    etapa = etapa,

                    preferences = preferences,

                    snackbarHostState =
                        snackbarHostState
                )
            }

            // ACLARACIÓN DEL ESTADO
            item {

                Surface(
                    modifier = Modifier.fillMaxWidth(),

                    color = colores.tarjeta,

                    shape = RoundedCornerShape(16.dp),

                    border = if (altoContraste) {
                        BorderStroke(
                            1.dp,
                            colores.borde
                        )
                    } else {
                        null
                    }
                ) {

                    Text(
                        text = "Los estados se registran " +
                                "manualmente y representan " +
                                "tu seguimiento personal. " +
                                "Seleccionar 'Aprobado' " +
                                "no significa que ESUCAR " +
                                "haya aprobado oficialmente " +
                                "una entrega.",

                        modifier = Modifier.padding(16.dp),

                        color = colores.textoSecundario,

                        fontSize = 12.sp,

                        lineHeight = 19.sp
                    )
                }
            }
        }

        // MENSAJE DE CONFIRMACIÓN
        SnackbarHost(
            hostState = snackbarHostState,

            modifier = Modifier.align(
                Alignment.BottomCenter
            )
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


// ==================================================
// TARJETA DE ENTREGABLE
// ==================================================

@Composable
private fun TarjetaEntregableAccesible(
    etapa: Etapa,
    preferences: EntregablesPreferences,
    snackbarHostState: SnackbarHostState
) {

    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    val scope = rememberCoroutineScope()

    // RECUPERAR EL ESTADO GUARDADO
    val flujoEstado = remember(
        preferences,
        etapa.id
    ) {
        preferences.obtenerEstado(etapa.id)
    }

    val estado by flujoEstado.collectAsState(
        initial = etapa.estado
    )

    // CONTROLAR VENTANA DE SELECCIÓN
    var mostrarOpciones by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = colores.tarjeta
        ),

        shape = RoundedCornerShape(18.dp),

        border = if (altoContraste) {
            BorderStroke(
                1.dp,
                colores.borde
            )
        } else {
            null
        },

        elevation = CardDefaults.cardElevation(
            defaultElevation = if (altoContraste) {
                0.dp
            } else {
                2.dp
            }
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            // NÚMERO Y TÍTULO
            Row(
                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
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
                            text = etapa.id.toString()
                                .padStart(2, '0'),

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

            // DESCRIPCIÓN
            Text(
                text = etapa.descripcion,

                color = colores.textoSecundario,

                fontSize = 14.sp,

                lineHeight = 21.sp
            )

            HorizontalDivider(
                color = colores.borde
            )

            Text(
                text = "Estado actual",

                color = colores.textoSecundario,

                fontSize = 13.sp
            )

            // ESTADO GUARDADO
            Surface(
                color = if (altoContraste) {
                    Color.Yellow
                } else {
                    when (estado) {
                        "Aprobado" -> Color(0xFFDCFCE7)
                        "Enviado" -> Color(0xFFDBEAFE)
                        "En desarrollo" -> Color(0xFFFFF4D6)
                        else -> Color(0xFFE8EFE9)
                    }
                },

                shape = RoundedCornerShape(50.dp)
            ) {

                Text(
                    text = estado,

                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    ),

                    color = if (altoContraste) {
                        Color.Black
                    } else {
                        when (estado) {
                            "Aprobado" -> Color(0xFF166534)
                            "Enviado" -> Color(0xFF1D4ED8)
                            "En desarrollo" -> Color(0xFF875A13)
                            else -> Color(0xFF174B35)
                        }
                    },

                    fontSize = 13.sp,

                    fontWeight = FontWeight.Bold
                )
            }

            // CAMBIAR ESTADO
            Button(
                onClick = {
                    mostrarOpciones = true
                },

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
                    text = "Cambiar estado",

                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // VENTANA PARA ELEGIR UNO DE LOS 4 ESTADOS
    if (mostrarOpciones) {

        AlertDialog(
            onDismissRequest = {
                mostrarOpciones = false
            },

            containerColor = colores.tarjeta,

            titleContentColor = colores.texto,

            textContentColor = colores.texto,

            title = {
                Text("Seleccionar estado")
            },

            text = {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(4.dp)
                ) {

                    Text(
                        text = etapa.titulo,

                        color = colores.textoSecundario,

                        fontSize = 13.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    opcionesEstado.forEach { opcion ->

                        OutlinedButton(
                            onClick = {

                                mostrarOpciones = false

                                scope.launch {

                                    preferences.guardarEstado(
                                        etapa.id,
                                        opcion
                                    )

                                    snackbarHostState.showSnackbar(
                                        message =
                                            "Estado guardado correctamente"
                                    )
                                }
                            },

                            modifier = Modifier.fillMaxWidth(),

                            border = BorderStroke(
                                width = if (altoContraste) {
                                    2.dp
                                } else {
                                    1.dp
                                },

                                color = if (altoContraste) {
                                    Color.White
                                } else {
                                    colores.texto
                                }
                            ),

                            colors =
                                ButtonDefaults.outlinedButtonColors(
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
                        mostrarOpciones = false
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
