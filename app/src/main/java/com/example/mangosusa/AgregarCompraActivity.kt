package com.example.mangosusa

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                Surface(modifier = Modifier.fillMaxSize().background(Color(0xFFEFEFEF))) {
                    Formulario(this)
                }
            }
        }
    }
}

@Composable
fun Formulario(activity: Activity) {
    val dbHelper = SqliteAuxiliar(activity)

    val intent = activity.intent
    val idEdit = intent.getIntExtra("ID", -1)

    var proveedor by remember { mutableStateOf(intent.getStringExtra("PROVEEDOR") ?: "") }
    var estado by remember { mutableStateOf(intent.getStringExtra("ESTADO") ?: "") }
    var toneladas by remember { mutableStateOf(if (idEdit != -1) intent.getDoubleExtra("TONELADAS", 0.0).toString() else "") }
    var costo by remember { mutableStateOf(if (idEdit != -1) intent.getDoubleExtra("COSTO", 0.0).toString() else "") }
    var error by remember { mutableStateOf("") }

    // --- LISTA DESPLEGABLE: VARIEDAD DE MANGOS ---
    val listaVariedades = listOf(
        "Ataulfo", "Manila", "Tommy Atkins", "Keitt", "Kent",
        "Haden", "Alphonso", "Palmer", "Carabao", "Osteen",
        "Nam Dok Mai", "Edward", "Kesar", "Francine"
    )
    var variedadExpandida by remember { mutableStateOf(false) }
    // Si la pantalla recibe una variedad para editar la usa, si no, pone "Ataulfo" por defecto
    val variedadGuardada = intent.getStringExtra("VARIEDAD")
    var variedadSeleccionada by remember { mutableStateOf(if (variedadGuardada.isNullOrEmpty()) "Ataulfo" else variedadGuardada) }

    // --- LISTA DESPLEGABLE: TAMAÑO ---
    val listaTamanos = listOf("Chico", "Mediano", "Grande")
    var tamanoExpandido by remember { mutableStateOf(false) }
    val tamanoGuardado = intent.getStringExtra("TAMANO")
    var tamanoSeleccionado by remember { mutableStateOf(if (tamanoGuardado.isNullOrEmpty()) "Mediano" else tamanoGuardado) }

    // --- LISTA DESPLEGABLE: MADUREZ ---
    val listaMadurez = listOf("Verde", "Medio", "Maduro")
    var madurezExpandida by remember { mutableStateOf(false) }
    val madurezGuardada = intent.getStringExtra("MADUREZ")
    var madurezSeleccionada by remember { mutableStateOf(if (madurezGuardada.isNullOrEmpty()) "Verde" else madurezGuardada) }

    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        Text(if (idEdit != -1) "Modificar Compra" else "Registrar Compra", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = proveedor,
            onValueChange = { proveedor = it },
            label = { Text("Sector/Proveedor") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        // --- CÓDIGO DE LA NUEVA LISTA DE VARIEDADES ---
        Text("Variedad de Mango:", color = Color.Gray, fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { variedadExpandida = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(variedadSeleccionada)
            }
            // Jetpack Compose hace que esta lista tenga "scroll" automático hacia abajo si es muy larga
            DropdownMenu(
                expanded = variedadExpandida,
                onDismissRequest = { variedadExpandida = false }
            ) {
                listaVariedades.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            variedadSeleccionada = opcion
                            variedadExpandida = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Text("Tamaño del Mango:", color = Color.Gray, fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { tamanoExpandido = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(tamanoSeleccionado)
            }
            DropdownMenu(
                expanded = tamanoExpandido,
                onDismissRequest = { tamanoExpandido = false }
            ) {
                listaTamanos.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            tamanoSeleccionado = opcion
                            tamanoExpandido = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Text("Madurez del Mango:", color = Color.Gray, fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { madurezExpandida = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(madurezSeleccionada)
            }
            DropdownMenu(
                expanded = madurezExpandida,
                onDismissRequest = { madurezExpandida = false }
            ) {
                listaMadurez.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            madurezSeleccionada = opcion
                            madurezExpandida = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = toneladas,
            onValueChange = { toneladas = it },
            label = { Text("Toneladas (Ej. 10.5)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = costo,
            onValueChange = { costo = it },
            label = { Text("Costo de la Compra ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = estado,
            onValueChange = { estado = it },
            label = { Text("Estado (Ej. En planta)") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = error, color = Color.Red, modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
            onClick = {
                val tonValidadas = toneladas.toDoubleOrNull()
                val costoValidado = costo.toDoubleOrNull()

                // Ya no validamos "variedad" porque al ser una lista cerrada nunca estará vacía
                if (proveedor.isEmpty() || toneladas.isEmpty() || costo.isEmpty() || estado.isEmpty()) {
                    error = "Llena todos los campos."
                } else if (tonValidadas == null || costoValidado == null) {
                    error = "Ingresa números válidos en Toneladas y Costo."
                } else {
                    if (idEdit == -1) {
                        dbHelper.insertCompra(proveedor, variedadSeleccionada, tonValidadas, costoValidado, tamanoSeleccionado, madurezSeleccionada, estado)
                        Toast.makeText(activity, "Guardado exitosamente", Toast.LENGTH_SHORT).show()
                    } else {
                        dbHelper.updateCompra(idEdit, proveedor, variedadSeleccionada, tonValidadas, costoValidado, tamanoSeleccionado, madurezSeleccionada, estado)
                        Toast.makeText(activity, "Actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    }
                    activity.finish()
                }
            }
        ) {
            Text(if (idEdit != -1) "Actualizar" else "Registrar", fontSize = 18.sp, color = Color.White)
        }
    }
}