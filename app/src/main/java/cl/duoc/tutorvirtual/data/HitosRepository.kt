package cl.duoc.tutorvirtual.data

import cl.duoc.tutorvirtual.model.Hito

object HitosRepository {

    // Fechas tomadas del calendario académico ESUCAR 2026.
    // El calendario indica que las fechas pueden ser modificadas.

    private val hitos = listOf(
        Hito(
            id = 1,
            titulo = "Estado de avance - abril",
            descripcion = "Estado de avance de Trabajo de Título I y II.",
            fechaLimite = "2026-04-24"
        ),
        Hito(
            id = 2,
            titulo = "Estado de avance - mayo",
            descripcion = "Estado de avance de Trabajo de Título I y II.",
            fechaLimite = "2026-05-29"
        ),
        Hito(
            id = 3,
            titulo = "Informe final - primer semestre",
            descripcion = "Entrega del informe final de Trabajo de Título I y II.",
            fechaLimite = "2026-06-19"
        ),
        Hito(
            id = 4,
            titulo = "Entrega de temas de Trabajo de Título II",
            descripcion = "Entrega de temas a Jefatura de Estudio (del 6 al 8 de julio).",
            fechaLimite = "2026-07-06"
        ),
        Hito(
            id = 5,
            titulo = "Validación del tema a investigar",
            descripcion = "Resultado de la validación del tema de Trabajo de Título II.",
            fechaLimite = "2026-07-11"
        ),
        Hito(
            id = 6,
            titulo = "Estado de avance - agosto",
            descripcion = "Estado de avance de Trabajo de Título I y II.",
            fechaLimite = "2026-08-28"
        ),
        Hito(
            id = 7,
            titulo = "Estado de avance - octubre",
            descripcion = "Estado de avance de Trabajo de Título I y II.",
            fechaLimite = "2026-10-02"
        ),
        Hito(
            id = 8,
            titulo = "Informe final - segundo semestre",
            descripcion = "Entrega del informe final de Trabajo de Título I y II.",
            fechaLimite = "2026-11-06"
        ),
        Hito(
            id = 9,
            titulo = "Defensas solemnes de Trabajo de Título II",
            descripcion = "Semana de defensas solemnes (del 16 al 20 de noviembre).",
            fechaLimite = "2026-11-16"
        ),
        Hito(
            id = 10,
            titulo = "Repetición de defensas solemnes",
            descripcion = "Semana de repetición de defensas (del 22 al 26 de noviembre).",
            fechaLimite = "2026-11-22"
        ),
        Hito(
            id = 11,
            titulo = "Entrega de temas de Trabajo de Título II",
            descripcion = "Entrega de temas a Jefatura de Estudio (del 7 al 11 de diciembre).",
            fechaLimite = "2026-12-07"
        ),
        Hito(
            id = 12,
            titulo = "Validación del tema a investigar",
            descripcion = "Resultado de la validación del tema de Trabajo de Título II.",
            fechaLimite = "2026-12-18"
        )
    )

    fun obtenerHitos(): List<Hito> {
        return hitos
    }

    fun obtenerHitoPorId(id: Int): Hito? {
        return hitos.find { it.id == id }
    }
}
