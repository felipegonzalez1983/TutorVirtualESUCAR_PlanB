package cl.duoc.tutorvirtual.data

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

// ==================================================
// AUTENTICACION BIOMETRICA
// TUTOR VIRTUAL ESUCAR - PLAN B
//
// Usa la ventana oficial de seguridad Android.
//
// No captura huellas ni rostros.
// No almacena datos biometricos.
// No cambia la sesion del estudiante.
//
// IMPORTANTE:
// Este componente no inicia sesion por si solo.
// La vinculacion con una cuenta demo se hara
// posteriormente, con autorizacion del usuario.
// ==================================================

object AutenticacionBiometricaHelper {

    // ==============================================
    // MOSTRAR LA VENTANA BIOMETRICA
    // ==============================================

    fun solicitarAutenticacion(
        activity: FragmentActivity,
        onAutenticacionCorrecta: () -> Unit,
        onError: (String) -> Unit,
        onIntentoFallido: () -> Unit = {}
    ) {

        // Verificar disponibilidad antes de abrir.
        val estado =
            BiometriaHelper.verificarDisponibilidad(
                activity
            )

        if (!estado.disponible) {

            onError(estado.mensaje)

            return
        }

        // ==========================================
        // RECIBIR EL RESULTADO DE ANDROID
        // ==========================================

        val biometricPrompt = BiometricPrompt(
            activity,

            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {

                    super.onAuthenticationSucceeded(result)

                    // Android confirmo la identidad
                    // biometrica del usuario del dispositivo.
                    onAutenticacionCorrecta()
                }

                override fun onAuthenticationFailed() {

                    super.onAuthenticationFailed()

                    // No coincide la huella o rostro.
                    // Android permite intentar nuevamente.
                    onIntentoFallido()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {

                    super.onAuthenticationError(
                        errorCode,
                        errString
                    )

                    // Cancelacion u otro error del sistema.
                    onError(errString.toString())
                }
            }
        )

        // ==========================================
        // CONFIGURACION DE LA VENTANA ANDROID
        // ==========================================

        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()

                .setTitle(
                    "Seguridad biométrica ESUCAR"
                )

                .setSubtitle(
                    "Verificación del dispositivo"
                )

                .setDescription(
                    "Utiliza la huella o el método " +
                            "biométrico compatible configurado " +
                            "en este dispositivo."
                )

                // Se utiliza el mismo nivel de
                // compatibilidad que nuestro diagnostico.
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
                )

                // Permite cancelar y continuar
                // usando el acceso tradicional.
                .setNegativeButtonText(
                    "Cancelar"
                )

                .build()

        // ==========================================
        // INICIAR LA VERIFICACION BIOMETRICA
        // ==========================================

        biometricPrompt.authenticate(promptInfo)
    }
}
