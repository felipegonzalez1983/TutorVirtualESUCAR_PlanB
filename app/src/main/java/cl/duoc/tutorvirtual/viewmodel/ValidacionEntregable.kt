package cl.duoc.tutorvirtual.viewmodel

import java.text.SimpleDateFormat
import java.util.Locale

// Reglas de validación de los formularios de entregables.
// No depende de la interfaz: los formularios solo muestran el mensaje que devuelve.
object ValidacionEntregable {

    const val MINIMO_DESCRIPCION = 10
    const val FECHA_POR_DEFINIR = "Por definir"

    fun errorTitulo(titulo: String): String? {
        if (titulo.trim().isEmpty()) {
            return "Debes ingresar un título."
        }
        return null
    }

    fun errorDescripcion(descripcion: String): String? {
        if (descripcion.trim().isEmpty()) {
            return "Debes ingresar una descripción."
        }
        if (descripcion.trim().length < MINIMO_DESCRIPCION) {
            return "Escribe al menos $MINIMO_DESCRIPCION caracteres."
        }
        return null
    }

    fun errorFecha(fecha: String, obligatoria: Boolean = true): String? {
        val limpia = fecha.trim()
        if (limpia.isEmpty() && !obligatoria) {
            return null
        }
        if (!esFechaValida(limpia)) {
            return "La fecha debe tener formato AAAA-MM-DD."
        }
        return null
    }

    private fun esFechaValida(fecha: String): Boolean {
        if (!Regex("""\d{4}-\d{2}-\d{2}""").matches(fecha)) {
            return false
        }

        return try {
            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            formato.isLenient = false
            formato.parse(fecha) != null
        } catch (e: Exception) {
            false
        }
    }
}
