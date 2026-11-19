package cl.duoc.tutorvirtual.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.tutorvirtual.ui.theme.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tutorvirtual.R
import cl.duoc.tutorvirtual.data.EntregablesPreferences
import cl.duoc.tutorvirtual.data.EsucarRoomRepository
import cl.duoc.tutorvirtual.data.local.EntregableEntity
import cl.duoc.tutorvirtual.ui.screens.AccesibilidadScreen
import cl.duoc.tutorvirtual.ui.screens.ActualizacionesScreen
import cl.duoc.tutorvirtual.ui.screens.AdjuntarEvidenciaScreen
import cl.duoc.tutorvirtual.ui.screens.DiagnosticoBiometriaScreen
import cl.duoc.tutorvirtual.ui.screens.EntregablesScreen
import cl.duoc.tutorvirtual.ui.screens.EvidenciaCamaraScreen
import cl.duoc.tutorvirtual.ui.screens.FaqScreen
import cl.duoc.tutorvirtual.ui.screens.GuiaPasoScreen
import cl.duoc.tutorvirtual.ui.screens.HitosScreen
import cl.duoc.tutorvirtual.ui.screens.RegistroEntregableScreen
import cl.duoc.tutorvirtual.ui.screens.RubricasScreen
import cl.duoc.tutorvirtual.ui.screens.SelectorDocumentoScreen
import cl.duoc.tutorvirtual.ui.screens.UbicacionScreen
import cl.duoc.tutorvirtual.viewmodel.EtapasViewModel
import cl.duoc.tutorvirtual.viewmodel.ValidacionEntregable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private data class EtapaInfo(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val objetivo: String,
    val actividades: List<String>,
    val formato: String
)

private data class Avance(
    val etapaId: Int,
    val titulo: String,
    val descripcion: String,
    val estado: String,
    val fecha: String = ValidacionEntregable.FECHA_POR_DEFINIR
)

private val etapas = listOf(
    EtapaInfo(1, "Planteamiento", "Define el problema, delimita el fenómeno y formula la pregunta, los objetivos y la justificación.", "Establecer el problema que se investigará y definir los objetivos del trabajo.", listOf("Identificar y delimitar el problema de investigación.", "Formular la pregunta de investigación.", "Definir los objetivos y la justificación."), "Capítulo con antecedentes, descripción del problema, pregunta de investigación, objetivos general y específicos, justificación y delimitación."),
    EtapaInfo(2, "Marco Teórico", "Reúne antecedentes, conceptos y fundamentos a partir de fuentes confiables.", "Construir los fundamentos teóricos que respaldan el trabajo.", listOf("Buscar y seleccionar fuentes confiables.", "Identificar los conceptos importantes.", "Citar y referenciar en norma APA 7."), "Capítulo con antecedentes investigativos, bases teóricas y conceptos clave, con citas y referencias en APA 7."),
    EtapaInfo(3, "Metodología", "Describe el diseño, el enfoque y las técnicas con que se desarrollará la investigación.", "Definir el procedimiento y los métodos que se utilizarán en la investigación.", listOf("Definir el diseño, tipo y enfoque.", "Determinar la población o muestra.", "Seleccionar técnicas e instrumentos."), "Capítulo con diseño, tipo y enfoque, población y muestra, técnicas e instrumentos y resguardos éticos."),
    EtapaInfo(4, "Recogida de información", "Organiza el trabajo de campo y el registro de los datos necesarios.", "Obtener y organizar la información necesaria para desarrollar el trabajo.", listOf("Aplicar los instrumentos definidos.", "Registrar los datos recopilados.", "Resguardar los criterios éticos."), "Registro de las técnicas e instrumentos aplicados y de la información recopilada, con los consentimientos en anexos."),
    EtapaInfo(5, "Análisis de resultados", "Examina e interpreta los resultados en relación con los objetivos.", "Interpretar la información recopilada y examinar los resultados.", listOf("Revisar los datos recopilados.", "Relacionar los resultados con cada objetivo.", "Proponer alternativas de solución."), "Capítulo de análisis de resultados vinculado a cada objetivo, con tablas y figuras numeradas y comentadas.")
)

private enum class Seccion {
    INICIO, ETAPAS, DETALLE, FORMULARIO, PROGRESO, MAS,
    // Pantallas con funcionalidad propia
    GUIA, HITOS, RUBRICAS, PREGUNTAS, ENTREGABLES, REGISTRO, NOVEDADES,
    ACCESIBILIDAD, BIOMETRIA, CAMARA, DOCUMENTOS, ADJUNTAR, UBICACION
}

// Secciones que se agrupan bajo la pestaña "Más"
private val seccionesMas = listOf(
    Seccion.MAS, Seccion.HITOS, Seccion.RUBRICAS, Seccion.PREGUNTAS,
    Seccion.ENTREGABLES, Seccion.REGISTRO, Seccion.NOVEDADES,
    Seccion.ACCESIBILIDAD, Seccion.BIOMETRIA, Seccion.CAMARA,
    Seccion.DOCUMENTOS, Seccion.ADJUNTAR, Seccion.UBICACION
)

// Equivalencia entre el estado mostrado y el guardado en EntregablesPreferences
private fun estadoParaPantalla(estadoGuardado: String): String = when (estadoGuardado) {
    "En desarrollo" -> "En progreso"
    "Enviado", "Aprobado" -> "Completado"
    else -> "Por iniciar"
}

private fun estadoParaGuardar(estadoPantalla: String): String = when (estadoPantalla) {
    "En progreso" -> "En desarrollo"
    "Completado" -> "Enviado"
    else -> "Por iniciar"
}


@Composable
fun TutorVirtualApp(
    nombreEstudiante: String,
    cursoEstudiante: String,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Datos y persistencia
    val etapasViewModel: EtapasViewModel = viewModel()
    val entregablesPreferences = remember(context) { EntregablesPreferences(context.applicationContext) }
    val roomRepository = remember(context) { EsucarRoomRepository(context.applicationContext) }

    var seccion by rememberSaveable { mutableStateOf(Seccion.INICIO.name) }
    var etapaId by rememberSaveable { mutableIntStateOf(1) }

    // Estado de cada etapa
    val estados by remember(etapasViewModel) {
        combine(etapas.map { etapasViewModel.observarEstadoEtapa(it.id) }) { it.toList() }
    }.collectAsState(initial = etapas.map { "Por iniciar" })

    // Último avance registrado de cada etapa
    val entregables by remember(roomRepository) {
        combine(etapas.map { roomRepository.observarEntregables(it.id) }) { listas -> listas.map { it.lastOrNull() } }
    }.collectAsState(initial = etapas.map { null })

    val avances = etapas.mapIndexed { index, etapa ->
        val estadoGuardado = estados.getOrElse(index) { "Por iniciar" }
        val entregable = entregables.getOrNull(index)
        if (estadoGuardado == "Por iniciar" && entregable == null) null
        else Avance(etapa.id, entregable?.titulo.orEmpty(), entregable?.descripcion.orEmpty(), estadoParaPantalla(estadoGuardado))
    }.filterNotNull()

    val etapaActual = etapas.first { it.id == etapaId }
    val current = Seccion.valueOf(seccion)
    val completadas = etapas.count { etapa -> avances.any { it.etapaId == etapa.id && it.estado == "Completado" } }

    // Botón Atrás del dispositivo
    BackHandler(enabled = current != Seccion.INICIO) {
        seccion = when (current) {
            Seccion.GUIA -> Seccion.DETALLE
            Seccion.DETALLE -> Seccion.ETAPAS
            Seccion.FORMULARIO -> Seccion.DETALLE
            Seccion.REGISTRO -> Seccion.ENTREGABLES
            in seccionesMas -> if (current == Seccion.MAS) Seccion.INICIO else Seccion.MAS
            else -> Seccion.INICIO
        }.name
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MarfilESUCAR,
        topBar = {
            // Datos del estudiante y cierre de sesión
            if (current == Seccion.INICIO) {
                Surface(color = Blanco, shadowElevation = 2.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(nombreEstudiante, color = GrisOscuroESUCAR, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(cursoEstudiante, color = GrisTexto, fontSize = 11.sp)
                        }
                        TextButton(onClick = onCerrarSesion) {
                            Text("Cerrar sesión", color = VerdeESUCAR, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (current == Seccion.ENTREGABLES) {
                FloatingActionButton(
                    onClick = { seccion = Seccion.REGISTRO.name },
                    containerColor = VerdeESUCAR,
                    contentColor = Blanco,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("+ Nuevo entregable", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
                }
            }
        },
        bottomBar = {
            if (current == Seccion.INICIO || current == Seccion.ETAPAS || current == Seccion.PROGRESO || current in seccionesMas) {
                NavigationBar(containerColor = VerdeESUCAR, contentColor = Blanco) {
                    val tabs = listOf(
                        Triple("Inicio", Icons.Default.Home, Seccion.INICIO),
                        Triple("Etapas", Icons.Default.Layers, Seccion.ETAPAS),
                        Triple("Progreso", Icons.Default.BarChart, Seccion.PROGRESO),
                        Triple("Más", Icons.Default.MoreHoriz, Seccion.MAS)
                    )
                    tabs.forEach { (label, icon, destination) ->
                        NavigationBarItem(
                            selected = current == destination || (label == "Más" && current in seccionesMas),
                            onClick = { seccion = destination.name },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DoradoESUCAR,
                                selectedTextColor = Blanco,
                                indicatorColor = GrisOscuroESUCAR,
                                unselectedIconColor = Blanco,
                                unselectedTextColor = Blanco
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            val pantallaPropia = Modifier.fillMaxSize()
            val volverMas = { seccion = Seccion.MAS.name }
            AnimatedContent(
                targetState = current,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                label = "transicion_secciones"
            ) { destino ->
            when (destino) {
                Seccion.INICIO -> InicioContent(
                    completadas = completadas,
                    onNavigate = { seccion = it.name }
                )
                Seccion.ETAPAS -> ListadoContent(
                    avances = avances,
                    onBack = { seccion = Seccion.INICIO.name },
                    onSelect = { etapaId = it; seccion = Seccion.DETALLE.name }
                )
                Seccion.DETALLE -> DetalleContent(
                    etapa = etapaActual,
                    avance = avances.lastOrNull { it.etapaId == etapaId },
                    onBack = { seccion = Seccion.ETAPAS.name },
                    onRegister = { seccion = Seccion.FORMULARIO.name },
                    onGuia = { seccion = Seccion.GUIA.name }
                )
                Seccion.FORMULARIO -> FormularioContent(
                    etapa = etapaActual,
                    onBack = { seccion = Seccion.DETALLE.name },
                    onSave = { avance ->
                        val idEtapa = etapaId
                        val estadoGuardado = estadoParaGuardar(avance.estado)
                        val existente = entregables.getOrNull(etapas.indexOfFirst { it.id == idEtapa })
                        scope.launch {
                            // Se guarda el estado (DataStore) y el avance (Room)
                            entregablesPreferences.guardarEstado(idEtapa, estadoGuardado)
                            try {
                                if (existente != null) {
                                    // Si no se escribió fecha se conserva la que ya tenía
                                    val fechaFinal = if (avance.fecha == ValidacionEntregable.FECHA_POR_DEFINIR) existente.fechaEntrega else avance.fecha
                                    roomRepository.actualizarEntregable(
                                        existente.copy(titulo = avance.titulo, descripcion = avance.descripcion, fechaEntrega = fechaFinal, estado = estadoGuardado)
                                    )
                                } else {
                                    roomRepository.guardarEntregable(
                                        EntregableEntity(
                                            etapaId = idEtapa,
                                            titulo = avance.titulo,
                                            descripcion = avance.descripcion,
                                            fechaEntrega = avance.fecha,
                                            estado = estadoGuardado
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("ESUCAR_DATABASE", "No se pudo guardar el avance: " + e.message)
                            }
                        }
                        seccion = Seccion.DETALLE.name
                    }
                )
                Seccion.PROGRESO -> ProgresoContent(avances, completadas)
                Seccion.MAS -> MasContent(
                    onHitos = { seccion = Seccion.HITOS.name },
                    onRubricas = { seccion = Seccion.RUBRICAS.name },
                    onPreguntas = { seccion = Seccion.PREGUNTAS.name },
                    onEntregables = { seccion = Seccion.ENTREGABLES.name },
                    onNovedades = { seccion = Seccion.NOVEDADES.name },
                    onAccesibilidad = { seccion = Seccion.ACCESIBILIDAD.name },
                    onBiometria = { seccion = Seccion.BIOMETRIA.name },
                    onCamara = { seccion = Seccion.CAMARA.name },
                    onDocumentos = { seccion = Seccion.DOCUMENTOS.name },
                    onAdjuntar = { seccion = Seccion.ADJUNTAR.name },
                    onUbicacion = { seccion = Seccion.UBICACION.name }
                )
                // Pantallas con su propio diseño
                Seccion.GUIA -> GuiaPasoScreen(etapaId = etapaId, modifier = pantallaPropia, onVolver = { seccion = Seccion.DETALLE.name })
                Seccion.HITOS -> HitosScreen(modifier = pantallaPropia, onVolver = volverMas)
                Seccion.RUBRICAS -> RubricasScreen(modifier = pantallaPropia, onVolver = volverMas)
                Seccion.PREGUNTAS -> FaqScreen(modifier = pantallaPropia, onVolver = volverMas)
                Seccion.ENTREGABLES -> EntregablesScreen(modifier = pantallaPropia)
                Seccion.REGISTRO -> RegistroEntregableScreen(
                    modifier = pantallaPropia,
                    onVolver = { seccion = Seccion.ENTREGABLES.name },
                    onGuardado = { seccion = Seccion.ENTREGABLES.name }
                )
                Seccion.NOVEDADES -> ActualizacionesScreen(modifier = pantallaPropia, onVolver = volverMas)
                Seccion.ACCESIBILIDAD -> AccesibilidadScreen(modifier = pantallaPropia, onVolver = volverMas)
                Seccion.BIOMETRIA -> DiagnosticoBiometriaScreen(modifier = pantallaPropia, onVolver = volverMas)
                Seccion.CAMARA -> EvidenciaCamaraScreen(modifier = pantallaPropia, onVolver = volverMas)
                Seccion.DOCUMENTOS -> SelectorDocumentoScreen(modifier = pantallaPropia, onVolver = volverMas)
                Seccion.ADJUNTAR -> AdjuntarEvidenciaScreen(modifier = pantallaPropia, onVolver = volverMas)
                Seccion.UBICACION -> UbicacionScreen(modifier = pantallaPropia, onVolver = volverMas)
            }
            }
        }
    }
}

@Composable
private fun Encabezado(titulo: String, subtitulo: String? = null, onBack: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().background(VerdeESUCAR).padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver a la pantalla anterior", tint = Blanco)
            }
        } else {
            Icon(Icons.Default.School, contentDescription = "Identidad institucional ESUCAR", tint = DoradoESUCAR, modifier = Modifier.size(42.dp).padding(4.dp))
        }
        Column(Modifier.padding(start = 8.dp)) {
            Text(titulo, color = Blanco, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            if (subtitulo != null) Text(subtitulo, color = Blanco, fontSize = 12.sp)
        }
    }
}

@Composable
private fun InicioContent(completadas: Int, onNavigate: (Seccion) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MarfilESUCAR)
    ) {
        // Encabezado institucional
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
            colors = CardDefaults.cardColors(containerColor = VerdeESUCAR),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_esucar),
                    contentDescription = "Identidad institucional ESUCAR",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text("ESUCAR", color = Blanco, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Text("Escuela de Suboficiales", color = Blanco, fontSize = 13.sp)
                    Text("Carabineros de Chile", color = Blanco.copy(alpha = 0.85f), fontSize = 11.sp)
                }
            }
        }

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = VerdeESUCAR,
                    modifier = Modifier.size(43.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Tutor Virtual", color = VerdeESUCAR, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Text("Trabajo de Título I - III", color = GrisOscuroESUCAR, fontSize = 13.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("¡Hola, Estudiante!", color = VerdeESUCAR, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Gestiona tu trabajo de título, avanza por etapas y resuelve tus dudas aquí.",
                    color = GrisOscuroESUCAR,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = VerdeESUCAR)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mi progreso", color = GrisOscuroESUCAR, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Text("$completadas de 5 etapas", color = GrisOscuroESUCAR, fontSize = 11.sp)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                            .clip(CircleShape)
                            .background(VerdeClaro)
                            .semantics {
                                contentDescription = "Progreso del trabajo de título: ${completadas * 20} por ciento"
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(completadas / 5f)
                                .fillMaxHeight()
                                .background(DoradoESUCAR)
                        )
                    }

                    Text(
                        "${completadas * 20}%",
                        color = VerdeESUCAR,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Text("¿Qué necesitas hacer?", color = GrisOscuroESUCAR, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AccesoOriginal("Etapas\nde la Tesis", Icons.Default.Layers, Modifier.weight(1f)) {
                    onNavigate(Seccion.ETAPAS)
                }
                AccesoOriginal("Preguntas\nFrecuentes", Icons.Default.Help, Modifier.weight(1f)) {
                    onNavigate(Seccion.PREGUNTAS)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AccesoOriginal("Hitos y\nEntregas", Icons.Default.CalendarMonth, Modifier.weight(1f)) {
                    onNavigate(Seccion.HITOS)
                }
                AccesoOriginal("Pautas y\nRúbricas", Icons.Default.MenuBook, Modifier.weight(1f)) {
                    onNavigate(Seccion.RUBRICAS)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_gobierno_chile),
                    contentDescription = "Gobierno de Chile",
                    modifier = Modifier.size(30.dp).alpha(0.75f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gobierno de Chile", color = GrisTexto, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun AccesoOriginal(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(112.dp)
            .clickable(onClick = onClick)
            .semantics { contentDescription = titulo.replace("\n", " ") },
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = VerdeClaro),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icono, contentDescription = null, tint = VerdeESUCAR, modifier = Modifier.size(31.dp))
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = titulo,
                color = GrisOscuroESUCAR,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun ListadoContent(avances: List<Avance>, onBack: () -> Unit, onSelect: (Int) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Encabezado("Etapas de la Tesis", "Guía metodológica", onBack)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Conoce las etapas que debes desarrollar durante tu trabajo de título.", color = GrisTexto)
            etapas.forEach { etapa ->
                val estado = avances.lastOrNull { it.etapaId == etapa.id }?.estado ?: "Por iniciar"
                Card(
                    Modifier.fillMaxWidth().clickable { onSelect(etapa.id) },
                    colors = CardDefaults.cardColors(containerColor = Blanco),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = VerdeClaro) {
                            Box(contentAlignment = Alignment.Center) { Text("${etapa.id}", color = VerdeESUCAR, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(etapa.nombre, color = GrisOscuroESUCAR, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(etapa.descripcion, color = GrisTexto, fontSize = 13.sp, lineHeight = 18.sp)
                            Text(estado, color = VerdeESUCAR, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Abrir ${etapa.nombre}", tint = DoradoESUCAR)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetalleContent(etapa: EtapaInfo, avance: Avance?, onBack: () -> Unit, onRegister: () -> Unit, onGuia: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Encabezado("Detalle de etapa", "Etapa ${etapa.id} de 5", onBack)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(etapa.nombre, color = VerdeESUCAR, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            SeccionCard("Objetivo", etapa.objetivo)
            SeccionCard("Descripción", etapa.descripcion)
            Card(colors = CardDefaults.cardColors(containerColor = Blanco), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("¿Qué debes desarrollar?", color = VerdeESUCAR, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    etapa.actividades.forEach { actividad ->
                        Text("•  $actividad", color = GrisOscuroESUCAR, lineHeight = 20.sp)
                    }
                }
            }
            SeccionCard("Información de entrega", "Formato requerido: ${etapa.formato}\nFecha límite: según calendario académico\nEstado: ${avance?.estado ?: "Por iniciar"}")
            if (avance != null && avance.titulo.isNotBlank()) SeccionCard("Último avance registrado", "${avance.titulo}\n\n${avance.descripcion}\n\nEstado: ${avance.estado}")
            Button(onClick = onRegister, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), colors = ButtonDefaults.buttonColors(containerColor = VerdeESUCAR)) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (avance == null || avance.titulo.isBlank()) "Registrar avance" else "Actualizar avance")
            }
            OutlinedButton(onClick = onGuia, modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)) {
                Text("Guía paso a paso")
            }
        }
    }
}

@Composable
private fun SeccionCard(titulo: String, contenido: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Blanco), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(titulo, color = VerdeESUCAR, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(contenido, color = GrisOscuroESUCAR, lineHeight = 21.sp)
        }
    }
}

@Composable
private fun FormularioContent(etapa: EtapaInfo, onBack: () -> Unit, onSave: (Avance) -> Unit) {
    var titulo by rememberSaveable(etapa.id) { mutableStateOf("") }
    var descripcion by rememberSaveable(etapa.id) { mutableStateOf("") }
    var estado by rememberSaveable(etapa.id) { mutableStateOf("En progreso") }
    var fecha by rememberSaveable(etapa.id) { mutableStateOf("") }
    var intentoGuardar by rememberSaveable(etapa.id) { mutableStateOf(false) }

    // Las reglas están en ValidacionEntregable, aquí solo se muestran los mensajes
    val mensajeTitulo = ValidacionEntregable.errorTitulo(titulo)
    val mensajeDescripcion = ValidacionEntregable.errorDescripcion(descripcion)
    val mensajeFecha = ValidacionEntregable.errorFecha(fecha, obligatoria = false)
    val tituloError = intentoGuardar && mensajeTitulo != null
    val descripcionError = intentoGuardar && mensajeDescripcion != null
    val fechaError = intentoGuardar && mensajeFecha != null

    Column(Modifier.fillMaxSize()) {
        Encabezado("Registrar avance", etapa.nombre, onBack)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Completa los datos del trabajo realizado. Los campos marcados son obligatorios.", color = GrisTexto)
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre del avance *") },
                placeholder = { Text("Ej. Definición del problema") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                trailingIcon = { IconoEstadoCampo(hayError = tituloError, esCorrecto = mensajeTitulo == null) },
                isError = tituloError,
                supportingText = { if (tituloError) Text(mensajeTitulo.orEmpty()) else Text("Escribe un título breve y claro.") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences)
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 130.dp),
                label = { Text("Descripción del avance *") },
                placeholder = { Text("Describe qué has desarrollado...") },
                minLines = 4,
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                trailingIcon = { IconoEstadoCampo(hayError = descripcionError, esCorrecto = mensajeDescripcion == null) },
                isError = descripcionError,
                supportingText = { if (descripcionError) Text(mensajeDescripcion.orEmpty()) else Text("${descripcion.length} caracteres") }
            )
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Fecha de entrega (opcional)") },
                placeholder = { Text("AAAA-MM-DD") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                trailingIcon = { IconoEstadoCampo(hayError = fechaError, esCorrecto = fecha.isNotBlank() && mensajeFecha == null) },
                isError = fechaError,
                supportingText = { if (fechaError) Text(mensajeFecha.orEmpty()) else Text("Ejemplo: 2026-12-10") }
            )
            Text("Estado del avance *", color = GrisOscuroESUCAR, fontWeight = FontWeight.SemiBold)
            listOf("Por iniciar", "En progreso", "Completado").forEach { opcion ->
                Row(
                    Modifier.fillMaxWidth().clickable { estado = opcion }.semantics { contentDescription = "Estado $opcion" }.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = estado == opcion, onClick = { estado = opcion })
                    Text(opcion, color = GrisOscuroESUCAR)
                }
            }
            AnimatedVisibility(visible = tituloError || descripcionError || fechaError) {
                Text("Revisa los campos indicados antes de guardar.", color = RojoError, fontWeight = FontWeight.Medium)
            }
            Button(
                onClick = {
                    intentoGuardar = true
                    if (mensajeTitulo == null && mensajeDescripcion == null && mensajeFecha == null) {
                        val fechaGuardar = fecha.trim().ifEmpty { ValidacionEntregable.FECHA_POR_DEFINIR }
                        onSave(Avance(etapa.id, titulo.trim(), descripcion.trim(), estado, fechaGuardar))
                    }
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeESUCAR)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar avance")
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)) {
                Text("Cancelar")
            }
            Text("Los avances quedan guardados en el dispositivo, así que puedes cerrar la aplicación y seguir después.", color = GrisTexto, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ProgresoContent(avances: List<Avance>, completadas: Int) {
    val progresoAnimado by animateFloatAsState(
        targetValue = completadas / 5f,
        animationSpec = tween(600),
        label = "progreso_general"
    )
    Column(Modifier.fillMaxSize()) {
        Encabezado("Mi progreso", "Resumen de trabajo de título")
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("$completadas de 5 etapas completadas", color = VerdeESUCAR, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            LinearProgressIndicator(progress = { progresoAnimado }, modifier = Modifier.fillMaxWidth().height(12.dp), color = DoradoESUCAR, trackColor = VerdeClaro)
            Text("${completadas * 20}% de avance", color = GrisTexto)
            etapas.forEach { etapa ->
                val avance = avances.lastOrNull { it.etapaId == etapa.id }
                SeccionCard(etapa.nombre, "Estado: ${avance?.estado ?: "Por iniciar"}${if (avance != null && avance.titulo.isNotBlank()) "\nAvance: ${avance.titulo}" else ""}")
            }
        }
    }
}

@Composable
private fun MasContent(
    onHitos: () -> Unit,
    onRubricas: () -> Unit,
    onPreguntas: () -> Unit,
    onEntregables: () -> Unit,
    onNovedades: () -> Unit,
    onAccesibilidad: () -> Unit,
    onBiometria: () -> Unit,
    onCamara: () -> Unit,
    onDocumentos: () -> Unit,
    onAdjuntar: () -> Unit,
    onUbicacion: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Encabezado("Recursos de apoyo", "Información para el estudiante")
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Acceso("Hitos y\nentregas", Icons.Default.CalendarMonth, Modifier.weight(1f)) { onHitos() }
                Acceso("Pautas y\nrúbricas", Icons.Default.MenuBook, Modifier.weight(1f)) { onRubricas() }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Acceso("Preguntas\nfrecuentes", Icons.Default.Help, Modifier.weight(1f)) { onPreguntas() }
                Acceso("Novedades\nacadémicas", Icons.Default.School, Modifier.weight(1f)) { onNovedades() }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Acceso("Mis\nentregables", Icons.Default.Layers, Modifier.weight(1f)) { onEntregables() }
                Acceso("Adjuntar\nevidencia", Icons.Default.AttachFile, Modifier.weight(1f)) { onAdjuntar() }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Acceso("Cámara\nacadémica", Icons.Default.PhotoCamera, Modifier.weight(1f)) { onCamara() }
                Acceso("Documentos\nPDF", Icons.Default.PictureAsPdf, Modifier.weight(1f)) { onDocumentos() }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Acceso("Ubicación\ntrabajo de campo", Icons.Default.LocationOn, Modifier.weight(1f)) { onUbicacion() }
                Acceso("Seguridad\nbiométrica", Icons.Default.Fingerprint, Modifier.weight(1f)) { onBiometria() }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Acceso("Accesibilidad", Icons.Default.Visibility, Modifier.weight(1f)) { onAccesibilidad() }
                Spacer(Modifier.weight(1f))
            }
            SeccionCard("Ayuda", "Si tienes dudas sobre fechas, formatos o criterios de evaluación, consulta las instrucciones oficiales de tu docente.")
        }
    }
}

@Composable
private fun Acceso(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(112.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = VerdeClaro
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = VerdeESUCAR,
                modifier = Modifier.size(31.dp)
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = titulo,
                color = GrisOscuroESUCAR,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}