package cl.duoc.tutorvirtual.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import cl.duoc.tutorvirtual.ui.TutorVirtualApp
import cl.duoc.tutorvirtual.ui.screens.LoginScreen
import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar
import cl.duoc.tutorvirtual.viewmodel.LoginViewModel

import kotlinx.coroutines.launch

// ======================================================
// TUTOR VIRTUAL ESUCAR - NAVEGACION PRINCIPAL
//
// Login estudiantil
// Sesion persistente con DataStore
// Room + SQLite
// CRUD de entregables
// Diagnostico biometrico
// Camara academica
// Selector PDF
// Adjuntar PDF a un entregable
// Ubicacion GPS del trabajo de campo
// ======================================================

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel()
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    // ==============================================
    // AUTENTICACION ESTUDIANTIL
    // ==============================================

    val estudianteActual by
    loginViewModel.estudianteActual.collectAsState()

    val restaurandoSesion by
    loginViewModel.restaurandoSesion.collectAsState()

    val mensajeLoginHost = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    // ==============================================
    // RECUPERAR SESION DE DATASTORE
    // ==============================================

    if (restaurandoSesion) {

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colores.fondo),

            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier.padding(24.dp),

                horizontalAlignment = Alignment.CenterHorizontally,

                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                CircularProgressIndicator(
                    color = if (altoContraste) {
                        Color.Yellow
                    } else {
                        colores.acento
                    }
                )

                Text(
                    text = "Tutor Virtual ESUCAR",
                    color = colores.texto,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Verificando sesión estudiantil...",
                    color = colores.textoSecundario,
                    fontSize = 14.sp
                )
            }
        }

        return
    }

    // ==============================================
    // LOGIN SIN SESION ACTIVA
    // ==============================================

    if (estudianteActual == null) {

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colores.fondo)
        ) {

            LoginScreen(
                modifier = Modifier.fillMaxSize(),

                onIniciarSesion = { identificador, contrasena ->

                    val ingresoCorrecto =
                        loginViewModel.iniciarSesion(
                            identificador = identificador,
                            contrasena = contrasena
                        )

                    if (ingresoCorrecto) {

                        loginViewModel.limpiarError()

                    } else {

                        val mensaje =
                            loginViewModel.mensajeError.value
                                ?: "No se pudo iniciar sesión."

                        scope.launch {

                            mensajeLoginHost.showSnackbar(
                                message = mensaje,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            )

            SnackbarHost(
                hostState = mensajeLoginHost,

                modifier = Modifier.align(
                    Alignment.BottomCenter
                )
            ) { data ->

                Snackbar(
                    snackbarData = data,

                    containerColor = if (altoContraste) {
                        Color.Yellow
                    } else {
                        colores.encabezado
                    },

                    contentColor = if (altoContraste) {
                        Color.Black
                    } else {
                        Color.White
                    }
                )
            }
        }

        return
    }

    // ==============================================
    // ESTUDIANTE AUTENTICADO
    // ==============================================

    TutorVirtualApp(
        nombreEstudiante = estudianteActual?.nombre ?: "Estudiante",
        cursoEstudiante = estudianteActual?.curso ?: "",
        onCerrarSesion = { loginViewModel.cerrarSesion() },
        modifier = modifier
    )
}
