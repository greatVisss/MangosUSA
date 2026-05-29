package com.example.mangosusa

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Esta es la "caja" donde guardaremos los datos en la memoria de la app
data class CompraMango(
    val id: Int,
    val proveedor: String,
    val toneladas: Double
)

class SqliteAuxiliar(contexto: Context) : SQLiteOpenHelper(contexto, "MangosDB.sqlite", null, 1) {

    // Se ejecuta la primera vez para crear la tabla
    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE compras (id INTEGER PRIMARY KEY AUTOINCREMENT, proveedor TEXT, toneladas REAL)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    // 1. INSERTAR (Usando parámetros ? para seguridad)
    fun insertCompra(proveedor: String, toneladas: Double) {
        val db = this.writableDatabase
        val statement = db.compileStatement("INSERT INTO compras (proveedor, toneladas) VALUES (?, ?)")
        statement.bindString(1, proveedor)
        statement.bindDouble(2, toneladas)
        statement.executeInsert()
        db.close()
    }

    // 2. LEER (Consultar todas las notas)
    fun getAllCompras(): List<CompraMango> {
        val lista = mutableListOf<CompraMango>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM compras", null)

        // El cursor recorre fila por fila la tabla
        while (cursor.moveToNext()) {
            lista.add(
                CompraMango(
                    id = cursor.getInt(0),
                    proveedor = cursor.getString(1),
                    toneladas = cursor.getDouble(2)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    // 3. ACTUALIZAR
    fun updateCompra(id: Int, proveedor: String, toneladas: Double) {
        val db = this.writableDatabase
        val statement = db.compileStatement("UPDATE compras SET proveedor = ?, toneladas = ? WHERE id = ?")
        statement.bindString(1, proveedor)
        statement.bindDouble(2, toneladas)
        statement.bindLong(3, id.toLong())
        statement.executeUpdateDelete()
        db.close()
    }

    // 4. BORRAR
    fun deleteCompra(id: Int) {
        val db = this.writableDatabase
        val statement = db.compileStatement("DELETE FROM compras WHERE id = ?")
        statement.bindLong(1, id.toLong())
        statement.executeUpdateDelete()
        db.close()
    }
}