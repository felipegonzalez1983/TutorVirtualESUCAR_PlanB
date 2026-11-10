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
import androidx.compose.ui.platform.LocalContext

import cl.duoc.tutorvirtual.data.EntregablesPreferences
import cl.duoc.tutorvirtual.data.EtapasRepository
import cl.duoc.tutorvirtual.model.Etapa
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

import java.text.Normalizer
import java.util.Locale

// Permite buscar sin distinguir mayúsculas ni tildes
private fun normalizarTextoEtapas(texto: String): String {
    return Normalizer.normalize(
        texto.lowercase(Locale.ROOT),
        Normalizer.Form.NFD
    ).replace(
        Regex("\\p{M}+"),
        ""
    )
}

@Composable
fun EtapasScreen(
    modifier: Modifier = Modifier,
    onEtapaClick: (Int) -> Unit = {},
    onVolverInicio: () -> Unit = {}
) {

    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    val context = LocalContext.current

    val preferences = remember(context) {
        EntregablesPreferences(
            context.applicationContext
        )
    }

    val etapas = remember {
        EtapasRepository.obtenerEtapas()
            .sortedBy { it.id }
    }

    var busqueda by remember {
        mutableStateOf("")
    }

    val etapasFiltradas = remember(
        etapas,
        busqueda
    ) {

        val textoBuscado = normalizarTextoEtapas(
            busqueda.trim()
        )

        etapas.filter { etapa ->

            val titulo = normalizarTextoEtapas(
                etapa.titulo
            )

            val descripcion = normalizarTextoEtapas(
                etapa.descripcion
            )

            textoBuscado.isEmpty() ||
                    titulo.contains(textoBuscado) ||
                    descripcion.contains(textoBuscado)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo),

        contentPadding = PaddingValues(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // VOLVER AL INICIO
        item {

            TextButton(
                onClick = onVolverInicio
            ) {

                Text(
                    text = "← Volver al inicio",
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
                        Arrangement.spacedBy(10.dp)
                ) {

                    Text(
                        text = "ESUCAR · GUÍA METODOLÓGICA",
                        color = colores.acento,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Etapas metodológicas",
                        color = colores.textoEncabezado,
                        fontSize = 27.sp,
                        lineHeight = 33.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Consulta las cinco etapas " +
                                "de tu Trabajo de Título " +
                                "y revisa tu progreso académico.",
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
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Buscar etapa")
                },

                placeholder = {
                    Text(
                        text = "Escribe una palabra clave",
                        color = colores.textoSecundario
                    )
                },

                singleLine = true,

                shape = RoundedCornerShape(14.dp),

                colors = OutlinedTextFieldDefaults.colors(

                    focusedTextColor = colores.texto,

                    unfocusedTextColor = colores.texto,

                    focusedContainerColor = colores.tarjeta,

                    unfocusedContainerColor = colores.tarjeta,

                    focusedBorderColor =
                        if (altoContraste) {
                            Color.Yellow
                        } else {
                            colores.encabezado
                        },

                    unfocusedBorderColor = colores.borde,

                    focusedLabelColor = colores.texto,

                    unfocusedLabelColor =
                        colores.textoSecundario,

                    cursorColor = colores.acento
                )
            )
        }

        // CONTADOR
        item {

            Text(
                text = if (busqueda.isBlank()) {
                    "Etapas del proceso académico"
                } else {
                    "${etapasFiltradas.size} resultado(s)"
                },

                color = colores.texto,

                fontSize = 19.sp,

                fontWeight = FontWeight.Bold
            )
        }

        // RESULTADO SIN COINCIDENCIAS
        if (etapasFiltradas.isEmpty()) {

            item {

                Surface(
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
                        text = "No se encontraron etapas " +
                                "con esa búsqueda.",

                        modifier = Modifier.padding(20.dp),

                        color = colores.textoSecundario,

                        fontSize = 14.sp
                    )
                }
            }
        }

        // LISTADO DE ETAPAS
        items(
            items = etapasFiltradas,
            key = { it.id }
        ) { etapa ->

            TarjetaEtapaAccesible(
                etapa = etapa,
                preferences = preferences,
                onClick = {
                    onEtapaClick(etapa.id)
                }
            )
        }

        // AVISO INFORMATIVO
        item {

            Text(
                text = "Selecciona una etapa para ver " +
                        "su descripción, formato requerido " +
                        "y guía interactiva.",

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

// TARJETA DE UNA ETAPA METODOLÓGICA
@Composable
private fun TarjetaEtapaAccesible(
    etapa: Etapa,
    preferences: EntregablesPreferences,
    onClick: () -> Unit
) {

    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    // Recuperar el estado guardado en DataStore
    val flujoEstado = remember(
        preferences,
        etapa.id
    ) {
        preferences.obtenerEstado(etapa.id)
    }

    val estado by flujoEstado.collectAsState(
        initial = etapa.estado
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),

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

            // NÚMERO DE ETAPA
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
                        text = etapa.id.toString()
                            .padStart(2, '0'),

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

            // INFORMACIÓN DE LA ETAPA
            Column(
                modifier = Modifier.weight(1f),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = etapa.titulo,

                    color = colores.texto,

                    fontSize = 17.sp,

                    lineHeight = 23.sp,

                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = etapa.descripcion,

                    color = colores.textoSecundario,

                    fontSize = 13.sp,

                    lineHeight = 19.sp
                )

                // ESTADO ACADÉMICO
                Surface(
                    color = if (altoContraste) {
                        Color.White
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
                            horizontal = 12.dp,
                            vertical = 6.dp
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

                        fontSize = 12.sp,

                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // FLECHA AL DETALLE
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
