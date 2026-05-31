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
    // Column alineada al centro
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //imagen
        Image(
            painter = painterResource(id = R.drawable.mango),
            contentDescription = "Logo de la empresa",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // nombre
        Text(
            text = "Mangos USA",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2) // es un azul en otro formato admitido
        )
        Text(
            text = "Sistema de Control Logístico",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(64.dp))

        //boton para iniciar
        Button(
            modifier = Modifier.width(220.dp).height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Botón verde
            onClick = {
                // se va a la pantalla principal
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)

                // el activity.finish es para finalizar esta pantalla de inicio para que el usuario al regresarse,
                // se salga de la app en lugar de volver a esta pantalla.
                activity.finish()
            }
        ) {
            Text("Acceder al Sistema", fontSize = 18.sp, color = Color.White)
        }
    }
}