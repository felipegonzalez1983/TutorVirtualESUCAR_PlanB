package cl.duoc.tutorvirtual.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

import cl.duoc.tutorvirtual.ui.screens.HomeScreen
import cl.duoc.tutorvirtual.ui.screens.EtapasScreen
import cl.duoc.tutorvirtual.ui.screens.DetalleEtapaScreen
import cl.duoc.tutorvirtual.ui.screens.GuiaPasoScreen
import cl.duoc.tutorvirtual.ui.screens.FaqScreen
import cl.duoc.tutorvirtual.ui.screens.EntregablesScreen
import cl.duoc.tutorvirtual.ui.screens.RegistroEntregableScreen
import cl.duoc.tutorvirtual.ui.screens.HitosScreen
import cl.duoc.tutorvirtual.ui.screens.RubricasScreen
import cl.duoc.tutorvirtual.ui.screens.ActualizacionesScreen
import cl.duoc.tutorvirtual.ui.screens.AccesibilidadScreen
import cl.duoc.tutorvirtual.ui.screens.LoginScreen
import cl.duoc.tutorvirtual.ui.screens.DiagnosticoBiometriaScreen
import cl.duoc.tutorvirtual.ui.screens.EvidenciaCamaraScreen

import cl.duoc.tutorvirtual.viewmodel.LoginViewModel

import kotlinx.coroutines.launch

// ======================================================
// TUTOR VIRTUAL ESUCAR - NAVEGACION PRINCIPAL
//
// Login estudiantil
// Sesion persistente con DataStore
// Entregables con Room + SQLite
// Diagnostico biometrico
// Camara para evidencias academicas
// ======================================================

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel()
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    // ==============================================
    // AUTENTICACION ESTUDIANTIL
    // ==============================================

    val estudianteActual by
    loginViewModel.estudianteActual.collectAsState()

    val restaurandoSesion by
    loginViewModel.restaurandoSesion.collectAsState()

    val mensajeLoginHost = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    // ==============================================
    // NAVEGACION
    // ==============================================

    var pantallaActual by rememberSaveable {
        mutableStateOf("Inicio")
    }

    var etapaSeleccionada by rememberSaveable {
        mutableIntStateOf(1)
    }

    var origenFaq by rememberSaveable {
        mutableStateOf("Inicio")
    }

    var origenHitos by rememberSaveable {
        mutableStateOf("Inicio")
    }

    // ==============================================
    // RECUPERAR SESION DE DATASTORE
    // ==============================================

    if (restaurandoSesion) {

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colores.fondo),

            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier.padding(24.dp),

                horizontalAlignment = Alignment.CenterHorizontally,

                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                CircularProgressIndicator(
                    color = if (altoContraste) {
                        Color.Yellow
                    } else {
                        colores.acento
                    }
                )

                Text(
                    text = "Tutor Virtual ESUCAR",
                    color = colores.texto,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Verificando sesión estudiantil...",
                    color = colores.textoSecundario,
                    fontSize = 14.sp
                )
            }
        }

        return
    }

    // ==============================================
    // MOSTRAR LOGIN SI NO HAY SESION
    // ==============================================

    if (estudianteActual == null) {

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colores.fondo)
        ) {

            LoginScreen(
                modifier = Modifier.fillMaxSize(),

                onIniciarSesion = { identificador, contrasena ->

                    val ingresoCorrecto =
                        loginViewModel.iniciarSesion(
                            identificador = identificador,
                            contrasena = contrasena
                        )

                    if (ingresoCorrecto) {

                        pantallaActual = "Inicio"
                        loginViewModel.limpiarError()

                    } else {

                        val mensaje =
                            loginViewModel.mensajeError.value
                                ?: "No se pudo iniciar sesión."

                        scope.launch {

                            mensajeLoginHost.showSnackbar(
                                message = mensaje,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            )

            SnackbarHost(
                hostState = mensajeLoginHost,

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

        return
    }

    // ==============================================
    // ESTUDIANTE CON SESION ACTIVA
    // ==============================================

    val nombreEstudiante =
        estudianteActual?.nombre ?: "Estudiante"

    val cursoEstudiante =
        estudianteActual?.curso ?: ""

    // ==============================================
    // BOTON ATRAS
    // ==============================================

    BackHandler(
        enabled = pantallaActual != "Inicio"
    ) {

        pantallaActual = when (pantallaActual) {

            "GuiaPaso" -> "DetalleEtapa"

            "DetalleEtapa" -> "Etapas"

            "Faq" -> origenFaq

            "Hitos" -> origenHitos

            "Rubricas" -> "Recursos"

            "Actualizaciones" -> "Recursos"

            "Accesibilidad" -> "Recursos"

            "DiagnosticoBiometria" -> "Recursos"

            "EvidenciaCamara" -> "Recursos"

            "RegistroEntregable" -> "Entregables"

            else -> "Inicio"
        }
    }

    // ==============================================
    // ESTRUCTURA PRINCIPAL
    // ==============================================

    Scaffold(
        modifier = modifier,

        containerColor = colores.fondo,

        contentColor = colores.texto,

        // ==========================================
        // INFORMACION DEL ESTUDIANTE
        // ==========================================

        topBar = {

            if (pantallaActual == "Inicio") {

                Surface(
                    color = colores.tarjeta,

                    border = if (altoContraste) {
                        BorderStroke(
                            1.dp,
                            colores.borde
                        )
                    } else {
                        null
                    }
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),

                        verticalAlignment = Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = nombreEstudiante,
                                color = colores.texto,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )

                            Text(
                                text = cursoEstudiante,
                                color = colores.textoSecundario,
                                fontSize = 11.sp
                            )
                        }

                        TextButton(
                            onClick = {

                                pantallaActual = "Inicio"
                                etapaSeleccionada = 1
                                origenFaq = "Inicio"
                                origenHitos = "Inicio"

                                loginViewModel.cerrarSesion()
                            }
                        ) {

                            Text(
                                text = "Cerrar sesión",

                                color = if (altoContraste) {
                                    Color.Yellow
                                } else {
                                    colores.texto
                                },

                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },

        // ==========================================
        // BOTON DE NUEVO ENTREGABLE
        // ==========================================

        floatingActionButton = {

            if (pantallaActual == "Entregables") {

                FloatingActionButton(
                    onClick = {
                        pantallaActual = "RegistroEntregable"
                    },

                    containerColor = if (altoContraste) {
                        Color.Yellow
                    } else {
                        colores.encabezado
                    },

                    contentColor = if (altoContraste) {
                        Color.Black
                    } else {
                        Color.White
                    },

                    shape = RoundedCornerShape(16.dp)
                ) {

                    Text(
                        text = "+ Nuevo entregable",

                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        ),

                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },

        // ==========================================
        // BARRA INFERIOR
        // ==========================================

        bottomBar = {

            NavigationBar(
                containerColor = if (altoContraste) {
                    Color.Black
                } else {
                    Color.White
                }
            ) {

                val opciones = listOf(
                    "Inicio",
                    "Etapas",
                    "Entregables",
                    "Recursos"
                )

                opciones.forEach { opcion ->

                    val seleccionado = when (opcion) {

                        "Etapas" -> pantallaActual in listOf(
                            "Etapas",
                            "DetalleEtapa",
                            "GuiaPaso"
                        )

                        "Entregables" -> pantallaActual in listOf(
                            "Entregables",
                            "RegistroEntregable"
                        )

                        "Recursos" -> pantallaActual in listOf(
                            "Recursos",
                            "Rubricas",
                            "Faq",
                            "Hitos",
                            "Actualizaciones",
                            "Accesibilidad",
                            "DiagnosticoBiometria",
                            "EvidenciaCamara"
                        )

                        else -> pantallaActual == opcion
                    }

                    NavigationBarItem(
                        selected = seleccionado,

                        onClick = {
                            pantallaActual = opcion
                        },

                        icon = {

                            Text(
                                text = when (opcion) {
                                    "Inicio" -> "⌂"
                                    "Etapas" -> "◈"
                                    "Entregables" -> "☑"
                                    else -> "▤"
                                },

                                style = MaterialTheme
                                    .typography.titleLarge
                            )
                        },

                        label = {
                            Text(opcion)
                        },

                        colors = NavigationBarItemDefaults.colors(

                            selectedIconColor =
                                if (altoContraste) {
                                    Color.Black
                                } else {
                                    colores.texto
                                },

                            selectedTextColor = colores.texto,

                            unselectedIconColor =
                                if (altoContraste) {
                                    Color.White
                                } else {
                                    Color.DarkGray
                                },

                            unselectedTextColor =
                                if (altoContraste) {
                                    Color.White
                                } else {
                                    Color.DarkGray
                                },

                            indicatorColor =
                                if (altoContraste) {
                                    Color.Yellow
                                } else {
                                    colores.acento.copy(
                                        alpha = 0.25f
                                    )
                                }
                        )
                    )
                }
            }
        }

    ) { innerPadding ->

        val contenidoModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (pantallaActual) {

            // ======================================
            // INICIO
            // ======================================

            "Inicio" -> {

                HomeScreen(
                    modifier = contenidoModifier,

                    onOptionClick = { opcion ->

                        pantallaActual = when (opcion) {

                            "Guía metodológica" -> "Etapas"

                            "Preguntas frecuentes" -> {

                                origenFaq = "Inicio"
                                "Faq"
                            }

                            "Mis entregables" -> "Entregables"

                            "Calendario académico" -> {

                                origenHitos = "Inicio"
                                "Hitos"
                            }

                            else -> opcion
                        }
                    }
                )
            }

            // ======================================
            // ETAPAS METODOLOGICAS
            // ======================================

            "Etapas" -> {

                EtapasScreen(
                    modifier = contenidoModifier,

                    onEtapaClick = { id ->

                        etapaSeleccionada = id
                        pantallaActual = "DetalleEtapa"
                    },

                    onVolverInicio = {
                        pantallaActual = "Inicio"
                    }
                )
            }

            // ======================================
            // DETALLE DE ETAPA
            // ======================================

            "DetalleEtapa" -> {

                DetalleEtapaScreen(
                    etapaId = etapaSeleccionada,
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "Etapas"
                    },

                    onIniciarGuia = {
                        pantallaActual = "GuiaPaso"
                    }
                )
            }

            // ======================================
            // GUIA PASO A PASO
            // ======================================

            "GuiaPaso" -> {

                GuiaPasoScreen(
                    etapaId = etapaSeleccionada,
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "DetalleEtapa"
                    }
                )
            }

            // ======================================
            // PREGUNTAS FRECUENTES
            // ======================================

            "Faq" -> {

                FaqScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = origenFaq
                    }
                )
            }

            // ======================================
            // MIS ENTREGABLES
            // ======================================

            "Entregables" -> {

                EntregablesScreen(
                    modifier = contenidoModifier
                )
            }

            // ======================================
            // REGISTRAR ENTREGABLE
            // ======================================

            "RegistroEntregable" -> {

                RegistroEntregableScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "Entregables"
                    },

                    onGuardado = {
                        pantallaActual = "Entregables"
                    }
                )
            }

            // ======================================
            // CALENDARIO
            // ======================================

            "Hitos" -> {

                HitosScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = origenHitos
                    }
                )
            }

            // ======================================
            // RUBRICAS
            // ======================================

            "Rubricas" -> {

                RubricasScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "Recursos"
                    }
                )
            }

            // ======================================
            // NOVEDADES
            // ======================================

            "Actualizaciones" -> {

                ActualizacionesScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "Recursos"
                    }
                )
            }

            // ======================================
            // ACCESIBILIDAD
            // ======================================

            "Accesibilidad" -> {

                AccesibilidadScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "Recursos"
                    }
                )
            }

            // ======================================
            // DIAGNOSTICO BIOMETRICO
            // ======================================

            "DiagnosticoBiometria" -> {

                DiagnosticoBiometriaScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "Recursos"
                    }
                )
            }

            // ======================================
            // CAMARA ACADEMICA
            // ======================================

            "EvidenciaCamara" -> {

                EvidenciaCamaraScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "Recursos"
                    }
                )
            }

            // ======================================
            // RECURSOS
            // ======================================

            "Recursos" -> {

                RecursosScreen(
                    modifier = contenidoModifier,

                    onRubricas = {
                        pantallaActual = "Rubricas"
                    },

                    onFaq = {

                        origenFaq = "Recursos"
                        pantallaActual = "Faq"
                    },

                    onHitos = {

                        origenHitos = "Recursos"
                        pantallaActual = "Hitos"
                    },

                    onActualizaciones = {
                        pantallaActual = "Actualizaciones"
                    },

                    onAccesibilidad = {
                        pantallaActual = "Accesibilidad"
                    },

                    onBiometria = {
                        pantallaActual = "DiagnosticoBiometria"
                    },

                    onCamara = {
                        pantallaActual = "EvidenciaCamara"
                    }
                )
            }

            // ======================================
            // PANTALLAS EN DESARROLLO
            // ======================================

            else -> {

                Box(
                    modifier = contenidoModifier
                        .background(colores.fondo),

                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        modifier = Modifier.padding(24.dp),

                        horizontalAlignment = Alignment.CenterHorizontally,

                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        Text(
                            text = pantallaActual,
                            color = colores.texto,

                            style = MaterialTheme
                                .typography.headlineMedium
                        )

                        Text(
                            text = "Esta sección está en desarrollo.",
                            color = colores.textoSecundario
                        )

                        Button(
                            onClick = {
                                pantallaActual = "Inicio"
                            },

                            colors = ButtonDefaults.buttonColors(

                                containerColor =
                                    if (altoContraste) {
                                        Color.Yellow
                                    } else {
                                        colores.encabezado
                                    },

                                contentColor =
                                    if (altoContraste) {
                                        Color.Black
                                    } else {
                                        Color.White
                                    }
                            )
                        ) {

                            Text("Volver al inicio")
                        }
                    }
                }
            }
        }
    }
}

// ======================================================
// RECURSOS ACADEMICOS
// ======================================================

@Composable
private fun RecursosScreen(
    modifier: Modifier = Modifier,
    onRubricas: () -> Unit,
    onFaq: () -> Unit,
    onHitos: () -> Unit,
    onActualizaciones: () -> Unit,
    onAccesibilidad: () -> Unit,
    onBiometria: () -> Unit,
    onCamara: () -> Unit
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    Column(
        modifier = modifier
            .background(colores.fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ======================================
        // ENCABEZADO
        // ======================================

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
                    text = "ESUCAR · APOYO ACADÉMICO",
                    color = colores.acento,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Recursos académicos",
                    color = colores.textoEncabezado,
                    fontSize = 27.sp,
                    lineHeight = 33.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Herramientas para orientar " +
                            "tu Trabajo de Título.",

                    color = if (altoContraste) {
                        Color.White
                    } else {
                        Color(0xFFE2EEE5)
                    },

                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }
        }

        Text(
            text = "Herramientas disponibles",
            color = colores.texto,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // ======================================
        // NOVEDADES
        // ======================================

        TarjetaRecurso(
            titulo = "Novedades académicas",

            descripcion = "Consulta contenido actualizado " +
                    "desde internet y disponible sin conexión.",

            textoBoton = "Ver novedades",
            onClick = onActualizaciones,
            destacado = true
        )

        // ======================================
        // RUBRICAS
        // ======================================

        TarjetaRecurso(
            titulo = "Rúbricas de evaluación",

            descripcion = "Consulta criterios orientativos " +
                    "de las cinco etapas académicas.",

            textoBoton = "Ver rúbricas",
            onClick = onRubricas
        )

        // ======================================
        // PREGUNTAS FRECUENTES
        // ======================================

        TarjetaRecurso(
            titulo = "Preguntas frecuentes",

            descripcion = "Busca respuestas sobre citación, " +
                    "metodología y formato.",

            textoBoton = "Ver preguntas frecuentes",
            onClick = onFaq
        )

        // ======================================
        // CALENDARIO
        // ======================================

        TarjetaRecurso(
            titulo = "Calendario académico",

            descripcion = "Consulta fechas, hitos y " +
                    "recordatorios de entrega.",

            textoBoton = "Ver calendario",
            onClick = onHitos
        )

        // ======================================
        // ACCESIBILIDAD
        // ======================================

        TarjetaRecurso(
            titulo = "Accesibilidad",

            descripcion = "Configura el modo de alto contraste " +
                    "para facilitar la lectura.",

            textoBoton = "Configurar accesibilidad",
            onClick = onAccesibilidad
        )

        // ======================================
        // BIOMETRIA
        // ======================================

        TarjetaRecurso(
            titulo = "Seguridad biométrica",

            descripcion = "Comprueba la disponibilidad " +
                    "de huella o rostro y prueba " +
                    "la autenticación de Android.",

            textoBoton = "Comprobar biometría",
            onClick = onBiometria
        )

        // ======================================
        // NUEVO: CAMARA ACADEMICA
        // ======================================

        TarjetaRecurso(
            titulo = "Cámara académica",

            descripcion = "Abre la cámara del dispositivo " +
                    "para capturar y visualizar " +
                    "evidencias fotográficas de prueba.",

            textoBoton = "Abrir cámara académica",
            onClick = onCamara
        )

        // ======================================
        // AVISO INSTITUCIONAL
        // ======================================

        Text(
            text = "Contenido orientativo del prototipo. " +
                    "Las novedades y rúbricas de demostración " +
                    "no constituyen información oficial de ESUCAR.",

            color = colores.textoSecundario,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }
}

// ======================================================
// TARJETA DE RECURSOS
// ======================================================

@Composable
private fun TarjetaRecurso(
    titulo: String,
    descripcion: String,
    textoBoton: String,
    onClick: () -> Unit,
    destacado: Boolean = false
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    Card(
        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = colores.tarjeta
        ),

        border = if (altoContraste) {
            BorderStroke(1.dp, colores.borde)
        } else {
            null
        },

        shape = RoundedCornerShape(18.dp),

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

            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = titulo,
                color = colores.texto,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = descripcion,
                color = colores.textoSecundario,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            if (destacado) {

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),

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
                    )
                ) {

                    Text(textoBoton)
                }

            } else {

                OutlinedButton(
                    onClick = onClick,
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

                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colores.texto
                    )
                ) {

                    Text(textoBoton)
                }
            }
        }
    }
}
