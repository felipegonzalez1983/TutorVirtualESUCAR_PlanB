package cl.duoc.tutorvirtual

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

import cl.duoc.tutorvirtual.data.AccesibilidadPreferences
import cl.duoc.tutorvirtual.navigation.AppNavigation
import cl.duoc.tutorvirtual.notifications.NotificacionesHelper
import cl.duoc.tutorvirtual.ui.theme.ProveedorColoresEsucar

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear el canal de notificaciones académicas
        NotificacionesHelper.crearCanal(
            applicationContext
        )

        setContent {

            val context = LocalContext.current

            // Acceder a las preferencias de accesibilidad
            val accesibilidadPreferences = remember(context) {
                AccesibilidadPreferences(
                    context.applicationContext
                )
            }

            // Recuperar el modo de alto contraste guardado
            val flujoAltoContraste = remember(
                accesibilidadPreferences
            ) {
                accesibilidadPreferences.obtenerAltoContraste()
            }

            val altoContraste by flujoAltoContraste.collectAsState(
                initial = false
            )

            // Solicitar permiso para notificaciones
            val permisoNotificaciones =
                rememberLauncherForActivityResult(
                    contract =
                        ActivityResultContracts.RequestPermission()
                ) { autorizado ->

                    // El resultado queda disponible para
                    // futuras mejoras de los recordatorios.
                }

            // Verificar permiso en Android 13 o superior
            LaunchedEffect(Unit) {

                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.TIRAMISU
                ) {

                    val permisoConcedido =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                    if (!permisoConcedido) {

                        permisoNotificaciones.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                }
            }

            // Aplicar la configuración visual a la aplicación
            MaterialTheme {

                ProveedorColoresEsucar(
                    altoContraste = altoContraste
                ) {

                    // Mantener nuestra navegación existente
                    AppNavigation()
                }
            }
        }
    }
}
