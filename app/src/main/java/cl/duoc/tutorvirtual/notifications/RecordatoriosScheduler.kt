
package cl.duoc.tutorvirtual.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object RecordatoriosScheduler {

    // Crear el identificador único de cada aviso
    private fun obtenerId(
        hitoId: Int,
        diasAntes: Int
    ): Int {
        return hitoId * 10 + diasAntes
    }

    // Crear el PendingIntent del recordatorio
    private fun crearPendingIntent(
        context: Context,
        hitoId: Int,
        titulo: String,
        diasAntes: Int
    ): PendingIntent {

        val id = obtenerId(hitoId, diasAntes)

        val intent = Intent(
            context,
            RecordatorioReceiver::class.java
        ).apply {
            putExtra("hito_id", hitoId)
            putExtra("titulo_hito", titulo)
            putExtra("dias_antes", diasAntes)
            putExtra("id_notificacion", id)
        }

        return PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    // Programar los avisos de un hito
    fun programarRecordatorios(
        context: Context,
        hitoId: Int,
        titulo: String,
        fecha: String
    ) {

        // Eliminar avisos anteriores del mismo hito
        cancelarRecordatorios(
            context,
            hitoId
        )

        val formato = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.ROOT
        ).apply {
            isLenient = false
        }

        val fechaEntrega = try {
            formato.parse(fecha)
        } catch (e: Exception) {
            null
        } ?: return

        val administrador = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        // Crear avisos para 3 y 1 día antes
        listOf(3, 1).forEach { diasAntes ->

            val calendario = Calendar.getInstance().apply {
                time = fechaEntrega

                // Restar días a la fecha de entrega
                add(Calendar.DAY_OF_MONTH, -diasAntes)

                // Programar a las 9:00 AM
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val momentoAviso = calendario.timeInMillis

            // No programar recordatorios en el pasado
            if (momentoAviso > System.currentTimeMillis()) {

                val pendingIntent = crearPendingIntent(
                    context = context,
                    hitoId = hitoId,
                    titulo = titulo,
                    diasAntes = diasAntes
                )

                // Alarma aproximada compatible con API 24+
                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.M
                ) {
                    administrador.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        momentoAviso,
                        pendingIntent
                    )
                } else {
                    administrador.set(
                        AlarmManager.RTC_WAKEUP,
                        momentoAviso,
                        pendingIntent
                    )
                }
            }
        }
    }

    // Cancelar ambos avisos de un hito
    fun cancelarRecordatorios(
        context: Context,
        hitoId: Int
    ) {

        val administrador = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        listOf(3, 1).forEach { diasAntes ->

            val pendingIntent = crearPendingIntent(
                context = context,
                hitoId = hitoId,
                titulo = "",
                diasAntes = diasAntes
            )

            administrador.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
