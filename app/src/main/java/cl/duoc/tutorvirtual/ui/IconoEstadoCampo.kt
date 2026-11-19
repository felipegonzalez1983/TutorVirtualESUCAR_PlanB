package cl.duoc.tutorvirtual.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import cl.duoc.tutorvirtual.ui.theme.VerdeESUCAR

// Ícono al final de un campo: error, campo correcto o nada si aún está vacío
@Composable
fun IconoEstadoCampo(hayError: Boolean, esCorrecto: Boolean) {
    if (hayError) {
        Icon(Icons.Default.Error, contentDescription = "Campo con error")
    } else if (esCorrecto) {
        Icon(Icons.Default.CheckCircle, contentDescription = "Campo correcto", tint = VerdeESUCAR)
    }
}
