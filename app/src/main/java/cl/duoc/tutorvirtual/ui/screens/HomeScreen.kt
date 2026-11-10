package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

// Opciones disponibles en el inicio
private data class OpcionInicio(
    val numero: String,
    val titulo: String,
    val descripcion: String
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOptionClick: (String) -> Unit = {}
) {

    // Colores normales o de alto contraste
    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    // Mantenemos los nombres utilizados por AppNavigation
    val opciones = remember {
        listOf(
            OpcionInicio(
                numero = "01",
                titulo = "Guía metodológica",
                descripcion = "Consulta las cinco etapas " +
                        "de tu Trabajo de Título."
            ),

            OpcionInicio(
                numero = "02",
                titulo = "Preguntas frecuentes",
                descripcion = "Resuelve dudas sobre " +
                        "citación, metodología y formato."
            ),

            OpcionInicio(
                numero = "03",
                titulo = "Mis entregables",
                descripcion = "Revisa y actualiza el " +
                        "estado de tus entregas."
            ),

            OpcionInicio(
                numero = "04",
                titulo = "Normas APA",
                descripcion = "Sección de apoyo " +
                        "bibliográfico en desarrollo."
            ),

            OpcionInicio(
                numero = "05",
                titulo = "Biblioteca digital",
                descripcion = "Recursos de consulta " +
                        "pendientes de incorporar."
            ),

            OpcionInicio(
                numero = "06",
                titulo = "Calendario académico",
                descripcion = "Organiza hitos, fechas " +
                        "de entrega y recordatorios."
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo),

        contentPadding = PaddingValues(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ENCABEZADO PRINCIPAL
        item {

            Surface(
                color = colores.encabezado,
                shape = RoundedCornerShape(24.dp),

                border = if (altoContraste) {
                    BorderStroke(
                        width = 1.dp,
                        color = colores.borde
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
                        text = "TUTOR VIRTUAL ESUCAR",
                        color = colores.acento,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Mi Agenda Académica",
                        color = colores.textoEncabezado,
                        fontSize = 29.sp,
                        lineHeight = 35.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Organiza tu Trabajo de Título, " +
                                "consulta orientaciones y " +
                                "revisa tus actividades.",
                        color = colores.textoEncabezado,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )

                    HorizontalDivider(
                        color = if (altoContraste) {
                            Color.White
                        } else {
                            Color(0xFF7F9A8B)
                        }
                    )

                    Text(
                        text = "Tu espacio de apoyo académico",
                        color = colores.textoEncabezado,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // RESUMEN INFORMATIVO
        item {

            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = colores.tarjeta
                ),

                shape = RoundedCornerShape(18.dp),

                border = if (altoContraste) {
                    BorderStroke(
                        width = 1.dp,
                        color = colores.borde
                    )
                } else {
                    null
                }
            ) {

                Column(
                    modifier = Modifier.padding(18.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Tu seguimiento académico",
                        color = colores.texto,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Consulta Mis entregables " +
                                "para revisar el estado de " +
                                "cada etapa y Calendario académico " +
                                "para gestionar tus hitos.",
                        color = colores.textoSecundario,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // TÍTULO DE LAS HERRAMIENTAS
        item {

            Text(
                text = "Herramientas académicas",
                color = colores.texto,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // TARJETAS DE NAVEGACIÓN
        items(
            items = opciones,
            key = { it.numero }
        ) { opcion ->

            TarjetaOpcionInicio(
                opcion = opcion,
                onClick = {
                    onOptionClick(opcion.titulo)
                }
            )
        }

        // INFORMACIÓN DEL PROTOTIPO
        item {

            Text(
                text = "Tutor Virtual ESUCAR es un " +
                        "prototipo académico. Las secciones " +
                        "Normas APA y Biblioteca digital " +
                        "todavía están en desarrollo. " +
                        "El contenido de demostración no " +
                        "constituye información institucional oficial.",
                color = colores.textoSecundario,
                fontSize = 12.sp,
                lineHeight = 19.sp
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}

// TARJETA DE CADA OPCIÓN DEL INICIO
@Composable
private fun TarjetaOpcionInicio(
    opcion: OpcionInicio,
    onClick: () -> Unit
) {

    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),

        colors = CardDefaults.cardColors(
            containerColor = colores.tarjeta
        ),

        shape = RoundedCornerShape(18.dp),

        border = if (altoContraste) {
            BorderStroke(
                width = 1.dp,
                color = colores.borde
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            // IDENTIFICADOR DE LA OPCIÓN
            Surface(
                color = if (altoContraste) {
                    Color.Yellow
                } else {
                    Color(0xFFE8EFE9)
                },

                shape = RoundedCornerShape(12.dp)
            ) {

                Box(
                    modifier = Modifier.size(44.dp),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = opcion.numero,

                        color = if (altoContraste) {
                            Color.Black
                        } else {
                            Color(0xFF174B35)
                        },

                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // TÍTULO Y DESCRIPCIÓN
            Column(
                modifier = Modifier.weight(1f),

                verticalArrangement =
                    Arrangement.spacedBy(5.dp)
            ) {

                Text(
                    text = opcion.titulo,
                    color = colores.texto,
                    fontSize = 17.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = opcion.descripcion,
                    color = colores.textoSecundario,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

            // INDICADOR DE NAVEGACIÓN
            Text(
                text = "›",
                color = if (altoContraste) {
                    Color.Yellow
                } else {
                    colores.texto
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
