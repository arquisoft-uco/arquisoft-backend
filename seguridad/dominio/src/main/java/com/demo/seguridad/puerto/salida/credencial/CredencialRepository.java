package com.demo.seguridad.puerto.salida.credencial;

import com.demo.seguridad.modelo.CredencialRegistro;
import com.demo.seguridad.modelo.CredencialSesion;

public interface CredencialRepository {
    Integer crearCredencial(CredencialRegistro credencialRegistro);
    CredencialSesion consultarCredencialSesion(CredencialSesion credencialSesion);
    void editarCredencial(CredencialSesion credencialSesion);
}
