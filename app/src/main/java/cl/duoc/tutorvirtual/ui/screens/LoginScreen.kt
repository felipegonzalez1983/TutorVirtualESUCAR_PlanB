package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

// ==================================================
// LOGIN ESTUDIANTIL - TUTOR VIRTUAL ESUCAR
// INTERFAZ INICIAL, SIN AUTENTICACION TODAVIA
// ==================================================

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onIniciarSesion: (String, String) -> Unit = { _, _ -> }
) {

    val colores = LocalColoresEsucar.current
    val altoContraste = colores.fondo == Color.Black

    var rut by rememberSaveable {
        mutableStateOf("")
    }

    // No guardar la contraseña con rememberSaveable
    var contrasena by remember {
        mutableStateOf("")
    }

    var mostrarContrasena by remember {
        mutableStateOf(false)
    }

    var mensajeError by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ENCABEZADO INSTITUCIONAL
        Surface(
            modifier = Modifier.fillMaxWidth(),

            color = colores.encabezado,

            shape = RoundedCornerShape(24.dp),

            border = if (altoContraste) {
                BorderStroke(1.dp, colores.borde)
            } else {
                null
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),

                horizontalAlignment = Alignment.CenterHorizontally,

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "ESUCAR",
                    color = colores.acento,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Tutor Virtual",
                    color = colores.textoEncabezado,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Acceso para estudiantes",
                    color = colores.textoEncabezado,
                    fontSize = 15.sp
                )
            }
        }

        // FORMULARIO
        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(20.dp),

            colors = CardDefaults.cardColors(
                containerColor = colores.tarjeta
            ),

            border = if (altoContraste) {
                BorderStroke(1.dp, colores.borde)
            } else {
                null
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),

                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                Text(
                    text = "Iniciar sesión",
                    color = colores.texto,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Ingresa tus credenciales " +
                            "de estudiante para acceder.",

                    color = colores.textoSecundario,
                    fontSize = 14.sp
                )

                // RUT
                OutlinedTextField(
                    value = rut,

                    onValueChange = {
                        rut = it
                        mensajeError = null
                    },

                    label = {
                        Text("RUN / RUT")
                    },

                    placeholder = {
                        Text("Ej.: 12.345.678-9")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),

                    shape = RoundedCornerShape(12.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colores.texto,
                        unfocusedTextColor = colores.texto,
                        focusedContainerColor = colores.tarjeta,
                        unfocusedContainerColor = colores.tarjeta,
                        focusedBorderColor = colores.acento,
                        unfocusedBorderColor = colores.borde
                    )
                )

                // CONTRASEÑA
                OutlinedTextField(
                    value = contrasena,

                    onValueChange = {
                        contrasena = it
                        mensajeError = null
                    },

                    label = {
                        Text("Contraseña")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    visualTransformation =
                        if (mostrarContrasena) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),

                    trailingIcon = {
                        TextButton(
                            onClick = {
                                mostrarContrasena =
                                    !mostrarContrasena
                            }
                        ) {
                            Text(
                                text = if (mostrarContrasena) {
                                    "Ocultar"
                                } else {
                                    "Mostrar"
                                },

                                color = colores.texto
                            )
                        }
                    },

                    shape = RoundedCornerShape(12.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colores.texto,
                        unfocusedTextColor = colores.texto,
                        focusedContainerColor = colores.tarjeta,
                        unfocusedContainerColor = colores.tarjeta,
                        focusedBorderColor = colores.acento,
                        unfocusedBorderColor = colores.borde
                    )
                )

                if (mensajeError != null) {

                    Text(
                        text = mensajeError.orEmpty(),
                        color = if (altoContraste) {
                            Color.Yellow
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        fontSize = 13.sp
                    )
                }

                // BOTÓN DE ACCESO
                Button(
                    onClick = {

                        when {

                            rut.isBlank() -> {
                                mensajeError =
                                    "Ingresa tu RUN/RUT."
                            }

                            contrasena.isBlank() -> {
                                mensajeError =
                                    "Ingresa tu contraseña."
                            }

                            else -> {
                                mensajeError = null

                                // La autenticación real o de
                                // demostración se implementará
                                // en el siguiente paso.
                                onIniciarSesion(
                                    rut.trim(),
                                    contrasena
                                )
                            }
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 54.dp),

                    shape = RoundedCornerShape(12.dp),

                    colors = ButtonDefaults.buttonColors(
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
                ) {

                    Text(
                        text = "Iniciar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = "Prototipo académico de ESUCAR. " +
                    "Utilizar únicamente cuentas ficticias " +
                    "hasta implementar la autenticación.",

            color = colores.textoSecundario,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }
}
