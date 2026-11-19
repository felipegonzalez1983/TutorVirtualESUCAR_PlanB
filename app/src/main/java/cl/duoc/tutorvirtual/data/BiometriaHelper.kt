package cl.duoc.tutorvirtual.data

import android.content.Context
import androidx.biometric.BiometricManager

// ======================================================
// VERIFICADOR DE BIOMETRIA
// TUTOR VIRTUAL ESUCAR - PLAN B
// ======================================================
//
// Comprueba si el dispositivo admite biometria.
//
// No solicita huellas.
// No captura rostros.
// No almacena datos biometricos.
//
// ======================================================

data class EstadoBiometria(
    val disponible: Boolean,
    val mensaje: String
)

object BiometriaHelper {

    // ==============================================
    // COMPROBAR DISPONIBILIDAD BIOMETRICA
    // ==============================================

    fun verificarDisponibilidad(
        context: Context
    ): EstadoBiometria {

        val biometricManager =
            BiometricManager.from(context)

        // Permite consultar biometria compatible,
        // incluida biometria clasificada como debil
        // o fuerte por Android.
        val autenticadores =
            BiometricManager.Authenticators.BIOMETRIC_WEAK

        val resultado =
            biometricManager.canAuthenticate(
                autenticadores
            )

        return when (resultado) {

            BiometricManager.BIOMETRIC_SUCCESS -> {

                EstadoBiometria(
                    disponible = true,
                    mensaje = "La autenticación biométrica " +
                            "está disponible y configurada."
                )
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {

                EstadoBiometria(
                    disponible = false,
                    mensaje = "Este dispositivo no dispone " +
                            "de hardware biométrico compatible."
                )
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {

                EstadoBiometria(
                    disponible = false,
                    mensaje = "El sistema biométrico no está " +
                            "disponible en este momento."
                )
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {

                EstadoBiometria(
                    disponible = false,
                    mensaje = "El dispositivo admite " +
                            "biometría, pero no tiene un " +
                            "método biométrico configurado."
                )
            }

            else -> {

                EstadoBiometria(
                    disponible = false,
                    mensaje = "Android no pudo confirmar " +
                            "la disponibilidad biométrica. " +
                            "Código: $resultado"
                )
            }
        }
    }
}
