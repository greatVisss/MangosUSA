package com.example.mangosusa

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CompraMango(
    val id: Int,
    val proveedor: String,
    val variedad: String,
    val toneladas: Double,
    val fecha: String,
    val estado: String
)

class SqliteAuxiliar(contexto: Context) : SQLiteOpenHelper(contexto, "MangosDB.sqlite", null, 3) {

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE compras (id INTEGER PRIMARY KEY AUTOINCREMENT, proveedor TEXT, variedad TEXT, toneladas REAL, fecha TEXT, estado TEXT)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS compras")
        onCreate(db)
    }

    // --- CÓDIGO PRINCIPIANTE: Inserción paso a paso ---
    fun insertCompra(proveedor: String, variedad: String, toneladas: Double, estado: String) {
        val db = this.writableDatabase

        // 1. Obtenemos la fecha de hoy en texto simple
        val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // 2. Preparamos la consulta con los signos de interrogación
        val sql = "INSERT INTO compras (proveedor, variedad, toneladas, fecha, estado) VALUES (?, ?, ?, ?, ?)"
        val statement = db.compileStatement(sql)

        // 3. Asignamos cada valor en su posición
        statement.bindString(1, proveedor)
        statement.bindString(2, variedad)
        statement.bindDouble(3, toneladas)
        statement.bindString(4, fechaHoy)
        statement.bindString(5, estado)

        statement.executeInsert()
        db.close()
    }

    // --- CÓDIGO PRINCIPIANTE: Lectura de las compras de hoy ---
    fun getComprasDelDia(): List<CompraMango> {
        val lista = mutableListOf<CompraMango>()
        val db = this.readableDatabase
        val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Buscamos solo donde la columna fecha coincida con la fecha de hoy
        val cursor = db.rawQuery("SELECT * FROM compras WHERE fecha = ?", arrayOf(fechaHoy))

        while (cursor.moveToNext()) {
            lista.add(
                CompraMango(
                    id = cursor.getInt(0),
                    proveedor = cursor.getString(1),
                    variedad = cursor.getString(2),
                    toneladas = cursor.getDouble(3),
                    fecha = cursor.getString(4),
                    estado = cursor.getString(5)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    fun deleteCompra(id: Int) {
        val db = this.writableDatabase
        val statement = db.compileStatement("DELETE FROM compras WHERE id = ?")
        statement.bindLong(1, id.toLong())
        statement.executeUpdateDelete()
        db.close()
    }
}