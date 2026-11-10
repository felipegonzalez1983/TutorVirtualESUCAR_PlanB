
package cl.duoc.tutorvirtual.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

import cl.duoc.tutorvirtual.R

object NotificacionesHelper {

    const val CANAL_ID = "recordatorios_esucar"

    // Crear canal de notificaciones
    fun crearCanal(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val canal = NotificationChannel(
                CANAL_ID,
                "Recordatorios académicos",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description =
                    "Avisos de próximas entregas y hitos académicos"
            }

            val administrador = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            administrador.createNotificationChannel(canal)
        }
    }

    // Mostrar una notificación de prueba
    fun mostrarNotificacionPrueba(
        context: Context
    ): Boolean {

        // Comprobar autorización en Android 13+
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            val autorizado =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (!autorizado) {
                return false
            }
        }

        // Comprobar que las notificaciones estén habilitadas
        val administrador = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (!androidx.core.app.NotificationManagerCompat
                .from(context)
                .areNotificationsEnabled()
        ) {
            return false
        }

        crearCanal(context)

        // Construir el aviso de prueba
        val notificacion = NotificationCompat.Builder(
            context,
            CANAL_ID
        )
            .setSmallIcon(
                R.drawable.ic_launcher_foreground
            )
            .setContentTitle(
                "Tutor Virtual ESUCAR"
            )
            .setContentText(
                "¡Prueba de notificación enviada!"
            )
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Esta es una notificación de prueba " +
                            "del calendario académico ESUCAR."
                )
            )
            .setPriority(
                NotificationCompat.PRIORITY_DEFAULT
            )
            .setAutoCancel(true)
            .build()

        // Solicitar a Android que muestre el aviso
        administrador.notify(
            9999,
            notificacion
        )

        return true
    }
}
