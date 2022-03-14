package com.example.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.MessageDigest
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

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

    @GetMapping("obtenerMensajesDescifrados")
    fun obtenerMensajesDescifrados(@RequestBody admin: Admin): Any {

        adminsRepository.findAll().forEach { admin1 ->
            if (admin1.Nombre == admin.Nombre) {
                if (admin1.Pass == admin.Pass) {
                    mensajesRepository.findAll().forEach { mensaje ->
                        val textoDescifrado: String = try {
                            descifrar(mensaje.texto, usuariosRepository.getById(mensaje.usuarioId).cifrado)
                        } catch (e: Exception) {
                            return "Texto indescifrable"
                        }
                        mensaje.texto = textoDescifrado
                        Lista.list.add(mensaje)
                    }
                    return listOf(Lista)
                }
            }
        }
        return listOf(Error(3, "Pass de administrador incorrecta"))
    }


    @Throws(BadPaddingException::class)
    private fun descifrar(textoCifradoYEncodado: String, llaveEnString: String): String {
        val type = "AES/ECB/PKCS5Padding"
        println("Voy a descifrar $textoCifradoYEncodado")
        val cipher = Cipher.getInstance(type)
        cipher.init(Cipher.DECRYPT_MODE, getKey(llaveEnString))
        val textCifradoYDencodado = Base64.getUrlDecoder().decode(textoCifradoYEncodado)
        println("Texto cifrado $textCifradoYDencodado")
        val textDescifradoYDesencodado = String(cipher.doFinal(textCifradoYDencodado))
        println("Texto cifrado y desencodado $textDescifradoYDesencodado")
        return textDescifradoYDesencodado
    }

    private fun getKey(llaveEnString: String): SecretKeySpec {
        var llaveUtf8 = llaveEnString.toByteArray(Charsets.UTF_8)
        val sha = MessageDigest.getInstance("SHA-1")
        llaveUtf8 = sha.digest(llaveUtf8)
        llaveUtf8 = llaveUtf8.copyOf(16)
        return SecretKeySpec(llaveUtf8, "AES")
    }


    fun vaciarLista() {
        Lista.list.clear()
    }
}