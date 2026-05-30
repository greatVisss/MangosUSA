package com.example.mangosusa

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mangosusa.ui.theme.MangosUSATheme

class AgregarCompraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MangosUSATheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Formulario(this)
                }
            }
        }
    }
}

@Composable
fun Formulario(activity: Activity) {
    val dbHelper = SqliteAuxiliar(activity)

    // Cuatro variables simples para capturar el texto
    var proveedor by remember { mutableStateOf("") }
    var variedad by remember { mutableStateOf("") }
    var toneladas by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }

    var mensajeError by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Nueva Etiqueta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = proveedor,
            onValueChange = { proveedor = it },
            label = { Text("Proveedor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // CÓDIGO PRINCIPIANTE: En lugar de un menú desplegable, usamos campos de texto normales
        OutlinedTextField(
            value = variedad,
            onValueChange = { variedad = it },
            label = { Text("Variedad de Mango (Ej. Ataulfo)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = toneladas,
            onValueChange = { toneladas = it },
            label = { Text("Toneladas (Ej. 15.5)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = estado,
            onValueChange = { estado = it },
            label = { Text("Estado (En tránsito, En planta...)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Etiqueta para mostrar errores de validación
        Text(text = mensajeError, color = Color.Red, modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = {
                // Revisamos si las toneladas son números válidos
                val tonValidadas = toneladas.toDoubleOrNull()

                // Verificamos que no haya campos vacíos
                if (proveedor.isEmpty() || variedad.isEmpty() || toneladas.isEmpty() || estado.isEmpty()) {
                    mensajeError = "Por favor, llena todos los campos."
                } else if (tonValidadas == null) {
                    mensajeError = "Las toneladas deben ser un número válido."
                } else {
                    // Si todo está bien, mandamos a guardar a la base de datos
                    dbHelper.insertCompra(proveedor, variedad, tonValidadas, estado)
                    Toast.makeText(activity, "Compra registrada", Toast.LENGTH_SHORT).show()
                    activity.finish() // Cierra la pantalla
                }
            }
        ) {
            Text("Guardar Compra")
        }
    }
}