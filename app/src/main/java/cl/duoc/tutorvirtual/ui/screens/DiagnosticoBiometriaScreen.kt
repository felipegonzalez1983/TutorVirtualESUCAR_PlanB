package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.fragment.app.FragmentActivity

import cl.duoc.tutorvirtual.data.BiometriaHelper
import cl.duoc.tutorvirtual.data.AutenticacionBiometricaHelper

// ==================================================
// DIAGNOSTICO Y PRUEBA BIOMETRICA
// TUTOR VIRTUAL ESUCAR - PLAN B
//
// Consulta la disponibilidad biometrica.
// Permite probar la ventana oficial de Android.
//
// NO inicia sesion.
// NO almacena huellas ni rostros.
// NO modifica los datos del estudiante.
// ==================================================

@Composable
fun DiagnosticoBiometriaScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit
) {

    val context = LocalContext.current

    val activity = context as? FragmentActivity

    val estadoBiometria = remember(context) {
        BiometriaHelper.verificarDisponibilidad(context)
    }

    var mensajeResultado by remember {
        mutableStateOf(
            "Todavía no se ha realizado una prueba."
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.spacedBy(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Seguridad de ESUCAR",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Diagnóstico de autenticación biométrica",
            style = MaterialTheme.typography.bodyLarge
        )

        // ==========================================
        // ESTADO DEL DISPOSITIVO
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = if (estadoBiometria.disponible) {
                        "Biometría disponible"
                    } else {
                        "Biometría no disponible"
                    },

                    fontWeight = FontWeight.Bold,

                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = estadoBiometria.mensaje,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // ==========================================
        // BOTON PARA PROBAR BIOMETRIA
        // ==========================================

        Button(
            onClick = {

                val actividadActual = activity

                if (actividadActual == null) {

                    mensajeResultado =
                        "No se encontró una actividad " +
                                "compatible para la biometría."

                } else {

                    mensajeResultado =
                        "Esperando autenticación..."

                    AutenticacionBiometricaHelper
                        .solicitarAutenticacion(

                            activity = actividadActual,

                            onAutenticacionCorrecta = {

                                mensajeResultado =
                                    "Autenticación biométrica " +
                                            "correcta. Android confirmó " +
                                            "la verificación del dispositivo."
                            },

                            onError = { error ->

                                mensajeResultado =
                                    "Prueba cancelada o " +
                                            "interrumpida: $error"
                            },

                            onIntentoFallido = {

                                mensajeResultado =
                                    "No se reconoció la biometría. " +
                                            "Puedes intentarlo nuevamente."
                            }
                        )
                }
            },

            enabled = estadoBiometria.disponible &&
                    activity != null,

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Probar autenticación biométrica")
        }

        // ==========================================
        // RESULTADO DE LA PRUEBA
        // ==========================================

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Resultado de la prueba",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = mensajeResultado,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // ==========================================
        // INFORMACION DE SEGURIDAD
        // ==========================================

        Text(
            text = "Esta prueba utiliza la ventana " +
                    "oficial de Android. No activa el " +
                    "inicio de sesión biométrico ni " +
                    "almacena datos biométricos.",

            style = MaterialTheme.typography.bodySmall
        )

        // ==========================================
        // REGRESAR A RECURSOS
        // ==========================================

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Volver a Recursos")
        }
    }
}
