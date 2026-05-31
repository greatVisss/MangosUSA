package com.example.mangosusa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mangosusa.ui.theme.MangosUSATheme

class DirectorioProveedoresActivity : ComponentActivity() {
    private lateinit var dbHelper: SqliteAuxiliar
    private var proveedoresState = mutableStateOf<List<ProveedorInfo>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = SqliteAuxiliar(this)

        setContent {
            MangosUSATheme {
                Surface(modifier = Modifier.fillMaxSize().background(Color(0xFFEFEFEF))) {
                    PantallaDirectorio(this)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        proveedoresState.value = dbHelper.getTodosLosProveedores()
    }

    @Composable
    fun PantallaDirectorio(activity: Activity) {
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(26.dp))
            Text("Directorio de Huertos", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (proveedoresState.value.isEmpty()) {
                Text("No hay proveedores registrados aún.", color = Color.Gray)
            } else {
                LazyColumn {
                    items(proveedoresState.value) { prov ->
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
                                    Text(prov.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text("Ubicación: ${prov.ubicacion}", color = Color.DarkGray)
                                    Text("Encargado: ${prov.encargado}", color = Color.Gray)
                                    Text("Teléfono: ${prov.telefono}", color = Color(0xFF1976D2))
                                }

                                Row {
                                    IconButton(onClick = {
                                        val intent = Intent(activity, AgregarProveedorActivity::class.java)
                                        intent.putExtra("ID", prov.id)
                                        intent.putExtra("NOMBRE", prov.nombre)
                                        intent.putExtra("UBICACION", prov.ubicacion)
                                        intent.putExtra("ENCARGADO", prov.encargado)
                                        intent.putExtra("TELEFONO", prov.telefono)
                                        activity.startActivity(intent)
                                    }) {
                                        Icon(Icons.Filled.Settings, contentDescription = "Editar", tint = Color.Gray)
                                    }

                                    IconButton(onClick = {
                                        dbHelper.deleteProveedor(prov.id)
                                        proveedoresState.value = dbHelper.getTodosLosProveedores()
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