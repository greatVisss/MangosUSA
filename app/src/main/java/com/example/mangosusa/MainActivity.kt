package com.example.mangosusa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mangosusa.ui.theme.MangosUSATheme

class MainActivity : ComponentActivity() {
    private var comprasState = mutableStateOf<List<CompraMango>>(emptyList())
    private lateinit var dbHelper: SqliteAuxiliar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = SqliteAuxiliar(this)

        setContent {
            MangosUSATheme {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { startActivity(Intent(this, AgregarCompraActivity::class.java)) },
                            containerColor = Color(0xFF1976D2)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = Color.White)
                        }
                    }
                ) { padding ->
                    DashboardUI(padding)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        comprasState.value = dbHelper.getComprasDelDia()
    }

    // --- CÓDIGO PRINCIPIANTE: Funciones para la mini-memoria (SharedPreferences) ---
    private fun guardarMetaEnTelefono(meta: Float) {
        // Abrimos el archivo de ajustes en modo privado (solo esta app lo puede leer)
        val preferencias = getSharedPreferences("AjustesMangos", Context.MODE_PRIVATE)
        val editor = preferencias.edit()
        editor.putFloat("META_DIARIA", meta) // Guardamos el número
        editor.apply() // Confirmamos el guardado
    }

    private fun leerMetaDelTelefono(): Double {
        val preferencias = getSharedPreferences("AjustesMangos", Context.MODE_PRIVATE)
        // Intentamos leer la meta. Si es la primera vez y no hay nada, ponemos 150.0 por defecto
        return preferencias.getFloat("META_DIARIA", 150.0f).toDouble()
    }

    @Composable
    fun DashboardUI(padding: PaddingValues) {
        var textoBusqueda by remember { mutableStateOf("") }
        var totalComprado = 0.0

        // --- VARIABLES PARA LA NUEVA META DINÁMICA ---
        // Leemos la meta guardada al iniciar la pantalla
        var metaDiaria by remember { mutableStateOf(leerMetaDelTelefono()) }
        var mostrarDialogoMeta by remember { mutableStateOf(false) } // Controla si la ventanita está abierta o cerrada
        var textoNuevaMeta by remember { mutableStateOf("") } // Lo que el usuario escribe en la ventanita

        for (compra in comprasState.value) {
            totalComprado = totalComprado + compra.toneladas
        }

        // Calculamos el progreso usando la nueva meta modificable
        var progreso = (totalComprado / metaDiaria).toFloat()
        if (progreso > 1.0f) {
            progreso = 1.0f
        }

        // --- VENTANITA EMERGENTE (ALERT DIALOG) ---
        if (mostrarDialogoMeta == true) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoMeta = false }, // Si toca afuera, se cierra
                title = { Text("Definir Meta Diaria", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("¿Cuántas toneladas se deben comprar hoy?")
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = textoNuevaMeta,
                            onValueChange = { textoNuevaMeta = it },
                            label = { Text("Nueva Meta (Ton)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        onClick = {
                            val numeroValido = textoNuevaMeta.toDoubleOrNull()
                            if (numeroValido != null && numeroValido > 0) {
                                metaDiaria = numeroValido // Actualiza la pantalla
                                guardarMetaEnTelefono(numeroValido.toFloat()) // Guarda en la memoria
                                mostrarDialogoMeta = false // Cierra la ventanita
                            }
                        }
                    ) {
                        Text("Guardar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoMeta = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }
        // --- FIN DE LA VENTANITA EMERGENTE ---

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Dashboard: Mangos USA", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            // TARJETA DE LA META
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Fila para alinear el título y el botón de editar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Avance Diario de Acopio", color = Color.Gray, fontSize = 14.sp)

                        // Botón que abre la ventanita para cambiar la meta
                        IconButton(onClick = {
                            textoNuevaMeta = metaDiaria.toString() // Ponemos el número actual en la caja de texto
                            mostrarDialogoMeta = true // Abrimos la ventana
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar Meta", tint = Color.Gray)
                        }
                    }

                    Text("$totalComprado / $metaDiaria Toneladas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = progreso,
                        modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(8.dp)),
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                label = { Text("Buscar por proveedor...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Entregas de Hoy", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(comprasState.value) { compra ->
                    if (textoBusqueda.isEmpty() || compra.proveedor.lowercase().contains(textoBusqueda.lowercase())) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(compra.proveedor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text("Mango: ${compra.variedad} (${compra.tamano})", color = Color.DarkGray)
                                    Text("Madurez: ${compra.madurez}", color = Color.Gray)
                                    Text("Estado: ${compra.estado}", color = Color.Gray)
                                    Text("Fecha: ${compra.fecha}", color = Color.LightGray, fontSize = 12.sp)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${compra.toneladas} Ton", fontSize = 16.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                                    Text("Costo: $${compra.costo}", fontSize = 14.sp, color = Color(0xFF4CAF50))

                                    Row {
                                        IconButton(onClick = {
                                            val intent = Intent(this@MainActivity, AgregarCompraActivity::class.java)
                                            intent.putExtra("ID", compra.id)
                                            intent.putExtra("PROVEEDOR", compra.proveedor)
                                            intent.putExtra("VARIEDAD", compra.variedad)
                                            intent.putExtra("TONELADAS", compra.toneladas)
                                            intent.putExtra("COSTO", compra.costo)
                                            intent.putExtra("TAMANO", compra.tamano)
                                            intent.putExtra("MADUREZ", compra.madurez)
                                            intent.putExtra("ESTADO", compra.estado)
                                            startActivity(intent)
                                        }) {
                                            Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.Gray)
                                        }

                                        IconButton(onClick = {
                                            dbHelper.deleteCompra(compra.id)
                                            comprasState.value = dbHelper.getComprasDelDia()
                                        }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Borrar", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}