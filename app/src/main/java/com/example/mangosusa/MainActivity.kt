package com.example.mangosusa

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mangosusa.ui.theme.MangosUSATheme

class MainActivity : ComponentActivity() {
    // Variable para guardar la lista de compras que leemos de la BD
    private var comprasState = mutableStateOf<List<CompraMango>>(emptyList())
    private lateinit var dbHelper: SqliteAuxiliar

    // Meta fija de la empresa
    private val META_DIARIA = 150.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = SqliteAuxiliar(this)

        setContent {
            MangosUSATheme {
                Scaffold(
                    floatingActionButton = {
                        // Un botón flotante normal, sencillo y directo
                        FloatingActionButton(
                            onClick = { startActivity(Intent(this, AgregarCompraActivity::class.java)) },
                            containerColor = Color(0xFF1976D2) // Color Azul
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

    @Composable
    fun DashboardUI(padding: PaddingValues) {
        // --- MATEMÁTICAS SENCILLAS PARA EL PROGRESO ---
        var totalComprado = 0.0
        for (compra in comprasState.value) {
            totalComprado = totalComprado + compra.toneladas
        }

        var progreso = (totalComprado / META_DIARIA).toFloat()
        if (progreso > 1.0f) {
            progreso = 1.0f
        }

        // --- DISEÑO VISUAL ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF)) // Fondo gris clarito
                .padding(padding)
                .padding(16.dp)
        ) {
            // Título de la app (Sustituye a la TopBar compleja)
            Text("Dashboard: Mangos USA", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            // TARJETA DE META
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Avance Diario de Acopio", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostramos el total
                    Text("$totalComprado / $META_DIARIA Toneladas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))

                    Spacer(modifier = Modifier.height(16.dp))

                    // Barra de progreso redondeada (código muy corto)
                    LinearProgressIndicator(
                        progress = progreso,
                        modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(8.dp)),
                        color = Color(0xFF4CAF50) // Verde
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Entregas de Hoy", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // LISTA DE COMPRAS
            LazyColumn {
                items(comprasState.value) { compra ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(compra.proveedor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("Variedad: ${compra.variedad}", color = Color.Gray)
                                Text("Estado: ${compra.estado}", color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${compra.toneladas} Ton", fontSize = 16.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
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