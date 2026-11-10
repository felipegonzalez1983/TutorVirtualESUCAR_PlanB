package cl.duoc.tutorvirtual.ui.screens

import android.app.DatePickerDialog
import android.content.Context

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

import cl.duoc.tutorvirtual.data.HitosPreferences
import cl.duoc.tutorvirtual.data.HitosRepository
import cl.duoc.tutorvirtual.model.Hito
import cl.duoc.tutorvirtual.notifications.NotificacionesHelper
import cl.duoc.tutorvirtual.notifications.RecordatoriosScheduler
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

private data class HitoConEstado(
    val hito: Hito,
    val completado: Boolean,
    val fecha: String?,
    val recordatoriosActivos: Boolean
)

// Convierte fechas con formato yyyy-MM-dd
private fun convertirFechaCalendario(
    fecha: String?
): Long? {

    if (fecha.isNullOrBlank()) return null

    return try {
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.ROOT
        ).apply {
            isLenient = false
        }.parse(fecha)?.time

    } catch (e: Exception) {
        null
    }
}

// Fecha efectiva de un hito
private fun fechaDelHito(
    item: HitoConEstado
): String? {

    if (convertirFechaCalendario(item.fecha) != null) {
        return item.fecha
    }

    return item.hito.fechaLimite.takeIf {
        convertirFechaCalendario(it) != null
    }
}

// Días que quedan para el vencimiento
private fun diasRestantesCalendario(
    fecha: String?
): Int? {

    val fechaMillis =
        convertirFechaCalendario(fecha) ?: return null

    val destino = Calendar.getInstance().apply {
        timeInMillis = fechaMillis
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val hoy = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return (
            (destino.timeInMillis - hoy.timeInMillis)
                .toDouble() / 86_400_000.0
            ).roundToInt()
}

// Reprograma o cancela las alarmas de un hito
private fun actualizarAlarmasCalendario(
    context: Context,
    hito: Hito,
    fecha: String?,
    completado: Boolean,
    recordatoriosActivos: Boolean
) {

    RecordatoriosScheduler.cancelarRecordatorios(
        context,
        hito.id
    )

    if (
        !completado &&
        recordatoriosActivos &&
        convertirFechaCalendario(fecha) != null
    ) {

        RecordatoriosScheduler.programarRecordatorios(
            context,
            hito.id,
            hito.titulo,
            fecha!!
        )
    }
}

@Composable
fun HitosScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit = {}
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val preferences = remember(context) {
        HitosPreferences(context.applicationContext)
    }

    val hitos = remember {
        HitosRepository.obtenerHitos()
    }

    // Leer casillas, fechas y recordatorios desde DataStore
    val flujoHitos = remember(preferences, hitos) {

        val flujos: List<Flow<HitoConEstado>> =
            hitos.map { hito ->

                combine(
                    preferences.obtenerCompletado(hito.id),
                    preferences.obtenerFecha(hito.id),
                    preferences.obtenerRecordatoriosActivos(hito.id)
                ) { completado, fecha, recordatorios ->

                    HitoConEstado(
                        hito = hito,
                        completado = completado,
                        fecha = fecha,
                        recordatoriosActivos = recordatorios
                    )
                }
            }

        combine(flujos) { resultados ->

            resultados.toList().sortedWith(
                compareBy<HitoConEstado> {
                    convertirFechaCalendario(
                        fechaDelHito(it)
                    ) ?: Long.MAX_VALUE
                }.thenBy {
                    it.hito.id
                }
            )
        }
    }

    val hitosConEstado by flujoHitos.collectAsState(
        initial = hitos.map {
            HitoConEstado(
                hito = it,
                completado = it.completado,
                fecha = null,
                recordatoriosActivos = false
            )
        }
    )

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

            // VOLVER
            item {

                TextButton(
                    onClick = onVolver
                ) {

                    Text(
                        text = "← Volver",
                        color = colores.texto
                    )
                }
            }

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
                            text = "ESUCAR · ORGANIZACIÓN",
                            color = colores.acento,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Calendario académico",
                            color = colores.textoEncabezado,
                            fontSize = 27.sp,
                            lineHeight = 34.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Organiza tus hitos, " +
                                    "registra fechas y administra " +
                                    "los recordatorios de entrega.",
                            color = colores.textoEncabezado,
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )
                    }
                }
            }

            // PRUEBA DE NOTIFICACIONES
            item {

                OutlinedButton(
                    onClick = {

                        val enviada =
                            NotificacionesHelper
                                .mostrarNotificacionPrueba(
                                    context
                                )

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (enviada) {
                                    "Notificación de prueba enviada"
                                } else {
                                    "Revisa el permiso de notificaciones"
                                }
                            )
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),

                    border = BorderStroke(
                        if (altoContraste) 2.dp else 1.dp,
                        colores.borde
                    ),

                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = colores.texto
                        )
                ) {

                    Text("🔔 Probar notificación")
                }
            }

            // TÍTULO DEL LISTADO
            item {

                Text(
                    text = "Hitos y fechas de entrega",
                    color = colores.texto,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // HITOS ORDENADOS POR FECHA
            items(
                items = hitosConEstado,
                key = { it.hito.id }
            ) { item ->

                TarjetaHitoAccesible(
                    item = item,

                    onCambiarCompletado = { nuevoValor ->

                        scope.launch {

                            preferences.guardarCompletado(
                                item.hito.id,
                                nuevoValor
                            )

                            actualizarAlarmasCalendario(
                                context = context,
                                hito = item.hito,
                                fecha = fechaDelHito(item),
                                completado = nuevoValor,
                                recordatoriosActivos =
                                    item.recordatoriosActivos
                            )

                            snackbarHostState.showSnackbar(
                                "Estado guardado correctamente"
                            )
                        }
                    },

                    onCambiarFecha = { nuevaFecha ->

                        scope.launch {

                            preferences.guardarFecha(
                                item.hito.id,
                                nuevaFecha
                            )

                            actualizarAlarmasCalendario(
                                context = context,
                                hito = item.hito,
                                fecha = nuevaFecha,
                                completado = item.completado,
                                recordatoriosActivos =
                                    item.recordatoriosActivos
                            )

                            snackbarHostState.showSnackbar(
                                "Fecha guardada correctamente"
                            )
                        }
                    },

                    onQuitarFecha = {

                        scope.launch {

                            preferences.eliminarFecha(
                                item.hito.id
                            )

                            actualizarAlarmasCalendario(
                                context = context,
                                hito = item.hito,
                                fecha = item.hito.fechaLimite
                                    .takeIf {
                                        convertirFechaCalendario(it) != null
                                    },
                                completado = item.completado,
                                recordatoriosActivos =
                                    item.recordatoriosActivos
                            )

                            snackbarHostState.showSnackbar(
                                "Fecha personalizada eliminada"
                            )
                        }
                    },

                    onCambiarRecordatorios = { activos ->

                        scope.launch {

                            preferences.guardarRecordatoriosActivos(
                                item.hito.id,
                                activos
                            )

                            actualizarAlarmasCalendario(
                                context = context,
                                hito = item.hito,
                                fecha = fechaDelHito(item),
                                completado = item.completado,
                                recordatoriosActivos = activos
                            )

                            snackbarHostState.showSnackbar(
                                if (activos) {
                                    "Recordatorios activados"
                                } else {
                                    "Recordatorios desactivados"
                                }
                            )
                        }
                    }
                )
            }

            // ACLARACIÓN
            item {

                Text(
                    text = "Las fechas pueden modificarse " +
                            "en este dispositivo. Los recordatorios " +
                            "se programan para uno y tres días " +
                            "antes del vencimiento, aproximadamente " +
                            "a las 09:00. Su entrega depende " +
                            "de los permisos y del sistema Android.",

                    color = colores.textoSecundario,

                    fontSize = 12.sp,

                    lineHeight = 19.sp
                )
            }
        }

        // CONFIRMACIONES
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

// =====================================================
// TARJETA DEL HITO ACADÉMICO
// =====================================================

@Composable
private fun TarjetaHitoAccesible(
    item: HitoConEstado,
    onCambiarCompletado: (Boolean) -> Unit,
    onCambiarFecha: (String) -> Unit,
    onQuitarFecha: () -> Unit,
    onCambiarRecordatorios: (Boolean) -> Unit
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    val context = LocalContext.current
    val fecha = fechaDelHito(item)
    val diasRestantes = diasRestantesCalendario(fecha)

    val aviso = if (!item.completado) {
        when {
            diasRestantes == null -> null
            diasRestantes < 0 -> "Plazo vencido"
            diasRestantes == 0 -> "Vence hoy"
            diasRestantes == 1 -> "Vence mañana"
            diasRestantes <= 3 ->
                "Vence en $diasRestantes días"
            else -> null
        }
    } else {
        null
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
                Arrangement.spacedBy(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Checkbox(
                    checked = item.completado,

                    onCheckedChange = onCambiarCompletado,

                    colors = CheckboxDefaults.colors(
                        checkedColor = if (altoContraste) {
                            Color.Yellow
                        } else {
                            colores.encabezado
                        },

                        uncheckedColor = colores.texto,

                        checkmarkColor = if (altoContraste) {
                            Color.Black
                        } else {
                            Color.White
                        }
                    )
                )

                Column(
                    modifier = Modifier.weight(1f),

                    verticalArrangement =
                        Arrangement.spacedBy(5.dp)
                ) {

                    Text(
                        text = item.hito.titulo,

                        color = colores.texto,

                        fontSize = 18.sp,

                        lineHeight = 24.sp,

                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (item.completado) {
                            "Completado"
                        } else {
                            "Pendiente"
                        },

                        color = if (altoContraste) {
                            Color.Yellow
                        } else {
                            colores.textoSecundario
                        },

                        fontSize = 13.sp
                    )
                }
            }

            Text(
                text = item.hito.descripcion,

                color = colores.textoSecundario,

                fontSize = 14.sp,

                lineHeight = 21.sp
            )

            HorizontalDivider(
                color = colores.borde
            )

            Text(
                text = "Fecha de entrega",

                color = colores.texto,

                fontSize = 15.sp,

                fontWeight = FontWeight.Bold
            )

            Text(
                text = fecha ?: "Por definir",

                color = if (altoContraste) {
                    Color.Yellow
                } else {
                    colores.texto
                },

                fontSize = 17.sp,

                fontWeight = FontWeight.SemiBold
            )

            // AVISO DE PLAZO PRÓXIMO O VENCIDO
            if (aviso != null) {

                Surface(
                    color = if (altoContraste) {
                        Color.Yellow
                    } else {
                        Color(0xFFFFF0B8)
                    },

                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "⚠ $aviso",

                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 9.dp
                        ),

                        color = Color.Black,

                        fontSize = 13.sp,

                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // EDITAR FECHA
            Button(
                onClick = {

                    val calendario = Calendar.getInstance()

                    val fechaGuardada =
                        convertirFechaCalendario(fecha)

                    if (fechaGuardada != null) {
                        calendario.timeInMillis =
                            fechaGuardada
                    }

                    DatePickerDialog(
                        context,

                        { _, anio, mes, dia ->

                            val nuevaFecha = String.format(
                                Locale.ROOT,
                                "%04d-%02d-%02d",
                                anio,
                                mes + 1,
                                dia
                            )

                            onCambiarFecha(nuevaFecha)
                        },

                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),

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

                Text("Elegir fecha")
            }

            // QUITAR FECHA PERSONALIZADA
            if (
                convertirFechaCalendario(item.fecha) != null
            ) {

                OutlinedButton(
                    onClick = onQuitarFecha,

                    modifier = Modifier.fillMaxWidth(),

                    border = BorderStroke(
                        if (altoContraste) 2.dp else 1.dp,
                        colores.borde
                    ),

                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = colores.texto
                        )
                ) {

                    Text("Quitar fecha")
                }
            }

            HorizontalDivider(
                color = colores.borde
            )

            // RECORDATORIOS AUTOMÁTICOS
            Row(
                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Column(
                    modifier = Modifier.weight(1f),

                    verticalArrangement =
                        Arrangement.spacedBy(5.dp)
                ) {

                    Text(
                        text = "Recordatorios",

                        color = colores.texto,

                        fontSize = 16.sp,

                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Avisos 3 y 1 día antes " +
                                "de la fecha de entrega.",

                        color = colores.textoSecundario,

                        fontSize = 12.sp,

                        lineHeight = 18.sp
                    )
                }

                Switch(
                    checked = item.recordatoriosActivos,

                    onCheckedChange =
                        onCambiarRecordatorios,

                    enabled = !item.completado &&
                            fecha != null,

                    colors = SwitchDefaults.colors(
                        checkedTrackColor =
                            if (altoContraste) {
                                Color.Yellow
                            } else {
                                colores.encabezado
                            },

                        checkedThumbColor =
                            if (altoContraste) {
                                Color.Black
                            } else {
                                Color.White
                            },

                        uncheckedTrackColor =
                            if (altoContraste) {
                                Color.Black
                            } else {
                                Color(0xFFD6DDD7)
                            },

                        uncheckedThumbColor = Color.White,

                        uncheckedBorderColor = colores.borde
                    )
                )
            }

            if (item.completado) {

                Text(
                    text = "Los recordatorios de este hito " +
                            "se mantienen pausados porque " +
                            "está marcado como completado.",

                    color = colores.textoSecundario,

                    fontSize = 12.sp,

                    lineHeight = 18.sp
                )
            }
        }
    }
}
