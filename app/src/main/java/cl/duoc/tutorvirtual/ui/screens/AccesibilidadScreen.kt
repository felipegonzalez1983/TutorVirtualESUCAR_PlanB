package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cl.duoc.tutorvirtual.data.AccesibilidadPreferences
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

import kotlinx.coroutines.launch

@Composable
fun AccesibilidadScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit = {}
) {

    // Obtener colores compartidos
    val colores = LocalColoresEsucar.current

    val altoContraste =
        colores.fondo == Color.Black

    val context = LocalContext.current

    val preferences = remember(context) {
        AccesibilidadPreferences(
            context.applicationContext
        )
    }

    val flujoAltoContraste = remember(preferences) {
        preferences.obtenerAltoContraste()
    }

    val activado by flujoAltoContraste.collectAsState(
        initial = false
    )

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // BOTÓN VOLVER
        TextButton(
            onClick = onVolver
        ) {
            Text(
                text = "← Volver a recursos",
                color = colores.texto
            )
        }

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

                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "ESUCAR · CONFIGURACIÓN",
                    color = colores.acento,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Accesibilidad",
                    color = colores.textoEncabezado,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Personaliza la visualización " +
                            "de tu Tutor Virtual.",
                    color = colores.textoEncabezado,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }
        }

        // TARJETA DE CONFIGURACIÓN
        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(18.dp),

            colors = CardDefaults.cardColors(
                containerColor = colores.tarjeta
            ),

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
                    .padding(18.dp),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Column(
                    modifier = Modifier.weight(1f),

                    verticalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = "Modo de alto contraste",
                        color = colores.texto,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (activado) {
                            "Activado"
                        } else {
                            "Desactivado"
                        },
                        color = colores.textoSecundario,
                        fontSize = 13.sp
                    )
                }

                Switch(
                    checked = activado,

                    onCheckedChange = { nuevoValor ->

                        scope.launch {
                            preferences.guardarAltoContraste(
                                nuevoValor
                            )
                        }
                    },

                    modifier = Modifier.semantics {
                        contentDescription =
                            "Modo de alto contraste"
                    },

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

                        uncheckedThumbColor =
                            Color.White,

                        uncheckedBorderColor =
                            if (altoContraste) {
                                Color.White
                            } else {
                                Color.Gray
                            }
                    )
                )
            }
        }

        // VISTA PREVIA
        Text(
            text = "Vista previa",
            color = colores.texto,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        val fondoVista = if (activado) {
            Color.Black
        } else {
            Color.White
        }

        val textoVista = if (activado) {
            Color.White
        } else {
            Color(0xFF174B35)
        }

        val tarjetaVista = if (activado) {
            Color.Black
        } else {
            Color(0xFFE8EFE9)
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),

            color = fondoVista,

            shape = RoundedCornerShape(18.dp),

            border = if (activado) {
                BorderStroke(
                    2.dp,
                    Color.White
                )
            } else {
                null
            }
        ) {

            Column(
                modifier = Modifier.padding(20.dp),

                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "Tutor Virtual ESUCAR",
                    color = textoVista,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = tarjetaVista,

                    shape = RoundedCornerShape(12.dp),

                    border = if (activado) {
                        BorderStroke(
                            1.dp,
                            Color.White
                        )
                    } else {
                        null
                    }
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            text = "Etapas metodológicas",
                            color = textoVista,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Consulta tu progreso académico.",
                            color = textoVista,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // INFORMACIÓN DEL AJUSTE
        Text(
            text = "La configuración se guarda " +
                    "automáticamente en este dispositivo. " +
                    "El modo de alto contraste mejora la " +
                    "diferenciación visual de textos y controles.",
            color = colores.textoSecundario,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }
}
