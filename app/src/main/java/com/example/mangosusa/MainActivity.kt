package com.example.mangosusa

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mangosusa.ui.theme.MangosUSATheme

class MainActivity : ComponentActivity() {
    // Variable que guarda la lista de compras y avisa a la pantalla si hay cambios
    private var comprasState = mutableStateOf<List<CompraMango>>(emptyList())
    private lateinit var dbHelper: SqliteAuxiliar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = SqliteAuxiliar(this)

        setContent {
            MangosUSATheme {
                Scaffold(
                    floatingActionButton = {
                        // Botón flotante para ir a la pantalla de agregar
                        FloatingActionButton(onClick = {
                            val intent = Intent(this, AgregarCompraActivity::class.java)
                            startActivity(intent)
                        }) {
                            Text("+")
                        }
                    }
                ) { padding ->
                    PizarronUI(padding) // Llamamos a la interfaz visual
                }
            }
        }
    }

    // Se ejecuta al regresar a esta pantalla para recargar los datos
    override fun onResume() {
        super.onResume()
        comprasState.value = dbHelper.getAllCompras()
    }

    // --- DISEÑO DE LA PANTALLA ---
    @Composable
    fun PizarronUI(padding: PaddingValues) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray) // Fondo tipo pizarrón
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Pizarrón de Compras", color = Color.White, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // LazyColumn es la lista eficiente de Compose (el nuevo ListView)
            LazyColumn {
                items(comprasState.value) { compra ->
                    // Card es cada "notita"
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Yellow) // Color notita
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Proveedor: ${compra.proveedor}", color = Color.Black)
                            Text(text = "Cantidad: ${compra.toneladas} Toneladas", color = Color.Black)

                            Spacer(modifier = Modifier.height(8.dp))

                            // Fila para acomodar los botones
                            Row {
                                Button(onClick = {
                                    // Pasamos los datos a la otra pantalla para editarlos
                                    val intent = Intent(this@MainActivity, AgregarCompraActivity::class.java)
                                    intent.putExtra("ID", compra.id)
                                    intent.putExtra("PROVEEDOR", compra.proveedor)
                                    intent.putExtra("TONELADAS", compra.toneladas)
                                    startActivity(intent)
                                }) {
                                    Text("Editar")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        dbHelper.deleteCompra(compra.id) // Borra de la BD
                                        comprasState.value = dbHelper.getAllCompras() // Actualiza la lista visual
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Borrar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}