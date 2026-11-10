package cl.duoc.tutorvirtual.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

import cl.duoc.tutorvirtual.ui.screens.HomeScreen
import cl.duoc.tutorvirtual.ui.screens.EtapasScreen
import cl.duoc.tutorvirtual.ui.screens.DetalleEtapaScreen
import cl.duoc.tutorvirtual.ui.screens.GuiaPasoScreen
import cl.duoc.tutorvirtual.ui.screens.FaqScreen
import cl.duoc.tutorvirtual.ui.screens.EntregablesScreen
import cl.duoc.tutorvirtual.ui.screens.HitosScreen
import cl.duoc.tutorvirtual.ui.screens.RubricasScreen
import cl.duoc.tutorvirtual.ui.screens.ActualizacionesScreen
import cl.duoc.tutorvirtual.ui.screens.AccesibilidadScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {

    // Colores compartidos de la aplicación
    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    // Pantalla actual
    var pantallaActual by rememberSaveable {
        mutableStateOf("Inicio")
    }

    // Etapa metodológica seleccionada
    var etapaSeleccionada by rememberSaveable {
        mutableIntStateOf(1)
    }

    // Recordar desde dónde abrimos estas pantallas
    var origenFaq by rememberSaveable {
        mutableStateOf("Inicio")
    }

    var origenHitos by rememberSaveable {
        mutableStateOf("Inicio")
    }

    // Botón Atrás del dispositivo
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

            else -> "Inicio"
        }
    }

    Scaffold(
        modifier = modifier,

        containerColor = colores.fondo,

        contentColor = colores.texto,

        // BARRA INFERIOR DE NAVEGACIÓN
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

                        "Recursos" -> pantallaActual in listOf(
                            "Recursos",
                            "Rubricas",
                            "Faq",
                            "Hitos",
                            "Actualizaciones",
                            "Accesibilidad"
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

                            selectedIconColor = if (altoContraste) {
                                Color.Black
                            } else {
                                colores.texto
                            },

                            selectedTextColor = colores.texto,

                            unselectedIconColor = if (altoContraste) {
                                Color.White
                            } else {
                                Color.DarkGray
                            },

                            unselectedTextColor = if (altoContraste) {
                                Color.White
                            } else {
                                Color.DarkGray
                            },

                            indicatorColor = if (altoContraste) {
                                Color.Yellow
                            } else {
                                colores.acento.copy(alpha = 0.25f)
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

            // PANTALLA PRINCIPAL
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

            // LISTADO DE ETAPAS
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

            // DETALLE DE ETAPA
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

            // GUÍA PASO A PASO
            "GuiaPaso" -> {

                GuiaPasoScreen(
                    etapaId = etapaSeleccionada,

                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "DetalleEtapa"
                    }
                )
            }

            // PREGUNTAS FRECUENTES
            "Faq" -> {

                FaqScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = origenFaq
                    }
                )
            }

            // ENTREGABLES Y ESTADOS
            "Entregables" -> {

                EntregablesScreen(
                    modifier = contenidoModifier
                )
            }

            // CALENDARIO Y RECORDATORIOS
            "Hitos" -> {

                HitosScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = origenHitos
                    }
                )
            }

            // RÚBRICAS ACADÉMICAS
            "Rubricas" -> {

                RubricasScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "Recursos"
                    }
                )
            }

            // NOVEDADES ACADÉMICAS
            "Actualizaciones" -> {

                ActualizacionesScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "Recursos"
                    }
                )
            }

            // ACCESIBILIDAD
            "Accesibilidad" -> {

                AccesibilidadScreen(
                    modifier = contenidoModifier,

                    onVolver = {
                        pantallaActual = "Recursos"
                    }
                )
            }

            // MENÚ RECURSOS
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
                    }
                )
            }

            // PANTALLAS PENDIENTES
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

                            Text("Volver al inicio")
                        }
                    }
                }
            }
        }
    }
}


// ======================================================
// PANTALLA DE RECURSOS ACADÉMICOS
// ======================================================

@Composable
private fun RecursosScreen(
    modifier: Modifier = Modifier,

    onRubricas: () -> Unit,

    onFaq: () -> Unit,

    onHitos: () -> Unit,

    onActualizaciones: () -> Unit,

    onAccesibilidad: () -> Unit
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

        // ENCABEZADO
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

        // TÍTULO
        Text(
            text = "Herramientas disponibles",

            color = colores.texto,

            fontSize = 20.sp,

            fontWeight = FontWeight.Bold
        )

        // NOVEDADES ACADÉMICAS
        TarjetaRecurso(
            titulo = "Novedades académicas",

            descripcion = "Consulta contenido actualizado " +
                    "desde internet y disponible sin conexión.",

            textoBoton = "Ver novedades",

            onClick = onActualizaciones,

            destacado = true
        )

        // RÚBRICAS
        TarjetaRecurso(
            titulo = "Rúbricas de evaluación",

            descripcion = "Consulta criterios orientativos " +
                    "de las cinco etapas académicas.",

            textoBoton = "Ver rúbricas",

            onClick = onRubricas
        )

        // PREGUNTAS FRECUENTES
        TarjetaRecurso(
            titulo = "Preguntas frecuentes",

            descripcion = "Busca respuestas sobre citación, " +
                    "metodología y formato.",

            textoBoton = "Ver preguntas frecuentes",

            onClick = onFaq
        )

        // CALENDARIO
        TarjetaRecurso(
            titulo = "Calendario académico",

            descripcion = "Consulta fechas, hitos y " +
                    "recordatorios de entrega.",

            textoBoton = "Ver calendario",

            onClick = onHitos
        )

        // ACCESIBILIDAD
        TarjetaRecurso(
            titulo = "Accesibilidad",

            descripcion = "Configura el modo de alto contraste " +
                    "para facilitar la lectura.",

            textoBoton = "Configurar accesibilidad",

            onClick = onAccesibilidad
        )

        // ADVERTENCIA DEL PROTOTIPO
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
// TARJETA REUTILIZABLE PARA RECURSOS
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

                // BOTÓN PRINCIPAL
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

                // BOTONES SECUNDARIOS
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
