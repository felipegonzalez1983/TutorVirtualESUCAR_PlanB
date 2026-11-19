package cl.duoc.tutorvirtual.data

// ============================================
// ESTUDIANTES FICTICIOS PARA PRUEBAS DE ESUCAR
// ============================================

data class EstudianteDemo(
    val identificador: String,
    val nombre: String,
    val curso: String,
    val claveDemo: String
)

object EstudiantesDemoRepository {

    // Cuentas exclusivamente de demostración.
    // No contienen información real de alumnos.
    private val estudiantes = listOf(
        EstudianteDemo(
            identificador = "DEMO-001",
            nombre = "Estudiante de Prueba Uno",
            curso = "Curso A",
            claveDemo = "Demo2026!"
        ),
        EstudianteDemo(
            identificador = "DEMO-002",
            nombre = "Estudiante de Prueba Dos",
            curso = "Curso B",
            claveDemo = "Demo2026!"
        ),
        EstudianteDemo(
            identificador = "DEMO-003",
            nombre = "Estudiante de Prueba Tres",
            curso = "Curso C",
            claveDemo = "Demo2026!"
        )
    )

    // VALIDAR IDENTIFICADOR Y CONTRASEÑA
    fun autenticar(
        identificador: String,
        clave: String
    ): EstudianteDemo? {

        return estudiantes.firstOrNull {
            it.identificador.equals(
                identificador.trim(),
                ignoreCase = true
            ) && it.claveDemo == clave
        }?.copy(
            claveDemo = ""
        )
    }

    // RECUPERAR UN ESTUDIANTE FICTICIO POR SU ID
    // Esta función solo sirve para restaurar
    // una sesión de demostración previamente guardada.
    fun obtenerPorIdentificador(
        identificador: String
    ): EstudianteDemo? {

        return estudiantes.firstOrNull {
            it.identificador.equals(
                identificador.trim(),
                ignoreCase = true
            )
        }?.copy(
            claveDemo = ""
        )
    }

    fun obtenerCantidadEstudiantes(): Int {
        return estudiantes.size
    }
}
