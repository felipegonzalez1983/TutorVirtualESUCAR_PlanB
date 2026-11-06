
package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeEsucar = Color(0xFF174B35)
private val DoradoEsucar = Color(0xFFC7A34D)

private val opciones = listOf(
    "Guía metodológica",
    "Preguntas frecuentes",
    "Mis entregables",
    "Normas APA",
    "Biblioteca digital",
    "Calendario académico"
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOptionClick: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F2)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        VerdeEsucar,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = "ESCUELA DE SUBOFICIALES",
                    color = DoradoEsucar,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Tutor Virtual ESUCAR",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tu apoyo para el Trabajo de Título",
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Recursos académicos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = VerdeEsucar
            )
        }

        items(opciones) { opcion ->
            Card(
                onClick = { onOptionClick(opcion) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Text(
                    text = opcion,
                    modifier = Modifier.padding(20.dp),
                    color = VerdeEsucar,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        item {
            Text(
                text = "Prototipo académico · DUOC UC",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}
