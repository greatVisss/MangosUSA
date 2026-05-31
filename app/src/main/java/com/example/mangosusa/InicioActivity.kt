package com.example.mangosusa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mangosusa.ui.theme.MangosUSATheme

class InicioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MangosUSATheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaInicio(this)
                }
            }
        }
    }
}

@Composable
fun PantallaInicio(activity: Activity) {
    // Column alineada al centro para que todo quede en medio de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Fondo blanco limpio
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // --- LA IMAGEN REPRESENTATIVA ---
        // Por ahora usamos la imagen por defecto de Android para que compile.
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Logo de la empresa",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- EL NOMBRE DE LA COMPAÑÍA ---
        Text(
            text = "Mangos USA",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2) // Azul corporativo
        )
        Text(
            text = "Sistema de Control Logístico",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(64.dp))

        // --- EL BOTÓN DE ACCEDER ---
        Button(
            modifier = Modifier.width(220.dp).height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Botón verde
            onClick = {
                // Viajamos a la pantalla principal
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)

                // Finalizamos esta pantalla de inicio para que, si el usuario presiona "Atrás",
                // se salga de la app en lugar de volver a esta pantalla.
                activity.finish()
            }
        ) {
            Text("Acceder al Sistema", fontSize = 18.sp, color = Color.White)
        }
    }
}