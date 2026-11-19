package cl.duoc.tutorvirtual

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.FragmentActivity

import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

import cl.duoc.tutorvirtual.data.AccesibilidadPreferences
import cl.duoc.tutorvirtual.navigation.AppNavigation
import cl.duoc.tutorvirtual.notifications.NotificacionesHelper
import cl.duoc.tutorvirtual.ui.theme.ProveedorColoresEsucar
import cl.duoc.tutorvirtual.viewmodel.EsucarDatabaseViewModel

// ======================================================
// ACTIVIDAD PRINCIPAL - TUTOR VIRTUAL ESUCAR
//
// FragmentActivity permite utilizar BiometricPrompt
// para autenticacion con huella o rostro compatible.
//
// Conservamos Compose, Room, DataStore,
// accesibilidad y notificaciones.
// ======================================================

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ==========================================
        // CANAL DE NOTIFICACIONES ACADEMICAS
        // ==========================================

        NotificacionesHelper.crearCanal(
            applicationContext
        )

        // ==========================================
        // INTERFAZ JETPACK COMPOSE
        // ==========================================

        setContent {

            val context = LocalContext.current

            // ======================================
            // INICIALIZACION DE ROOM + SQLITE
            // ======================================

            val databaseViewModel: EsucarDatabaseViewModel =
                viewModel()

            val baseDatosLista by
            databaseViewModel.baseDatosLista.collectAsState()

            val mensajeError by
            databaseViewModel.mensajeError.collectAsState()

            // Registrar resultados en Logcat
            LaunchedEffect(
                baseDatosLista,
                mensajeError
            ) {

                if (baseDatosLista) {

                    Log.i(
                        "ESUCAR_DATABASE",
                        "Base de datos inicializada. " +
                                "Las etapas estan registradas en SQLite."
                    )
                }

                if (mensajeError != null) {

                    Log.e(
                        "ESUCAR_DATABASE",
                        "Error al inicializar SQLite: " +
                                mensajeError
                    )
                }
            }

            // ======================================
            // ACCESIBILIDAD
            // ======================================

            val accesibilidadPreferences =
                remember(context) {

                    AccesibilidadPreferences(
                        context.applicationContext
                    )
                }

            // Recuperar el modo de alto contraste
            val flujoAltoContraste =
                remember(accesibilidadPreferences) {

                    accesibilidadPreferences
                        .obtenerAltoContraste()
                }

            val altoContraste by
            flujoAltoContraste.collectAsState(
                initial = false
            )

            // ======================================
            // PERMISOS DE NOTIFICACIONES
            // ======================================

            val permisoNotificaciones =
                rememberLauncherForActivityResult(
                    contract =
                        ActivityResultContracts.RequestPermission()
                ) { autorizado ->

                    // Resultado disponible para futuras
                    // mejoras de las notificaciones.
                }

            // Android 13 o superior
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

            // ======================================
            // INTERFAZ INSTITUCIONAL ESUCAR
            // ======================================

            MaterialTheme {

                ProveedorColoresEsucar(
                    altoContraste = altoContraste
                ) {

                    // Conservamos la navegacion,
                    // login, CRUD y todas las pantallas.
                    AppNavigation()
                }
            }
        }
    }
}
