package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import cl.duoc.tutorvirtual.data.RubricasRepository
import cl.duoc.tutorvirtual.model.CriterioRubrica
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

@Composable
fun RubricasScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit = {}
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    // Obtener las cinco etapas existentes
    val etapas = remember {
        EtapasRepository.obtenerEtapas()
            .sortedBy { it.id }
    }

    // Mantener la etapa seleccionada
    var etapaSeleccionada by rememberSaveable {
        mutableIntStateOf(
            etapas.firstOrNull()?.id ?: 1
        )
    }

    // Recuperar los criterios de la etapa seleccionada
    val criterios = remember(etapaSeleccionada) {
        RubricasRepository.obtenerPorEtapa(
            etapaSeleccionada
        )
    }

    val nombreEtapa = etapas
        .firstOrNull { it.id == etapaSeleccionada }
        ?.titulo ?: "Etapa metodológica"

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
                        text = "ESUCAR · APOYO ACADÉMICO",
                        color = colores.acento,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Rúbricas de evaluación",
                        color = colores.textoEncabezado,
                        fontSize = 27.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Consulta criterios orientativos " +
                                "para revisar el desarrollo " +
                                "de tu Trabajo de Título.",
                        color = colores.textoEncabezado,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // SELECCIONAR ETAPA
        item {

            Text(
                text = "Selecciona una etapa",
                color = colores.texto,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {

            LazyRow(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = etapas,
                    key = { it.id }
                ) { etapa ->

                    val seleccionada =
                        etapa.id == etapaSeleccionada

                    FilterChip(
                        selected = seleccionada,

                        onClick = {
                            etapaSeleccionada = etapa.id
                        },

                        label = {
                            Text(
                                text = "Etapa ${etapa.id}"
                            )
                        },

                        colors =
                            FilterChipDefaults.filterChipColors(

                                containerColor =
                                    colores.tarjeta,

                                labelColor =
                                    colores.texto,

                                selectedContainerColor =
                                    if (altoContraste) {
                                        Color.Yellow
                                    } else {
                                        Color(0xFFE8EFE9)
                                    },

                                selectedLabelColor =
                                    if (altoContraste) {
                                        Color.Black
                                    } else {
                                        colores.texto
                                    }
                            ),

                        border =
                            FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = seleccionada,
                                borderColor = colores.borde,
                                selectedBorderColor =
                                    colores.acento,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 2.dp
                            )
                    )
                }
            }
        }

        // ETAPA ACTUAL
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
                        Arrangement.spacedBy(10.dp)
                ) {

                    Text(
                        text = "ETAPA $etapaSeleccionada",

                        color = if (altoContraste) {
                            Color.Yellow
                        } else {
                            colores.texto
                        },

                        fontSize = 13.sp,

                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = nombreEtapa,

                        color = colores.texto,

                        fontSize = 21.sp,

                        lineHeight = 29.sp,

                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${criterios.size} " +
                                "criterio(s) disponibles",

                        color = colores.textoSecundario,

                        fontSize = 14.sp
                    )
                }
            }
        }

        // CRITERIOS DE LA ETAPA
        items(
            items = criterios,
            key = { it.id }
        ) { criterio ->

            TarjetaCriterioAccesible(
                criterio = criterio
            )
        }

        // SIN CRITERIOS
        if (criterios.isEmpty()) {

            item {

                Text(
                    text = "No hay criterios registrados " +
                            "para esta etapa.",

                    color = colores.textoSecundario,

                    fontSize = 14.sp
                )
            }
        }

        // AVISO ACADÉMICO
        item {

            Surface(
                modifier = Modifier.fillMaxWidth(),

                color = colores.tarjeta,

                shape = RoundedCornerShape(16.dp),

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
                    text = "Importante: estas rúbricas " +
                            "son ejemplos orientativos " +
                            "del prototipo. No constituyen " +
                            "pautas oficiales de ESUCAR. " +
                            "Confirma los criterios y puntajes " +
                            "con tu institución.",

                    modifier = Modifier.padding(16.dp),

                    color = colores.textoSecundario,

                    fontSize = 12.sp,

                    lineHeight = 20.sp
                )
            }
        }
    }
}

// ==================================================
// TARJETA DE CRITERIO DE EVALUACIÓN
// ==================================================

@Composable
private fun TarjetaCriterioAccesible(
    criterio: CriterioRubrica
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
                text = "CRITERIO DE EVALUACIÓN",

                color = if (altoContraste) {
                    Color.Yellow
                } else {
                    Color(0xFF896A2C)
                },

                fontSize = 12.sp,

                fontWeight = FontWeight.Bold
            )

            Text(
                text = criterio.titulo,

                color = colores.texto,

                fontSize = 18.sp,

                lineHeight = 25.sp,

                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(
                color = colores.borde
            )

            Text(
                text = criterio.descripcion,

                color = colores.textoSecundario,

                fontSize = 14.sp,

                lineHeight = 22.sp
            )

            if (criterio.puntajeMaximo > 0) {

                Text(
                    text = "Puntaje máximo: " +
                            "${criterio.puntajeMaximo}",

                    color = colores.texto,

                    fontSize = 13.sp,

                    fontWeight = FontWeight.SemiBold
                )

            } else {

                Text(
                    text = "Puntaje oficial no definido",

                    color = colores.textoSecundario,

                    fontSize = 12.sp
                )
            }
        }
    }
}
