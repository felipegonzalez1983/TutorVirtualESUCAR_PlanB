package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cl.duoc.tutorvirtual.data.EtapasRepository
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

// Modelo interno de cada paso
private data class PasoGuiaAccesible(
    val titulo: String,
    val descripcion: String,
    val recomendacion: String
)

@Composable
fun GuiaPasoScreen(
    etapaId: Int,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    // Recuperar la etapa seleccionada
    val etapa = remember(etapaId) {
        EtapasRepository.obtenerEtapaPorId(etapaId)
    }

    // Cinco orientaciones generales
    val pasos = remember {
        listOf(
            PasoGuiaAccesible(
                titulo = "Comprender la etapa",
                descripcion =
                    "Lee la descripción de esta etapa " +
                            "e identifica cuál es su objetivo.",
                recomendacion =
                    "Anota las ideas principales antes " +
                            "de comenzar a trabajar."
            ),

            PasoGuiaAccesible(
                titulo = "Revisar los requisitos",
                descripcion =
                    "Consulta el formato requerido " +
                            "y las orientaciones disponibles " +
                            "para desarrollar esta etapa.",
                recomendacion =
                    "Comprueba los requisitos con " +
                            "los documentos académicos vigentes."
            ),

            PasoGuiaAccesible(
                titulo = "Organizar el trabajo",
                descripcion =
                    "Divide las actividades de la etapa " +
                            "en tareas pequeñas y organiza " +
                            "su desarrollo.",
                recomendacion =
                    "Utiliza el calendario académico " +
                            "para registrar fechas de entrega."
            ),

            PasoGuiaAccesible(
                titulo = "Desarrollar el contenido",
                descripcion =
                    "Prepara el contenido de la etapa " +
                            "siguiendo la metodología " +
                            "que corresponda.",
                recomendacion =
                    "Revisa tus avances y conserva " +
                            "una copia de tu trabajo."
            ),

            PasoGuiaAccesible(
                titulo = "Revisar y finalizar",
                descripcion =
                    "Comprueba que el contenido esté " +
                            "completo, organizado y coherente " +
                            "con los objetivos de la etapa.",
                recomendacion =
                    "Consulta la rúbrica orientativa " +
                            "y confirma los criterios oficiales " +
                            "antes de entregar."
            )
        )
    }

    // Recordar el paso actual
    var indicePaso by rememberSaveable(etapaId) {
        mutableIntStateOf(0)
    }

    // Progreso animado
    val progreso by animateFloatAsState(
        targetValue =
            (indicePaso + 1).toFloat() / pasos.size,
        label = "progreso_guia"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        // BOTÓN VOLVER
        TextButton(
            onClick = onVolver
        ) {
            Text(
                text = "← Volver al detalle",
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

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "ESUCAR · GUÍA INTERACTIVA",
                    color = colores.acento,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = etapa?.titulo
                        ?: "Etapa metodológica",
                    color = colores.textoEncabezado,
                    fontSize = 25.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Avanza por las orientaciones " +
                            "de esta etapa académica.",
                    color = colores.textoEncabezado,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }
        }

        // INDICADOR DE PROGRESO
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
                    text = "Paso ${indicePaso + 1} " +
                            "de ${pasos.size}",
                    color = colores.texto,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),

                    color = if (altoContraste) {
                        Color.Yellow
                    } else {
                        colores.encabezado
                    },

                    trackColor = if (altoContraste) {
                        Color.DarkGray
                    } else {
                        Color(0xFFE0E8E1)
                    }
                )

                Text(
                    text = "${((indicePaso + 1) * 100) / pasos.size}% de la guía",
                    color = colores.textoSecundario,
                    fontSize = 13.sp
                )
            }
        }

        // CONTENIDO CON ANIMACIÓN
        AnimatedContent(
            targetState = indicePaso,
            label = "transicion_pasos"
        ) { indiceAnimado ->

            val paso = pasos[indiceAnimado]

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
                    modifier = Modifier.padding(20.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {

                    Text(
                        text = "ORIENTACIÓN ${
                            indiceAnimado + 1
                        }",
                        color = if (altoContraste) {
                            Color.Yellow
                        } else {
                            Color(0xFF896A2C)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = paso.titulo,
                        color = colores.texto,
                        fontSize = 22.sp,
                        lineHeight = 29.sp,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider(
                        color = colores.borde
                    )

                    Text(
                        text = paso.descripcion,
                        color = colores.textoSecundario,
                        fontSize = 15.sp,
                        lineHeight = 24.sp
                    )

                    Text(
                        text = "Recomendación",
                        color = colores.texto,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = paso.recomendacion,
                        color = colores.textoSecundario,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // BOTONES ANTERIOR Y SIGUIENTE
        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = {
                    if (indicePaso > 0) {
                        indicePaso--
                    }
                },

                enabled = indicePaso > 0,

                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 52.dp),

                border = BorderStroke(
                    if (altoContraste) 2.dp else 1.dp,
                    if (altoContraste) {
                        Color.White
                    } else {
                        colores.texto
                    }
                ),

                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = colores.texto,
                        disabledContentColor =
                            if (altoContraste) {
                                Color.LightGray
                            } else {
                                Color.Gray
                            }
                    )
            ) {
                Text("Anterior")
            }

            Button(
                onClick = {
                    if (indicePaso < pasos.lastIndex) {
                        indicePaso++
                    } else {
                        onVolver()
                    }
                },

                modifier = Modifier
                    .weight(1f)
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

                Text(
                    text = if (
                        indicePaso == pasos.lastIndex
                    ) {
                        "Finalizar"
                    } else {
                        "Siguiente"
                    },

                    fontWeight = FontWeight.Bold
                )
            }
        }

        // INFORMACIÓN DEL PROTOTIPO
        Text(
            text = "Esta guía contiene orientaciones " +
                    "generales del prototipo y no sustituye " +
                    "las instrucciones oficiales de ESUCAR.",
            color = colores.textoSecundario,
            fontSize = 12.sp,
            lineHeight = 19.sp
        )
    }
}
