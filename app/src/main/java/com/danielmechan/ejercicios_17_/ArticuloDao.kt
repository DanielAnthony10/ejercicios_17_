package com.danielmechan.ejercicios_17_

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticuloDao {

    // EJERCICIO 30: CREATE
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(articulo: Articulo): Long

    // EJERCICIO 30: UPDATE
    @Update
    suspend fun actualizar(articulo: Articulo): Int

    // EJERCICIO 30: DELETE
    @Delete
    suspend fun eliminar(articulo: Articulo): Int

    // EJERCICIO 30: DELETE por código
    @Query("DELETE FROM articulos WHERE codigo = :codigo")
    suspend fun eliminarPorCodigo(codigo: Int): Int

    // EJERCICIO 30: READ por código
    @Query("SELECT * FROM articulos WHERE codigo = :codigo LIMIT 1")
    suspend fun buscarPorCodigo(codigo: Int): Articulo?

    // EJERCICIO 31: Flow para observar cambios en tiempo real
    @Query("SELECT * FROM articulos ORDER BY codigo ASC")
    fun listarTodos(): Flow<List<Articulo>>
}