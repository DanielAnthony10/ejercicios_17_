package com.danielmechan.ejercicios_17_

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.danielmechan.ejercicios_17_.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // EJERCICIO 32: Ya no usamos DAO directo en la Activity.
    // Ahora la Activity trabaja con Repository.
    private lateinit var repository: ArticuloRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // EJERCICIO 30: Se obtiene Room Database.
        val dao = AppDatabase.getInstance(this).articuloDao()

        // EJERCICIO 32: Se conecta DAO con Repository.
        repository = ArticuloRepository(dao)

        binding.btnRegistrar.setOnClickListener {
            registrar()
        }

        binding.btnBuscar.setOnClickListener {
            buscar()
        }

        binding.btnModificar.setOnClickListener {
            modificar()
        }

        binding.btnEliminar.setOnClickListener {
            eliminar()
        }

        // EJERCICIO 29: Botón para guardar archivo externo.
        binding.btnGuardarExterno.setOnClickListener {
            guardarArchivoExterno()
        }

        // EJERCICIO 31: Observar la lista en tiempo real.
        observarArticulos()
    }

    private fun toast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun limpiarCampos() {
        binding.txtCodigo.setText("")
        binding.txtDescripcion.setText("")
        binding.txtPrecio.setText("")
    }

    // EJERCICIO 29:
    // Guarda un fichero en almacenamiento externo privado de la app.
    // En Android 10+ funciona con Scoped Storage y no necesita permisos.
    private fun guardarArchivoExterno() {
        try {
            val carpetaExterna = getExternalFilesDir(null)
            val archivo = File(carpetaExterna, "articulos_externo.txt")

            val contenido = """
                Archivo guardado con getExternalFilesDir()
                Proyecto: actividades17
                Este archivo pertenece al almacenamiento externo privado de la app.
            """.trimIndent()

            archivo.writeText(contenido)

            toast("Archivo externo guardado correctamente")

        } catch (e: Exception) {
            toast("Error al guardar externo: ${e.message}")
        }
    }

    // EJERCICIO 31:
    // Observa los artículos en tiempo real usando Flow + repeatOnLifecycle.
    private fun observarArticulos() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.listarTodos().collect { lista ->
                    // Aquí se actualiza automáticamente cuando registras, modificas o eliminas.
                    // Para evidenciarlo, mostramos la cantidad de artículos.
                    binding.textView.text = "Artículos: ${lista.size}"
                }
            }
        }
    }

    // EJERCICIO 30:
    // CREATE usando Room + corutinas.
    private fun registrar() {
        val codigo = binding.txtCodigo.text.toString()
        val descripcion = binding.txtDescripcion.text.toString()
        val precio = binding.txtPrecio.text.toString()

        if (codigo.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {
            toast("Debe llenar todos los campos")
            return
        }

        val articulo = Articulo(
            codigo = codigo.toInt(),
            descripcion = descripcion,
            precio = precio.toDouble()
        )

        lifecycleScope.launch {
            try {
                repository.insertar(articulo)
                limpiarCampos()
                toast("Registro exitoso")
            } catch (e: Exception) {
                toast("Error al registrar: ${e.message}")
            }
        }
    }

    // EJERCICIO 30:
    // READ usando Room + corutinas.
    private fun buscar() {
        val codigo = binding.txtCodigo.text.toString()

        if (codigo.isEmpty()) {
            toast("Debe introducir el código del artículo")
            return
        }

        lifecycleScope.launch {
            val articulo = repository.buscarPorCodigo(codigo.toInt())

            if (articulo != null) {
                binding.txtDescripcion.setText(articulo.descripcion)
                binding.txtPrecio.setText(articulo.precio.toString())
            } else {
                toast("No existe el artículo")
            }
        }
    }

    // EJERCICIO 30:
    // DELETE usando Room + corutinas.
    private fun eliminar() {
        val codigo = binding.txtCodigo.text.toString()

        if (codigo.isEmpty()) {
            toast("Debe introducir el código del artículo")
            return
        }

        lifecycleScope.launch {
            val filasEliminadas = repository.eliminarPorCodigo(codigo.toInt())

            limpiarCampos()

            if (filasEliminadas == 1) {
                toast("Artículo eliminado exitosamente")
            } else {
                toast("El artículo no existe")
            }
        }
    }

    // EJERCICIO 30:
    // UPDATE usando Room + corutinas.
    private fun modificar() {
        val codigo = binding.txtCodigo.text.toString()
        val descripcion = binding.txtDescripcion.text.toString()
        val precio = binding.txtPrecio.text.toString()

        if (codigo.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {
            toast("Debe llenar todos los campos")
            return
        }

        val articulo = Articulo(
            codigo = codigo.toInt(),
            descripcion = descripcion,
            precio = precio.toDouble()
        )

        lifecycleScope.launch {
            val filasActualizadas = repository.actualizar(articulo)

            if (filasActualizadas == 1) {
                toast("Artículo modificado correctamente")
            } else {
                toast("El artículo no existe")
            }
        }
    }
}