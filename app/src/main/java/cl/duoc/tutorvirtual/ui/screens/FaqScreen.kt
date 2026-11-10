package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

import java.text.Normalizer
import java.util.Locale

// MODELO DE PREGUNTA FRECUENTE
private data class PreguntaFaq(
    val id: Int,
    val categoria: String,
    val pregunta: String,
    val respuesta: String
)

// BÚSQUEDA SIN DIFERENCIAR MAYÚSCULAS NI TILDES
private fun normalizarFaq(texto: String): String {
    return Normalizer.normalize(
        texto.lowercase(Locale.ROOT),
        Normalizer.Form.NFD
    ).replace(
        Regex("\\p{M}+"),
        ""
    )
}

// CONTENIDO ORIENTATIVO DEL PROTOTIPO
private val preguntasFaq = listOf(

    PreguntaFaq(
        1,
        "Metodología",
        "¿Qué debo hacer antes de comenzar mi Trabajo de Título?",
        "Primero debes comprender el tema, revisar las " +
                "instrucciones académicas disponibles y " +
                "organizar las actividades que desarrollarás. " +
                "Confirma siempre los requisitos con tu docente."
    ),

    PreguntaFaq(
        2,
        "Metodología",
        "¿Cómo puedo organizar las etapas de mi trabajo?",
        "Puedes revisar las etapas metodológicas en el " +
                "orden presentado por la aplicación, registrar " +
                "tus avances y utilizar el calendario para " +
                "planificar las entregas."
    ),

    PreguntaFaq(
        3,
        "Metodología",
        "¿Qué es un objetivo general?",
        "Es el propósito principal que se busca alcanzar " +
                "mediante el desarrollo del trabajo. Debe ser " +
                "coherente con el problema y el tema investigado."
    ),

    PreguntaFaq(
        4,
        "Metodología",
        "¿Qué diferencia hay entre objetivos generales y específicos?",
        "El objetivo general expresa la finalidad principal. " +
                "Los objetivos específicos describen resultados " +
                "o acciones concretas que ayudan a alcanzarla."
    ),

    PreguntaFaq(
        5,
        "Formato",
        "¿Dónde puedo consultar el formato de cada etapa?",
        "Ingresa a Etapas metodológicas, selecciona una " +
                "etapa y revisa el apartado Formato requerido. " +
                "El contenido del prototipo es orientativo; " +
                "verifica las exigencias oficiales."
    ),

    PreguntaFaq(
        6,
        "Formato",
        "¿Todas las etapas utilizan el mismo formato?",
        "No necesariamente. Cada etapa puede solicitar " +
                "contenidos y estructuras diferentes. Debes " +
                "revisar las indicaciones vigentes de tu institución."
    ),

    PreguntaFaq(
        7,
        "Citación",
        "¿Qué son las normas APA?",
        "Son un conjunto de pautas utilizadas para " +
                "presentar trabajos académicos, citar fuentes " +
                "y elaborar referencias bibliográficas. " +
                "Consulta la edición solicitada por tu docente."
    ),

    PreguntaFaq(
        8,
        "Citación",
        "¿Por qué debo citar las fuentes consultadas?",
        "Porque permite reconocer la autoría de la " +
                "información utilizada y facilita que otras " +
                "personas identifiquen las fuentes originales."
    ),

    PreguntaFaq(
        9,
        "Entregas",
        "¿Cómo puedo registrar el avance de una etapa?",
        "Abre Mis entregables, selecciona la etapa y " +
                "utiliza Cambiar estado. Puedes elegir " +
                "Por iniciar, En desarrollo, Enviado o Aprobado."
    ),

    PreguntaFaq(
        10,
        "Entregas",
        "¿El estado Aprobado significa aprobación oficial?",
        "No. Los estados de esta aplicación corresponden " +
                "al seguimiento personal del estudiante y " +
                "no reemplazan la evaluación institucional."
    ),

    PreguntaFaq(
        11,
        "Aplicación",
        "¿Mis estados se guardan al cerrar la aplicación?",
        "Sí. Los estados de las etapas se almacenan " +
                "localmente en el dispositivo mediante DataStore."
    ),

    PreguntaFaq(
        12,
        "Aplicación",
        "¿Puedo utilizar la aplicación sin internet?",
        "Las etapas, las preguntas frecuentes y los " +
                "datos locales pueden consultarse sin conexión. " +
                "Las novedades remotas requieren conexión para " +
                "descargar contenido nuevo."
    ),

    PreguntaFaq(
        13,
        "Aplicación",
        "¿Cómo activo el modo de alto contraste?",
        "Entra a Recursos académicos, abre Accesibilidad " +
                "y activa el interruptor Modo de alto contraste."
    )
)

@Composable
fun FaqScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit = {}
) {

    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    var categoriaSeleccionada by rememberSaveable {
        mutableStateOf("Todas")
    }

    var preguntaExpandida by rememberSaveable {
        mutableIntStateOf(-1)
    }

    val categorias = remember {
        listOf(
            "Todas",
            "Metodología",
            "Formato",
            "Citación",
            "Entregas",
            "Aplicación"
        )
    }

    val preguntasFiltradas = remember(
        busqueda,
        categoriaSeleccionada
    ) {

        val textoBuscado = normalizarFaq(
            busqueda.trim()
        )

        preguntasFaq.filter { pregunta ->

            val coincideCategoria =
                categoriaSeleccionada == "Todas" ||
                        pregunta.categoria == categoriaSeleccionada

            val contenidoBuscable = normalizarFaq(
                pregunta.pregunta + " " +
                        pregunta.respuesta + " " +
                        pregunta.categoria
            )

            coincideCategoria &&
                    (
                            textoBuscado.isEmpty() ||
                                    contenidoBuscable.contains(textoBuscado)
                            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo),

        contentPadding = PaddingValues(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // VOLVER
        item {

            TextButton(onClick = onVolver) {

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
                        text = "ESUCAR · APOYO ACADÉMICO",
                        color = colores.acento,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Preguntas frecuentes",
                        color = colores.textoEncabezado,
                        fontSize = 27.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Encuentra respuestas sobre " +
                                "metodología, formato y " +
                                "uso de la aplicación.",
                        color = colores.textoEncabezado,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }
        }

        // BUSCADOR
        item {

            OutlinedTextField(
                value = busqueda,

                onValueChange = {
                    busqueda = it
                    preguntaExpandida = -1
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Buscar pregunta")
                },

                placeholder = {
                    Text("Escribe una palabra clave")
                },

                singleLine = true,

                shape = RoundedCornerShape(14.dp),

                colors = OutlinedTextFieldDefaults.colors(

                    focusedTextColor = colores.texto,

                    unfocusedTextColor = colores.texto,

                    focusedContainerColor = colores.tarjeta,

                    unfocusedContainerColor = colores.tarjeta,

                    focusedBorderColor = colores.acento,

                    unfocusedBorderColor = colores.borde,

                    focusedLabelColor = colores.texto,

                    unfocusedLabelColor = colores.textoSecundario,

                    focusedPlaceholderColor =
                        colores.textoSecundario,

                    unfocusedPlaceholderColor =
                        colores.textoSecundario,

                    cursorColor = colores.acento
                )
            )
        }

        // CATEGORÍAS
        item {

            Text(
                text = "Categorías",
                color = colores.texto,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {

            LazyRow(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(categorias) { categoria ->

                    val seleccionada =
                        categoriaSeleccionada == categoria

                    FilterChip(
                        selected = seleccionada,

                        onClick = {
                            categoriaSeleccionada = categoria
                            preguntaExpandida = -1
                        },

                        label = {
                            Text(categoria)
                        },

                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = colores.tarjeta,

                            labelColor = colores.texto,

                            selectedContainerColor = if (altoContraste) {
                                Color.Yellow
                            } else {
                                Color(0xFFE8EFE9)
                            },

                            selectedLabelColor = if (altoContraste) {
                                Color.Black
                            } else {
                                colores.texto
                            }
                        ),

                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = seleccionada,
                            borderColor = colores.borde,
                            selectedBorderColor = colores.acento,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 2.dp
                        )
                    )
                }
            }
        }

        // TOTAL DE RESULTADOS
        item {

            Text(
                text = "${preguntasFiltradas.size} " +
                        "pregunta(s) encontrada(s)",

                color = colores.texto,

                fontSize = 17.sp,

                fontWeight = FontWeight.Bold
            )
        }

        // SIN RESULTADOS
        if (preguntasFiltradas.isEmpty()) {

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
                        text = "No encontramos preguntas " +
                                "con esos criterios. " +
                                "Prueba otra palabra o categoría.",

                        modifier = Modifier.padding(20.dp),

                        color = colores.textoSecundario,

                        fontSize = 14.sp,

                        lineHeight = 21.sp
                    )
                }
            }
        }

        // PREGUNTAS EXPANDIBLES
        items(
            items = preguntasFiltradas,
            key = { it.id }
        ) { pregunta ->

            TarjetaPreguntaAccesible(
                pregunta = pregunta,

                expandida =
                    preguntaExpandida == pregunta.id,

                onClick = {

                    preguntaExpandida =
                        if (preguntaExpandida == pregunta.id) {
                            -1
                        } else {
                            pregunta.id
                        }
                }
            )
        }

        // ACLARACIÓN
        item {

            Text(
                text = "Las respuestas son orientativas " +
                        "y forman parte de un prototipo académico. " +
                        "Verifica siempre las instrucciones " +
                        "vigentes de ESUCAR y de tu docente.",

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


// ===================================================
// TARJETA EXPANDIBLE
// ===================================================

@Composable
private fun TarjetaPreguntaAccesible(
    pregunta: PreguntaFaq,
    expandida: Boolean,
    onClick: () -> Unit
) {

    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),

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
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = pregunta.categoria.uppercase(Locale.ROOT),

                color = if (altoContraste) {
                    Color.Yellow
                } else {
                    Color(0xFF896A2C)
                },

                fontSize = 11.sp,

                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = pregunta.pregunta,

                    modifier = Modifier.weight(1f),

                    color = colores.texto,

                    fontSize = 16.sp,

                    lineHeight = 23.sp,

                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (expandida) "−" else "+",

                    color = if (altoContraste) {
                        Color.Yellow
                    } else {
                        colores.texto
                    },

                    fontSize = 24.sp,

                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(
                visible = expandida
            ) {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    HorizontalDivider(
                        color = colores.borde
                    )

                    Text(
                        text = pregunta.respuesta,

                        color = colores.textoSecundario,

                        fontSize = 14.sp,

                        lineHeight = 23.sp
                    )
                }
            }
        }
    }
}
