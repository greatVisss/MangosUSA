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
    val costo: Double,
    val tamano: String,
    val madurez: String,
    val fecha: String,
    val estado: String
)

class SqliteAuxiliar(contexto: Context) : SQLiteOpenHelper(contexto, "MangosDB.sqlite", null, 7) {

    override fun onCreate(db: SQLiteDatabase?) {
        // 1. Tabla de compras
        val queryCompras = "CREATE TABLE compras (id INTEGER PRIMARY KEY AUTOINCREMENT, proveedor TEXT, variedad TEXT, toneladas REAL, costo REAL, tamano TEXT, madurez TEXT, fecha TEXT, estado TEXT)"
        db?.execSQL(queryCompras)

        // 2. NUEVA TABLA: Proveedores
        val queryProveedores = "CREATE TABLE proveedores (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT)"
        db?.execSQL(queryProveedores)

        // 3. Insertamos un proveedor de prueba para que la lista no empiece vacía
        db?.execSQL("INSERT INTO proveedores (nombre) VALUES ('Huerta San José')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS compras")
        db?.execSQL("DROP TABLE IF EXISTS proveedores")
        onCreate(db)
    }

    // --- FUNCIONES PARA LA NUEVA TABLA DE PROVEEDORES ---
    fun insertProveedor(nombre: String) {
        val db = this.writableDatabase
        val sql = "INSERT INTO proveedores (nombre) VALUES (?)"
        val statement = db.compileStatement(sql)
        statement.bindString(1, nombre)
        statement.executeInsert()
        db.close()
    }

    fun getProveedores(): List<String> {
        val lista = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM proveedores", null)
        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return lista
    }
    // ----------------------------------------------------

    // --- FUNCIONES DE COMPRAS (Se quedan igual) ---
    fun insertCompra(proveedor: String, variedad: String, toneladas: Double, costo: Double, tamano: String, madurez: String, estado: String) {
        val db = this.writableDatabase
        val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val sql = "INSERT INTO compras (proveedor, variedad, toneladas, costo, tamano, madurez, fecha, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        val statement = db.compileStatement(sql)
        statement.bindString(1, proveedor)
        statement.bindString(2, variedad)
        statement.bindDouble(3, toneladas)
        statement.bindDouble(4, costo)
        statement.bindString(5, tamano)
        statement.bindString(6, madurez)
        statement.bindString(7, fechaHoy)
        statement.bindString(8, estado)
        statement.executeInsert()
        db.close()
    }

    fun updateCompra(id: Int, proveedor: String, variedad: String, toneladas: Double, costo: Double, tamano: String, madurez: String, estado: String) {
        val db = this.writableDatabase
        val sql = "UPDATE compras SET proveedor = ?, variedad = ?, toneladas = ?, costo = ?, tamano = ?, madurez = ?, estado = ? WHERE id = ?"
        val statement = db.compileStatement(sql)
        statement.bindString(1, proveedor)
        statement.bindString(2, variedad)
        statement.bindDouble(3, toneladas)
        statement.bindDouble(4, costo)
        statement.bindString(5, tamano)
        statement.bindString(6, madurez)
        statement.bindString(7, estado)
        statement.bindLong(8, id.toLong())
        statement.executeUpdateDelete()
        db.close()
    }

    fun getComprasDelDia(): List<CompraMango> {
        val lista = mutableListOf<CompraMango>()
        val db = this.readableDatabase
        val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val cursor = db.rawQuery("SELECT * FROM compras WHERE fecha = ?", arrayOf(fechaHoy))
        while (cursor.moveToNext()) {
            lista.add(CompraMango(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8)))
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