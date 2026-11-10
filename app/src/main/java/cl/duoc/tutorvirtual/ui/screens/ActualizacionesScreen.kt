package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cl.duoc.tutorvirtual.data.ActualizacionesPreferences
import cl.duoc.tutorvirtual.data.ActualizacionesRepository
import cl.duoc.tutorvirtual.model.ActualizacionAcademica
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val URL_ACTUALIZACIONES =
    "https://raw.githubusercontent.com/" +
            "felipegonzalez1983/" +
            "TutorVirtualESUCAR_PlanB/" +
            "refs/heads/main/" +
            "contenido/actualizaciones.json"

// Formatear la fecha de la última sincronización
private fun formatearSincronizacion(
    fecha: Long?
): String {

    if (fecha == null || fecha <= 0L) {
        return "Todavía no se ha sincronizado"
    }

    return SimpleDateFormat(
        "dd/MM/yyyy 'a las' HH:mm",
        Locale("es", "CL")
    ).format(Date(fecha))
}

@Composable
fun ActualizacionesScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit = {}
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    val context = LocalContext.current

    val repository = remember(context) {
        ActualizacionesRepository(
            context.applicationContext
        )
    }

    val preferences = remember(context) {
        ActualizacionesPreferences(
            context.applicationContext
        )
    }

    // Información que se mostrará en pantalla
    var actualizaciones by remember {
        mutableStateOf<List<ActualizacionAcademica>>(
            emptyList()
        )
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var mensaje by remember {
        mutableStateOf(
            "Preparando novedades académicas..."
        )
    }

    var ultimaSincronizacion by remember {
        mutableStateOf<Long?>(null)
    }

    // null = todavía no sabemos si hubo conexión
    var desdeInternet by remember {
        mutableStateOf<Boolean?>(null)
    }

    // Permite repetir la sincronización
    var recarga by rememberSaveable {
        mutableIntStateOf(0)
    }

    // Recuperar copia local y sincronizar con GitHub
    LaunchedEffect(repository, recarga) {

        cargando = true

        // En la primera carga, mostrar datos guardados
        if (recarga == 0) {

            try {

                actualizaciones = withContext(
                    Dispatchers.IO
                ) {
                    repository.obtenerGuardadas()
                }

                ultimaSincronizacion = withContext(
                    Dispatchers.IO
                ) {
                    preferences
                        .obtenerUltimaSincronizacion()
                        .first()
                }

                if (actualizaciones.isNotEmpty()) {
                    mensaje =
                        "Copia local disponible. " +
                                "Buscando novedades..."
                }

            } catch (e: Exception) {

                mensaje =
                    "No fue posible leer la copia local."
            }
        }

        // Intentar descargar la versión más reciente
        try {

            val resultado = withContext(
                Dispatchers.IO
            ) {
                repository.sincronizar(
                    URL_ACTUALIZACIONES
                )
            }

            actualizaciones =
                resultado.actualizaciones

            ultimaSincronizacion =
                resultado.ultimaSincronizacion

            desdeInternet =
                resultado.desdeInternet

            mensaje =
                resultado.mensaje

        } catch (e: Exception) {

            desdeInternet = false

            mensaje =
                "No se pudo conectar. " +
                        "Se muestran las novedades guardadas, " +
                        "si están disponibles."
        }

        cargando = false
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo),

        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 20.dp,
            bottom = 30.dp
        ),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        // VOLVER A RECURSOS
        item {

            TextButton(
                onClick = onVolver
            ) {

                Text(
                    text = "← Volver a recursos",
                    color = colores.texto
                )
            }
        }

        // ENCABEZADO
        item {

            Surface(
                modifier = Modifier.fillMaxWidth(),

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
                        text = "ESUCAR · INFORMACIÓN",
                        color = colores.acento,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Novedades académicas",
                        color = colores.textoEncabezado,
                        fontSize = 27.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Consulta novedades " +
                                "y conserva una copia " +
                                "para leer sin conexión.",
                        color = colores.textoEncabezado,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // ESTADO DE CONEXIÓN
        item {

            Surface(
                modifier = Modifier.fillMaxWidth(),

                color = colores.tarjeta,

                shape = RoundedCornerShape(18.dp),

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
                    modifier = Modifier.padding(18.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Estado de actualización",
                        color = colores.texto,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = when {

                            cargando ->
                                "Sincronizando..."

                            desdeInternet == true ->
                                "Actualización descargada desde internet"

                            desdeInternet == false ->
                                "Modo sin conexión o descarga no disponible"

                            else ->
                                "Comprobando información"
                        },

                        color = if (altoContraste) {
                            Color.Yellow
                        } else {
                            colores.texto
                        },

                        fontSize = 14.sp,

                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = mensaje,

                        color = colores.textoSecundario,

                        fontSize = 14.sp,

                        lineHeight = 21.sp
                    )

                    HorizontalDivider(
                        color = colores.borde
                    )

                    Text(
                        text = "Última sincronización exitosa",

                        color = colores.texto,

                        fontSize = 14.sp,

                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = formatearSincronizacion(
                            ultimaSincronizacion
                        ),

                        color = colores.textoSecundario,

                        fontSize = 14.sp
                    )
                }
            }
        }

        // BOTÓN PARA SINCRONIZAR NUEVAMENTE
        item {

            Button(
                onClick = {
                    recarga++
                },

                enabled = !cargando,

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
                )
            ) {

                if (cargando) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = if (altoContraste) {
                            Color.Black
                        } else {
                            Color.White
                        }
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text("Sincronizando...")

                } else {

                    Text(
                        text = "Actualizar novedades",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // TÍTULO DEL LISTADO
        item {

            Text(
                text = "Información disponible",

                color = colores.texto,

                fontSize = 20.sp,

                fontWeight = FontWeight.Bold
            )
        }

        // NOVEDADES
        if (actualizaciones.isEmpty()) {

            item {

                Surface(
                    modifier = Modifier.fillMaxWidth(),

                    color = colores.tarjeta,

                    shape = RoundedCornerShape(18.dp),

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
                        text = if (cargando) {
                            "Cargando información académica..."
                        } else {
                            "Todavía no hay novedades disponibles. " +
                                    "Conéctate a internet y presiona " +
                                    "Actualizar novedades."
                        },

                        modifier = Modifier.padding(20.dp),

                        color = colores.textoSecundario,

                        fontSize = 14.sp,

                        lineHeight = 22.sp
                    )
                }
            }

        } else {

            items(
                items = actualizaciones,
                key = { it.id }
            ) { actualizacion ->

                TarjetaActualizacionAccesible(
                    actualizacion = actualizacion
                )
            }
        }

        // ADVERTENCIA SOBRE EL CONTENIDO
        item {

            Text(
                text = "Este módulo utiliza contenido " +
                        "de demostración alojado en GitHub. " +
                        "Las novedades no representan " +
                        "comunicaciones oficiales de ESUCAR. " +
                        "Confirma siempre los avisos " +
                        "académicos con tu institución.",

                color = colores.textoSecundario,

                fontSize = 12.sp,

                lineHeight = 20.sp
            )
        }
    }
}

// ==================================================
// TARJETA DE NOVEDAD ACADÉMICA
// ==================================================

@Composable
private fun TarjetaActualizacionAccesible(
    actualizacion: ActualizacionAcademica
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

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
            defaultElevation =
                if (altoContraste) 0.dp else 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "NOVEDAD ACADÉMICA",

                color = if (altoContraste) {
                    Color.Yellow
                } else {
                    Color(0xFF896A2C)
                },

                fontSize = 12.sp,

                fontWeight = FontWeight.Bold
            )

            Text(
                text = actualizacion.titulo,

                color = colores.texto,

                fontSize = 18.sp,

                lineHeight = 25.sp,

                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(
                color = colores.borde
            )

            Text(
                text = actualizacion.descripcion,

                color = colores.textoSecundario,

                fontSize = 14.sp,

                lineHeight = 22.sp
            )

            Text(
                text = "Publicado: " +
                        actualizacion.fechaPublicacion,

                color = colores.texto,

                fontSize = 12.sp,

                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Fuente: " +
                        actualizacion.fuente,

                color = colores.textoSecundario,

                fontSize = 12.sp
            )
        }
    }
}
