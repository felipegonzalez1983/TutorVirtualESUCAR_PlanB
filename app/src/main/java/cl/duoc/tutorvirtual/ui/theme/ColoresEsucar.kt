package cl.duoc.tutorvirtual.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ColoresEsucar(
    val fondo: Color,
    val tarjeta: Color,
    val texto: Color,
    val textoSecundario: Color,
    val encabezado: Color,
    val textoEncabezado: Color,
    val acento: Color,
    val borde: Color
)

// Paleta normal
private val coloresNormales = ColoresEsucar(
    fondo = Color(0xFFF5F7F3),
    tarjeta = Color.White,
    texto = Color(0xFF174B35),
    textoSecundario = Color(0xFF526259),
    encabezado = Color(0xFF174B35),
    textoEncabezado = Color.White,
    acento = Color(0xFFC7A34D),
    borde = Color(0xFFD3DDD5)
)

// Paleta de alto contraste
private val coloresAltoContraste = ColoresEsucar(
    fondo = Color.Black,
    tarjeta = Color.Black,
    texto = Color.White,
    textoSecundario = Color.White,
    encabezado = Color.Black,
    textoEncabezado = Color.White,
    acento = Color.Yellow,
    borde = Color.White
)

// Colores disponibles para todas las pantallas
val LocalColoresEsucar = staticCompositionLocalOf {
    coloresNormales
}

@Composable
fun ProveedorColoresEsucar(
    altoContraste: Boolean,
    contenido: @Composable () -> Unit
) {
    val colores = if (altoContraste) {
        coloresAltoContraste
    } else {
        coloresNormales
    }

    CompositionLocalProvider(
        LocalColoresEsucar provides colores
    ) {
        contenido()
    }
}
