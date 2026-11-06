
package cl.duoc.tutorvirtual.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.tutorvirtual.ui.screens.HomeScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    var pantallaActual by rememberSaveable {
        mutableStateOf("Inicio")
    }

    if (pantallaActual == "Inicio") {
        HomeScreen(
            modifier = modifier,
            onOptionClick = { opcion ->
                pantallaActual = opcion
            }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = pantallaActual)

            Button(
                onClick = { pantallaActual = "Inicio" }
            ) {
                Text("Volver al inicio")
            }
        }
    }
}
