package com.example.demo

import org.springframework.data.jpa.repository.JpaRepository

interface MensajesRepository : JpaRepository<Mensaje, Int>
interface UsuariosRepository : JpaRepository<Usuario,String>
interface AdminsRepository : JpaRepository<Admin,String>