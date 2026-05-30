package com.example.mangosusa // Usa tu paquete

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var proveedor by remember { mutableStateOf("") }
    var variedad by remember { mutableStateOf("") }
    var toneladas by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(24.dp)) {
        // Título Material 3 más grande y profesional
        Text("Registrar Compra Agropecuaria", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // NUEVO: Campos estilizados y mejor espaciado
        OutlinedTextField(
            value = proveedor,
            onValueChange = { proveedor = it },
            label = { Text("Sector/Proveedor") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = variedad,
            onValueChange = { variedad = it },
            label = { Text("Variedad de Mango (Ej. Manila)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = toneladas,
            onValueChange = { toneladas = it },
            label = { Text("Toneladas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            // NUEVO: Suffix para indicar la unidad de medida
            suffix = { Text("Ton") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = estado,
            onValueChange = { estado = it },
            label = { Text("Estado Logístico (Ej. En tránsito)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Uso del color de error del tema native de M3
        if (error.isNotEmpty()) {
            Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

        // NUEVO: Botón más grande y corporativo
        Button(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            onClick = {
                val tonValidadas = toneladas.toDoubleOrNull()

                if (proveedor.isEmpty() || variedad.isEmpty() || toneladas.isEmpty() || estado.isEmpty()) {
                    error = "Llena todos los campos."
                } else if (tonValidadas == null || tonValidadas <= 0) {
                    error = "Ingresa una cantidad de toneladas válida."
                } else {
                    dbHelper.insertCompra(proveedor, variedad, tonValidadas, estado)
                    Toast.makeText(activity, "Compra registrada", Toast.LENGTH_SHORT).show()
                    activity.finish()
                }
            }
        ) {
            Text("Registrar Compra", fontSize = 16.sp)
        }
    }
}