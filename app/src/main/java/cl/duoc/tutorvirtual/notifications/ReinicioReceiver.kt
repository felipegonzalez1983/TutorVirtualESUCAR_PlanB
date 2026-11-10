
package cl.duoc.tutorvirtual.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import cl.duoc.tutorvirtual.data.HitosPreferences
import cl.duoc.tutorvirtual.data.HitosRepository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReinicioReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        // Ejecutar solamente después del reinicio
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        val pendiente = goAsync()

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val preferences = HitosPreferences(
                    context.applicationContext
                )

                val hitos = HitosRepository.obtenerHitos()

                // Revisar cada hito académico
                hitos.forEach { hito ->

                    val completado = preferences
                        .obtenerCompletado(hito.id)
                        .first()

                    val activos = preferences
                        .obtenerRecordatoriosActivos(hito.id)
                        .first()

                    val fecha = preferences
                        .obtenerFecha(hito.id)
                        .first()

                    // Recuperar solo recordatorios válidos
                    if (
                        activos &&
                        !completado &&
                        fecha != null
                    ) {

                        RecordatoriosScheduler
                            .programarRecordatorios(
                                context = context,
                                hitoId = hito.id,
                                titulo = hito.titulo,
                                fecha = fecha
                            )
                    }
                }

            } finally {

                // Informar a Android que finalizó el trabajo
                pendiente.finish()
            }
        }
    }
}
