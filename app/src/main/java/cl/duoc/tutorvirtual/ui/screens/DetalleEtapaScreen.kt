package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cl.duoc.tutorvirtual.data.EntregablesPreferences
import cl.duoc.tutorvirtual.data.EtapasRepository
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

@Composable
fun DetalleEtapaScreen(
    etapaId: Int,
    onVolver: () -> Unit,
    onIniciarGuia: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    val context = LocalContext.current

    // Recuperar información de la etapa
    val etapa = remember(etapaId) {
        EtapasRepository.obtenerEtapaPorId(etapaId)
    }

    // Conservar el estado guardado en DataStore
    val preferences = remember(context) {
        EntregablesPreferences(
            context.applicationContext
        )
    }

    val flujoEstado = remember(preferences, etapaId) {
        preferences.obtenerEstado(etapaId)
    }

    val estado by flujoEstado.collectAsState(
        initial = etapa?.estado ?: "Por iniciar"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // BOTÓN VOLVER
        TextButton(onClick = onVolver) {
            Text(
                text = "← Volver a etapas",
                color = colores.texto
            )
        }

        if (etapa == null) {

            Text(
                text = "No se encontró la etapa solicitada.",
                color = colores.texto,
                fontSize = 17.sp
            )

        } else {

            // ENCABEZADO
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
                        text = "ESUCAR · GUÍA METODOLÓGICA",
                        color = colores.acento,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "ETAPA ${etapa.id}",
                        color = colores.acento,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = etapa.titulo,
                        color = colores.textoEncabezado,
                        fontSize = 27.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Información y orientación " +
                                "para esta etapa académica.",
                        color = colores.textoEncabezado,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            // TARJETA DE ESTADO
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
                        text = "Estado de la etapa",
                        color = colores.texto,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

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
                                horizontal = 16.dp,
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

                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Este estado corresponde " +
                                "al seguimiento personal " +
                                "registrado en la aplicación.",
                        color = colores.textoSecundario,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // DESCRIPCIÓN
            TarjetaInformacionEtapa(
                titulo = "Descripción de la etapa",
                contenido = etapa.descripcion
            )

            // FORMATO REQUERIDO
            TarjetaInformacionEtapa(
                titulo = "Formato requerido",
                contenido = etapa.formatoRequerido
            )

            // FECHA LÍMITE
            TarjetaInformacionEtapa(
                titulo = "Fecha límite",
                contenido = etapa.fechaLimite
            )

            // BOTÓN DE GUÍA INTERACTIVA
            Button(
                onClick = onIniciarGuia,
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

                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = "Iniciar guía interactiva",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            // AVISO DEL PROTOTIPO
            Text(
                text = "Contenido orientativo del prototipo. " +
                        "Los formatos y plazos deben " +
                        "confirmarse con la institución.",
                color = colores.textoSecundario,
                fontSize = 12.sp,
                lineHeight = 19.sp
            )
        }
    }
}

// TARJETA REUTILIZABLE DE INFORMACIÓN
@Composable
private fun TarjetaInformacionEtapa(
    titulo: String,
    contenido: String
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
            defaultElevation = if (altoContraste) {
                0.dp
            } else {
                2.dp
            }
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = titulo,
                color = colores.texto,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(
                color = colores.borde
            )

            Text(
                text = contenido,
                color = colores.textoSecundario,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}