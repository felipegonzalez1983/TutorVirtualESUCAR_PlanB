package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tutorvirtual.ui.IconoEstadoCampo
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar
import cl.duoc.tutorvirtual.viewmodel.RegistroEntregableViewModel
import cl.duoc.tutorvirtual.viewmodel.ValidacionEntregable
import kotlinx.coroutines.launch

@Composable
fun RegistroEntregableScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit = {},
    onGuardado: () -> Unit = {},
    registroViewModel: RegistroEntregableViewModel = viewModel()
) {
    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    // DATOS DEL FORMULARIO
    var etapaSeleccionada by rememberSaveable {
        mutableIntStateOf(2)
    }

    var titulo by rememberSaveable {
        mutableStateOf("")
    }

    var descripcion by rememberSaveable {
        mutableStateOf("")
    }

    var fechaEntrega by rememberSaveable {
        mutableStateOf("")
    }

    var guardando by remember {
        mutableStateOf(false)
    }

    var intentoGuardar by remember {
        mutableStateOf(false)
    }

    // Los mensajes salen de ValidacionEntregable, aquí solo se muestran
    val errorTitulo = ValidacionEntregable.errorTitulo(titulo)
    val errorDescripcion = ValidacionEntregable.errorDescripcion(descripcion)
    val errorFecha = ValidacionEntregable.errorFecha(fechaEntrega)

    var mostrarEtapas by remember {
        mutableStateOf(false)
    }

    val etapaActual = registroViewModel.etapas
        .firstOrNull { it.id == etapaSeleccionada }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 100.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // BOTON VOLVER
            TextButton(
                onClick = onVolver
            ) {
                Text(
                    text = "← Volver",
                    color = colores.texto
                )
            }

            // ENCABEZADO INSTITUCIONAL
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ESUCAR · REGISTRO ACADÉMICO",
                        color = colores.acento,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Nuevo entregable",
                        color = colores.textoEncabezado,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Registra un documento asociado " +
                                "a una etapa metodológica.",
                        color = colores.textoEncabezado,
                        fontSize = 14.sp
                    )
                }
            }

            // TITULO DEL FORMULARIO
            Text(
                text = "Datos del entregable",
                color = colores.texto,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // SELECCION DE ETAPA
            Text(
                text = "Etapa metodológica",
                color = colores.texto,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedButton(
                onClick = {
                    mostrarEtapas = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = etapaActual?.let {
                        "${it.id}. ${it.titulo}"
                    } ?: "Seleccionar etapa",
                    color = colores.texto
                )
            }

            // CAMPO TITULO
            OutlinedTextField(
                value = titulo,
                onValueChange = {
                    titulo = it
                },
                label = {
                    Text("Título del entregable")
                },
                placeholder = {
                    Text("Ej.: Informe de Marco Teórico")
                },
                leadingIcon = {
                    Icon(Icons.Default.Title, contentDescription = null)
                },
                trailingIcon = {
                    IconoEstadoCampo(
                        hayError = intentoGuardar && errorTitulo != null,
                        esCorrecto = titulo.isNotBlank() && errorTitulo == null
                    )
                },
                isError = intentoGuardar && errorTitulo != null,
                supportingText = {
                    if (intentoGuardar && errorTitulo != null) {
                        Text(errorTitulo)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // CAMPO DESCRIPCION
            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                },
                label = {
                    Text("Descripción")
                },
                placeholder = {
                    Text("Describe el documento académico")
                },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null)
                },
                trailingIcon = {
                    IconoEstadoCampo(
                        hayError = intentoGuardar && errorDescripcion != null,
                        esCorrecto = descripcion.isNotBlank() && errorDescripcion == null
                    )
                },
                isError = intentoGuardar && errorDescripcion != null,
                supportingText = {
                    if (intentoGuardar && errorDescripcion != null) {
                        Text(errorDescripcion)
                    } else {
                        Text("${descripcion.trim().length} caracteres")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            // CAMPO FECHA
            OutlinedTextField(
                value = fechaEntrega,
                onValueChange = {
                    fechaEntrega = it
                },
                label = {
                    Text("Fecha de entrega")
                },
                placeholder = {
                    Text("AAAA-MM-DD")
                },
                leadingIcon = {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                },
                trailingIcon = {
                    IconoEstadoCampo(
                        hayError = intentoGuardar && errorFecha != null,
                        esCorrecto = fechaEntrega.isNotBlank() && errorFecha == null
                    )
                },
                isError = intentoGuardar && errorFecha != null,
                supportingText = {
                    if (intentoGuardar && errorFecha != null) {
                        Text(errorFecha)
                    } else {
                        Text("Ejemplo: 2026-12-10")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // ESTADO INICIAL
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colores.tarjeta,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    1.dp,
                    colores.borde
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Estado inicial",
                        color = colores.textoSecundario,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "Por iniciar",
                        color = colores.texto,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // GUARDAR ENTREGABLE EN ROOM
            Button(
                onClick = {
                    intentoGuardar = true

                    if (!guardando) {
                        guardando = true

                        scope.launch {
                            try {
                                val error =
                                    registroViewModel.registrarEntregable(
                                        etapaId = etapaSeleccionada,
                                        titulo = titulo,
                                        descripcion = descripcion,
                                        fechaEntrega = fechaEntrega,
                                        estado = "Por iniciar"
                                    )

                                if (error == null) {
                                    snackbarHostState.showSnackbar(
                                        message = "Entregable guardado correctamente",
                                        duration = SnackbarDuration.Short
                                    )

                                    onGuardado()
                                } else {
                                    snackbarHostState.showSnackbar(
                                        message = error
                                    )
                                }

                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(
                                    message = "Ocurrió un error al guardar"
                                )
                            } finally {
                                guardando = false
                            }
                        }
                    }
                },
                enabled = !guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp),
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
                    text = if (guardando) {
                        "Guardando..."
                    } else {
                        "Guardar entregable"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // MENSAJES DE CONFIRMACION
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

    // VENTANA PARA SELECCIONAR ETAPA
    if (mostrarEtapas) {
        AlertDialog(
            onDismissRequest = {
                mostrarEtapas = false
            },
            containerColor = colores.tarjeta,
            titleContentColor = colores.texto,
            textContentColor = colores.texto,

            title = {
                Text("Seleccionar etapa")
            },

            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    registroViewModel.etapas.forEach { etapa ->
                        TextButton(
                            onClick = {
                                etapaSeleccionada = etapa.id
                                mostrarEtapas = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${etapa.id}. ${etapa.titulo}",
                                color = colores.texto
                            )
                        }
                    }
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarEtapas = false
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
