package cl.duoc.tutorvirtual.notifications

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

import cl.duoc.tutorvirtual.R
import cl.duoc.tutorvirtual.data.HitosPreferences

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RecordatorioReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val hitoId = intent.getIntExtra(
            "hito_id",
            -1
        )

        if (hitoId == -1) return

        val tituloHito = intent.getStringExtra(
            "titulo_hito"
        ) ?: return

        val diasAntes = intent.getIntExtra(
            "dias_antes",
            1
        )

        val idNotificacion = intent.getIntExtra(
            "id_notificacion",
            100
        )

        // Permitir consultar DataStore antes de terminar
        val resultadoPendiente = goAsync()

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val preferences = HitosPreferences(
                    context.applicationContext
                )

                // Consultar los valores actuales
                val completado = preferences
                    .obtenerCompletado(hitoId)
                    .first()

                val activos = preferences
                    .obtenerRecordatoriosActivos(hitoId)
                    .first()

                val fecha = preferences
                    .obtenerFecha(hitoId)
                    .first()

                // No avisar si el hito ya está realizado
                if (completado) return@launch

                // No avisar si el estudiante desactivó avisos
                if (!activos) return@launch

                // No avisar si ya no existe fecha
                if (fecha == null) return@launch

                // Verificar permiso en Android 13+
                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.TIRAMISU
                ) {

                    val permiso =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        )

                    if (
                        permiso !=
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        return@launch
                    }
                }

                val mensaje = when (diasAntes) {
                    1 -> "Tu entrega de $tituloHito vence mañana."
                    3 -> "Tu entrega de $tituloHito vence en 3 días."
                    else -> "Tienes una entrega académica próxima."
                }

                val notificacion =
                    NotificationCompat.Builder(
                        context,
                        NotificacionesHelper.CANAL_ID
                    )
                        .setSmallIcon(
                            R.drawable.ic_launcher_foreground
                        )
                        .setContentTitle(
                            "Recordatorio académico ESUCAR"
                        )
                        .setContentText(mensaje)
                        .setStyle(
                            NotificationCompat.BigTextStyle()
                                .bigText(mensaje)
                        )
                        .setPriority(
                            NotificationCompat.PRIORITY_DEFAULT
                        )
                        .setAutoCancel(true)
                        .build()

                val administrador =
                    context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                    ) as NotificationManager

                administrador.notify(
                    idNotificacion,
                    notificacion
                )

            } finally {

                // Informar a Android que terminamos
                resultadoPendiente.finish()
            }
        }
    }
}
