package cl.duoc.tutorvirtual.ui.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.tutorvirtual.data.EtapasRepository
import cl.duoc.tutorvirtual.data.UbicacionHelper
import cl.duoc.tutorvirtual.data.UbicacionPreferences
import cl.duoc.tutorvirtual.data.UbicacionTrabajo
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==================================================
// UBICACION DEL TRABAJO DE CAMPO
// TUTOR VIRTUAL ESUCAR - PLAN B
//
// Permite registrar el lugar donde se realizo
// el trabajo de campo de una etapa.
//
// Solo se consulta la ubicacion al pulsar el boton.
// Los datos quedan guardados en este dispositivo.
// ==================================================

private fun formatearFecha(milisegundos: Long): String {
    return SimpleDateFormat(
        "dd-MM-yyyy HH:mm",
        Locale("es", "CL")
    ).format(Date(milisegundos))
}

private fun formatearCoordenadas(ubicacion: UbicacionTrabajo): String {
    return String.format(
        Locale.US,
        "Latitud: %.5f\nLongitud: %.5f\nPrecisión aproximada: %d m\nFecha: %s",
        ubicacion.latitud,
        ubicacion.longitud,
        ubicacion.precisionMetros.toInt(),
        formatearFecha(ubicacion.fechaRegistro)
    )
}

@Composable
fun UbicacionScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit
) {

    val context = LocalContext.current
    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black
    val scope = rememberCoroutineScope()

    val preferencias = remember(context) {
        UbicacionPreferences(context.applicationContext)
    }

    val etapas = remember {
        EtapasRepository.obtenerEtapas().sortedBy { it.id }
    }

    var etapaSeleccionada by rememberSaveable {
        mutableIntStateOf(etapas.firstOrNull()?.id ?: 1)
    }

    var buscando by remember { mutableStateOf(false) }

    var ubicacionActual by remember {
        mutableStateOf<UbicacionTrabajo?>(null)
    }

    var mensaje by remember {
        mutableStateOf("Todavía no se ha consultado la ubicación.")
    }

    val ubicacionGuardada by remember(etapaSeleccionada) {
        preferencias.obtenerUbicacion(etapaSeleccionada)
    }.collectAsState(initial = null)

    // ==============================================
    // CONSULTAR LA UBICACION ACTUAL
    // ==============================================

    fun consultarUbicacion() {

        if (!UbicacionHelper.ubicacionActivada(context)) {

            mensaje = "La ubicación del teléfono está desactivada. " +
                    "Actívala desde los ajustes e inténtalo nuevamente."

            return
        }

        buscando = true
        ubicacionActual = null
        mensaje = "Buscando ubicación..."

        UbicacionHelper.obtenerUbicacion(context) { ubicacion ->

            buscando = false

            if (ubicacion == null) {

                mensaje = "No se pudo obtener la ubicación. " +
                        "Prueba en un lugar abierto o con mejor señal."

            } else {

                ubicacionActual = UbicacionTrabajo(
                    latitud = ubicacion.latitude,
                    longitud = ubicacion.longitude,
                    precisionMetros = ubicacion.accuracy,
                    fechaRegistro = System.currentTimeMillis()
                )

                mensaje = "Ubicación obtenida correctamente."
            }
        }
    }

    // ==============================================
    // SOLICITUD DE PERMISO
    // ==============================================

    val solicitudPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->

        if (resultado.values.any { it }) {

            consultarUbicacion()

        } else {

            mensaje = "Sin el permiso de ubicación no es posible " +
                    "registrar el lugar del trabajo de campo."
        }
    }

    // Abrir una ubicacion en la aplicacion de mapas
    fun abrirEnMapa(ubicacion: UbicacionTrabajo) {

        val coordenadas = "${ubicacion.latitud},${ubicacion.longitud}"

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:$coordenadas?q=$coordenadas(Trabajo de campo)")
        )

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No se encontró una aplicación de mapas.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val bordeTarjeta = if (altoContraste) {
        BorderStroke(1.dp, colores.borde)
    } else {
        null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Ubicación del trabajo de campo",
            color = colores.texto,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Registra el lugar donde realizaste la recogida " +
                    "de información de tu trabajo de título.",
            color = colores.textoSecundario,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )

        // ==========================================
        // ETAPA A LA QUE PERTENECE
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colores.tarjeta),
            border = bordeTarjeta,
            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = "Etapa",
                    color = colores.texto,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                etapas.forEach { etapa ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { etapaSeleccionada = etapa.id },

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RadioButton(
                            selected = etapaSeleccionada == etapa.id,
                            onClick = { etapaSeleccionada = etapa.id },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colores.acento,
                                unselectedColor = colores.texto
                            )
                        )

                        Text(
                            text = etapa.titulo,
                            color = colores.texto,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // ==========================================
        // UBICACION ACTUAL
        // ==========================================

        Button(
            onClick = {

                if (UbicacionHelper.hayPermiso(context)) {

                    consultarUbicacion()

                } else {

                    solicitudPermiso.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },

            enabled = !buscando,

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

            Text(if (buscando) "Buscando..." else "Obtener ubicación actual")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colores.tarjeta),
            border = bordeTarjeta,
            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Ubicación actual",
                    color = colores.texto,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = ubicacionActual?.let { formatearCoordenadas(it) }
                        ?: mensaje,

                    color = colores.textoSecundario,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )

                ubicacionActual?.let { ubicacion ->

                    Button(
                        onClick = {
                            scope.launch {
                                preferencias.guardarUbicacion(
                                    etapaSeleccionada,
                                    ubicacion
                                )
                            }

                            ubicacionActual = null
                            mensaje = "Ubicación guardada para la etapa seleccionada."
                        },

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
                        Text("Guardar en esta etapa")
                    }

                    OutlinedButton(
                        onClick = { abrirEnMapa(ubicacion) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver en el mapa")
                    }
                }
            }
        }

        // ==========================================
        // UBICACION GUARDADA DE LA ETAPA
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colores.tarjeta),
            border = bordeTarjeta,
            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Ubicación guardada",
                    color = colores.texto,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                val guardada = ubicacionGuardada

                if (guardada == null) {

                    Text(
                        text = "Esta etapa no tiene una ubicación guardada.",
                        color = colores.textoSecundario,
                        fontSize = 14.sp
                    )

                } else {

                    Text(
                        text = formatearCoordenadas(guardada),
                        color = colores.textoSecundario,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )

                    OutlinedButton(
                        onClick = { abrirEnMapa(guardada) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver en el mapa")
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                preferencias.borrarUbicacion(etapaSeleccionada)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar ubicación")
                    }
                }
            }
        }

        Text(
            text = "La ubicación solo se consulta cuando pulsas el botón " +
                    "y se guarda únicamente en este dispositivo. " +
                    "No se envía a ningún servidor.",

            color = colores.textoSecundario,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a recursos")
        }
    }
}
