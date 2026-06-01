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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mangosusa.ui.theme.MangosUSATheme

class AgregarProveedorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MangosUSATheme {
                Surface(modifier = Modifier.fillMaxSize().background(Color(0xFFEFEFEF))) {
                    FormularioProveedor(this)
                }
            }
        }
    }
}

@Composable
fun FormularioProveedor(activity: Activity) {
    val dbHelper = SqliteAuxiliar(activity)

    val intent = activity.intent
    val idEdit = intent.getIntExtra("ID", -1)

    var nombre by remember { mutableStateOf(intent.getStringExtra("NOMBRE") ?: "") }
    var ubicacion by remember { mutableStateOf(intent.getStringExtra("UBICACION") ?: "") }
    var encargado by remember { mutableStateOf(intent.getStringExtra("ENCARGADO") ?: "") }
    var telefono by remember { mutableStateOf(intent.getStringExtra("TELEFONO") ?: "") }

    var error by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(if (idEdit != -1) "Modificar Huerto" else "Alta de Nuevo Huerto", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del Huerto") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Estado o Municipio") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = encargado,
            onValueChange = { encargado = it },
            label = { Text("Dueño o Encargado") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono de Contacto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(text = error, color = Color.Red, modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            onClick = {
                if (nombre.isEmpty() || ubicacion.isEmpty() || encargado.isEmpty() || telefono.isEmpty()) {
                    error = "Por favor, llena toda la información."
                } else {
                    if (idEdit == -1) {
                        dbHelper.insertProveedor(nombre, ubicacion, encargado, telefono)
                        Toast.makeText(activity, "Huerto registrado", Toast.LENGTH_SHORT).show()
                    } else {
                        dbHelper.updateProveedor(idEdit, nombre, ubicacion, encargado, telefono)
                        Toast.makeText(activity, "Huerto actualizado", Toast.LENGTH_SHORT).show()
                    }
                    activity.finish()
                }
            }
        ) {
            if (idEdit != -1) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Actualizar Huerto",
                    modifier = Modifier.size(40.dp),
                )
            }
            else {
                // en vez de usar el texto se colca un icno de agregar
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Guardar HUerto",
                    modifier = Modifier.size(40.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = {
                val intentDirectorio = Intent(activity, DirectorioProveedoresActivity::class.java)
                activity.startActivity(intentDirectorio)
            }
        ) {
            Icon(
                imageVector = Icons.Filled.AccountBox,
                contentDescription = "Actualizar HUerto",
                modifier = Modifier.size(40.dp)
            )
            Text("Directorio de Proveedores", color = Color(0xFF1976D2))
        }
    }
}