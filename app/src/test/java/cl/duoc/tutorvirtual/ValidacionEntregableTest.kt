package cl.duoc.tutorvirtual

import cl.duoc.tutorvirtual.viewmodel.ValidacionEntregable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionEntregableTest {

    @Test
    fun titulo_vacio_devuelve_error() {
        assertEquals("Debes ingresar un título.", ValidacionEntregable.errorTitulo("   "))
    }

    @Test
    fun titulo_con_texto_es_valido() {
        assertNull(ValidacionEntregable.errorTitulo("Marco teórico"))
    }

    @Test
    fun descripcion_corta_devuelve_error() {
        assertNotNull(ValidacionEntregable.errorDescripcion("Muy corta"))
    }

    @Test
    fun descripcion_suficiente_es_valida() {
        assertNull(ValidacionEntregable.errorDescripcion("Avance del capítulo dos"))
    }

    @Test
    fun fecha_con_formato_correcto_es_valida() {
        assertNull(ValidacionEntregable.errorFecha("2026-12-10"))
    }

    @Test
    fun fecha_inexistente_devuelve_error() {
        assertNotNull(ValidacionEntregable.errorFecha("2026-02-31"))
    }

    @Test
    fun fecha_vacia_depende_de_si_es_obligatoria() {
        assertNotNull(ValidacionEntregable.errorFecha("", obligatoria = true))
        assertNull(ValidacionEntregable.errorFecha("", obligatoria = false))
    }
}
