package cl.duoc.tutorvirtual.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsucarColorScheme = lightColorScheme(
    primary = VerdeESUCAR, onPrimary = Blanco,
    secondary = DoradoESUCAR, onSecondary = GrisOscuroESUCAR,
    tertiary = GrisOscuroESUCAR, onTertiary = Blanco,
    background = MarfilESUCAR, onBackground = GrisOscuroESUCAR,
    surface = Blanco, onSurface = GrisOscuroESUCAR,
    surfaceVariant = VerdeClaro, onSurfaceVariant = GrisOscuroESUCAR,
    error = RojoError, onError = Blanco
)

@Composable
fun TutorVirtualESUCARTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsucarColorScheme, typography = Typography, content = content)
}
