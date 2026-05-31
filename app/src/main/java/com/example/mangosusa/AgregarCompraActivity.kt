package com.example.mangosusa

import android.app.Activity
import android.content.Intent
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
    private lateinit var dbHelper: SqliteAuxiliar
    // Variable global para que la lista se recargue al volver de la pantalla de proveedores
    private var listaProveedoresState = mutableStateOf<List<String>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = SqliteAuxiliar(this)

        setContent {
            MangosUSATheme {
                Surface(modifier = Modifier.fillMaxSize().background(Color(0xFFEFEFEF))) {
                    Formulario(this, listaProveedoresState.value) // Pasamos la lista al diseño
                }
            }
        }
    }

    // Se ejecuta cada que esta pantalla aparece (incluso cuando regresas de agregar un proveedor)
    override fun onResume() {
        super.onResume()
        listaProveedoresState.value = dbHelper.getProveedores()
    }
}

@Composable
fun Formulario(activity: Activity, listaProveedores: List<String>) {
    val dbHelper = SqliteAuxiliar(activity)
    val intent = activity.intent
    val idEdit = intent.getIntExtra("ID", -1)

    // --- VARIABLES DE LISTAS ---
    var variedadExpandida by remember { mutableStateOf(false) }
    val variedadGuardada = intent.getStringExtra("VARIEDAD")
    var variedadSeleccionada by remember { mutableStateOf(if (variedadGuardada.isNullOrEmpty()) "Ataulfo" else variedadGuardada) }

    var tamanoExpandido by remember { mutableStateOf(false) }
    val tamanoGuardado = intent.getStringExtra("TAMANO")
    var tamanoSeleccionado by remember { mutableStateOf(if (tamanoGuardado.isNullOrEmpty()) "Mediano" else tamanoGuardado) }

    var madurezExpandida by remember { mutableStateOf(false) }
    val madurezGuardada = intent.getStringExtra("MADUREZ")
    var madurezSeleccionada by remember { mutableStateOf(if (madurezGuardada.isNullOrEmpty()) "Verde" else madurezGuardada) }

    // --- NUEVO: LISTA DE PROVEEDORES ---
    var proveedorExpandido by remember { mutableStateOf(false) }
    val proveedorGuardado = intent.getStringExtra("PROVEEDOR")
    var proveedorSeleccionado by remember { mutableStateOf(if (proveedorGuardado.isNullOrEmpty()) "Seleccione uno..." else proveedorGuardado) }

    // --- TEXTOS NORMALES ---
    var estado by remember { mutableStateOf(intent.getStringExtra("ESTADO") ?: "") }
    var toneladas by remember { mutableStateOf(if (idEdit != -1) intent.getDoubleExtra("TONELADAS", 0.0).toString() else "") }
    var costo by remember { mutableStateOf(if (idEdit != -1) intent.getDoubleExtra("COSTO", 0.0).toString() else "") }
    var error by remember { mutableStateOf("") }

    val listaVariedades = listOf("Ataulfo", "Manila", "Tommy Atkins", "Keitt", "Kent", "Haden", "Alphonso")
    val listaTamanos = listOf("Chico", "Mediano", "Grande")
    val listaMadurez = listOf("Verde", "Medio", "Maduro")

    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        Text(if (idEdit != -1) "Modificar Compra" else "Registrar Compra", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // --- CÓDIGO NUEVO PARA EL MENÚ DE PROVEEDORES ---
        Text("Sector/Proveedor:", color = Color.Gray, fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { proveedorExpandido = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(proveedorSeleccionado)
            }
            DropdownMenu(
                expanded = proveedorExpandido,
                onDismissRequest = { proveedorExpandido = false }
            ) {
                listaProveedores.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            proveedorSeleccionado = opcion
                            proveedorExpandido = false
                        }
                    )
                }
            }
        }

        // Botón para ir a la 3ra ventana y crear un proveedor nuevo
        TextButton(onClick = {
            val intentNuevo = Intent(activity, AgregarProveedorActivity::class.java)
            activity.startActivity(intentNuevo)
        }) {
            Text("+ Registrar nuevo proveedor", color = Color(0xFF1976D2))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Variedad de Mango:", color = Color.Gray, fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { variedadExpandida = true }, modifier = Modifier.fillMaxWidth()) {
                Text(variedadSeleccionada)
            }
            DropdownMenu(expanded = variedadExpandida, onDismissRequest = { variedadExpandida = false }) {
                listaVariedades.forEach { opcion ->
                    DropdownMenuItem(text = { Text(opcion) }, onClick = { variedadSeleccionada = opcion; variedadExpandida = false })
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Text("Tamaño del Mango:", color = Color.Gray, fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { tamanoExpandido = true }, modifier = Modifier.fillMaxWidth()) {
                Text(tamanoSeleccionado)
            }
            DropdownMenu(expanded = tamanoExpandido, onDismissRequest = { tamanoExpandido = false }) {
                listaTamanos.forEach { opcion ->
                    DropdownMenuItem(text = { Text(opcion) }, onClick = { tamanoSeleccionado = opcion; tamanoExpandido = false })
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Text("Madurez del Mango:", color = Color.Gray, fontSize = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { madurezExpandida = true }, modifier = Modifier.fillMaxWidth()) {
                Text(madurezSeleccionada)
            }
            DropdownMenu(expanded = madurezExpandida, onDismissRequest = { madurezExpandida = false }) {
                listaMadurez.forEach { opcion ->
                    DropdownMenuItem(text = { Text(opcion) }, onClick = { madurezSeleccionada = opcion; madurezExpandida = false })
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

                if (proveedorSeleccionado == "Seleccione uno..." || toneladas.isEmpty() || costo.isEmpty() || estado.isEmpty()) {
                    error = "Llena todos los campos."
                } else if (tonValidadas == null || costoValidado == null) {
                    error = "Ingresa números válidos en Toneladas y Costo."
                } else {
                    if (idEdit == -1) {
                        dbHelper.insertCompra(proveedorSeleccionado, variedadSeleccionada, tonValidadas, costoValidado, tamanoSeleccionado, madurezSeleccionada, estado)
                        Toast.makeText(activity, "Guardado exitosamente", Toast.LENGTH_SHORT).show()
                    } else {
                        dbHelper.updateCompra(idEdit, proveedorSeleccionado, variedadSeleccionada, tonValidadas, costoValidado, tamanoSeleccionado, madurezSeleccionada, estado)
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