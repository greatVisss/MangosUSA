package com.example.mangosusa

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- MODELOS DE DATOS ---
data class CompraMango(
    val id: Int, val proveedor: String, val variedad: String,
    val toneladas: Double, val costo: Double, val tamano: String,
    val madurez: String, val fecha: String, val estado: String,
    val hora: String // NUEVO: Agregamos la hora al modelo
)

data class ProveedorInfo(
    val id: Int, val nombre: String, val ubicacion: String,
    val encargado: String, val telefono: String
)

// Subimos la versión a 9 para aplicar los cambios
class SqliteAuxiliar(contexto: Context) : SQLiteOpenHelper(contexto, "MangosDB.sqlite", null, 9) {

    override fun onCreate(db: SQLiteDatabase?) {
        // NUEVO: Agregamos "hora TEXT" al final de la tabla de compras
        val queryCompras = "CREATE TABLE compras (id INTEGER PRIMARY KEY AUTOINCREMENT, proveedor TEXT, variedad TEXT, toneladas REAL, costo REAL, tamano TEXT, madurez TEXT, fecha TEXT, estado TEXT, hora TEXT)"
        db?.execSQL(queryCompras)

        val queryProveedores = "CREATE TABLE proveedores (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, ubicacion TEXT, encargado TEXT, telefono TEXT)"
        db?.execSQL(queryProveedores)

        db?.execSQL("INSERT INTO proveedores (nombre, ubicacion, encargado, telefono) VALUES ('Huerta San José', 'Michoacán', 'José Pérez', '555-1234'), ('Huerta Mría', 'Oaxaca', 'Maria Delfina', '555-98456')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS compras")
        db?.execSQL("DROP TABLE IF EXISTS proveedores")
        onCreate(db)
    }

    // ==========================================
    //        FUNCIONES DE PROVEEDORES
    // ==========================================
    fun insertProveedor(nombre: String, ubicacion: String, encargado: String, telefono: String) {
        val db = this.writableDatabase
        val sql = "INSERT INTO proveedores (nombre, ubicacion, encargado, telefono) VALUES (?, ?, ?, ?)"
        val statement = db.compileStatement(sql)
        statement.bindString(1, nombre)
        statement.bindString(2, ubicacion)
        statement.bindString(3, encargado)
        statement.bindString(4, telefono)
        statement.executeInsert()
        db.close()
    }

    fun getTodosLosProveedores(): List<ProveedorInfo> {
        val lista = mutableListOf<ProveedorInfo>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM proveedores", null)
        while (cursor.moveToNext()) {
            lista.add(ProveedorInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)))
        }
        cursor.close()
        db.close()
        return lista
    }

    fun updateProveedor(id: Int, nombre: String, ubicacion: String, encargado: String, telefono: String) {
        val db = this.writableDatabase
        val sql = "UPDATE proveedores SET nombre = ?, ubicacion = ?, encargado = ?, telefono = ? WHERE id = ?"
        val statement = db.compileStatement(sql)
        statement.bindString(1, nombre)
        statement.bindString(2, ubicacion)
        statement.bindString(3, encargado)
        statement.bindString(4, telefono)
        statement.bindLong(5, id.toLong())
        statement.executeUpdateDelete()
        db.close()
    }

    fun deleteProveedor(id: Int) {
        val db = this.writableDatabase
        val statement = db.compileStatement("DELETE FROM proveedores WHERE id = ?")
        statement.bindLong(1, id.toLong())
        statement.executeUpdateDelete()
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

    // ==========================================
    //        FUNCIONES DE COMPRAS
    // ==========================================
    fun insertCompra(proveedor: String, variedad: String, toneladas: Double, costo: Double, tamano: String, madurez: String, estado: String) {
        val db = this.writableDatabase

        // Obtenemos la Fecha y la Hora actuales automáticamente
        val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) // NUEVO

        // Agregamos la columna 'hora' al código SQL
        val sql = "INSERT INTO compras (proveedor, variedad, toneladas, costo, tamano, madurez, fecha, estado, hora) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        val statement = db.compileStatement(sql)

        statement.bindString(1, proveedor)
        statement.bindString(2, variedad)
        statement.bindDouble(3, toneladas)
        statement.bindDouble(4, costo)
        statement.bindString(5, tamano)
        statement.bindString(6, madurez)
        statement.bindString(7, fechaHoy)
        statement.bindString(8, estado)
        statement.bindString(9, horaActual) // NUEVO: Guardamos la hora

        statement.executeInsert()
        db.close()
    }

    fun updateCompra(id: Int, proveedor: String, variedad: String, toneladas: Double, costo: Double, tamano: String, madurez: String, estado: String) {
        val db = this.writableDatabase
        // Nota: Al modificar, NO actualizamos la fecha ni la hora para que mantenga la original
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
            lista.add(CompraMango(
                id = cursor.getInt(0),
                proveedor = cursor.getString(1),
                variedad = cursor.getString(2),
                toneladas = cursor.getDouble(3),
                costo = cursor.getDouble(4),
                tamano = cursor.getString(5),
                madurez = cursor.getString(6),
                fecha = cursor.getString(7),
                estado = cursor.getString(8),
                hora = cursor.getString(9) // NUEVO: Leemos la hora de la base de datos
            ))
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