package com.example.mangosusa

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mangosusa.ui.theme.MangosUSATheme

class MainActivity : ComponentActivity() {
    private var comprasState = mutableStateOf<List<CompraMango>>(emptyList())
    private lateinit var dbHelper: SqliteAuxiliar

    // Nuestra meta para que se llene la barra
    private val META_DIARIA = 150.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = SqliteAuxiliar(this)

        setContent {
            MangosUSATheme {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            startActivity(Intent(this, AgregarCompraActivity::class.java))
                        }) {
                            Text("+", fontSize = 24.sp)
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
        // Carga la lista cada vez que volvemos a la pantalla
        comprasState.value = dbHelper.getComprasDelDia()
    }

    @Composable
    fun DashboardUI(padding: PaddingValues) {
        // --- LÓGICA BÁSICA PARA CALCULAR EL PROGRESO ---

        // 1. Iniciamos la suma en 0
        var totalComprado = 0.0

        // 2. Sumamos las toneladas una por una usando un ciclo for clásico
        for (compra in comprasState.value) {
            totalComprado = totalComprado + compra.toneladas
        }

        // 3. Calculamos el porcentaje (lo que llevamos entre la meta)
        var progreso = (totalComprado / META_DIARIA).toFloat()

        // 4. Si el progreso pasa del 100% (1.0), lo topamos para que la barra no marque error
        if (progreso > 1.0f) {
            progreso = 1.0f
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // TARJETA DE LA META
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Avance de Acopio (Hoy)", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$totalComprado / $META_DIARIA Toneladas", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // La barra que se pinta sola usando la variable 'progreso'
                    LinearProgressIndicator(
                        progress = progreso,
                        modifier = Modifier.fillMaxWidth().height(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Operaciones del Día", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // LISTA DE NOTAS
            LazyColumn {
                items(comprasState.value) { compra ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Prov: ${compra.proveedor}")
                                Text("Mango: ${compra.variedad}")
                                Text("Estado: ${compra.estado}", color = Color.DarkGray)
                            }
                            Column {
                                Text("${compra.toneladas} Ton", color = Color.Blue)
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