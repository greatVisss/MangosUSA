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
    // Recibimos los datos (si viene vacío, idEdit será -1, lo que significa que es NUEVO)
    val intent = activity.intent
    val idEdit = intent.getIntExtra("ID", -1)
    val dbHelper = SqliteAuxiliar(activity)

    // Variables que guardan lo que escribimos en pantalla
    var proveedor by remember { mutableStateOf(intent.getStringExtra("PROVEEDOR") ?: "") }
    var toneladas by remember { mutableStateOf(if (idEdit != -1) intent.getDoubleExtra("TONELADAS", 0.0).toString() else "") }
    var error by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Notita de Compra", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = proveedor,
            onValueChange = { proveedor = it },
            label = { Text("Proveedor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = toneladas,
            onValueChange = { toneladas = it },
            label = { Text("Toneladas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = error, color = Color.Red) // Aquí se muestra el mensaje si algo sale mal

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Convertimos el texto a número. Si mete letras, validador será null
                val validador = toneladas.toDoubleOrNull()

                if (proveedor.isEmpty() || toneladas.isEmpty()) {
                    error = "Llena todos los campos"
                } else if (validador == null) {
                    error = "Pon un número válido para las toneladas"
                } else {
                    // Decidimos si hacemos INSERT o UPDATE
                    if (idEdit == -1) {
                        dbHelper.insertCompra(proveedor, validador)
                        Toast.makeText(activity, "Guardado", Toast.LENGTH_SHORT).show()
                    } else {
                        dbHelper.updateCompra(idEdit, proveedor, validador)
                        Toast.makeText(activity, "Actualizado", Toast.LENGTH_SHORT).show()
                    }
                    activity.finish() // Cierra esta pantalla y regresa al pizarrón
                }
            }
        ) {
            Text("Guardar Notita")
        }
    }
}