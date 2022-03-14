package com.example.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
    val usuariosRepository: UsuariosRepository,
    val adminsRepository: AdminsRepository,
    val mensajesRepository: MensajesRepository
) {
    @PostMapping("crearUsuario")
    fun crearUsuario(@RequestBody usuario: Usuario): Any {
        usuariosRepository.findAll().forEach {
            if (it.nombre == usuario.nombre) {
                if (it.pass == usuario.pass) {
                    return it.cifrado
                } else {
                    return Error(1, "Pass invalida")
                }
            }
        }
        usuariosRepository.save(usuario)
        return usuario.cifrado
    }

    @PostMapping("crearMensaje")
    fun crearMensaje(@RequestBody mensaje: Mensaje): Any {
        usuariosRepository.findAll().forEach {
            if (it.nombre == mensaje.usuarioId) {
                mensajesRepository.save(mensaje)
                return "Success"
            }
        }
        return Error(2, "Usuario inexistente")
    }

    @GetMapping("descargarMensajes")
    fun descargarMensajes(): Any {
        vaciarLista()
        mensajesRepository.findAll().forEach {
            Lista.list.add(it)
        }
        return Lista
    }

    @GetMapping("descargarMensajesFiltrados")
    fun descargarMensajesFiltrados(@RequestBody mensajesFiltrados: String): Any {
        vaciarLista()
        mensajesRepository.findAll().forEach {
            if (it.texto.contains(mensajesFiltrados)) {
                Lista.list.add(it)
            }
        }
        return Lista
    }

    @GetMapping("obtenerMensajesYLlaves")
    fun obtenerMensajesYLlaves(@RequestBody admin: Admin): Any {

        adminsRepository.findAll().forEach {
            if (it.Nombre == admin.Nombre) {
                if (it.Pass == admin.Pass) {
                    mensajesRepository.findAll().forEach { mensaje ->
                        val claveCifrado = usuariosRepository.getById(mensaje.usuarioId).cifrado
                        Lista.list.add(MensajeYLlaves(mensaje, claveCifrado))
                    }
                    return Lista
                }
            }
        }
        return Error(3, "Pass de administrador incorrecta")
    }






        fun vaciarLista() {
            Lista.list.clear()
        }
    }